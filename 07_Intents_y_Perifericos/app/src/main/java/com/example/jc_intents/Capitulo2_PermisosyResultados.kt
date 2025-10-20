package com.example.jc_intents

/*
────────────────────────────────────────────────────────────
CAPÍTULO 2 — PERMISOS Y MANEJO DE RESULTADOS (API moderna)
────────────────────────────────────────────────────────────

Objetivo del capítulo:
- Entender cómo solicitar permisos en tiempo de ejecución
  con la API moderna (Activity Result).
- Preparar la base para lanzar acciones que devuelven resultados
  (cámara, galería, archivos), que iremos agregando paso a paso.

En esta primera entrega:
- Implementamos la sección de permisos (cámara + lectura de imágenes),
  con manejo de rationale y acceso a Ajustes de la app.
- Deja listos los contenedores para las siguientes secciones.

Nota:
- Android 13+ (API 33) separa permisos de lectura por tipo de medio.
  Aquí usamos READ_MEDIA_IMAGES para 33+ y READ_EXTERNAL_STORAGE para 32-.
────────────────────────────────────────────────────────────
*/

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jc_intents.ui.theme.JC_IntentsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip




/* ────────────────────────────────────────────────────────
  Composable principal del capítulo
  - Estructura base (Scaffold + Snackbars)
  - Por ahora solo renderiza la sección de permisos
  - Las demás secciones se agregarán en funciones separadas
──────────────────────────────────────────────────────── */
@Composable
fun Capitulo2_PermisosYResultados() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // Barra superior simple (Material 3 estable)
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Capítulo 2 — Permisos y resultados",
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
            // ───────────────────────────────────────────────
            // SECCIÓN 1: Permisos base (implementada)
            // ───────────────────────────────────────────────
            PermisosBaseSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )
            // 2) Cámara preview (solo muestra thumbnail temporal)
            CamaraPreviewSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )

            // 3) Cámara completa — guardando directamente en galería
            CamaraFullGaleriaSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )

            GaleriaGetContentSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )

            AbrirDocumentoPdfSection(
                context = context,
                snackbar = snackbar,
                scope = scope
            )

        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — Permisos base (Cámara + Lectura de imágenes)
────────────────────────────────────────────────────────────
Objetivo:
• Mostrar los permisos más comunes de Android (CAMERA y READ_MEDIA_IMAGES).
• Solicitar permisos en tiempo de ejecución usando la API moderna:
  → ActivityResultLauncher + ActivityResultContracts.RequestPermission.
• Detectar cuándo el usuario marcó “No volver a preguntar” y ofrecer
  el botón "Ir a Ajustes" para activarlo manualmente.

────────────────────────────────────────────────────────────
CONCEPTOS CLAVE
────────────────────────────────────────────────────────────
1) Desde Android 6.0 (API 23), los permisos sensibles se solicitan
   mientras la app está en ejecución, no solo en el Manifest.

2) La API moderna de resultados (ActivityResultLauncher) reemplaza
   el método clásico onRequestPermissionsResult(), facilitando
   la integración con Jetpack Compose.

3) Si el usuario marca “No volver a preguntar”, la app debe guiarlo
   a los Ajustes de Android, donde puede reactivar manualmente el permiso.

4) Android 13 (API 33) separa los permisos de lectura por tipo de medio:
   → READ_MEDIA_IMAGES (API 33+)
   → READ_EXTERNAL_STORAGE (API 32 o menor)
