package com.example.jc_intents

/*
────────────────────────────────────────────────────────────
MÓDULO 07 — INTENTS Y PERIFÉRICOS (Compose + Kotlin)
────────────────────────────────────────────────────────────

OBJETIVO DE ESTE ARCHIVO (MainActivity)
───────────────────────────────────────
• Ser el **punto de entrada** de la app (Activity lanzada por el Launcher).
• Montar la UI declarativa (Compose) y **aplicar el tema Material 3**.
• Proveer una **estructura base con Scaffold** para todos los capítulos
  de este módulo (barra superior/inferior si luego agregas, FAB, snackbars).
• Permitir **alternar rápidamente** el capítulo visible descomentando
  una única línea (ideal para desarrollo y docencia).

QUÉ ENCONTRARÁS AQUÍ
─────────────────────
1) `enableEdgeToEdge()` → activa un layout de “borde a borde” moderno.
2) `JC_IntentsTheme` → tu tema Material 3 (colores, tipografía, etc).
3) `Scaffold` → contenedor de alto nivel (áreas estándar + padding correcto).
4) Zona de **capítulos** (descomentar uno).
5) `Preview` con el mismo contenido para revisar desde Android Studio.

CAPÍTULOS DEL MÓDULO 07 (a crear en archivos separados):
──────────────────────────────────────────────────────────
• Capitulo1_IntentsBasicos()
• Capitulo2_PermisosYResultados()
• Capitulo3_PerifericosAvanzados()

Cada uno vivirá en su propio `.kt` dentro del mismo paquete y expondrá
una función @Composable con ese nombre para invocarla desde aquí.
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

class MainActivity : ComponentActivity() {

    /*
    onCreate: punto de arranque de la Activity.
    - enableEdgeToEdge(): pide al sistema “dibujar bajo” las barras del sistema para
      aprovechar todo el alto/ancho de pantalla. Ojo: los contenedores como Scaffold
      manejarán correctamente los insets (espacios seguros) al usar su padding.
    - setContent { ... }: a partir de aquí todo es Compose (UI declarativa).
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // UI moderna: contenido hasta los bordes (status/navigation bar)

        setContent {
            // 1) Aplicamos tema Material 3 de la app (colores, tipografías, etc.)
            JC_IntentsTheme {

                // 2) Surface opcional para unificar fondo si luego prescindes de Scaffold.
                //    En este caso usamos Scaffold directamente y la Surface la incluimos dentro.
                Scaffold(
                    /*
                    Scaffold: contenedor de alto nivel con “zonas estándar”:
                    - topBar / bottomBar
                    - floatingActionButton (FAB)
                    - snackbarHost
                    - content (área central)

                    ¿Por qué usarlo?
                    • Gestiona automáticamente el padding (insets) para evitar que el
                      contenido quede detrás de las barras.
                    • Nos da una estructura coherente y escalable para toda la app.
                    */
                    modifier = Modifier.fillMaxSize()
                    // topBar = { /* si más adelante quieres una TopAppBar */ }
                    // bottomBar = { /* si más adelante quieres una BottomBar */ }
                    // floatingActionButton = { /* si más adelante agregas un FAB */ }
                    // snackbarHost = { /* host para snackbars si lo necesitas */ }
                ) { innerPadding ->
                    /*
                    innerPadding: MUY IMPORTANTE.
                    Scaffold aplica “espacios seguros” para que tu contenido NO quede
                    tapado por status bar, navigation bar, barras, etc. Debes propagar
                    este padding al contenido real con Modifier.padding(innerPadding).
                    */

                    // 3) Superficie base: aplica el color de fondo del tema y evita artefactos.
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        // ── MÓDULO 07: INTENTS Y PERIFÉRICOS ─────────
                        // Capitulo1_IntentsBasicos()
                        // Capitulo2_PermisosYResultados()
                        // Capitulo3_PerifericosAvanzados()

                        BienvenidaModulo07()
                    }
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
Composable de respaldo: mensaje temporal de bienvenida
────────────────────────────────────────────────────────────
Útil mientras creas/integres los archivos de capítulo.
Puedes eliminarlo cuando ya invoques un capítulo real.
*/
@Composable
fun BienvenidaModulo07() {
    Text(text = "👋 Módulo 07 — Intents y periféricos (Compose + Kotlin)")
}

/*
────────────────────────────────────────────────────────────
Preview (Android Studio)
────────────────────────────────────────────────────────────
• Renderiza este contenido sin ejecutar la app completa.
• Descomenta aquí el capítulo que quieras previsualizar.

TIP:
Si usas un capítulo que depende de permisos/Intents reales,
la Preview puede no demostrar toda la funcionalidad (normal);
útil para revisar el layout estático y estilos.
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMain_M07() {
    JC_IntentsTheme {
        // Capitulo1_IntentsBasicos()
        // Capitulo2_PermisosYResultados()
        // Capitulo3_PerifericosAvanzados()
        BienvenidaModulo07()
    }
}
