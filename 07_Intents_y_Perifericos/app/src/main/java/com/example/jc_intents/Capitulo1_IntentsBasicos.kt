package com.example.jc_intents

/*
────────────────────────────────────────────────────────────
CAPÍTULO 1 — INTENTS BÁSICOS (Compose + Kotlin)
────────────────────────────────────────────────────────────

Objetivo:
- Entender qué es un Intent implícito y cómo usarlo para delegar
  acciones comunes a otras apps del sistema sin pedir permisos.
- Practicar patrones típicos: navegar, llamar (marcador), email,
  mapas, SMS, compartir texto, abrir ajustes.
- Extras opcionales: insertar evento en calendario, abrir contactos,
  y abrir un archivo PDF (si hay app compatible).

Qué es un Intent implícito:
- Es una solicitud de acción (ACTION_*) + datos (Uri) que describe
  "qué" queremos hacer, sin indicar una app específica.
- El sistema elige la app compatible instalada.

Buenas prácticas:
- Validar entradas (URL, email, teléfono).
- Manejar ausencia de apps compatibles (try/catch).
- Usar Snackbar para feedback breve al usuario.

────────────────────────────────────────────────────────────
*/

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jc_intents.ui.theme.JC_IntentsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.core.net.toUri

/* ────────────────────────────────────────────────────────
  Composable principal del capítulo
  - Estructura base (Scaffold + Snackbars)
  - Estados de los campos (rememberSaveable)
  - Llama a cada sección (una función por Intent)
──────────────────────────────────────────────────────── */
@Composable
fun Capitulo1_IntentsBasicos() {
    // 1) Contexto de Android: se necesita para lanzar Intents (startActivity) y acceder a recursos del sistema.
    val context = LocalContext.current

    // 2) Host de Snackbars: administra los mensajes breves mostrados en pantalla.
    val snackbarHostState = remember { SnackbarHostState() }

    // 3) Alcance de coroutines en Compose: permite ejecutar tareas breves (ej. mostrar Snackbar) desde callbacks.
    val scope = rememberCoroutineScope()

    // 4) Estados de entradas del usuario
    //    rememberSaveable guarda los valores ante cambios de configuración (rotación), útil en formularios.
    var url by rememberSaveable { mutableStateOf("https://developer.android.com") }
    var phone by rememberSaveable { mutableStateOf("+57 300 123 4567") }
    var email by rememberSaveable { mutableStateOf("edward@unicauca.edu.co") }
    var subject by rememberSaveable { mutableStateOf("Consulta sobre el curso") }
    var body by rememberSaveable { mutableStateOf("Hola, tengo una duda sobre…") }
    var address by rememberSaveable { mutableStateOf("Universidad del Cauca, Popayán") }
    var smsTo by rememberSaveable { mutableStateOf("+57 300 123 4567") }
    var smsBody by rememberSaveable { mutableStateOf("Hola, esto es un SMS de prueba.") }
    var shareText by rememberSaveable { mutableStateOf("Estoy aprendiendo Intents con Compose") }

    // 5) Estados para secciones opcionales (extras del capítulo)
    var eventTitle by rememberSaveable { mutableStateOf("Reunión de ejemplo") }
    var eventLocation by rememberSaveable { mutableStateOf("Popayán") }
    var pdfUrl by rememberSaveable { mutableStateOf("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf") }

    // 6) Estructura base de pantalla: Scaffold organiza áreas estándar y gestiona insets del sistema.
    Scaffold(
        topBar = {
            // Barra superior simple: título del capítulo.
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Capítulo 1 — Intents básicos",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        // Conectamos el host para poder mostrar Snackbars desde cualquier sección.
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        // 7) Contenido desplazable: asegura que todas las secciones quepan en pantallas pequeñas.
        Column(
            modifier = Modifier
                .fillMaxSize()
                // innerPadding: evita que el contenido quede detrás de barras del sistema o topBar.
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Sección teórica breve (qué es un Intent implícito y por qué se usa).
            SeccionTeoria()

            // 1) Navegador — ACTION_VIEW + https://
            NavegadorSection(url, onUrlChange = { url = it }, context, snackbarHostState, scope)

            // 2) Marcador — ACTION_DIAL + tel:
            MarcadorSection(phone, onPhoneChange = { phone = it }, context, snackbarHostState, scope)

            // 3) Email — ACTION_SENDTO + mailto:
            EmailSection(
                email = email, onEmailChange = { email = it },
                subject = subject, onSubjectChange = { subject = it },
                body = body, onBodyChange = { body = it },
                context, snackbarHostState, scope
            )

            // 4) Mapas — ACTION_VIEW + geo:0,0?q=
            MapasSection(address, onAddressChange = { address = it }, context, snackbarHostState, scope)

            // 5) SMS — ACTION_SENDTO + smsto:
            SmsSection(
                smsTo = smsTo, onSmsToChange = { smsTo = it },
                smsBody = smsBody, onSmsBodyChange = { smsBody = it },
                context, snackbarHostState, scope
            )

            // 6) Compartir texto — ACTION_SEND + chooser
            CompartirSection(shareText, onShareChange = { shareText = it }, context, snackbarHostState, scope)

            // 7) Ajustes de la app — ACTION_APPLICATION_DETAILS_SETTINGS
            AjustesAppSection(context, snackbarHostState, scope)

            // ─ Extras opcionales ─

            // 8) Calendario — ACTION_INSERT (insertar evento)
            CalendarioSection(
                title = eventTitle, onTitleChange = { eventTitle = it },
                location = eventLocation, onLocationChange = { eventLocation = it },
                context, snackbarHostState, scope
            )

            // 9) Contactos — ACTION_VIEW (abre la agenda)
            ContactosSection(context, snackbarHostState, scope)

            // 10) Abrir archivo PDF — ACTION_VIEW + MIME type
            AbrirArchivoSection(
                pdfUrl = pdfUrl, onPdfUrlChange = { pdfUrl = it },
                context, snackbarHostState, scope
            )

        }
    }
}


/* ────────────────────────────────────────────────────────
  Sección teórica breve (qué y por qué de los Intents)
──────────────────────────────────────────────────────── */
@Composable
private fun SeccionTeoria() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumen: Intent implícito", style = MaterialTheme.typography.titleMedium)
            Text(
                "Permite solicitar una acción (ACTION_*) indicando datos (Uri), " +
                        "sin elegir manualmente la app. El sistema decide qué app puede atenderla."
            )
            Text(
                "Patrón común: ACTION_VIEW/ACTION_SEND/ACTION_SENDTO/ACTION_DIAL + Uri (https://, tel:, mailto:, geo:, smsto:). " +
                        "Valida entradas y maneja el caso sin app compatible con un try/catch."
            )
        }
    }
}

