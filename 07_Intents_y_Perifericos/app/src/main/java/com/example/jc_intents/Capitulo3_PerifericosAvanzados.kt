package com.example.jc_intents

/*
────────────────────────────────────────────────────────────
MÓDULO 07 — CAPÍTULO 3: PERIFÉRICOS AVANZADOS
────────────────────────────────────────────────────────────

Objetivo del capítulo:
• Usar APIs modernas para interactuar directamente con hardware
  sin depender de Intents (p. ej., CameraX para cámara en tiempo real).

En esta primera entrega:
• CameraX — Preview en vivo + captura y guardado en galería (MediaStore).

Notas:
• CameraX se integra con el ciclo de vida: “bindToLifecycle()”.
• Guardamos en MediaStore (Scoped Storage), sin permisos legacy de escritura
  en Android 10+ (API 29+). Se requiere solo el permiso de cámara.
────────────────────────────────────────────────────────────
*/

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.jc_intents.ui.theme.JC_IntentsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

// IMPORTS: usa alias para la de CameraX
import androidx.camera.core.Preview as CameraXPreview
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner


/* ────────────────────────────────────────────────────────
  Pantalla principal del capítulo
  - Estructura base (Scaffold + Snackbars)
  - Sección 1: CameraX (preview + captura)
  - Secciones futuras: Audio, Sensores (a agregar)
──────────────────────────────────────────────────────── */
@Composable
fun Capitulo3_PerifericosAvanzados() {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Capítulo 3 — Periféricos avanzados",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1) CameraX: preview + captura guardando en galería
            CamaraX_PreviewCaptureSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )

            AudioSection_GrabarYReproducir(
                context = context,
                snackbar = snackbar,
                scope = scope
            )
            
            // SensoresSection_Acelerometro(...)
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — CameraX (preview + captura a galería)
────────────────────────────────────────────────────────────
Qué hace:
• Muestra el preview en vivo usando PreviewView (CameraX).
• Captura una foto de alta calidad con ImageCapture.
• Guarda directamente en la galería (MediaStore → Pictures/JC_Intents).
• Muestra la última foto guardada (decodificada, corrigiendo EXIF).

Por qué así:
• CameraX maneja la cámara de forma consistente y ligada al ciclo de vida.
• Usamos MediaStore para integrarnos bien con Scoped Storage (Android 10+).
────────────────────────────────────────────────────────────
*/
@Composable
fun CamaraX_PreviewCaptureSection(
    context: Context,
    snackbar: SnackbarHostState,
    scope: CoroutineScope
) {
    // 1) Infraestructura de CameraX
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val mainExecutor: Executor = remember { ContextCompat.getMainExecutor(context) }

    // 2) Referencias mutables a los “use cases” de CameraX
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraReady by remember { mutableStateOf(false) }

    // 3) Resultado de la última captura (Uri en galería)
    var lastSavedUri by remember { mutableStateOf<Uri?>(null) }

    // 4) Permisos: solicitamos CAMERA justo antes de iniciar la cámara
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch { snackbar.showSnackbar("Permiso de cámara concedido") }
            // Nada más; el binding se hace al pulsar el botón Iniciar/Reconectar
        } else {
            scope.launch { snackbar.showSnackbar("Permiso de cámara denegado") }
        }
    }

    // 5) Vista y lógica de CameraX
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("CameraX — Preview + captura", style = MaterialTheme.typography.titleMedium)

            // Contenedor AndroidView: aloja el PreviewView (vista nativa de CameraX)
            var previewView by remember { mutableStateOf<PreviewView?>(null) }
            AndroidPreview(
                onReady = { pv -> previewView = pv },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            // Botones de control
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    // a) Comprobamos permiso
                    val granted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                    if (!granted) {
                        requestCameraPermission.launch(Manifest.permission.CAMERA)
                        return@Button
                    }

                    // b) Iniciar/reenlazar CameraX
                    val pv = previewView ?: run {
                        scope.launch { snackbar.showSnackbar("PreviewView no está listo") }
                        return@Button
                    }
                    startCameraX(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        previewView = pv,
                        onImageCaptureReady = { imageCapture = it },
                        onBound = { cameraReady = true },
                        onError = { msg -> scope.launch { snackbar.showSnackbar(msg) } }
                    )
                }) {
                    Text(if (cameraReady) "Reconectar cámara" else "Iniciar cámara")
                }

                OutlinedButton(
                    onClick = {
                        val capture = imageCapture
                        if (capture == null) {
                            scope.launch { snackbar.showSnackbar("Inicia la cámara antes de capturar") }
                            return@OutlinedButton
                        }
                        // Creamos destino en MediaStore y lanzamos captura
                        val outputOptions = buildGalleryOutputOptions(context)
                        capture.takePicture(
                            outputOptions,
                            mainExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onError(exception: ImageCaptureException) {
                                    scope.launch { snackbar.showSnackbar("Error al guardar: ${exception.message}") }
                                }
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    lastSavedUri = outputFileResults.savedUri
                                    scope.launch { snackbar.showSnackbar("Foto guardada en galería") }
                                }
                            }
                        )
                    },
                    enabled = cameraReady
                ) {
                    Text("Capturar")
                }
            }

            // Vista previa de la última foto guardada (decodifica y corrige EXIF)
            lastSavedUri?.let { uri ->
                val bmp = remember(uri) { decodeBitmapRespectingExif(context, uri, 2000, 2000) }
                bmp?.let { img ->
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = "Última foto guardada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Text(text = "Uri: $uri", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/* ────────────────────────────────────────────────────────
AndroidPreview:
- Crea un PreviewView (vista nativa para CameraX).
- No inicia la cámara; solo entrega la vista cuando está lista.
──────────────────────────────────────────────────────── */
@Composable
private fun AndroidPreview(
    onReady: (PreviewView) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                // Estrategia de escalado común para encajar el preview
                scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                // Entregamos la vista al que la llama
                onReady(this)
            }
        }
    )
}


