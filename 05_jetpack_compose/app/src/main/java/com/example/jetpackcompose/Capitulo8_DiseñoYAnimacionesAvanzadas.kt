package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 9 — DISEÑO AVANZADO Y ANIMACIONES MODERNAS (Material 3)
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Reunir en una sola pantalla conceptos avanzados de UI en Compose:
✔ Personalización con Material 3 (colores/typography/shapes locales)
✔ Animaciones reactivas con animate*AsState (color, tamaño, elevación)
✔ Transiciones controladas con updateTransition
✔ Animaciones infinitas con rememberInfiniteTransition
✔ Animaciones por corutina con Animatable + LaunchedEffect
✔ Ejemplo integrado: “Pantalla de bienvenida” con estilo y movimiento

IDEA BASE
─────────
En Compose, el *diseño* (MaterialTheme) y el *movimiento* (animaciones)
son funciones del estado. Cambiamos un estado → la UI reacciona:
    UI = f(themeState, animationState)

Este archivo muestra *patrones reutilizables* que luego podrás mezclar
en cualquier pantalla real de tu app.
────────────────────────────────────────────────────────────
*/

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ──────────────────────────────────────────────────────────
SECCIÓN 1 — DEMO DE MATERIAL 3 “LOCAL”
───────────────────────────────────────────────────────────
MaterialTheme suele definirse a nivel app, pero también se
puede *anidar* para crear “islas” de estilo. Esto es útil para
prototipar variaciones de color/typography/shapes sin tocar
el tema global.
─────────────────────────────────────────────────────────── */
@Composable
fun SeccionMaterial3TemaDemo() {
    // Estado: alternar entre dos esquemas de color (simulación claro/oscuro)
    var oscuro by remember { mutableStateOf(false) }

    val lightColors = lightColorScheme(
        primary = Color(0xFF6750A4),
        onPrimary = Color.White,
        secondary = Color(0xFF625B71),
        tertiary = Color(0xFF7D5260)
    )
    val darkColors = darkColorScheme(
        primary = Color(0xFFD0BCFF),
        onPrimary = Color(0xFF201A2B),
        secondary = Color(0xFFCCC2DC),
        tertiary = Color(0xFFEFB8C8)
    )

    // Tipografía y shapes personalizados (solo para esta tarjeta)
    val localTypography = Typography(
        titleLarge = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.3.sp)
    )
    val localShapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(28.dp)
    )

    // Anidamos un MaterialTheme local
    MaterialTheme(
        colorScheme = if (oscuro) darkColors else lightColors,
        typography = localTypography,
        shapes = localShapes
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Material 3 (Tema Local)",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Este bloque usa un esquema de color, tipografía y formas distintos al tema global.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { oscuro = !oscuro }) {
                        Text(if (oscuro) "Usar claro" else "Usar oscuro")
                    }
                    OutlinedButton(onClick = { /* Placeholder: podrías abrir un diálogo de tema */ }) {
                        Text("Más estilos…")
                    }
                }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 2 — ANIMACIONES REACTIVAS CON animate*AsState
───────────────────────────────────────────────────────────
Para “detalles” visuales: color, tamaño, elevación, bordes, etc.
Cambia un boolean/int y Compose *interpola* al valor objetivo.
─────────────────────────────────────────────────────────── */
@Composable
fun SeccionAnimacionesReactivas() {
    var activo by remember { mutableStateOf(false) }

    // Colores y dimensiones animados en función del estado
    val fondo by animateColorAsState(
        targetValue = if (activo) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(450, easing = FastOutSlowInEasing)
    )
    val elevacion by animateDpAsState(
        targetValue = if (activo) 18.dp else 2.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val radio by animateDpAsState(
        targetValue = if (activo) 24.dp else 8.dp,
        animationSpec = tween(450)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(elevacion),
        shape = RoundedCornerShape(radio)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Animaciones reactivas (animate*AsState)", fontWeight = FontWeight.Bold)
            Text("Toca el botón para cambiar color, elevación y radios con interpolación suave.")
            Button(onClick = { activo = !activo }) {
                Text(if (activo) "Desactivar efectos" else "Activar efectos")
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 3 — TRANSICIÓN ORQUESTADA con updateTransition
───────────────────────────────────────────────────────────
Cuando hay *varios* valores que deben animarse *juntos* al
cambiar un mismo estado, usamos una Transition.
─────────────────────────────────────────────────────────── */
enum class Estado { Contraido, Expandido }

@Composable
fun SeccionTransicionControlada() {
    var estado by remember { mutableStateOf(Estado.Contraido) }
    val transicion = updateTransition(targetState = estado, label = "panelTransition")

    val alto by transicion.animateDp(label = "alto") {
        if (it == Estado.Expandido) 160.dp else 64.dp
    }
    val alpha by transicion.animateFloat(label = "alpha") {
        if (it == Estado.Expandido) 1f else 0.4f
    }
    val color by transicion.animateColor(label = "color") {
        if (it == Estado.Expandido) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.secondaryContainer
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color,
        tonalElevation = 4.dp,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Transición orquestada (updateTransition)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alto)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Contenido con alpha → %.1f".format(alpha),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = {
                estado = if (estado == Estado.Contraido) Estado.Expandido else Estado.Contraido
            }) {
                Text(if (estado == Estado.Contraido) "Expandir" else "Contraer")
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 4 — ANIMACIONES INFINITAS (rememberInfiniteTransition)
───────────────────────────────────────────────────────────
Para efectos “vivos”: latidos, brillos, loaders que respiran, etc.
─────────────────────────────────────────────────────────── */
@Composable
fun SeccionAnimacionInfinita() {
    val infinite = rememberInfiniteTransition(label = "infinite")

    // Pulso de escala (0.9 ↔ 1.1)
    val escala by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )

    // Barrido de brillo (gradiente que se mueve)
    val desplazamiento by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing)
        ),
        label = "desplazamiento"
    )

    val gradiente = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        ),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset(600f * desplazamiento, 0f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradiente),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((26.dp * escala))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 5 — ANIMACIONES POR CORUTINA (Animatable + LaunchedEffect)