────────────────────────────────────────────────────────────
*/
@Composable
fun PermisosBaseSection(
    context: Context,             // Necesario para acceder al sistema y abrir Ajustes.
    snackbar: SnackbarHostState,  // Muestra mensajes breves al usuario.
    scope: CoroutineScope         // Controla las corrutinas para lanzar Snackbars.
) {
    // 1) Determinamos el permiso correcto para lectura de imágenes según la versión.
    val mediaReadPermission = remember { buildMediaImagesPermission() }

    // 2) Estados observables: indican si el permiso está concedido o no.
    //    Compose redibuja automáticamente la UI cuando cambian.
    var cameraGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    var mediaGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, mediaReadPermission) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    // 3) Flags adicionales: detectan si el usuario marcó “No volver a preguntar”.
    //    Si esto ocurre, ya no podemos mostrar el cuadro de solicitud otra vez.
    var cameraPermanentlyDenied by remember { mutableStateOf(false) }
    var mediaPermanentlyDenied by remember { mutableStateOf(false) }

    // 4) Obtenemos la Activity actual (necesaria para shouldShowRequestPermissionRationale()).
    val activity = context.findActivity()

    /*
    ────────────────────────────────────────────────────────────
    LAUNCHERS DE PERMISOS
    ────────────────────────────────────────────────────────────
    Cada launcher lanza el cuadro de solicitud y recibe el resultado
    (granted = true / false). Si el usuario deniega y el sistema ya
    no volverá a mostrar el cuadro, consideramos que es “denegación
    permanente” y mostramos el botón “Ir a Ajustes”.
    */

    // Launcher para permiso de CÁMARA
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraGranted = granted
        if (!granted && activity != null) {
            cameraPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        }
        // Mensaje visual en Snackbar
        scope.launch {
            snackbar.showSnackbar(
                if (granted) "Permiso de cámara concedido"
                else "Permiso de cámara denegado"
            )
        }
    }

    // Launcher para permiso de LECTURA DE IMÁGENES
    val requestMediaPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        mediaGranted = granted
        if (!granted && activity != null) {
            mediaPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                mediaReadPermission
            )
        }
        val label =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                "imágenes (API 33+)"
            else
                "almacenamiento (API ≤32)"
        scope.launch {
            snackbar.showSnackbar(
                if (granted) "Permiso de lectura de $label concedido"
                else "Permiso de lectura de $label denegado"
            )
        }
    }

    /*
    ────────────────────────────────────────────────────────────
    INTERFAZ VISUAL (UI)
    ────────────────────────────────────────────────────────────
    Cada permiso se representa con una fila (PermisoRow) que muestra:
    - El nombre del permiso.
    - Un chip indicando si está concedido o no.
    - Botón “Solicitar” para pedirlo.
    - Botón “Ir a Ajustes” si fue denegado permanentemente.
    */
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Permisos: cámara y lectura de imágenes",
                style = MaterialTheme.typography.titleMedium
            )

            // ─────────────────────────────
            // 1) Permiso de cámara
            // ─────────────────────────────
            PermisoRow(
                titulo = "Cámara (CAMERA)",
                concedido = cameraGranted,
                rationaleVisible = false,
                onSolicitar = { requestCameraPermission.launch(Manifest.permission.CAMERA) },
                onIrAjustes = { openAppSettings(context) },
                showAjustes = !cameraGranted && cameraPermanentlyDenied
            )

            // ─────────────────────────────
            // 2) Permiso de lectura de imágenes
            // ─────────────────────────────
            val mediaLabel =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    "Leer imágenes (READ_MEDIA_IMAGES)"
                else
                    "Leer almacenamiento (READ_EXTERNAL_STORAGE)"

            PermisoRow(
                titulo = mediaLabel,
                concedido = mediaGranted,
                rationaleVisible = false,
                onSolicitar = { requestMediaPermission.launch(mediaReadPermission) },
                onIrAjustes = { openAppSettings(context) },
                showAjustes = !mediaGranted && mediaPermanentlyDenied
            )

            // Mensaje educativo (sección inferior)
            Text(
                text = "Si el usuario marcó 'No volver a preguntar', aparecerá el botón 'Ir a Ajustes' " +
                        "para reactivar el permiso manualmente desde el sistema.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
PermisoRow()
────────────────────────────────────────────────────────────
Función auxiliar que muestra una fila con la información y
acciones de un permiso específico.
────────────────────────────────────────────────────────────
*/
@Composable
private fun PermisoRow(
    titulo: String,
    concedido: Boolean,
    rationaleVisible: Boolean,
    onSolicitar: () -> Unit,
    onIrAjustes: () -> Unit,
    showAjustes: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // Encabezado: título + estado actual del permiso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge)
            AssistChip(
                onClick = { /* Decorativo, sin acción */ },
                label = { Text(if (concedido) "Concedido" else "No concedido") }
            )
        }

        // Bloque de botones de acción
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSolicitar) { Text("Solicitar") }

            // Solo se muestra si el permiso fue denegado permanentemente
            if (showAjustes) {
                OutlinedButton(onClick = onIrAjustes) { Text("Ir a Ajustes") }
            }
        }
    }
}

