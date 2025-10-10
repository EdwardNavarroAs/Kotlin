package com.example.jetpackcomposekotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 1 — INTERACCIÓN BÁSICA ENTRE COMPONENTES COMPOSE
────────────────────────────────────────────────────────────

VERSIÓN: 100% ESTABLE (SIN APIs EXPERIMENTALES)

OBJETIVO DEL CAPÍTULO
──────────────────────
Construir una pantalla que demuestre **interacciones reales** entre
componentes base de Jetpack Compose (Material 3) y explicar **qué son**,
**por qué** y **para qué** se usa cada uno. Este archivo está escrito
como si fuera un **capítulo de libro**: incluye teoría, contexto de
arquitectura, decisiones de diseño y comentarios pedagógicos.

✔ Text / Button / OutlinedButton / IconButton (conceptos base)
✔ TextField / OutlinedTextField (entrada controlada)
✔ Checkbox / Switch (estados booleanos)
✔ DropdownMenu (selección)
✔ Slider (valores continuos)
✔ Snackbar vs Toast (feedback visual y del sistema)
✔ Card / OutlinedCard (presentación de información)
✔ Scaffold (estructura base de pantalla con top bar personalizada + FAB)

IMPORTANTE: evitamos APIs experimentales. La barra superior se implementa
manual (Surface + Row + Text) en vez de usar TopAppBar de M3.

────────────────────────────────────────────────────────────
MAPA DEL CAPÍTULO (ÍNDICE)
────────────────────────────────────────────────────────────
1) Capitulo1_InteraccionBasica(): orquesta todo con Scaffold
2) TopBarPersonalizada(): barra superior estable y centrada
3) SeccionTextosYBotonesBasicos(...): estado + eventos onClick
4) SeccionEntradasTexto(...): validación + rememberSaveable
5) SeccionCheckboxYSwitchBasico(): binarios controlados
6) SeccionDropdownBasico(): menú contextual de opciones
7) SeccionSliderBasico(): valores continuos con Slider
8) SeccionSnackbarYToast(...): feedback de app vs sistema
9) SeccionTarjetasResumen(...): jerarquía visual con Cards
10) Preview: render rápido en Android Studio

────────────────────────────────────────────────────────────
GLOSARIO RÁPIDO (para el/la estudiante)
────────────────────────────────────────────────────────────
• Estado (State): dato que, al cambiar, **redibuja** (recompone) la UI
  que lo lee. Ej.: nombre, email, switches, etc.
• remember: recuerda un valor **mientras** la función composable siga
  en composición. Se pierde si Activity se recrea (rotación, proceso).
• rememberSaveable: igual que remember, pero **persiste** entre
  recreaciones usando SavedInstanceState (serializa tipos comunes).
• Hoisting de estado: mantener el estado en un **padre** y pasarlo a los
  **hijos** por parámetros, junto con callbacks para cambiarlo. Mejora
  testeo, reusabilidad y trazabilidad.
• Scaffold: contenedor de alto nivel con zonas estándar: topBar,
  snackbarHost, floatingActionButton (FAB) y contenido. Ahorra layout
  manual y soluciona insets (espacios) automáticamente.

