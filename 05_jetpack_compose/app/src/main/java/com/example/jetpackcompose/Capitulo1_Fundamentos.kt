package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 1 — FUNDAMENTOS DE JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Comprender los conceptos base para crear interfaces declarativas en Android:
✔ Cómo estructurar una UI usando funciones @Composable.
✔ Uso de Column, Row, Box y Spacer.
✔ Cómo aplicar modificadores (Modifier).
✔ Cómo manejar estados reactivos (remember, mutableStateOf).
✔ Creación de botones e interacción básica.
✔ Vista previa (@Preview) y renderizado directo en el editor.

────────────────────────────────────────────────────────────
CONCEPTO CENTRAL: UI DECLARATIVA
────────────────────────────────────────────────────────────
En Jetpack Compose **no se dibuja la UI paso a paso** como en XML.
En su lugar, se declara **qué debe mostrarse** según el estado actual.
Cuando el estado cambia, Compose “recompone” automáticamente
solo las partes necesarias, manteniendo la interfaz sincronizada.

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Introducción a Column, Row, Spacer.
2) Uso de Text y Button.
3) Ejemplo práctico de interacción (contador).
4) Ejemplo visual con colores y paddings.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — ESTRUCTURA BÁSICA CON COLUMN Y TEXT
────────────────────────────────────────────────────────────
`Column` es el equivalente moderno a un LinearLayout vertical.
Permite apilar elementos de arriba a abajo.

    NOTA IMPORTANTE:
    -`fillMaxSize()`, ocupa toda la pantalla.
    - `fillMaxWidth()` → ocupa todo el ancho disponible.
    - `wrapContentHeight()` → solo usa la altura que necesite.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionColumnBasica() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center, // Centra el contenido verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido horizontalmente
    ) {
        Text(
            text = "Hola desde Jetpack Compose",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp)) // Espacio vertical
        Text(
            text = "Esto es una interfaz declarativa moderna",
            fontSize = 16.sp
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — INTERACTIVIDAD CON ESTADOS
────────────────────────────────────────────────────────────
En Compose, los valores que cambian en tiempo real se controlan
con “estados” reactivos. Estos se crean con:
    remember { mutableStateOf(valorInicial) }

Cuando el valor cambia, Compose detecta el cambio y **recompone**
automáticamente las partes de la UI que dependen de ese estado.

Esto elimina la necesidad de usar métodos como `findViewById` o
`setText()`; Compose se encarga de actualizar la interfaz.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionContadorInteractivo() {
    // Declaramos un estado: "contador" con valor inicial 0.
    var contador by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contador: $contador",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para incrementar el contador
        Button(onClick = { contador++ }) {
            Text(text = "Incrementar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón para reiniciar el contador
        OutlinedButton(onClick = { contador = 0 }) {
            Text(text = "Reiniciar")
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — USO DE ROW Y BOX
────────────────────────────────────────────────────────────
`Row` organiza elementos de izquierda a derecha.
`Box` es un contenedor simple que puede apilar o superponer
otros elementos (equivalente moderno a FrameLayout en XML).

Usaremos tres cajas (Box) de colores diferentes dentro de una Row.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionRowYBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Ejemplo de Row y Box",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Distribuye las cajas equitativamente
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Green)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Blue)
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — PANTALLA COMPLETA DEL CAPÍTULO 1
────────────────────────────────────────────────────────────
Aquí combinamos todas las secciones anteriores para crear una
pantalla educativa y demostrativa de los conceptos base.

Cada sección está separada por un `HorizontalDivider` para
una mejor lectura visual.

Más adelante, en capítulos siguientes, aprenderemos a convertir
esta columna en una vista desplazable (scrollable).
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo1_Fundamentos() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "CAPÍTULO 1 — FUNDAMENTOS DE COMPOSE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp)

            // Sección 1: Texto básico
            Spacer(modifier = Modifier.height(16.dp))
            SeccionColumnBasica()

            // Sección 2: Interactividad
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionContadorInteractivo()

            // Sección 3: Ejemplo visual con colores
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionRowYBox()
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA COMPLETA DEL CAPÍTULO 1
────────────────────────────────────────────────────────────
Permite ver el resultado sin ejecutar la app en un dispositivo.

@Preview:
- showBackground → muestra un fondo detrás de los composables.
- showSystemUi → muestra barra de estado y navegación.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo1() {
    JetpackComposeTheme {
        Capitulo1_Fundamentos()
    }
}