/* ────────────────────────────────────────────────────────
1) NAVEGADOR — ACTION_VIEW + https://
────────────────────────────────────────────────────────────
Objetivo:
Abrir una página web en el navegador predeterminado del sistema.

Conceptos clave:
- Intent implícito: describe la acción (ver una URL) sin indicar
  qué aplicación debe hacerlo. Android elegirá una app compatible.
- ACTION_VIEW: acción estándar para "ver" datos.
- Uri: representa la dirección del recurso (en este caso, una URL).
────────────────────────────────────────────────────────────
*/
@Composable
private fun NavegadorSection(
    url: String,                      // Valor actual del campo de texto.
    onUrlChange: (String) -> Unit,    // Callback para actualizar el estado cuando el usuario escribe.
    context: Context,                 // Contexto necesario para lanzar el Intent (startActivity).
    host: SnackbarHostState,          // Host que muestra mensajes (Snackbars) si ocurre un error.
    scope: CoroutineScope             // CoroutineScope usado para lanzar mensajes desde eventos.
) {
    // "CardSeccion" es un contenedor visual reutilizable que muestra un título y su contenido.
    CardSeccion("Abrir navegador (ACTION_VIEW + https://)") {

        /*
         OutlinedTextField:
         - Campo de entrada donde el usuario puede escribir una URL.
         - Usamos singleLine = true para mantener una sola línea de texto.
         - Modifier.fillMaxWidth() hace que ocupe todo el ancho disponible.
        */
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("URL (https://...)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        /*
         Button:
         Al presionarlo se validará la URL y, si es correcta, se creará
         y lanzará un Intent implícito para abrirla en el navegador.
        */
        Button(onClick = {
            // 1) Validar y corregir la URL ingresada
            //    ensureHttps agrega "https://" si el usuario olvidó incluirlo.
            val fixed = ensureHttps(url)

            // 2) Verificamos si la URL tiene un formato válido.
            if (!isValidHttpUrl(fixed)) {
                // Mostramos un mensaje breve si la URL no es válida.
                scope.launch { host.showSnackbar("URL inválida. Usa http(s)://") }
                return@Button
            }

            // 3) Crear el Intent implícito
            // ACTION_VIEW → indica que queremos "ver" algo (en este caso, la URL).
            // Uri.parse(fixed) → convierte el texto en un objeto Uri.
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixed))

            // 4) Lanzar el Intent de forma segura
            // safeStartActivity() usa try/catch internamente para evitar que
            // la app se cierre si no hay navegador instalado.
            context.safeStartActivity(intent) {
                // Si ocurre un error, mostramos una notificación al usuario.
                scope.launch { host.showSnackbar("No hay navegador disponible.") }
            }
        }) {
            Text("Abrir navegador")
        }
    }
}


