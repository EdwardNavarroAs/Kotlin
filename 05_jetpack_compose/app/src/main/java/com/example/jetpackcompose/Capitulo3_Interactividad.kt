package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3 — INTERACTIVIDAD Y ESTADO EN JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Comprender cómo Jetpack Compose gestiona la interactividad mediante
el uso de estados (State) y funciones reactivas.

✔ Diferencia entre estado y variable normal.
✔ Uso de remember y mutableStateOf.
✔ rememberSaveable para persistir estado ante rotaciones.
✔ Manejo de eventos (onClick, onValueChange, etc.).
✔ Uso de Slider, TextField y Checkbox.
✔ Actualización automática de la UI sin llamadas manuales.

────────────────────────────────────────────────────────────
INTRODUCCIÓN TEÓRICA
────────────────────────────────────────────────────────────
En Jetpack Compose, la **UI es una función del estado**:
    UI = f(state)

Esto significa que **no se actualiza la vista directamente**.
En cambio, se modifica el estado, y Compose se encarga de redibujar
solo las partes necesarias de la interfaz.

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Concepto de estado y remember.
2) TextField y actualización de texto reactiva.
3) Checkbox y Switch (interacción binaria).
4) Slider y manejo numérico en tiempo real.
5) Ejemplo completo de interactividad integrada.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — ESTADO BÁSICO CON remember Y mutableStateOf
────────────────────────────────────────────────────────────
El estado en Compose se guarda usando `remember { mutableStateOf() }`.

→ remember → recuerda el valor durante recomposiciones.
→ mutableStateOf → permite que Compose observe los cambios.

Cada vez que el valor cambia, Compose “recompone” solo
las funciones afectadas.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionEstadoBasico() {
    // Variable reactiva de texto
    var nombre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Introduce tu nombre:",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo de texto controlado por estado
        TextField(
            value = nombre,
            onValueChange = { nombre = it }, // actualiza estado al escribir
            label = { Text("Nombre") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (nombre.isNotEmpty()) "Hola, $nombre 👋" else "Esperando tu nombre...",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — ESTADO PERSISTENTE CON rememberSaveable
────────────────────────────────────────────────────────────
`rememberSaveable` guarda el estado incluso cuando el dispositivo
rota o la actividad se recrea.

Ideal para conservar datos de entrada del usuario.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionRememberSaveable() {
    var edad by rememberSaveable { mutableStateOf(18) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona tu edad:",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Slider reactivo
        Slider(
            value = edad.toFloat(),
            onValueChange = { edad = it.toInt() },
            valueRange = 0f..100f
        )

        Text(
            text = "Edad seleccionada: $edad años",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — CHECKBOX Y SWITCH
────────────────────────────────────────────────────────────
Componentes booleanos que representan estados de activación.

En Compose, basta con mantener un estado booleano (true/false)
y cambiarlo en el evento `onCheckedChange`.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionCheckboxYSwitch() {
    var suscrito by remember { mutableStateOf(false) }
    var modoOscuro by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = suscrito,
                onCheckedChange = { suscrito = it }
            )
            Text("Recibir notificaciones")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo oscuro:")
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = modoOscuro,
                onCheckedChange = { modoOscuro = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (suscrito) "Notificaciones activadas" else "Notificaciones desactivadas",
            color = if (suscrito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Text(
            text = if (modoOscuro) "Modo oscuro ON" else "Modo oscuro OFF",
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — INTEGRACIÓN COMPLETA: FORMULARIO INTERACTIVO
────────────────────────────────────────────────────────────
Este ejemplo combina todos los elementos anteriores:
TextField, Slider, Checkbox y Button.

Muestra cómo el estado y los eventos se comunican naturalmente
entre componentes.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionFormularioInteractivo() {
    var nombre by remember { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf(25) }
    var suscrito by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("Formulario de usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })

        Spacer(modifier = Modifier.height(16.dp))
        Text("Edad: $edad años")
        Slider(value = edad.toFloat(), onValueChange = { edad = it.toInt() }, valueRange = 0f..100f)

        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = suscrito, onCheckedChange = { suscrito = it })
            Text("Suscribirme al boletín")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                resultado = if (nombre.isNotEmpty()) {
                    "Usuario: $nombre ($edad años) — " +
                            if (suscrito) "Suscrito" else "No suscrito"
                } else "Por favor, ingresa tu nombre."
            }
        ) {
            Text("Enviar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(resultado, color = MaterialTheme.colorScheme.primary)
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 5 — PANTALLA COMPLETA DEL CAPÍTULO 3
────────────────────────────────────────────────────────────
Integra todas las secciones anteriores como demostración unificada
del manejo de estado y eventos en Jetpack Compose.

────────────────────────────────────────────────────────────
NUEVO: SCROLL EN COLUMN
────────────────────────────────────────────────────────────
Se agregó un `verticalScroll(rememberScrollState())` para permitir
desplazamiento vertical cuando el contenido excede la altura de la
pantalla. Este modificador convierte la `Column` en un contenedor
desplazable sin alterar la composición interna.
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo3_Interactividad() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Scroll vertical agregado aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // ← Permite desplazamiento
        ) {
            Text(
                text = "CAPÍTULO 3 — INTERACTIVIDAD Y ESTADO",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
            SeccionEstadoBasico()

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionRememberSaveable()

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionCheckboxYSwitch()

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionFormularioInteractivo()
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA COMPLETA DEL CAPÍTULO 3
────────────────────────────────────────────────────────────
Permite renderizar toda la interactividad del capítulo directamente
en el panel de Preview de Android Studio.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo3() {
    JetpackComposeTheme {
        Capitulo3_Interactividad()
    }
}