/* ────────────────────────────────────────────────────────
  SECCIÓN 2 — Audio: grabar y reproducir (MediaRecorder/MediaPlayer)
  Objetivo:
  • Grabar audio desde el micrófono y guardarlo directo en la galería de audio (MediaStore).
  • Reproducir la última grabación con MediaPlayer.
  • Pedir RECORD_AUDIO justo antes de grabar (permiso runtime).

  Por qué así:
  • En Android 10+ (API 29) podemos insertar en MediaStore sin permisos legacy de escritura.
  • Guardar en MediaStore hace que aparezca en apps de música/archivos.

  Formato:
  • .m4a (MPEG_4 + AAC) → buena compatibilidad y compresión.
──────────────────────────────────────────────────────── */
@Composable
fun AudioSection_GrabarYReproducir(
    context: Context,
    snackbar: SnackbarHostState,
    scope: CoroutineScope
) {
    // Estado de UI
    var isRecording by remember { mutableStateOf(false) }
    var lastAudioUri by remember { mutableStateOf<Uri?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Recursos nativos (se limpian en onDispose)
    var recorder by remember { mutableStateOf<android.media.MediaRecorder?>(null) }
    var player by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var currentRecordingUri by remember { mutableStateOf<Uri?>(null) }

    // Permiso RECORD_AUDIO
    val requestRecordAudio = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            scope.launch { snackbar.showSnackbar("Permiso de micrófono denegado") }
            return@rememberLauncherForActivityResult
        }
        // Si concede, iniciamos grabación inmediatamente (flujo didáctico)
        startRecording(
            context = context,
            onPrepared = { r, uri ->
                recorder = r; currentRecordingUri = uri; isRecording = true
                scope.launch { snackbar.showSnackbar("Grabando…") }
            },
            onError = { msg -> scope.launch { snackbar.showSnackbar(msg) } }
        )
    }

    // UI
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Audio — Grabar y reproducir", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                // Iniciar grabación
                Button(
                    onClick = {
                        if (isRecording) {
                            scope.launch { snackbar.showSnackbar("Ya estás grabando") }
                            return@Button
                        }
                        val micGranted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                        if (!micGranted) {
                            requestRecordAudio.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            startRecording(
                                context = context,
                                onPrepared = { r, uri ->
                                    recorder = r; currentRecordingUri = uri; isRecording = true
                                    scope.launch { snackbar.showSnackbar("Grabando…") }
                                },
                                onError = { msg -> scope.launch { snackbar.showSnackbar(msg) } }
                            )
                        }
                    }
                ) { Text("Grabar") }

                // Detener grabación
                OutlinedButton(
                    onClick = {
                        if (!isRecording) {
                            scope.launch { snackbar.showSnackbar("No hay grabación en curso") }
                            return@OutlinedButton
                        }
                        runCatching { recorder?.stop() }
                        runCatching { recorder?.release() }
                        recorder = null
                        isRecording = false

                        // Guardamos el Uri como último audio válido
                        lastAudioUri = currentRecordingUri
                        currentRecordingUri = null

                        scope.launch { snackbar.showSnackbar("Grabación guardada") }
                    },
                    enabled = isRecording
                ) { Text("Detener") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Reproducir última grabación
                Button(
                    onClick = {
                        val uri = lastAudioUri ?: run {
                            scope.launch { snackbar.showSnackbar("No hay grabaciones") }
                            return@Button
                        }
                        if (isPlaying) {
                            scope.launch { snackbar.showSnackbar("Ya se está reproduciendo") }
                            return@Button
                        }
                        startPlayback(
                            context = context,
                            uri = uri,
                            onStarted = { p -> player = p; isPlaying = true },
                            onCompleted = {
                                isPlaying = false
                                scope.launch { snackbar.showSnackbar("Reproducción finalizada") }
                            },
                            onError = { msg -> scope.launch { snackbar.showSnackbar(msg) } }
                        )
                    }
                ) { Text("Reproducir") }

                // Detener reproducción
                OutlinedButton(
                    onClick = {
                        runCatching { player?.stop() }
                        runCatching { player?.release() }
                        player = null
                        isPlaying = false
                    },
                    enabled = isPlaying
                ) { Text("Detener audio") }
            }

            // Info útil para el alumno
            lastAudioUri?.let { uri ->
                Text("Última grabación: $uri", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // Limpieza de recursos si el composable sale del árbol
    DisposableEffect(Unit) {
        onDispose {
            runCatching { recorder?.stop() }
            runCatching { recorder?.release() }
            recorder = null

            runCatching { player?.stop() }
            runCatching { player?.release() }
            player = null
        }
    }
}

/* ────────────────────────────────────────────────────────
startRecording:
- Inserta un item en MediaStore (Music/JC_Intents) y configura MediaRecorder
  para escribir directamente sobre ese Uri.
- Llama a onPrepared cuando el recorder está listo y la grabación ha iniciado.
──────────────────────────────────────────────────────── */
private fun startRecording(
    context: Context,
    onPrepared: (android.media.MediaRecorder, Uri) -> Unit,
    onError: (String) -> Unit
) {
    // Solo soportamos guardado directo en galería en API 29+ (Scoped Storage)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        onError("API ≤ 28: para guardar en galería se requiere WRITE_EXTERNAL_STORAGE o usar almacenamiento interno.")
        return
    }

    // 1) Crear destino en MediaStore (Music/JC_Intents, MIME: audio/mp4)
    val name = "AUD_${System.currentTimeMillis()}.m4a"
    val values = ContentValues().apply {
        put(MediaStore.Audio.Media.DISPLAY_NAME, name)
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
        put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/JC_Intents")
        put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        // Opcional: título, artista, etc.
        put(MediaStore.Audio.Media.TITLE, name)
    }
    val resolver = context.contentResolver
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val itemUri = resolver.insert(collection, values)
    if (itemUri == null) {
        onError("No se pudo crear el archivo de audio en MediaStore.")
        return
    }

    // 2) Abrir el FileDescriptor del Uri para que MediaRecorder escriba ahí
    val pfd = resolver.openFileDescriptor(itemUri, "w") ?: run {
        onError("No se pudo abrir el destino de audio.")
        // Limpieza: borrar entrada vacía
        runCatching { resolver.delete(itemUri, null, null) }
        return
    }

    // 3) Configurar y arrancar MediaRecorder
    try {
        val recorder = android.media.MediaRecorder().apply {
            setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128_000)
            setOutputFile(pfd.fileDescriptor)
            prepare()
            start()
        }
        onPrepared(recorder, itemUri)
    } catch (e: Exception) {
        // Si algo falla, eliminamos el item para no dejar “huecos”
        runCatching { resolver.delete(itemUri, null, null) }
        onError("Error al iniciar grabación: ${e.message}")
    }
}

