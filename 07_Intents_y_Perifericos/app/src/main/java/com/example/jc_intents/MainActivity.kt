package com.example.jc_intents

/*
────────────────────────────────────────────────────────────
MÓDULO 07 — INTENTS Y PERIFÉRICOS (Compose + Kotlin)
────────────────────────────────────────────────────────────

Propósito:
Este archivo representa el punto de entrada de la aplicación.
Aquí se configura la interfaz principal con Jetpack Compose,
se aplica el tema Material 3 y se define la estructura base
que compartirán todos los capítulos del módulo 07.

Capítulos (cada uno en su propio archivo):
1) Capitulo1_IntentsBasicos()
2) Capitulo2_PermisosYResultados()
3) Capitulo3_PerifericosAvanzados()
────────────────────────────────────────────────────────────
*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jc_intents.ui.theme.JC_IntentsTheme

/*
────────────────────────────────────────────────────────────
Selector de capítulo
────────────────────────────────────────────────────────────
Usamos un enum para decidir qué contenido se muestra.
Esto evita tener que comentar o descomentar varias líneas.
────────────────────────────────────────────────────────────
*/
enum class CapituloActivo {
    CAP1_INTENTS_BASICOS,
    CAP2_PERMISOS_Y_RESULTADOS,
    CAP3_PERIFERICOS_AVANZADOS,
    BIENVENIDA
}

// Cambia este valor para seleccionar qué capítulo se mostrará.
private val CAPITULO_ACTUAL = CapituloActivo.CAP3_PERIFERICOS_AVANZADOS

class MainActivity : ComponentActivity() {

    /*
    onCreate(): punto de entrada de la Activity.
    - enableEdgeToEdge(): aprovecha toda la pantalla (borde a borde).
    - setContent { }: inicializa la interfaz declarativa con Compose.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JC_IntentsTheme { // Aplica el tema Material 3

                /*
                Scaffold: contenedor principal que organiza la interfaz.
                Maneja automáticamente el espacio para barras del sistema
                y ofrece áreas estándar (contenido, barra superior, etc.).
                */
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    /*
                    Surface: superficie base que aplica el color de fondo
                    del tema y respeta los márgenes de seguridad del Scaffold.
                    */
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Aquí se carga el contenido correspondiente al capítulo activo.
                        ContenidoCapitulo(capitulo = CAPITULO_ACTUAL)
                    }
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
ContenidoCapitulo()
────────────────────────────────────────────────────────────
Decide qué Composable se mostrará según el capítulo activo.
Por ahora, usa una pantalla de bienvenida mientras se crean
los archivos reales de cada capítulo.
────────────────────────────────────────────────────────────
*/
@Composable
fun ContenidoCapitulo(capitulo: CapituloActivo) {
    when (capitulo) {
        CapituloActivo.CAP1_INTENTS_BASICOS -> {
            Capitulo1_IntentsBasicos()
        }
        CapituloActivo.CAP2_PERMISOS_Y_RESULTADOS -> {
            Capitulo2_PermisosYResultados()
        }
        CapituloActivo.CAP3_PERIFERICOS_AVANZADOS -> {
            Capitulo3_PerifericosAvanzados()

        }
        CapituloActivo.BIENVENIDA -> {
            BienvenidaModulo07()
        }
    }
}

/*
Pantalla temporal de bienvenida.
Se muestra mientras los capítulos reales están en desarrollo.
*/
@Composable
fun BienvenidaModulo07() {
    Text(text = "Módulo 07 — Intents y periféricos (Compose + Kotlin)")
}

/*
Preview (solo para Android Studio)
Permite revisar el diseño del capítulo actual sin ejecutar la app.
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMain_M07() {
    JC_IntentsTheme {
        ContenidoCapitulo(capitulo = CAPITULO_ACTUAL)
    }
}
