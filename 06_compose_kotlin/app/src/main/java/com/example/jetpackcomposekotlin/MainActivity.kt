package com.example.jetpackcomposekotlin

/*
────────────────────────────────────────────────────────────
JETPACK COMPOSE + KOTLIN — ESTRUCTURA BASE DEL PROYECTO
────────────────────────────────────────────────────────────

OBJETIVO DEL ARCHIVO
──────────────────────
Este archivo funciona como **punto de entrada principal** del curso
y como contenedor general donde se integrarán los diferentes capítulos.

Cada capítulo se implementará en un archivo independiente dentro del
paquete del proyecto, y desde aquí podrás invocar su función principal
para visualizarlo en ejecución o en la vista previa (Preview).

────────────────────────────────────────────────────────────
ESTRUCTURA GENERAL
────────────────────────────────────────────────────────────
1) Configuración principal de la actividad.
2) Carga del tema y superficie base.
3) Invocación del capítulo o ejemplo deseado.
4) Funciones de vista previa.
────────────────────────────────────────────────────────────
*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcomposekotlin.ui.theme.JetpackComposeKotlinTheme

/*
────────────────────────────────────────────────────────────
CLASE PRINCIPAL — MainActivity
────────────────────────────────────────────────────────────
Es el punto de entrada de la aplicación.

Aquí se configura el tema, la superficie principal, y se llama a la
función Composable correspondiente al capítulo que se desea mostrar.
────────────────────────────────────────────────────────────
*/
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*
                    ────────────────────────────────────────────────
                    🔹 SECCIÓN DE CAPÍTULOS
                    ────────────────────────────────────────────────
                    Descomenta la función correspondiente para
                    ejecutar el capítulo que deseas visualizar.
                    ────────────────────────────────────────────────
                    */
                    Capitulo1_InteraccionBasica()
                    //Capitulo2_ListasYEstados()
                    //Capitulo3_NavegacionYAnimaciones()
                    //Capitulo4_MiniAppCompleta()
                    //CapituloExtra_BuenasPracticas()

                    BienvenidaCurso()
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
FUNCIÓN COMPOSABLE — Ejemplo base o mensaje de bienvenida
────────────────────────────────────────────────────────────
Esta función es temporal y solo sirve para comprobar que la estructura
del proyecto y los temas funcionan correctamente.
────────────────────────────────────────────────────────────
*/
@Composable
fun BienvenidaCurso() {
    androidx.compose.material3.Text(
        text = "👋 Bienvenido al curso avanzado de Jetpack Compose + Kotlin"
    )
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA (Preview)
────────────────────────────────────────────────────────────
Permite visualizar cualquier capítulo o Composable desde
Android Studio sin necesidad de ejecutar la app completa.

Puedes comentar/descomentar la función que desees previsualizar.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainPreview() {
    JetpackComposeKotlinTheme {
        Capitulo1_InteraccionBasica()
        //Capitulo2_ListasYEstados()
        //Capitulo3_NavegacionYAnimaciones()
        //Capitulo4_MiniAppCompleta()
        //CapituloExtra_BuenasPracticas()

        BienvenidaCurso()
    }
}