/* ────────────────────────────────────────────────────────
  2) Marcador — ACTION_DIAL + tel:
  Abre la app de teléfono con el número listo en el marcador.
  - Sin permisos en tiempo de ejecución.
  - La llamada directa (ACTION_CALL) requiere permisos → Cap. 2.
──────────────────────────────────────────────────────── */
@Composable
private fun MarcadorSection(
    phone: String,                           // Estado actual del número.
    onPhoneChange: (String) -> Unit,         // Actualiza el número cuando el usuario escribe.
    context: Context,                        // Necesario para lanzar el Intent.
    host: SnackbarHostState,                 // Muestra mensajes breves (errores, avisos).
    scope: CoroutineScope                    // Permite lanzar Snackbars desde callbacks.
) {
    CardSeccion("Abrir marcador telefónico (ACTION_DIAL)") {

        // Campo de texto para ingresar el número (teclado de tipo teléfono).
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Número de teléfono") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // Al presionar el botón se crea y lanza el Intent implícito.
        Button(onClick = {
            // 1) Limpieza mínima: quitamos espacios para formar tel:XXXXXXXX
            val tel = phone.filterNot(Char::isWhitespace)

            // 2) Validación básica: no permitir número vacío.
            if (tel.isBlank()) {
                scope.launch { host.showSnackbar("Ingresa un número.") }
                return@Button
            }

            // 3) Construcción del Intent:
            //    - ACTION_DIAL abre la app de Teléfono con el número precargado.
            //    - Uri "tel:" es el esquema estándar para números.
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))

            // 4) Lanzamiento seguro: evitamos crash si no hay app compatible.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app de teléfono.") }
            }
        }) {
            Text("Abrir marcador")
        }

        // Nota docente: diferencia entre DIAL (sin permiso) y CALL (requiere permiso).
        Text(
            text = "Nota: para llamar directamente (ACTION_CALL) se requiere permiso CALL_PHONE.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/* ────────────────────────────────────────────────────────
  3) Email — ACTION_SENDTO + mailto:
  Redacta un correo en la app de email seleccionada por el sistema.
  - Usar mailto: filtra a clientes de correo (mejor UX que ACTION_SEND).
  - Asunto y cuerpo se pasan como parámetros de la Uri.
