package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 2 — ESTILOS, COLORES Y TIPOGRAFÍA EN JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Comprender cómo Jetpack Compose implementa el sistema de temas (MaterialTheme)
y cómo aplicar estilos visuales coherentes en toda la aplicación.

✔ Uso de MaterialTheme y su estructura interna:
     - colorScheme → Colores principales del tema.
     - typography → Fuentes, tamaños y pesos.
     - shapes → Formas de botones, tarjetas, etc.

✔ Creación de temas personalizados (Light / Dark).
✔ Aplicación de colores y tipografía a Text, Button y Surface.
✔ Diferencia entre estilo local y estilo global.

────────────────────────────────────────────────────────────
INTRODUCCIÓN: ¿QUÉ ES UN TEMA EN COMPOSE?
────────────────────────────────────────────────────────────
Un "tema" define la identidad visual completa de una app:
    🎨 Colores → primario, secundario, fondo, superficie, texto, etc.
    ✍️ Tipografía → fuentes, tamaños y pesos coherentes.
    🟪 Formas → esquinas redondeadas, elevaciones, bordes.

MaterialTheme centraliza estos estilos, de modo que cada componente
(MaterialButton, Card, Text, etc.) hereda automáticamente el aspecto del tema.

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Introducción práctica al tema global.
2) Uso de colores del tema (MaterialTheme.colorScheme).
3) Tipografía con MaterialTheme.typography.
4) Formas y superficies (MaterialTheme.shapes).
5) Ejemplo completo integrando color, texto y botones.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
SECCIÓN 1 — USO BÁSICO DE MaterialTheme.colorScheme
────────────────────────────────────────────────────────────
MaterialTheme.colorScheme es una paleta de colores que compone automáticamente
los tonos adecuados para fondo, texto, superficies y acentos.

ColorScheme contiene:
  - primary / onPrimary → color principal y su color de texto.
  - secondary / onSecondary → color secundario.
  - background / onBackground → fondo general y texto sobre él.
  - surface / onSurface → colores de tarjetas, botones, etc.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionColores() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Colores del tema actual",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo de superficies coloreadas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Usando primary, secondary y tertiary",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — TIPOGRAFÍA CON MaterialTheme.typography
────────────────────────────────────────────────────────────
El sistema tipográfico define jerarquías visuales coherentes.
MaterialTheme.typography incluye estilos como:
  - headlineLarge, titleMedium, bodySmall, labelLarge, etc.

Puedes usarlos directamente o crear tus propios estilos.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionTipografia() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Ejemplo de Tipografía Material 3",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Título medio — titleMedium",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Cuerpo — bodyLarge",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Etiqueta pequeña — labelSmall",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — FORMAS Y SUPERFICIES (MaterialTheme.shapes)
────────────────────────────────────────────────────────────
`Surface` es un contenedor Material que respeta el color, la forma
y la elevación definidas por el tema.

MaterialTheme.shapes define las esquinas redondeadas y estilos
aplicables a botones, tarjetas, diálogos, etc.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionSuperficiesYFormas() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp // agrega una leve elevación visual
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Surface con esquinas redondeadas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { }) {
                Text("Botón dentro de Surface")
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — PANTALLA COMPLETA DEL CAPÍTULO 2
────────────────────────────────────────────────────────────
Integramos colores, tipografía y superficies para demostrar
cómo los temas afectan automáticamente a todos los componentes.

Más adelante veremos cómo crear temas personalizados
en la carpeta `ui/theme`.
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo2_TemasYEstilos() {
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
                text = "CAPÍTULO 2 — ESTILOS Y TEMAS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))
            SeccionColores()

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionTipografia()

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(thickness = 1.dp)
            SeccionSuperficiesYFormas()
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA COMPLETA DEL CAPÍTULO 2
────────────────────────────────────────────────────────────
Permite previsualizar el tema aplicado a todos los elementos
sin ejecutar la app en un emulador.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo2() {
    JetpackComposeTheme {
        Capitulo2_TemasYEstilos()
    }
}
