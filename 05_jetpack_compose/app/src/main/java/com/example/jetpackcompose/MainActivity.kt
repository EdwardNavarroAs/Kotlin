package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
JETPACK COMPOSE — INTRODUCCIÓN Y ESTRUCTURA BASE DEL CURSO
────────────────────────────────────────────────────────────
*/

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview  // ← IMPORTANTE: necesario para @Preview
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //EjemploBasico()
                    //Capitulo1_Fundamentos()
                    //Capitulo2_TemasYEstilos()
                    //Capitulo3_Interactividad()
                    //Capitulo4_ListasYLazyColumn()
                    //Capitulo5_NavegacionBasica()
                    Capitulo6_LayoutsAvanzados()
                }
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
FUNCIÓN COMPOSABLE BÁSICA
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploBasico() {
    Text(text = "Hola desde un Composable simple")
}

/*
────────────────────────────────────────────────────────────
FUNCIÓN DE VISTA PREVIA (Preview)
────────────────────────────────────────────────────────────
La anotación @Preview permite renderizar el Composable directamente
en Android Studio, sin ejecutar la app completa.

Puedes tener varias previews para mostrar diferentes configuraciones.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EjemploBasicoPreview() {
    JetpackComposeTheme {
        //EjemploBasico()
        //Capitulo1_Fundamentos()
        //Capitulo2_TemasYEstilos()
        //Capitulo3_Interactividad()
        //Capitulo4_ListasYLazyColumn()
        //Capitulo5_NavegacionBasica()
        Capitulo6_LayoutsAvanzados()
    }
}
