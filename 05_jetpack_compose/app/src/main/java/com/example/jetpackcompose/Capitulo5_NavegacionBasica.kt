package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 5 — NAVEGACIÓN BÁSICA CON JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Aprender a manejar la navegación entre pantallas (screens) usando
**Navigation Compose**, el sistema oficial de Android Jetpack
para control de rutas, argumentos y flujo dentro de la app.

✔ Qué es Navigation Compose y por qué se usa.
✔ Cómo configurar NavHost y NavController.
✔ Cómo definir pantallas (destinos) con rutas únicas.
✔ Navegación con y sin argumentos.
✔ Cómo volver atrás en la navegación (popBackStack).
✔ Ejemplo completo: navegación entre tres pantallas.

────────────────────────────────────────────────────────────
¿QUÉ ES NAVIGATION COMPOSE?
────────────────────────────────────────────────────────────
Es una librería que permite **gestionar el flujo entre pantallas**
de forma declarativa y segura con tipos, eliminando la necesidad
de Intents y Fragments tradicionales.

Cada pantalla se declara como un destino (`composable(route)`),
y la navegación se controla con un `NavController`.

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Configuración básica de NavHost y NavController.
2) Pantalla de inicio (HomeScreen).
3) Pantalla de detalles (DetailScreen).
4) Pantalla de perfil (ProfileScreen).
5) Ejemplo completo con paso de argumentos y navegación inversa.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — CONFIGURACIÓN BÁSICA DE NAVEGACIÓN
────────────────────────────────────────────────────────────
En Compose, toda la navegación se define dentro de un `NavHost`.

- `NavController`: controla la pila de pantallas activas.
- `NavHost`: define los destinos disponibles (rutas).
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo5_NavegacionBasica() {
    // Creamos un controlador de navegación (NavController)
    val navController = rememberNavController()

    // NavHost: define las pantallas (destinos) disponibles
    NavHost(
        navController = navController,
        startDestination = "home" // pantalla inicial
    ) {
        // Pantalla principal
        composable("home") {
            HomeScreen(navController)
        }

        // Pantalla de detalles
        composable("detail/{itemName}") { backStackEntry ->
            // Se recupera el argumento enviado
            val itemName = backStackEntry.arguments?.getString("itemName") ?: "Desconocido"
            DetailScreen(navController, itemName)
        }

        // Pantalla de perfil (sin argumentos)
        composable("profile") {
            ProfileScreen(navController)
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — PANTALLA PRINCIPAL (HomeScreen)
────────────────────────────────────────────────────────────
Esta pantalla ofrece botones para navegar a otras rutas.

La navegación se realiza con:
    navController.navigate("ruta")
────────────────────────────────────────────────────────────
*/
@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pantalla Principal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Navegación a la pantalla de detalles con argumento
            Button(onClick = { navController.navigate("detail/Jetpack Compose") }) {
                Text("Ir a Detalles (con argumento)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navegación a la pantalla de perfil
            Button(onClick = { navController.navigate("profile") }) {
                Text("Ir al Perfil")
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — PANTALLA DE DETALLES (DetailScreen)
────────────────────────────────────────────────────────────
Demuestra cómo recibir un argumento desde la ruta (itemName).
────────────────────────────────────────────────────────────
*/
@Composable
fun DetailScreen(navController: NavHostController, itemName: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Detalle del ítem:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = itemName,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para volver atrás
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver a inicio")
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — PANTALLA DE PERFIL (ProfileScreen)
────────────────────────────────────────────────────────────
Ejemplo de destino adicional sin argumentos. Incluye navegación inversa.
────────────────────────────────────────────────────────────
*/
@Composable
fun ProfileScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pantalla de Perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aquí podrías mostrar información del usuario.",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver a inicio")
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA DEL CAPÍTULO 5
────────────────────────────────────────────────────────────
Se muestra solo la pantalla principal (HomeScreen) porque la
navegación completa requiere ejecución en tiempo real.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo5() {
    JetpackComposeTheme {
        // Solo vista previa de la pantalla inicial
        HomeScreen(rememberNavController())
    }
}