──────────────────────────────────────────────────────── */
@Composable
private fun EmailSection(
    email: String,                           // Destinatario.
    onEmailChange: (String) -> Unit,         // Actualiza destinatario.
    subject: String,                         // Asunto.
    onSubjectChange: (String) -> Unit,       // Actualiza asunto.
    body: String,                            // Cuerpo del mensaje.
    onBodyChange: (String) -> Unit,          // Actualiza cuerpo.
    context: Context,                        // Necesario para lanzar el Intent.
    host: SnackbarHostState,                 // Para mostrar avisos/errores.
    scope: CoroutineScope                    // Para lanzar Snackbars desde callbacks.
) {
    CardSeccion("Redactar email (ACTION_SENDTO + mailto:)") {

        // Campos de formulario básicos: destinatario, asunto y cuerpo.
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Para (email)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = subject,
            onValueChange = onSubjectChange,
            label = { Text("Asunto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = body,
            onValueChange = onBodyChange,
            label = { Text("Cuerpo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que compone el Intent y lo lanza.
        Button(onClick = {
            // 1) Validación mínima de email para evitar Intents inválidos.
            if (!isValidEmail(email)) {
                scope.launch { host.showSnackbar("Email inválido.") }
                return@Button
            }

            // 2) Construcción de la Uri "mailto:" con parámetros de consulta:
            //    - appendQueryParameter se encarga de codificar los valores (espacios, acentos).
            val uri = Uri.parse("mailto:$email")
                .buildUpon()
                .appendQueryParameter("subject", subject)
                .appendQueryParameter("body", body)
                .build()

            // 3) Intent con ACTION_SENDTO: restringe a clientes de correo (mejor UX que ACTION_SEND).
            val intent = Intent(Intent.ACTION_SENDTO, uri)

            // 4) Lanzamiento seguro con manejo de ausencia de app compatible.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay cliente de correo.") }
            }
        }) {
            Text("Redactar email")
        }
    }
}


/* ────────────────────────────────────────────────────────
  4) Mapas — ACTION_VIEW + geo:0,0?q=
  Objetivo:
  Abrir la app de mapas y buscar una dirección o lugar.

  Claves:
  - ACTION_VIEW: "ver/mostrar" un recurso.
  - URI geo: "geo:0,0?q=consulta" le pasa una búsqueda a la app de mapas.
  - Uri.encode(): codifica espacios y acentos en la query.
──────────────────────────────────────────────────────── */
@Composable
private fun MapasSection(
    address: String,                       // Dirección o lugar ingresado por el usuario.
    onAddressChange: (String) -> Unit,     // Actualiza el campo cuando el usuario escribe.
    context: Context,                      // Requerido para startActivity.
    host: SnackbarHostState,               // Para mostrar mensajes breves (Snackbars).
    scope: CoroutineScope                  // Para lanzar Snackbars desde callbacks.
) {
    CardSeccion("Abrir mapas (ACTION_VIEW + geo:0,0?q=...)") {

        // Campo de texto para ingresar la dirección o el nombre del lugar.
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            label = { Text("Dirección o lugar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que construye y lanza el Intent implícito hacia la app de mapas.
        Button(onClick = {
            // 1) Validación: no permitir búsqueda vacía.
            if (address.isBlank()) {
                scope.launch { host.showSnackbar("Ingresa una dirección o lugar.") }
                return@Button
            }

            // 2) Construcción de la URI "geo" con query:
            //    - "geo:0,0?q=..." deja que la app de mapas resuelva la búsqueda.
            //    - Uri.encode(...) asegura que espacios/acentos se codifiquen correctamente.
            val uri = "geo:0,0?q=${Uri.encode(address)}".toUri()

            // 3) Intent implícito con ACTION_VIEW para mostrar el resultado en la app de mapas.
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // 4) Lanzamiento seguro (manejo de ausencia de app compatible).
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app de mapas.") }
            }
        }) {
            Text("Abrir mapas")
        }
    }
}

/* ────────────────────────────────────────────────────────
  5) SMS — ACTION_SENDTO + smsto:
  Objetivo:
  Abrir una app de SMS con el destinatario y un mensaje sugerido.

  Claves:
  - ACTION_SENDTO restringe a apps que admiten el esquema indicado.
  - Esquema "smsto:" especifica que el destino es de SMS.
  - Extra "sms_body" sugiere el texto del mensaje.
──────────────────────────────────────────────────────── */
@Composable
private fun SmsSection(
    smsTo: String,                          // Número de teléfono destino.
    onSmsToChange: (String) -> Unit,        // Actualiza el número.
    smsBody: String,                        // Texto sugerido del SMS.
    onSmsBodyChange: (String) -> Unit,      // Actualiza el texto del SMS.
    context: Context,                       // Requerido para startActivity.
    host: SnackbarHostState,                // Para mostrar mensajes breves.
    scope: CoroutineScope                   // Para lanzar Snackbars desde callbacks.
) {
    CardSeccion("Componer SMS (ACTION_SENDTO + smsto:)") {

        // Campo para el número del destinatario (teclado tipo teléfono).
        OutlinedTextField(
            value = smsTo,
            onValueChange = onSmsToChange,
            label = { Text("Para (teléfono)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para el texto del mensaje.
        OutlinedTextField(
            value = smsBody,
            onValueChange = onSmsBodyChange,
            label = { Text("Mensaje") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que genera el Intent y abre la app de SMS.
        Button(onClick = {
            // 1) Limpieza y validación básica del número.
            val to = smsTo.filterNot(Char::isWhitespace)
            if (to.isBlank()) {
                scope.launch { host.showSnackbar("Ingresa un número de teléfono.") }
                return@Button
            }

            // 2) Intent implícito restringido a SMS:
            //    - ACTION_SENDTO con data "smsto:<numero>".
            //    - putExtra("sms_body", ...) sugiere el contenido del mensaje.
            val intent = Intent(Intent.ACTION_SENDTO, "smsto:$to".toUri()).apply {
                putExtra("sms_body", smsBody)
            }

            // 3) Lanzamiento seguro con manejo de error si no hay app de SMS.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app de SMS.") }
            }
        }) {
            Text("Abrir app de SMS")
        }
    }
}


/* ────────────────────────────────────────────────────────
  6) Compartir texto — ACTION_SEND + chooser
  Objetivo:
  Compartir un texto plano dejando que el usuario elija la app.

  Claves:
  - ACTION_SEND con type = "text/plain" indica contenido de texto.
  - Intent.createChooser(...) muestra un selector neutral del sistema.
  - EXTRA_TEXT lleva el contenido a compartir.
──────────────────────────────────────────────────────── */
@Composable
private fun CompartirSection(
    shareText: String,                    // Texto actual a compartir.
    onShareChange: (String) -> Unit,      // Actualiza el texto cuando el usuario escribe.
    context: Context,                     // Requerido para lanzar el Intent.
    host: SnackbarHostState,              // Para mostrar mensajes breves (errores, avisos).
    scope: CoroutineScope                 // Para lanzar Snackbars desde callbacks.
) {
    CardSeccion("Compartir texto (ACTION_SEND + chooser)") {

        // Campo de entrada del texto a compartir.
        OutlinedTextField(
            value = shareText,
            onValueChange = onShareChange,
            label = { Text("Texto a compartir") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que crea el Intent de compartir y lanza el "chooser".
        Button(onClick = {
            // 1) Validación simple: evitar compartir texto vacío.
            if (shareText.isBlank()) {
                scope.launch { host.showSnackbar("Texto vacío.") }
                return@Button
            }

            // 2) Intent de envío genérico de texto.
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            // 3) Chooser: selector del sistema que lista apps compatibles.
            val chooser = Intent.createChooser(sendIntent, "Compartir con…")

            // 4) Lanzamiento seguro (manejo de ausencia de apps compatibles).
            context.safeStartActivity(chooser) {
                scope.launch { host.showSnackbar("No hay apps para compartir.") }
            }
        }) {
            Text("Compartir…")
        }
    }
}

/* ────────────────────────────────────────────────────────
  7) Ajustes de la app — Settings.ACTION_APPLICATION_DETAILS_SETTINGS
  Objetivo:
  Abrir la pantalla de Ajustes de Android de ESTA aplicación,
  útil para guiar al usuario a permisos o notificaciones.

  Claves:
  - Uri de paquete: "package:<tu_paquete>" identifica la app.
  - ACTION_APPLICATION_DETAILS_SETTINGS abre la ficha de la app.
──────────────────────────────────────────────────────── */
@Composable
private fun AjustesAppSection(
    context: Context,                     // Requerido para lanzar el Intent.
    host: SnackbarHostState,              // Para mostrar mensajes breves.
    scope: CoroutineScope                 // Para lanzar Snackbars desde callbacks.
) {
    CardSeccion("(Opcional) Abrir ajustes de esta app") {
        Button(onClick = {
            // 1) Construimos la Uri con el nombre del paquete de esta app.
            val uri = Uri.fromParts("package", context.packageName, null)

            // 2) Intent directo a la pantalla de detalles de la aplicación.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)

            // 3) Lanzamiento seguro con manejo de error.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No se pudo abrir ajustes.") }
            }
        }) {
            Text("Abrir ajustes de la app")
        }
    }
}

/* ────────────────────────────────────────────────────────
  8) Calendario — ACTION_INSERT (insertar evento)
  Objetivo:
  Abrir la UI de calendario con un evento nuevo “pre-rellenado”
  (título y ubicación). El usuario decide finalmente guardarlo o no.

  Claves:
  - ACTION_INSERT con data = CalendarContract.Events.CONTENT_URI
    abre la pantalla de creación de eventos.
  - No requiere permisos para mostrar la UI de inserción.
  - Puedes sugerir más campos (DTSTART, DTEND, ALL_DAY) en milisegundos.
──────────────────────────────────────────────────────── */
@Composable
private fun CalendarioSection(
    title: String,                         // Título sugerido para el evento.
    onTitleChange: (String) -> Unit,       // Actualiza el título.
    location: String,                      // Ubicación sugerida (opcional).
    onLocationChange: (String) -> Unit,    // Actualiza la ubicación.
    context: Context,                      // Requerido para startActivity.
    host: SnackbarHostState,               // Para mensajes breves.
    scope: CoroutineScope                  // Para lanzar Snackbars.
) {
    CardSeccion("(Opcional) Insertar evento en Calendario (ACTION_INSERT)") {

        // Campos de formulario básicos: título (obligatorio) y ubicación (opcional).
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Título del evento") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Ubicación (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que crea el Intent y abre la UI de calendario.
        Button(onClick = {
            // 1) Validación mínima: el título es lo único realmente necesario aquí.
            if (title.isBlank()) {
                scope.launch { host.showSnackbar("El título no puede estar vacío.") }
                return@Button
            }

            // 2) Intent para insertar un nuevo evento con datos sugeridos.
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                // Ejemplos adicionales (opcional):
                // putExtra(CalendarContract.Events.ALL_DAY, true)
                // putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 3600000)
                // putExtra(CalendarContract.EXTRA_EVENT_END_TIME,   System.currentTimeMillis() + 7200000)
            }

            // 3) Lanzamiento seguro (si no hay app de calendario compatible, informamos).
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app de calendario compatible.") }
            }
        }) {
            Text("Insertar evento")
        }
    }
}

/* ────────────────────────────────────────────────────────
  9) Contactos — ACTION_VIEW (abre la agenda)
  Objetivo:
  Abrir la app de Contactos para navegarla.

  Claves:
  - ACTION_VIEW con ContactsContract.Contacts.CONTENT_URI abre la agenda.
  - “Seleccionar un contacto y devolverlo” se verá en el Cap. 2 con
    ActivityResultContracts (flujo con resultado).
──────────────────────────────────────────────────────── */
@Composable
private fun ContactosSection(
    context: Context,                      // Requerido para startActivity.
    host: SnackbarHostState,               // Para mensajes breves.
    scope: CoroutineScope                  // Para lanzar Snackbars.
) {
    CardSeccion("(Opcional) Abrir Contactos (ACTION_VIEW)") {
        Button(onClick = {
            // 1) Intent para abrir la lista/agenda de contactos.
            val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)

            // 2) Lanzamiento seguro con manejo de ausencia de app compatible.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app de contactos disponible.") }
            }
        }) {
            Text("Abrir contactos")
        }
    }
}

/* ────────────────────────────────────────────────────────
  10) Abrir archivo PDF — ACTION_VIEW + MIME
  Objetivo:
  Intent genérico para abrir un contenido “application/pdf”.
  Usamos una URL pública como ejemplo. Si no hay visor de PDF,
  se informa mediante Snackbar.

  Claves:
  - ACTION_VIEW + setDataAndType(uri, "application/pdf")
  - Si no hay app compatible, capturamos el error con safeStartActivity.
  - Para URIs de archivos locales se recomienda FileProvider (Cap. 2/3).
──────────────────────────────────────────────────────── */
@Composable
private fun AbrirArchivoSection(
    pdfUrl: String,                        // URL http(s) hacia un PDF de ejemplo.
    onPdfUrlChange: (String) -> Unit,      // Actualiza la URL.
    context: Context,                      // Requerido para startActivity.
    host: SnackbarHostState,               // Para mensajes breves.
    scope: CoroutineScope                  // Para lanzar Snackbars.
) {
    CardSeccion("(Opcional) Abrir archivo PDF (ACTION_VIEW + MIME)") {

        // Campo de texto con la URL del PDF a abrir.
        OutlinedTextField(
            value = pdfUrl,
            onValueChange = onPdfUrlChange,
            label = { Text("URL de un PDF") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que crea el Intent y lo lanza.
        Button(onClick = {
            // 1) Validación mínima: debe empezar por http(s)://
            if (!isValidHttpUrl(pdfUrl)) {
                scope.launch { host.showSnackbar("Ingresa una URL http(s) válida a un PDF.") }
                return@Button
            }

            // 2) Intent genérico de vista con MIME específico para PDF.
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(pdfUrl), "application/pdf")
                // FLAG opcional: limpia la actividad superior si ya había una instancia.
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            // 3) Lanzamiento seguro con manejo de ausencia de visor PDF.
            context.safeStartActivity(intent) {
                scope.launch { host.showSnackbar("No hay app compatible para PDF.") }
            }
        }) {
            Text("Abrir PDF")
        }
    }
}


/* ────────────────────────────────────────────────────────
  UI auxiliar: tarjeta de sección con título
──────────────────────────────────────────────────────── */
@Composable
private fun CardSeccion(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
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
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            contenido()
        }
    }
}

/* ────────────────────────────────────────────────────────
  Helpers: validaciones y startActivity seguro
──────────────────────────────────────────────────────── */
private fun ensureHttps(input: String): String {
    val t = input.trim()
    if (t.startsWith("http://") || t.startsWith("https://")) return t
    return "https://$t"
}
private fun isValidHttpUrl(text: String): Boolean =
    text.startsWith("http://") || text.startsWith("https://")

private fun isValidEmail(text: String): Boolean =
    "@" in text && "." in text.substringAfter("@")

private inline fun Context.safeStartActivity(
    intent: Intent,
    onError: () -> Unit
) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        onError()
    }
}

/* ────────────────────────────────────────────────────────
  Preview (UI estática; no lanza Intents)
──────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewCapitulo1_IntentsBasicos() {
    JC_IntentsTheme {
        Capitulo1_IntentsBasicos()
    }
}