────────────────────────────────────────────────────────────
NOTA SOBRE RECOMPOSICIÓN
────────────────────────────────────────────────────────────
En Compose, las funciones @Composable pueden ejecutarse muchas veces.
**No** guardes estado en variables normales si debe sobrevivir recomposi-
ción. Usa remember/rememberSaveable para que el valor permanezca visible
para la UI. Evita trabajo pesado en cada recomposición.
────────────────────────────────────────────────────────────
*/

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposekotlin.ui.theme.JetpackComposeKotlinTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/*
────────────────────────────────────────────────────────────
1) FUNCIÓN PRINCIPAL — Orquestador con Scaffold
────────────────────────────────────────────────────────────
PROPÓSITO
─────────
• Centralizar el estado **compartido** (hoisting) y distribuirlo a las
  secciones hijas. Así, cada sección muestra un aspecto del capítulo sin
  duplicar estados ni lógica.
• Proveer estructura visual consistente mediante `Scaffold`.

DECISIONES CLAVE
────────────────
• Los estados del formulario (nombre, email, aceptación) viven **aquí**
  para poder reutilizarlos abajo (resumen, validación en FAB, etc.).
• `snackbarHostState` vive aquí para que **todas** las secciones puedan
  mostrar mensajes mediante un callback (evita dependencias cruzadas).
*/
@Composable
fun Capitulo1_InteraccionBasica() {
    // Contexto Android (para Toast del sistema)
    val context = LocalContext.current

    // SnackbarHostState: canal oficial para mostrar snackbars desde un Scaffold
    val snackbarHostState = remember { SnackbarHostState() }

    // Alcance de corrutinas ligado a la composición (se cancela automáticamente)
    val scope = rememberCoroutineScope()

    // ──────────────────────────────────────────────────────
    // ESTADO COMPARTIDO (Formulario simple)
    // Se usa rememberSaveable para persistir datos ante rotaciones.
    // ──────────────────────────────────────────────────────
    var nombre by rememberSaveable { mutableStateOf("") } // campo obligatorio
    var email by rememberSaveable { mutableStateOf("") }  // debe contener "@"
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) } // obligación legal

    // Reglas mínimas de validación (ejemplo pedagógico)
    val esValido = nombre.isNotBlank() && email.contains("@") && aceptaTerminos

    Scaffold(
        // 2) Barra superior 100% estable (sin APIs experimentales)
        topBar = { TopBarPersonalizada() },

        // Conectar el host de snackbars para toda la pantalla
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        // Acción principal: enviar (FAB). Aquí validamos y damos feedback.
        floatingActionButton = {
            /*
            ─────────────────────────────────────────────────────────────
            EXTENDED FLOATING ACTION BUTTON (FAB)
            ─────────────────────────────────────────────────────────────
            El FAB (Floating Action Button) representa la **acción principal**
            de la pantalla. Su versión “Extended” combina el ícono + texto o,
            si lo prefieres, solo texto.

            ❖ Por qué se usa aquí:
              → Es el punto final de interacción del formulario (“Enviar”).
              → Es visible y accesible desde cualquier parte del scroll.
              → Encapsula la acción más importante del usuario.

            ❖ Diferencias con FloatingActionButton clásico:
              • El clásico es solo circular (ícono).
              • El “Extended” puede tener texto e ícono, y se expande o
                contrae automáticamente si lo configuramos con “expanded”.

            ❖ Nota sobre sintaxis:
              En Compose moderno, el bloque de contenido `{}` (RowScope)
              reemplaza los parámetros antiguos `text = {}` o `icon = {}`.
              Por eso escribimos:

                  ExtendedFloatingActionButton(
                      onClick = { ... }
                  ) {
                      Text("Enviar")
                  }

              Aquí el contenido `{ Text("Enviar") }` define el cuerpo visual
              del FAB. Podrías incluir Row, Icon, Spacer, etc.
            ─────────────────────────────────────────────────────────────
            */
            ExtendedFloatingActionButton(
                onClick = {
                    // Se lanza una corrutina (asincronía controlada)
                    scope.launch {
                        // Validación: si el formulario no cumple, avisamos
                        if (!esValido) {
                            snackbarHostState.showSnackbar(
                                "Completa los campos obligatorios y acepta los términos."
                            )
                            // return@launch → corta la ejecución solo de este bloque
                            return@launch
                        }

                        // Simulación de envío (en una app real, aquí iría la lógica)
                        snackbarHostState.showSnackbar("Enviando datos…")
                        delay(800)

                        // Feedback visual dentro de la app (Snackbar)
                        snackbarHostState.showSnackbar(
                            message = "¡Enviado!",
                            withDismissAction = true
                        )

                        // Feedback del sistema (Toast)
                        Toast.makeText(context, "Gracias, $nombre", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                /*
                ─────────────────────────────────────────────────────────────
                CONTENIDO VISUAL DEL FAB
                ─────────────────────────────────────────────────────────────
                Este bloque representa el cuerpo del FAB. Compose le pasa
                implícitamente un RowScope, de modo que puedes incluir varios
                elementos horizontales (ícono, texto, espaciador…).

                Ejemplo posible:
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Enviar")
                    }

                Aquí lo simplificamos solo a un texto, para enfocarnos en
                la semántica de acción principal.
                ─────────────────────────────────────────────────────────────
                */
                Text("Enviar")
            }
        }

    ) { innerPadding ->
        /*
        innerPadding: margen que evita que el contenido quede oculto
        detrás de la top bar o del FAB. Siempre úsalo cuando definas
        `topBar`/`bottomBar`/FAB; Compose calcula los insets por ti.
        */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // Scroll vertical: si el contenido crece, no se corta
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*
            Cada sección ilustra un aspecto del capítulo. Observa cómo
            pasamos estado y callbacks cuando la sección lo requiere.
            */

            // 3) Textos y botones: primera interacción con estado
            SeccionTextosYBotonesBasicos(
                nombre = nombre,
                onNombreChange = { nombre = it },
                onSaludar = {
                    // Nota de UX: el Toast no depende del tema; es del SO.
                    Toast.makeText(context, "Hola, $nombre 👋", Toast.LENGTH_SHORT).show()
                }
            )

            // 4) Entradas de texto y validación mínima
            SeccionEntradasTexto(
                email = email,
                onEmailChange = { email = it },
                aceptaTerminos = aceptaTerminos,
                onAceptaTerminosChange = { aceptaTerminos = it }
            )

            // 5) Binarios: checkbox y switch controlados internamente
            SeccionCheckboxYSwitchBasico()

            // 6) Selección con Dropdown (lista corta)
            SeccionDropdownBasico()

            // 7) Valor continuo con Slider
            SeccionSliderBasico()

            // 8) Feedback visual (Snackbars) vs sistema (Toasts)
            SeccionSnackbarYToast(
                onShowSnackbar = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
                onShowToast = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )

            // 9) Resumen visual del estado compartido
            SeccionTarjetasResumen(
                nombre = nombre,
                email = email,
                aceptaTerminos = aceptaTerminos,
                esValido = esValido
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
2) TOP BAR PERSONALIZADA — alternativa 100% estable
────────────────────────────────────────────────────────────
¿POR QUÉ ESTA DECISIÓN?
• Las TopAppBar de Material 3 pueden estar anotadas como experimentales.
• Implementar la barra a mano te permite comprender cómo componer
  componentes básicos para lograr el mismo resultado de manera estable.

DISEÑO
• `Surface` da fondo y (opcionalmente) sombra con `tonalElevation`.
• `Row` centra el contenido horizontalmente.
• `Text` muestra el título. Usa los colores del tema para accesibilidad.
*/
@Composable
fun TopBarPersonalizada() {
    Surface(
        tonalElevation = 3.dp, // Realza la barra sobre el contenido
        color = MaterialTheme.colorScheme.primaryContainer // Fondo acorde al tema
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp), // Altura cómoda para tocar/leer
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Capítulo 1 — Interacción básica",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
3) SECCIÓN — Textos y Botones básicos
────────────────────────────────────────────────────────────
OBJETIVO
• Mostrar cómo un **estado** (nombre) habilita/deshabilita acciones.
• Practicar eventos `onClick` y el patrón de **state hoisting**.

DECISIONES
• `OutlinedTextField` mejora la legibilidad de campos en formularios.
• `Row` con `Arrangement.spacedBy` separa botones sin Spacers manuales.
*/
@Composable
private fun SeccionTextosYBotonesBasicos(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onSaludar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Text / Button — Fundamentos", style = MaterialTheme.typography.titleMedium)
            Text(
                "Los botones ejecutan acciones por eventos 'onClick'. " +
                        "Habilitamos 'Saludar' solo si el nombre no está vacío."
            )

            // Campo controlado: el valor viene del estado de nivel superior
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange, // devolvemos el dato al padre
                label = { Text("Nombre (requerido para habilitar)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(enabled = nombre.isNotBlank(), onClick = onSaludar) {
                    Text("Saludar")
                }
                OutlinedButton(onClick = { onNombreChange("") }) {
                    Text("Limpiar")
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
4) SECCIÓN — Entradas de texto + validación mínima
────────────────────────────────────────────────────────────
OBJETIVO
• Demostrar validación **visual** (isError + supportingText) y por qué es
  importante la UX de formularios.
• Introducir `PasswordVisualTransformation` para ocultar contraseñas.

NOTAS
• `onValueChange` recibe SIEMPRE el texto completo nuevo. Aplica `trim()`
  si no quieres espacios accidentales.
• La validación en UI es inmediata, pero **no reemplaza** validaciones
  de dominio/servidor.
*/
@Composable
private fun SeccionEntradasTexto(
    email: String,
    onEmailChange: (String) -> Unit,
    aceptaTerminos: Boolean,
    onAceptaTerminosChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("TextField / Validación básica", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { onEmailChange(it.trim()) },
                label = { Text("Email *") },
                singleLine = true,
                isError = email.isNotBlank() && !email.contains("@"), // Regla UX mínima
                supportingText = {
                    if (email.isNotBlank() && !email.contains("@"))
                        Text("Debe contener @")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de contraseña demostrativo (no se valida aquí)
            var password by rememberSaveable { mutableStateOf("") }
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña (demo)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Aceptación de términos: requisito típico legal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = aceptaTerminos, onCheckedChange = onAceptaTerminosChange)
                Spacer(Modifier.width(8.dp))
                Text("Acepto términos y condiciones *")
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
5) SECCIÓN — Checkbox y Switch básicos
────────────────────────────────────────────────────────────
OBJETIVO
• Mostrar controles ON/OFF: ambos son booleanos y se actualizan con
  `onCheckedChange`. Se usan para preferencias y elecciones rápidas.

NOTA
• Aquí el estado es **local** a la sección porque no se necesita fuera.
  Si un padre necesitará leerlo, lo hoistearíamos como en otras secciones.
*/
@Composable
private fun SeccionCheckboxYSwitchBasico() {
    var suscrito by rememberSaveable { mutableStateOf(false) }
    var modoOscuro by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Checkbox / Switch", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = suscrito, onCheckedChange = { suscrito = it })
                Spacer(Modifier.width(8.dp))
                Text(if (suscrito) "Notificaciones activadas" else "Notificaciones desactivadas")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Modo oscuro")
                Spacer(Modifier.width(12.dp))
                Switch(checked = modoOscuro, onCheckedChange = { modoOscuro = it })
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
6) SECCIÓN — DropdownMenu básico
────────────────────────────────────────────────────────────
OBJETIVO
• Ofrecer una selección de tamaño **reducido** (pocas opciones) sin
  ocupar espacio permanente en pantalla.

DISEÑO
• Un botón "ancla" abre/cierra el menú (controlado por un booleano).
• Al seleccionar, guardamos el valor y cerramos el menú.
*/
@Composable
private fun SeccionDropdownBasico() {
    val opciones = listOf("Usuario", "Administrador", "Invitado")
    var expandido by rememberSaveable { mutableStateOf(false) }
    var seleccion by rememberSaveable { mutableStateOf(opciones.first()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("DropdownMenu", style = MaterialTheme.typography.titleMedium)
            Text("Rol actual: $seleccion")

            OutlinedButton(onClick = { expandido = true }) { Text("Elegir rol") }
            DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                opciones.forEach { rol ->
                    DropdownMenuItem(
                        text = { Text(rol) },
                        onClick = {
                            seleccion = rol // guardamos opción
                            expandido = false // cerramos menú
                        }
                    )
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
7) SECCIÓN — Slider (valor continuo)
────────────────────────────────────────────────────────────
OBJETIVO
• Controlar un valor numérico continuo con feedback visual inmediato.

NOTAS
• `valueRange` define el rango permitido.
• `steps` agrega marcas intermedias sin fraccionar el valor almacenado.
*/

@Composable
private fun SeccionSliderBasico() {
    // Nivel como Float para Slider; rememberSaveable persiste tras rotaciones
    var nivel by rememberSaveable { mutableStateOf(5f) } // valor inicial

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Slider", style = MaterialTheme.typography.titleMedium)

            // Mostrar el entero 0..10 (¡ojo a ${...} para evaluar expresiones!)
            Text("Nivel: ${nivel.toInt()} / 10")

            // Slider discreto; al soltar, normalizamos a entero exacto
            Slider(
                value = nivel,
                onValueChange = { nuevo -> nivel = nuevo },
                valueRange = 0f..10f,
                steps = 9,
                onValueChangeFinished = {
                    nivel = nivel.roundToInt().toFloat()
                }
            )

            // Nota: si quisieras “snap” inmediato mientras arrastras:
            // onValueChange = { nivel = it.roundToInt().toFloat() }
            // (más estricto, pero arrastre menos suave)
        }
    }
}


/*
────────────────────────────────────────────────────────────
8) SECCIÓN — Snackbar y Toast
────────────────────────────────────────────────────────────
DIFERENCIAS CLAVE
• Snackbar: aparece dentro de la jerarquía de la app (tema, accesible,
  puede tener acción). Ideal para feedback contextual y reversible.
• Toast: superpuesto por el SISTEMA (no sigue el tema). Útil para
  avisos breves que no requieren acción dentro de la UI.
*/
@Composable
private fun SeccionSnackbarYToast(
    onShowSnackbar: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Snackbar vs Toast", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onShowSnackbar("Este es un Snackbar dentro de la app") }) {
                    Text("Mostrar Snackbar")
                }
                OutlinedButton(onClick = { onShowToast("Este es un Toast del sistema") }) {
                    Text("Mostrar Toast")
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
9) SECCIÓN — Resumen visual (Cards)
────────────────────────────────────────────────────────────
OBJETIVO
• Mostrar cómo **presentar** información de forma jerárquica.
• `Card` para resaltar bloque; `OutlinedCard` para contorno discreto.

NOTA
• Evitamos chips experimentales; usamos solo textos para estados.
*/
@Composable
private fun SeccionTarjetasResumen(
    nombre: String,
    email: String,
    aceptaTerminos: Boolean,
    esValido: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Resumen actual", style = MaterialTheme.typography.titleMedium)
            Text("Nombre: $nombre")
            Text("Email: $email")
            Text("Acepta términos: $aceptaTerminos")
            Text(if (esValido) "✔ Listo para enviar" else "✖ Faltan datos obligatorios")
        }
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Notas rápidas", style = MaterialTheme.typography.titleSmall)
            Text(
                "• Usa rememberSaveable para conservar datos ante rotaciones.\n" +
                        "• Prefiere Snackbar para feedback en contexto; Toast para avisos breves del sistema.\n" +
                        "• Aplica hoisting de estado: el padre guarda el estado y los hijos emiten eventos.\n" +
                        "• Evita lógica pesada dentro de composables; usa ViewModel/casos de uso."
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
10) PREVIEW — Vista previa rápida en Android Studio
────────────────────────────────────────────────────────────
Permite renderizar el capítulo sin ejecutar la app completa. Útil para
iterar en diseño, espaciados y tipografía.
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo1_InteraccionBasica() {
    JetpackComposeKotlinTheme {
        Capitulo1_InteraccionBasica()
    }
}