/* ────────────────────────────────────────────────────────
  SECCIÓN 2 — Cámara (preview) con TakePicturePreview()
  Objetivo:
  - Abrir la cámara del sistema y obtener un Bitmap pequeño (thumbnail).
  - No guarda archivo; es ideal para demostrar “resultado” sencillo.
  Por qué así:
  - Evita FileProvider al inicio; nos concentramos en permisos + launcher.
──────────────────────────────────────────────────────── */
@Composable
fun CamaraPreviewSection(
    context: Context,             // Para consultar permiso si hace falta.
    snackbar: SnackbarHostState,  // Feedback breve (éxito / cancelación).
    scope: CoroutineScope         // Lanzar Snackbars desde callbacks.
) {
    // 1) Estado del último resultado (Bitmap). Con remember basta (no persiste rotación).
    var lastPreview by remember { mutableStateOf<Bitmap?>(null) }

    // 2) Launcher moderno: devuelve un Bitmap? (null si el usuario cancela).
    val takePicturePreview = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        lastPreview = bmp
        if (bmp == null) {
            scope.launch { snackbar.showSnackbar("Captura cancelada") }
        } else {
            scope.launch { snackbar.showSnackbar("Preview capturado") }
        }
    }

    // 3) Launcher para pedir permiso de cámara si aún no está concedido.
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // si el usuario concede, disparamos de inmediato la cámara en preview
            takePicturePreview.launch(null)
        } else {
            scope.launch { snackbar.showSnackbar("Permiso de cámara denegado") }
        }
    }

    // 4) UI: botón para capturar y vista previa del Bitmap resultante.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Cámara — Preview (TakePicturePreview)", style = MaterialTheme.typography.titleMedium)

            Button(onClick = {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                // Pedimos permiso justo antes de usar la cámara.
                if (!granted) {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                } else {
                    takePicturePreview.launch(null)
                }
            }) {
                Text("Abrir cámara (preview)")
            }

            // Si hay resultado, lo mostramos.
            lastPreview?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Preview capturado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}


/* ────────────────────────────────────────────────────────
  SECCIÓN 3 — Cámara (foto completa) guardando en GALERÍA
  Objetivo:
  - Crear un Uri en MediaStore (galería) antes de lanzar la cámara.
  - Pasar ese Uri al contrato TakePicture() para que la cámara escriba ahí.
  - Mostrar la foto y confirmar que quedó guardada.

  Por qué así:
  - En Android 10+ (API 29) con Scoped Storage, insertar en MediaStore
    NO requiere permisos de escritura (la app obtiene acceso a su propio Uri).
  - Evitamos usar caché y el usuario ve la foto directamente en la galería.

  Nota:
  - En API ≤ 28 se necesitaría WRITE_EXTERNAL_STORAGE para escribir en galería.
    Aquí mostramos un aviso para mantener el capítulo simple y moderno.
──────────────────────────────────────────────────────── */

