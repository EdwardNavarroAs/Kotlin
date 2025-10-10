package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 6 — LAYOUTS AVANZADOS EN JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Comprender los principales contenedores de disposición (layouts)
en Jetpack Compose y cómo usarlos para construir interfaces
flexibles, alineadas y eficientes.

✔ Column y Row: organización vertical y horizontal.
✔ Box: superposición de elementos.
✔ Uso de `weight` para distribución proporcional del espacio.
✔ Alineación global e individual (Alignment vs. align()).
✔ Introducción a ConstraintLayout.
✔ Ejemplo completo comparativo.

────────────────────────────────────────────────────────────
CONCEPTO CLAVE
────────────────────────────────────────────────────────────
Los *layouts* en Compose son **contenedores que organizan elementos**.
Su posición y tamaño se controlan con:

- `Arrangement`: cómo se distribuyen los elementos (espaciado, orden).
- `Alignment`: cómo se alinean dentro del eje disponible.
- `Modifier.align()`: alineación específica de un solo hijo.
- `weight`: proporción de espacio asignada dentro de Row o Column.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — COLUMN
────────────────────────────────────────────────────────────
`Column` organiza sus hijos de manera vertical.

Propiedades más importantes:
- verticalArrangement → distribución vertical
- horizontalAlignment → alineación horizontal de los hijos
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploColumn() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Column — Disposición vertical", fontWeight = FontWeight.Bold)

        Box(modifier = Modifier.size(60.dp).background(Color.Red))
        Box(modifier = Modifier.size(60.dp).background(Color.Green))
        Box(modifier = Modifier.size(60.dp).background(Color.Blue))
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — ROW Y USO DE WEIGHT
────────────────────────────────────────────────────────────
`Row` coloca los elementos en línea horizontal.

`Modifier.weight()` permite asignar **proporciones relativas**
de espacio entre los hijos (igual que el atributo `layout_weight`
en XML).

Ejemplo: dos cajas con pesos 1f y 2f → la segunda ocupa el doble
de ancho que la primera.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploRowConWeight() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0))
            .padding(16.dp)
    ) {
        Text("Row — Uso de weight", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFFFE0B2)),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f) // ocupa 1 parte proporcional
                    .fillMaxHeight()
                    .background(Color.Red)
            )
            Box(
                modifier = Modifier
                    .weight(2f) // ocupa 2 partes proporcionales
                    .fillMaxHeight()
                    .background(Color.Blue)
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — BOX Y ALINEACIÓN INDIVIDUAL
────────────────────────────────────────────────────────────
`Box` permite superponer elementos (como un FrameLayout).

Diferencias importantes:
- `contentAlignment` → alinea todos los hijos por defecto.
- `Modifier.align()` → alinea un hijo de forma individual.

Ejemplo: varios elementos en diferentes esquinas.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploBoxConAlineacion() {
    Box(
        modifier = Modifier
            .size(250.dp)
            .background(Color(0xFFD1C4E9))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Fondo base
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF9575CD))
        )

        // Caja alineada manualmente
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFBA68C8))
                .align(Alignment.TopStart)
        )

        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFF8E24AA))
                .align(Alignment.BottomEnd)
        )

        Text(
            "Box — Alineaciones",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EjemploConstraintLayout() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFC8E6C9))
            .padding(16.dp)
    ) {

        /*
        ──────────────────────────────────────────────
        DEFINICIÓN DE REFERENCIAS
        ──────────────────────────────────────────────
        Cada elemento dentro del ConstraintLayout necesita
        una referencia para poder aplicar restricciones.

        createRefs() devuelve un grupo de identificadores
        que luego se usan en constrainAs().
        */
        val (titulo, rojo, verde, azul) = createRefs()

        /*
        ──────────────────────────────────────────────
        ELEMENTO 1 — TÍTULO
        ──────────────────────────────────────────────
        Este texto se posiciona en la parte superior y
        centrado horizontalmente dentro del padre.
        */
        Text(
            "ConstraintLayout — Posiciones relativas",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(titulo) {
                // top.linkTo() une el borde superior del texto
                // con el borde superior del padre.
                top.linkTo(parent.top)
                // start.linkTo() y end.linkTo() centran horizontalmente.
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        /*
        ──────────────────────────────────────────────
        ELEMENTO 2 — CAJA ROJA
        ──────────────────────────────────────────────
        Se ubica justo debajo del título y alineada al
        borde izquierdo del padre.
        */
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Red)
                .constrainAs(rojo) {
                    // top del rojo conectado al bottom del título
                    top.linkTo(titulo.bottom, margin = 24.dp)
                    // alineado a la izquierda del padre
                    start.linkTo(parent.start)
                }
        )

        /*
        ──────────────────────────────────────────────
        ELEMENTO 3 — CAJA VERDE
        ──────────────────────────────────────────────
        Está alineada a la derecha de la caja roja,
        compartiendo su altura (top.linkTo(rojo.top)).
        */
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Green)
                .constrainAs(verde) {
                    // Mismo nivel vertical que la roja
                    top.linkTo(rojo.top)
                    // Ubicada a la derecha de la roja
                    start.linkTo(rojo.end, margin = 24.dp)
                }
        )

        /*
        ──────────────────────────────────────────────
        ELEMENTO 4 — CAJA AZUL
        ──────────────────────────────────────────────
        Se posiciona debajo de la caja roja, y centrada
        horizontalmente en el contenedor.
        */
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Blue)
                .constrainAs(azul) {
                    // Debajo de la caja roja
                    top.linkTo(rojo.bottom, margin = 24.dp)
                    // Centrada horizontalmente
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}


/*
────────────────────────────────────────────────────────────
SECCIÓN 5 — COMPARATIVA COMPLETA CON SCROLL
────────────────────────────────────────────────────────────
Integramos todos los ejemplos en una sola pantalla con scroll
para comparar los distintos comportamientos.
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo6_LayoutsAvanzados() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = "CAPÍTULO 6 — LAYOUTS AVANZADOS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(thickness = 1.dp)

            EjemploColumn()
            EjemploRowConWeight()
            EjemploBoxConAlineacion()
            EjemploConstraintLayout()
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA DEL CAPÍTULO 6
────────────────────────────────────────────────────────────
Muestra todos los ejemplos en una sola vista con desplazamiento.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo6() {
    JetpackComposeTheme {
        Capitulo6_LayoutsAvanzados()
    }
}