/* ────────────────────────────────────────────────────────
startPlayback:
- Configura MediaPlayer con el Uri de la última grabación y reproduce.
- Notifica onCompleted al terminar.
──────────────────────────────────────────────────────── */
private fun startPlayback(
    context: Context,
    uri: Uri,
    onStarted: (android.media.MediaPlayer) -> Unit,
    onCompleted: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val player = android.media.MediaPlayer().apply {
            setDataSource(context, uri)
            setOnCompletionListener { onCompleted() }
            prepare()
            start()
        }
        onStarted(player)
    } catch (e: Exception) {
        onError("No se pudo reproducir: ${e.message}")
    }
}



/* ────────────────────────────────────────────────────────
startCameraX:
- Obtiene el CameraProvider y enlaza los “use cases” al ciclo de vida.
- Use cases:
  • Preview: muestra imagen en vivo en el PreviewView.
  • ImageCapture: captura foto de alta calidad.
- targetRotation: ayuda a que la captura respete la orientación del dispositivo.
──────────────────────────────────────────────────────── */
private fun startCameraX(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onBound: () -> Unit,
    onError: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        try {
            // Use case: Preview
            val preview = CameraXPreview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // Use case: ImageCapture (modo priorizando calidad)
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // o MAXIMIZE_QUALITY según tu caso
                .setTargetRotation(previewView.display?.rotation ?: android.view.Surface.ROTATION_0)
                .build()

            // Selector de cámara: trasera por defecto
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            // Primero des-enlazamos si ya había algo
            cameraProvider.unbindAll()
            // Enlazamos al ciclo de vida
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            onImageCaptureReady(imageCapture)
            onBound()
        } catch (e: Exception) {
            onError("No se pudo iniciar CameraX: ${e.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}

/* ────────────────────────────────────────────────────────
buildGalleryOutputOptions:
- Crea el destino en la galería (MediaStore) para guardar la foto.
- Android 10+ (API 29) → Scoped Storage: no requiere permisos legacy.
──────────────────────────────────────────────────────── */
private fun buildGalleryOutputOptions(context: Context): ImageCapture.OutputFileOptions {
    val name = "IMG_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, name)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        // Carpeta virtual en la galería
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/JC_Intents")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
    }
    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    return ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        contentUri,
        contentValues
    ).build()
}

/* ────────────────────────────────────────────────────────
decodeBitmapRespectingExif:
- Decodifica un Uri a Bitmap y corrige orientación usando EXIF.
- Evita que la foto se muestre “girada” según el sensor.
──────────────────────────────────────────────────────── */
private fun decodeBitmapRespectingExif(
    context: Context,
    uri: Uri,
    maxWidth: Int,
    maxHeight: Int
): Bitmap? {
    return try {
        // 1) Bitmap base
        val base = context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) return null
            BitmapFactory.decodeStream(input)
        } ?: return null

        // 2) EXIF → orientación
        val exifOrientation = context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) return@use null
            androidx.exifinterface.media.ExifInterface(input)
                .getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
                )
        } ?: androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL

        // 3) Matriz de rotación/flip
        val matrix = Matrix().apply {
            when (exifOrientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
            }
        }

        val rotated = Bitmap.createBitmap(base, 0, 0, base.width, base.height, matrix, true)

        // 4) Redimensionar si es demasiado grande (para evitar OOM)
        val ratio = minOf(maxWidth.toFloat() / rotated.width, maxHeight.toFloat() / rotated.height, 1f)
        if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                rotated,
                (rotated.width * ratio).toInt(),
                (rotated.height * ratio).toInt(),
                true
            )
        } else rotated
    } catch (_: Exception) {
        null
    }
}

/* ────────────────────────────────────────────────────────
Preview (UI estática; no ejecuta CameraX)
──────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCapitulo3_PerifericosAvanzados() {
    JC_IntentsTheme {
        Capitulo3_PerifericosAvanzados()
    }
}