───────────────────────────────────────────────────────────
Cuando necesitas controlar *paso a paso* la animación (encadenar,
esperar, revertir, etc.), usa Animatable y corrutinas.
─────────────────────────────────────────────────────────── */
@Composable
fun SeccionAnimatableCorutina() {
    val scope = rememberCoroutineScope()
    val x = remember { Animatable(0f) } // desplazamiento X
    val alpha = remember { Animatable(1f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Animatable + corutinas (control total)", fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = x.value.dp)
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = alpha.value))
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        // ida
                        x.animateTo(220f, animationSpec = tween(600, easing = FastOutLinearInEasing))
                        alpha.animateTo(0.4f, animationSpec = tween(300))
                        // vuelta
                        x.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                        alpha.animateTo(1f, animationSpec = tween(300))
                    }
                }) { Text("Reproducir secuencia") }

                OutlinedButton(onClick = {
                    scope.launch {
                        x.snapTo(0f); alpha.snapTo(1f)
                    }
                }) { Text("Reset") }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 6 — PANTALLA INTEGRADA: BIENVENIDA DINÁMICA
───────────────────────────────────────────────────────────
Integra diseño y animaciones:
- Encabezado con gradiente
- Título con AnimatedContent alternando frases
- Botones con animación reactiva
─────────────────────────────────────────────────────────── */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PantallaBienvenidaDinamica() {
    // Rotamos mensajes cada 2.2s
    val mensajes = listOf("Diseña rápido", "Anima con fluidez", "Itera sin miedo")
    var indice by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2200)
            indice = (indice + 1) % mensajes.size
        }
    }

    val headerGradiente = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column {
            // Encabezado “hero”
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(headerGradiente),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = mensajes[indice],
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut())
                    },
                    label = "animatedTitle"
                ) { txt ->
                    Text(
                        text = txt,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Cuerpo
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Bienvenido a Compose",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Material 3 + Animaciones = UIs modernas, expresivas y mantenibles.",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Botón con feedback reactivo
                var pressed by remember { mutableStateOf(false) }
                val elev by animateDpAsState(if (pressed) 2.dp else 10.dp)
                val bg by animateColorAsState(
                    if (pressed) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
                Button(
                    onClick = { pressed = !pressed },
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = elev),
                    colors = ButtonDefaults.buttonColors(containerColor = bg)
                ) {
                    Text(if (pressed) "Gracias 🎉" else "¡Empezar ahora!")
                }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────
SECCIÓN 7 — PANTALLA COMPLETA DEL CAPÍTULO
───────────────────────────────────────────────────────────
Usamos Column + verticalScroll para recorrer todas las secciones.
─────────────────────────────────────────────────────────── */
@Composable
fun Capitulo8_DiseñoYAnimacionesAvanzadas() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "CAPÍTULO 8 — DISEÑO AVANZADO Y ANIMACIONES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(thickness = 1.dp)

            SeccionMaterial3TemaDemo()
            SeccionAnimacionesReactivas()
            SeccionTransicionControlada()
            SeccionAnimacionInfinita()
            SeccionAnimatableCorutina()
            PantallaBienvenidaDinamica()
        }
    }
}

/* ──────────────────────────────────────────────────────────
PREVIEW COMPLETA
─────────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo8() {
    com.example.jetpackcompose.ui.theme.JetpackComposeTheme {
        Capitulo8_DiseñoYAnimacionesAvanzadas()
    }
}
