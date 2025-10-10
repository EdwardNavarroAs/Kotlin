package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 7 — ANIMACIONES Y GESTOS EN JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Aprender cómo Jetpack Compose maneja animaciones y gestos táctiles
de forma declarativa y sencilla.

✔ Concepto de animación reactiva (basada en estado).
✔ Uso de animate*AsState (Float, Dp, Color, etc.).
✔ Transiciones con AnimatedVisibility.
✔ Uso de Modifier.pointerInput y detectTapGestures.
✔ Ejemplo completo: animar color, tamaño y movimiento.

────────────────────────────────────────────────────────────
INTRODUCCIÓN
────────────────────────────────────────────────────────────
Compose adopta un modelo **declarativo** para las animaciones:
la animación ocurre automáticamente cuando el estado cambia.

Ejemplo:
    var expandido by remember { mutableStateOf(false) }
    val altura by animateDpAsState(if (expandido) 200.dp else 100.dp)

Cada vez que cambie `expandido`, la altura se animará sola.

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Animaciones básicas con animate*AsState.
2) Animaciones de visibilidad (fade, expand, shrink).
3) Animaciones controladas con Transition.
4) Gestos táctiles con detectTapGestures.
5) Ejemplo combinado (gesto + animación).
────────────────────────────────────────────────────────────
*/

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme
import kotlinx.coroutines.launch

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — ANIMACIÓN BÁSICA CON animate*AsState
────────────────────────────────────────────────────────────
Este enfoque es ideal para animar valores simples (Color, Dp, Float).

→ El valor animado se actualiza automáticamente cuando cambia el estado.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploAnimacionBasica() {
    var expandido by remember { mutableStateOf(false) }

    // Animación de color
    val color by animateColorAsState(
        targetValue = if (expandido) Color(0xFF80DEEA) else Color(0xFFB39DDB),
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
    )

    // Animación de tamaño
    val altura by animateDpAsState(
        targetValue = if (expandido) 200.dp else 100.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(altura)
            .background(color)
            .padding(12.dp)
            .clickable { expandido = !expandido },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (expandido) "¡Expandido!" else "Toca para expandir",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — ANIMATEDVISIBILITY
────────────────────────────────────────────────────────────
Permite mostrar u ocultar elementos con animaciones integradas.

Soporta animaciones de entrada y salida: fadeIn, slideIn, expandVertically, etc.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploAnimatedVisibility() {
    var visible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { visible = !visible }) {
            Text(if (visible) "Ocultar" else "Mostrar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFF81C784)),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Hola!", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — TRANSICIÓN CONTROLADA (updateTransition)
────────────────────────────────────────────────────────────
`updateTransition` permite animar varios valores en paralelo,
basados en un mismo estado.

Ejemplo: animar color + tamaño + opacidad al mismo tiempo.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploTransicionControlada() {
    var activo by remember { mutableStateOf(false) }
    val transicion = updateTransition(targetState = activo, label = "transicion")

    val color by transicion.animateColor(label = "color") { estado ->
        if (estado) Color(0xFFFFB74D) else Color(0xFF64B5F6)
    }

    val tamaño by transicion.animateDp(label = "tamaño") { estado ->
        if (estado) 160.dp else 80.dp
    }

    val opacidad by transicion.animateFloat(label = "opacidad") { estado ->
        if (estado) 1f else 0.6f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(tamaño)
                .graphicsLayer(alpha = opacidad)
                .background(color)
                .clickable { activo = !activo },
            contentAlignment = Alignment.Center
        ) {
            Text("Transición", color = Color.White)
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — GESTOS TÁCTILES CON pointerInput
────────────────────────────────────────────────────────────
Podemos detectar gestos personalizados con detectTapGestures.

Soporta:
✔ onTap
✔ onDoubleTap
✔ onLongPress
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploGestosBasicos() {
    var mensaje by remember { mutableStateOf("Toca, mantén o haz doble tap") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFB39DDB))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { mensaje = "Tap simple 👆" },
                    onDoubleTap = { mensaje = "Doble tap 👏" },
                    onLongPress = { mensaje = "Presión larga 🤚" }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(mensaje, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 5 — EJEMPLO COMPLETO: GESTO + ANIMACIÓN
────────────────────────────────────────────────────────────
Combina interacción táctil y animación.

Cada vez que el usuario toca el círculo, este cambia de color,
tamaño y posición de manera animada.
────────────────────────────────────────────────────────────
*/
@Composable
fun EjemploAnimacionConGesto() {
    var desplazamiento by remember { mutableStateOf(0.dp) }
    var color by remember { mutableStateOf(Color(0xFF4DD0E1)) }
    val alcance = rememberCoroutineScope()

    val animOffset = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFE1F5FE))
            .pointerInput(Unit) {
                detectTapGestures {
                    alcance.launch {
                        animOffset.animateTo(
                            targetValue = if (animOffset.value == 0f) 200f else 0f,
                            animationSpec = tween(700)
                        )
                        color = if (color == Color(0xFF4DD0E1)) Color(0xFFFF8A65) else Color(0xFF4DD0E1)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset(y = Dp(animOffset.value))
                .size(100.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text("Tap!", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 6 — PANTALLA COMPLETA DEL CAPÍTULO 7
────────────────────────────────────────────────────────────
Incluye todos los ejemplos con scroll vertical.
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo7_AnimacionesYGestos() {
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

            // ──────────────────────────────────────────────
            // ENCABEZADO PRINCIPAL DEL CAPÍTULO
            // ──────────────────────────────────────────────
            Text(
                text = "CAPÍTULO 7 — ANIMACIONES Y GESTOS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(thickness = 1.dp)

            // ──────────────────────────────────────────────
            // SECCIÓN 1 — ANIMACIÓN BÁSICA
            // ──────────────────────────────────────────────
            Text(
                text = "ANIMACIÓN BÁSICA CON animate*AsState\n(Haz click sobre el recuadro para expandirlo o contraerlo)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            EjemploAnimacionBasica()

            // ──────────────────────────────────────────────
            // SECCIÓN 2 — ANIMATED VISIBILITY
            // ──────────────────────────────────────────────
            Text(
                text = "ANIMATEDVISIBILITY\n(Pulsa el botón para mostrar u ocultar el elemento con animación)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            EjemploAnimatedVisibility()

            // ──────────────────────────────────────────────
            // SECCIÓN 3 — TRANSICIÓN CONTROLADA
            // ──────────────────────────────────────────────
            Text(
                text = "TRANSICIÓN CONTROLADA CON updateTransition\n(Toca el recuadro para animar color, tamaño y opacidad simultáneamente)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            EjemploTransicionControlada()

            // ──────────────────────────────────────────────
            // SECCIÓN 4 — GESTOS BÁSICOS
            // ──────────────────────────────────────────────
            Text(
                text = "GESTOS BÁSICOS CON detectTapGestures\n(Realiza tap, doble tap o presión larga sobre el área morada)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            EjemploGestosBasicos()

            // ──────────────────────────────────────────────
            // SECCIÓN 5 — GESTO + ANIMACIÓN COMBINADOS
            // ──────────────────────────────────────────────
            Text(
                text = "ANIMACIÓN + GESTO COMBINADOS\n(Toca el círculo para hacerlo moverse y cambiar de color)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            EjemploAnimacionConGesto()
        }
    }
}


/*
────────────────────────────────────────────────────────────
VISTA PREVIA COMPLETA DEL CAPÍTULO 7
────────────────────────────────────────────────────────────
Muestra todas las animaciones y gestos en una sola vista.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo7() {
    JetpackComposeTheme {
        Capitulo7_AnimacionesYGestos()
    }
}