/* ────────────────────────────────────────────────────────
  SECCIÓN 3 — Cámara (foto completa) guardando en GALERÍA
  Cambios:
  - Eliminamos IS_PENDING (al delegar a la app de cámara puede fallar).
  - Marcamos correctamente éxito/cancel y limpiamos pendingImageUri.
  - Mostramos la imagen corrigiendo orientación con EXIF.
──────────────────────────────────────────────────────── */
@Composable
fun CamaraFullGaleriaSection(
    context: Context,
    snackbar: SnackbarHostState,
    scope: CoroutineScope
) {
    var lastImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    // 1) Contrato: cámara escribe en el Uri que le pasamos.
    val takePictureFull = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingImageUri != null) {
            // Éxito: fijamos como último resultado y limpiamos pending
            lastImageUri = pendingImageUri
            pendingImageUri = null
            scope.launch { snackbar.showSnackbar("Foto guardada en la galería") }
        } else {
            // Cancelado o error: si insertamos un item, lo borramos para no dejar “huecos”
            pendingImageUri?.let { uri ->
                runCatching { context.contentResolver.delete(uri, null, null) }
            }
            pendingImageUri = null
            scope.launch { snackbar.showSnackbar("Captura cancelada") }
        }
    }

    // 2) Permiso de cámara justo antes de usar.
    val requestCameraPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            scope.launch { snackbar.showSnackbar("Permiso de cámara denegado") }
            return@rememberLauncherForActivityResult
        }
        // Concedido → crear Uri en galería y lanzar cámara
        createImageItemInGallery(context) { createdUri ->
            pendingImageUri = createdUri
            if (createdUri != null) {
                takePictureFull.launch(createdUri)
            } else {
                scope.launch { snackbar.showSnackbar("No se pudo crear el destino en la galería") }
            }
        }
    }

    // 3) UI
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Cámara — Foto completa en galería (TakePicture + MediaStore)", style = MaterialTheme.typography.titleMedium)

            Button(onClick = {
                // Solo guardamos directo en galería en API 29+ (Scoped Storage)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    scope.launch {
                        snackbar.showSnackbar("API ≤ 28: para guardar en galería se requiere WRITE_EXTERNAL_STORAGE o usar un fallback.")
                    }
                    return@Button
                }

                val camGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (!camGranted) {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                } else {
                    createImageItemInGallery(context) { createdUri ->
                        pendingImageUri = createdUri
                        if (createdUri != null) {
                            takePictureFull.launch(createdUri)
                        } else {
                            scope.launch { snackbar.showSnackbar("No se pudo crear el destino en la galería") }
                        }
                    }
                }
            }) {
                Text("Capturar y guardar en galería")
            }

            // Vista previa: decodifica aplicando corrección de orientación EXIF
            lastImageUri?.let { uri ->
                val bmp = remember(uri) { decodeBitmapRespectingExif(context, uri, 2000, 2000) }
                bmp?.let { img ->
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = "Foto guardada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Text(text = "Uri de la imagen: $uri", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/* ────────────────────────────────────────────────────────
  SECCIÓN 4 — Seleccionar imagen (SAF) con GetContent
  Objetivo:
  - Abrir el selector del sistema para elegir una imagen.
  - No guarda archivo ni requiere permisos de almacenamiento.
  - Devuelve un Uri temporal que podemos mostrar en pantalla.

  Nota:
  - GetContent NO otorga acceso persistente. Si quieres reabrir
    el contenido tras reiniciar la app, usa OpenDocument.
──────────────────────────────────────────────────────── */
@Composable
fun GaleriaGetContentSection(
    context: Context,
    snackbar: SnackbarHostState,
    scope: CoroutineScope
) {
    // Última imagen elegida (Uri temporal)
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Contrato moderno: selecciona contenido con un filtro MIME
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        pickedImageUri = uri
        scope.launch {
            snackbar.showSnackbar(if (uri != null) "Imagen seleccionada" else "Selección cancelada")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Galería — Seleccionar imagen (GetContent)", style = MaterialTheme.typography.titleMedium)

            Button(onClick = { pickImage.launch("image/*") }) {
                Text("Elegir imagen…")
            }

            // Vista previa: decodifica el Uri a Bitmap (corrigiendo EXIF si existe)
            pickedImageUri?.let { uri ->
                val bmp = remember(uri) { decodeBitmapRespectingExif(context, uri, 2000, 2000) }
                bmp?.let { img ->
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = "Imagen seleccionada",
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
  SECCIÓN 5 — Abrir documento (PDF) con OpenDocument
  Objetivo:
  - Elegir un PDF con el selector del sistema (SAF).
  - Solicitar acceso persistente al Uri (persistable permission)
    para reabrir el documento en futuros lanzamientos.
  - Permitir abrir el PDF con apps compatibles (ACTION_VIEW).

  Por qué OpenDocument:
  - A diferencia de GetContent, permite "persistir" el acceso al Uri.
  - No requiere permisos de almacenamiento; es la vía moderna.
──────────────────────────────────────────────────────── */
@Composable
fun AbrirDocumentoPdfSection(
    context: Context,
    snackbar: SnackbarHostState,
    scope: CoroutineScope
) {
    // Último PDF elegido (persistible)
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    // Contrato: el sistema devuelve un Uri al PDF seleccionado
    val openPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) {
            scope.launch { snackbar.showSnackbar("Selección cancelada") }
            return@rememberLauncherForActivityResult
        }
        pdfUri = uri

        // Pedimos acceso persistente SOLO si el intent/sistema lo permite (OpenDocument lo hace)
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        scope.launch { snackbar.showSnackbar("PDF seleccionado (acceso persistente otorgado)") }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Documentos — Abrir PDF (OpenDocument)", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { openPdf.launch(arrayOf("application/pdf")) }) {
                    Text("Elegir PDF…")
                }
                OutlinedButton(
                    onClick = {
                        val uri = pdfUri ?: run {
                            scope.launch { snackbar.showSnackbar("Primero elige un PDF") }
                            return@OutlinedButton
                        }
                        // Abrimos con cualquier visor disponible (Drive PDF Viewer, etc.)
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.safeStartActivity(intent) {
                            scope.launch { snackbar.showSnackbar("No hay app compatible para PDF") }
                        }
                    },
                    enabled = pdfUri != null
                ) {
                    Text("Abrir con visor")
                }
            }

            // Info del Uri (útil para el alumno)
            pdfUri?.let { uri ->
                Text(text = "Uri persistente: $uri", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private inline fun Context.safeStartActivity(
    intent: Intent,
    onError: () -> Unit
) {
    try { startActivity(intent) } catch (_: ActivityNotFoundException) { onError() }
}


/* Crea un ítem en MediaStore (galería) y devuelve su Uri.
   Notas:
   - API 29+: NO pedimos WRITE_EXTERNAL_STORAGE.
   - No usamos IS_PENDING porque otra app (cámara) hará la escritura. */
private inline fun createImageItemInGallery(
    context: Context,
    crossinline onUriReady: (Uri?) -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        onUriReady(null); return
    }

    val name = "IMG_${System.currentTimeMillis()}.jpg"
    val values = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, name)
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/JC_Intents")
        // Opcional: fecha tomada (ayuda a ordenar en algunas galerías)
        put(android.provider.MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    }

    val resolver = context.contentResolver
    val collection = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val itemUri = resolver.insert(collection, values)

    onUriReady(itemUri)
}

/* Decodifica el Uri a Bitmap y corrige orientación usando EXIF (si existe).
   Evita imágenes “volteadas” por el sensor. */
private fun decodeBitmapRespectingExif(
    context: Context,
    uri: Uri,
    maxWidth: Int,
    maxHeight: Int
): android.graphics.Bitmap? {
    return try {
        // 1) Leer Bitmap básico
        val base = context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) return null
            android.graphics.BitmapFactory.decodeStream(input)
        } ?: return null

        // 2) Leer EXIF para orientación
        val exifOrientation = context.contentResolver.openInputStream(uri).use { input ->
            if (input == null) return@use null
            androidx.exifinterface.media.ExifInterface(input)
                .getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
                )
        } ?: androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL

        // 3) Construir matriz de rotación/flip según EXIF
        val matrix = android.graphics.Matrix().apply {
            when (exifOrientation) {
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
                androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
                // Otros casos menos comunes pueden combinarse
            }
        }

        val rotated = android.graphics.Bitmap.createBitmap(
            base, 0, 0, base.width, base.height, matrix, true
        )

        // 4) Redimensionar si excede límites (para evitar OOM)
        val ratio = minOf(maxWidth.toFloat() / rotated.width, maxHeight.toFloat() / rotated.height, 1f)
        if (ratio < 1f) {
            android.graphics.Bitmap.createScaledBitmap(
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
Helpers de permisos / Ajustes / Context → Activity
──────────────────────────────────────────────────────── */
private fun buildMediaImagesPermission(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

private fun openAppSettings(context: Context) {
    val uri = Uri.fromParts("package", context.packageName, null)
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

private fun Context.findActivity(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/* ────────────────────────────────────────────────────────
Preview (UI estática; no ejecuta permisos)
──────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCapitulo2_PermisosYResultados() {
    JC_IntentsTheme {
        Capitulo2_PermisosYResultados()
    }
}
