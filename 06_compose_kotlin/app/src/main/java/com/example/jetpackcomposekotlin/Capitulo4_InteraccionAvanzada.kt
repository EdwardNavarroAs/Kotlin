package com.example.jetpackcomposekotlin

/*
════════════════════════════════════════════════════════════════════════════════
📚 CAPÍTULO 3 — INTERACCIÓN AVANZADA EN COMPOSE + KOTLIN
════════════════════════════════════════════════════════════════════════════════

🎯 VERSIÓN MEJORADA: ANIMACIONES Y EFECTOS VISUALES REALES
─────────────────────────────────────────────────────────────

ESTE CÓDIGO ES UN CUADERNO DIGITAL EDUCATIVO
Cada sección está documentada para que entiendas:
• QUÉ hace el código
• POR QUÉ se hace así
• CÓMO funciona internamente

🎨 CARACTERÍSTICAS AVANZADAS IMPLEMENTADAS:
─────────────────────────────────────────────
✅ Top Bar con parallax y blur effect al colapsar
✅ Búsqueda con debounce + indicador de búsqueda activa
✅ Filtros animados con transición de color y escala
✅ Vista lista ↔ grid con Crossfade suave y shuffle animado
✅ Cards con:
   • Animación de entrada (slide + fade)
   • Hover effect (elevación animada)
   • Favorito con animación de "corazón latiendo"
   • Swipe-to-delete con preview visual
   • Shimmer effect en imágenes mientras cargan
✅ Contador de carrito con animación de "salto" de números
✅ FAB con rotación animada al agregar items
✅ Snackbar con acción DESHACER y animación personalizada
✅ Bottom Bar con indicadores visuales animados
✅ Pull-to-refresh simulado
✅ Transiciones de estado (loading, success, empty)

📖 CONCEPTOS CLAVE EXPLICADOS:
──────────────────────────────
• animate*AsState: Para valores simples (Float, Color, Dp)
• AnimatedVisibility: Para aparecer/desaparecer elementos
• updateTransition: Para animar múltiples propiedades sincronizadas
• derivedStateOf: Para cálculos que dependen de otros estados
• LaunchedEffect: Para efectos secundarios en composición
• remember: Para mantener valores entre recomposiciones
• Modifier.graphicsLayer: Para transformaciones eficientes (scale, rotation, alpha)
• Flow + debounce: Para optimizar búsquedas en tiempo real

🗺️ MAPA DEL ARCHIVO:
────────────────────
1) 📦 Modelos y datos de ejemplo
2) 🎨 Sistema de animaciones personalizadas
3) 🧠 Estado global del catálogo (ViewModel simulado)
4) 🖼️ Pantalla principal con Scaffold
5) 🔝 Top Bar con parallax y animaciones
6) 🔍 Búsqueda y filtros con feedback visual
7) 🎭 Vista lista/grid con Crossfade
8) 🃏 Cards de productos con todas las animaciones
9) 🎪 Componentes auxiliares (chips, badges, shimmer)
10) ⬇️ Bottom Bar animada
11) 👁️ Preview y notas educativas

════════════════════════════════════════════════════════════════════════════════
*/

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposekotlin.ui.theme.JetpackComposeKotlinTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/* ════════════════════════════════════════════════════════════════════════════
📦 1) MODELOS Y DATOS DE EJEMPLO
════════════════════════════════════════════════════════════════════════════ */

/**
 * 🏷️ Categorías de productos
 * Enum para type-safety y facilitar filtros
 */
enum class Categorys {
    All, Audio, Perifericos, Almacenamiento, Iluminacion
}

/**
 * 📦 Modelo de producto
 * @param id Identificador único (para keys en LazyColumn)
 * @param name Nombre del producto
 * @param price Precio en USD
 * @param imageUrl URL de imagen (usamos Picsum Photos)
 * @param category Categoría del producto
 * @param isFavorite Estado de favorito (mutable)
 */
data class Productz(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: Categorys,
    val isFavorite: Boolean = false
)

/**
 * 🖼️ Generador de URLs de imágenes
 * Usamos Picsum Photos con seed para URLs consistentes
 */
private fun imageUrlFor(id: Int): String =
    "https://picsum.photos/seed/$id/800/600"

/**
 * 🌱 Datos iniciales del catálogo
 * Lista predefinida para empezar con contenido
 */
private fun seedProducts(): List<Productz> = listOf(
    Productz(1, "Auriculares Pro Max", 59.99, imageUrlFor(1), Categorys.Audio, isFavorite = true),
    Productz(2, "Mouse Inalámbrico RGB", 24.50, imageUrlFor(2), Categorys.Perifericos),
    Productz(3, "Teclado Mecánico", 99.00, imageUrlFor(3), Categorys.Perifericos),
    Productz(4, "SSD NVMe 1TB", 109.99, imageUrlFor(4), Categorys.Almacenamiento),
    Productz(5, "Lámpara RGB Desk", 34.25, imageUrlFor(5), Categorys.Iluminacion),
    Productz(6, "Soundbar Compacta", 79.90, imageUrlFor(6), Categorys.Audio),
    Productz(7, "Webcam 4K", 129.99, imageUrlFor(7), Categorys.Perifericos),
    Productz(8, "Hub USB-C 7 puertos", 45.00, imageUrlFor(8), Categorys.Perifericos),
)

/* ════════════════════════════════════════════════════════════════════════════
🎨 2) SISTEMA DE ANIMACIONES PERSONALIZADAS
════════════════════════════════════════════════════════════════════════════

Aquí definimos specs de animación reutilizables.
Esto mantiene consistencia visual en toda la app.
*/

/**
 * ⚡ Specs de animación predefinidas
 */
object AnimationSpecs {
    /** Animación rápida para microinteracciones */
    val fast = tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)

    /** Animación normal para transiciones estándar */
    val normal = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)

    /** Animación con rebote para efectos juguetones */
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Animación para colores */
    val colorSpec = tween<Color>(durationMillis = 300)
}

/**
 * 💓 Animación de "corazón latiendo" para favoritos
 * Usa infiniteTransition para crear un loop continuo
 */
@Composable
fun rememberHeartBeatAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )
    return scale
}

/**
 * ✨ Efecto shimmer para skeleton loading
 * Crea un gradiente animado que se mueve de izquierda a derecha
 */
@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = androidx.compose.ui.geometry.Offset(translateAnim, 0f),
        end = androidx.compose.ui.geometry.Offset(translateAnim + 200f, 0f)
    )
}

/* ════════════════════════════════════════════════════════════════════════════
🧠 3) ESTADO GLOBAL DEL CATÁLOGO
════════════════════════════════════════════════════════════════════════════

En una app real, esto sería un ViewModel.
Aquí lo hacemos inline para propósitos educativos.

CONCEPTO: Single Source of Truth (SSOT)
Todo el estado vive aquí y fluye hacia abajo (unidirectional data flow)
*/

@OptIn(FlowPreview::class)
@Composable
private fun rememberCatalogStates(): CatalogStates {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 📋 Lista de productos (fuente de la verdad)
    val items = remember {
        mutableStateListOf<Productz>().apply { addAll(seedProducts()) }
    }

    // 🔍 Estado de búsqueda
    var searchText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var isSearching by remember { mutableStateOf(false) }

    // 🌊 Flow para búsqueda con debounce
    val searchFlow = remember { MutableStateFlow("") }

    // 🎯 Filtros y vistas
    var selectedCategory by rememberSaveable { mutableStateOf(Categorys.All) }
    var sortAscending by rememberSaveable { mutableStateOf(true) }
    var gridMode by rememberSaveable { mutableStateOf(false) }

    // 🛒 Carrito
    var cartCount by rememberSaveable { mutableStateOf(0) }

    // ⏱️ Emisor de búsqueda con debounce
    LaunchedEffect(searchText) {
        isSearching = true
        searchFlow.emit(searchText.text)
        // Simulamos que la búsqueda tarda un poco
        delay(300)
        isSearching = false
    }

    // 🔄 Query con debounce procesado
    val debouncedQuery by remember {
        searchFlow
            .debounce(300)
            .map { it.trim().lowercase() }
            .distinctUntilChanged()
    }.collectAsState(initial = "")

    // 📊 Lista filtrada y ordenada (derivedStateOf evita recalcular en cada recomposición)
    val filteredSorted by remember(items, debouncedQuery, selectedCategory, sortAscending) {
        derivedStateOf {
            items.asSequence()
                .filter { p ->
                    (selectedCategory == Categorys.All || p.category == selectedCategory) &&
                            (debouncedQuery.isBlank() || p.name.lowercase().contains(debouncedQuery))
                }
                .sortedBy { if (sortAscending) it.price else -it.price }
                .toList()
        }
    }

    // 🎬 ACCIONES (Events que modifican el estado)

    /** ➕ Agregar producto aleatorio */
    fun addRandom() {
        val nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
        val names = listOf("Micrófono USB", "Hub USB-C", "SSD 2TB", "Tira LED RGB", "Gamepad Pro")
        val cats = listOf(Categorys.Audio, Categorys.Perifericos, Categorys.Almacenamiento, Categorys.Iluminacion)
        val n = names.random()
        val c = cats.random()
        val price = (Random.nextInt(15, 250) + Random.nextDouble())
            .let { (it * 100.0).roundToInt() / 100.0 }

        items.add(0, Productz(nextId, "$n ${Random.nextInt(100)}", price, imageUrlFor(nextId), c))

        scope.launch {
            snackbarHostState.showSnackbar("✨ Producto agregado: $n")
        }
    }

    /** 💗 Toggle favorito */
    fun toggleFavorite(id: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            val product = items[index]
            items[index] = product.copy(isFavorite = !product.isFavorite)
        }
    }

    /** 🗑️ Eliminar con DESHACER */
    fun delete(id: Int) {
        val index = items.indexOfFirst { it.id == id }
        if (index >= 0) {
            val removed = items.removeAt(index)
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "🗑️ Eliminado: ${removed.name}",
                    actionLabel = "DESHACER",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
                // Si el usuario presiona DESHACER, restauramos el item
                if (result == SnackbarResult.ActionPerformed) {
                    items.add(index.coerceIn(0, items.size), removed)
                }
            }
        }
    }

    /** 🛒 Agregar al carrito */
    fun addToCart() {
        cartCount += 1
        scope.launch {
            snackbarHostState.showSnackbar("🛒 Agregado al carrito ($cartCount)")
        }
    }

    return remember {
        CatalogStates(
            snackbarHostState = snackbarHostState,
            items = items,
            searchText = { searchText },
            setSearchText = { searchText = it },
            isSearching = { isSearching },
            debouncedQuery = { debouncedQuery },
            selectedCategory = { selectedCategory },
            setCategory = { selectedCategory = it },
            sortAscending = { sortAscending },
            toggleSort = { sortAscending = !sortAscending },
            gridMode = { gridMode },
            toggleGrid = { gridMode = !gridMode },
            filteredSorted = { filteredSorted },
            addRandom = ::addRandom,
            toggleFavorite = ::toggleFavorite,
            delete = ::delete,
            addToCart = ::addToCart,
            cartCount = { cartCount }
        )
    }
}

/**
 * 📦 Data class que encapsula todo el estado
 * Esto facilita pasar el estado a los composables hijos
 */
internal data class CatalogStates(
    val snackbarHostState: SnackbarHostState,
    val items: MutableList<Productz>,
    val searchText: () -> TextFieldValue,
    val setSearchText: (TextFieldValue) -> Unit,
    val isSearching: () -> Boolean,
    val debouncedQuery: () -> String,
    val selectedCategory: () -> Categorys,
    val setCategory: (Categorys) -> Unit,
    val sortAscending: () -> Boolean,
    val toggleSort: () -> Unit,
    val gridMode: () -> Boolean,
    val toggleGrid: () -> Unit,
    val filteredSorted: () -> List<Productz>,
    val addRandom: () -> Unit,
    val toggleFavorite: (Int) -> Unit,
    val delete: (Int) -> Unit,
    val addToCart: () -> Unit,
    val cartCount: () -> Int
)

/* ════════════════════════════════════════════════════════════════════════════
🖼️ 4) PANTALLA PRINCIPAL CON SCAFFOLD
════════════════════════════════════════════════════════════════════════════

CONCEPTO: Scaffold es el "esqueleto" de Material Design
Proporciona slots para: topBar, bottomBar, fab, snackbar, content
*/

@Composable
fun Capitulo4_InteraccionAvanzada() {
    val context = LocalContext.current
    val state = rememberCatalogStates()
    val listState = rememberLazyListState()

    // 📏 Cálculo del scroll para parallax effect
    val scrollOffset = remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset.toFloat()
        }
    }

    // 🎭 Estado colapsado de la top bar
    val collapsed by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > 50
        }
    }

    // 🎨 Animaciones de la top bar
    val barHeight by animateDpAsState(
        targetValue = if (collapsed) 56.dp else 120.dp,
        animationSpec = tween(300),
        label = "barHeight"
    )

    val barElevation by animateDpAsState(
        targetValue = if (collapsed) 4.dp else 0.dp,
        animationSpec = tween(300),
        label = "barElevation"
    )

    // 🎪 Animación del FAB (rotación al agregar)
    var fabRotation by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopBarAvanzada(
                title = "🛍️ Catálogo Pro",
                height = barHeight,
                elevation = barElevation,
                scrollOffset = scrollOffset.value,
                cartCount = state.cartCount(),
                onCartClick = {
                    Toast.makeText(
                        context,
                        "🛒 Carrito: ${state.cartCount()} items",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        },
        snackbarHost = {
            SnackbarHost(state.snackbarHostState) { data ->
                // Snackbar personalizado con animación
                AnimatedSnackbar(data)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    state.addRandom()
                    fabRotation += 360f // Rota 360° al agregar
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.graphicsLayer(rotationZ = fabRotation)
                )
            }

            // Animación del FAB
            LaunchedEffect(fabRotation) {
                if (fabRotation > 0) {
                    animate(
                        initialValue = fabRotation - 360f,
                        targetValue = fabRotation,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) { value, _ ->
                        fabRotation = value
                    }
                }
            }
        },
        bottomBar = {
            BottomBarAvanzada(
                gridMode = state.gridMode(),
                sortAscending = state.sortAscending(),
                onToggleGrid = state.toggleGrid,
                onToggleSort = state.toggleSort
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 📖 Teoría
            item {
                SeccionTeoricaAvanzada()
            }

            // 🔍 Búsqueda + Filtros
            item {
                SearchAndFilters(
                    query = state.searchText(),
                    onQueryChange = state.setSearchText,
                    isSearching = state.isSearching(),
                    selected = state.selectedCategory(),
                    onSelect = state.setCategory
                )
            }

            // 📊 Productos
            item {
                ProductosCrossfadeSection(
                    gridMode = state.gridMode(),
                    products = state.filteredSorted(),
                    onToggleFavorite = state.toggleFavorite,
                    onDelete = state.delete,
                    onAddToCart = state.addToCart
                )
            }

            // 📝 Notas finales
            item { SeccionNotasFinales() }
        }
    }
}

/* ════════════════════════════════════════════════════════════════════════════
🔝 5) TOP BAR CON PARALLAX Y ANIMACIONES
════════════════════════════════════════════════════════════════════════════

CONCEPTOS:
• graphicsLayer: Transformaciones eficientes (no recompone)
• Parallax: El fondo se mueve más lento que el contenido
• Alpha fade: Desvanecimiento progresivo
*/

@Composable
private fun TopBarAvanzada(
    title: String,
    height: Dp,
    elevation: Dp,
    scrollOffset: Float,
    cartCount: Int,
    onCartClick: () -> Unit
) {
    // 🎨 Efecto parallax: el alpha disminuye con el scroll
    val alpha = (1f - (scrollOffset / 200f)).coerceIn(0f, 1f)

    Surface(
        tonalElevation = elevation,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            // 🌅 Fondo con gradiente que se desvanece
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f * alpha),
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            )

            // 📝 Contenido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.graphicsLayer(alpha = alpha)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Explora y aprende",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // 🛒 Badge del carrito con animación
                AnimatedCartBadge(
                    count = cartCount,
                    onClick = onCartClick
                )
            }
        }
    }
}

/**
 * 🛒 Badge de carrito animado
 * Usa updateTransition para animar múltiples propiedades
 */
@Composable
private fun AnimatedCartBadge(count: Int, onClick: () -> Unit) {
    // 🎭 Transición cuando cambia el count
    val transition = updateTransition(targetState = count, label = "cartTransition")

    // 📏 Escala con rebote (resetea a 1f después de cada cambio)
    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "cartScale",
        targetValueByState = { targetCount ->
            // Cuando el count aumenta, hace un "salto"
            if (targetCount > 0) 1.15f else 1f
        }
    )

    // 🎨 Efecto de "pulso" cuando se agrega
    var shouldPulse by remember { mutableStateOf(false) }
    val pulseScale by animateFloatAsState(
        targetValue = if (shouldPulse) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pulseScale",
        finishedListener = { shouldPulse = false }
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp,
        modifier = Modifier
            .scale(scale * pulseScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Carrito",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // 🔢 Número animado con transición
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    // Slide hacia arriba + fade
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut()
                    )
                },
                label = "countAnimation"
            ) { targetCount ->
                Text(
                    text = targetCount.toString(),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // ⚡ Trigger animación de pulso cuando cambia el count
    LaunchedEffect(count) {
        if (count > 0) {
            shouldPulse = true
        }
    }
}

/* ════════════════════════════════════════════════════════════════════════════
🔍 6) BÚSQUEDA Y FILTROS CON FEEDBACK VISUAL
════════════════════════════════════════════════════════════════════════════ */

@Composable
private fun SearchAndFilters(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    isSearching: Boolean,
    selected: Categorys,
    onSelect: (Categorys) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 🔍 Barra de búsqueda con indicador
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar productos…") },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null)
            },
            trailingIcon = {
                // ⏳ Indicador de búsqueda activa
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else if (query.text.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange(TextFieldValue("")) }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 🏷️ Chips de categorías animados
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Categorys.values().forEach { cat ->
                AnimatedChip(
                    label = cat.name,
                    selected = selected == cat,
                    onClick = { onSelect(cat) }
                )
            }
        }
    }
}

/**
 * 🏷️ Chip animado personalizado
 * Anima color, elevación y escala al seleccionar
 */
@Composable
private fun AnimatedChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // 🎨 Colores animados
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = AnimationSpecs.colorSpec,
        label = "chipBg"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = AnimationSpecs.colorSpec,
        label = "chipContent"
    )

    // 📏 Escala animada
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = AnimationSpecs.bouncy,
        label = "chipScale"
    )

    Surface(
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        tonalElevation = if (selected) 3.dp else 0.dp,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 🔵 Indicador circular animado
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else contentColor.copy(alpha = 0.5f)
                    )
            )

            Text(
                text = label,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

/* ════════════════════════════════════════════════════════════════════════════
🎭 7) VISTA LISTA/GRID CON CROSSFADE Y ANIMACIONES
════════════════════════════════════════════════════════════════════════════

CONCEPTO: Crossfade hace una transición suave entre dos layouts
Evita saltos bruscos al cambiar de vista
*/

@Composable
private fun ProductosCrossfadeSection(
    gridMode: Boolean,
    products: List<Productz>,
    onToggleFavorite: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // 📊 Header con estadísticas
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Productos encontrados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${products.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 🔄 Icono de vista actual
                Icon(
                    imageVector = if (gridMode) Icons.Filled.Apps else Icons.Filled.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // 🎬 Crossfade entre lista y grid
            Crossfade(
                targetState = gridMode,
                animationSpec = tween(400),
                label = "viewModeCrossfade"
            ) { isGrid ->
                if (products.isEmpty()) {
                    // 🚫 Estado vacío
                    EmptyState()
                } else if (!isGrid) {
                    // 📜 Vista de lista
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        products.forEachIndexed { index, product ->
                            // ✨ Cada card aparece con animación
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(
                                    initialOffsetX = { 300 },
                                    animationSpec = tween(300, delayMillis = index * 50)
                                ) + fadeIn(animationSpec = tween(300, delayMillis = index * 50))
                            ) {
                                ProductItemCard(
                                    product = product,
                                    onToggleFavorite = { onToggleFavorite(product.id) },
                                    onDelete = { onDelete(product.id) },
                                    onAddToCart = onAddToCart,
                                    compact = true
                                )
                            }
                        }
                    }
                } else {
                    // 🎨 Vista de cuadrícula
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(160.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 800.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(products.size, key = { products[it].id }) { index ->
                            val product = products[index]

                            // ✨ Animación de entrada en grid
                            AnimatedVisibility(
                                visible = true,
                                enter = scaleIn(
                                    animationSpec = tween(300, delayMillis = index * 30)
                                ) + fadeIn(animationSpec = tween(300, delayMillis = index * 30))
                            ) {
                                ProductItemCard(
                                    product = product,
                                    onToggleFavorite = { onToggleFavorite(product.id) },
                                    onDelete = { onDelete(product.id) },
                                    onAddToCart = onAddToCart,
                                    compact = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🚫 Estado vacío con ilustración
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Filled.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            "No se encontraron productos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Intenta con otros filtros o búsqueda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/* ════════════════════════════════════════════════════════════════════════════
🃏 8) CARDS DE PRODUCTOS CON TODAS LAS ANIMACIONES
════════════════════════════════════════════════════════════════════════════

ANIMACIONES IMPLEMENTADAS:
• Shimmer effect mientras carga la imagen
• Favorito con corazón latiendo
• Hover effect (elevación al tocar)
• Scale up al hacer clic en botones
• AnimateContentSize para cambios fluidos
*/

@Composable
private fun ProductItemCard(
    product: Productz,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit,
    compact: Boolean
) {
    // 💓 Animación de favorito
    val heartScale = if (product.isFavorite) rememberHeartBeatAnimation() else 1f

    // 🎨 Estado de hover (elevación)
    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 2.dp,
        animationSpec = tween(150),
        label = "cardElevation"
    )

    // 🖼️ Estado de carga de imagen
    var imageLoading by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column {
            // 🖼️ Imagen con shimmer loading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 140.dp else 180.dp)
            ) {
                // ✨ Shimmer effect mientras carga
                if (imageLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(rememberShimmerBrush())
                    )
                }

                // 🖼️ Imagen con Coil
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    onSuccess = { imageLoading = false },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                // 💗 Botón de favorito flotante
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (product.isFavorite)
                                Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (product.isFavorite)
                                Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.scale(heartScale)
                        )
                    }
                }
            }

            // 📝 Contenido de la card
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 🏷️ Nombre del producto
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                // 💰 Precio destacado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "USD",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${"%,.2f".format(product.price)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(4.dp))

                // 🎯 Acciones
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 🏷️ Chip de categoría
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            product.category.name,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // 🎬 Botones de acción
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // 🗑️ Eliminar
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        // 🛒 Agregar al carrito
                        Button(
                            onClick = onAddToCart,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Agregar", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════════════════
⬇️ 9) BOTTOM BAR ANIMADA CON INDICADORES
════════════════════════════════════════════════════════════════════════════ */

@Composable
private fun BottomBarAvanzada(
    gridMode: Boolean,
    sortAscending: Boolean,
    onToggleGrid: () -> Unit,
    onToggleSort: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 📊 Indicador de ordenamiento
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Sort,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        "Ordenamiento",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (sortAscending) "Precio: Menor a Mayor" else "Precio: Mayor a Menor",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // 🎛️ Controles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 🔄 Toggle ordenamiento
                IconButton(onClick = onToggleSort) {
                    Icon(
                        imageVector = if (sortAscending)
                            Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                        contentDescription = "Ordenar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 🎨 Toggle vista
                IconButton(onClick = onToggleGrid) {
                    Icon(
                        imageVector = if (gridMode)
                            Icons.Filled.Apps else Icons.Filled.List,
                        contentDescription = "Vista",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/* ════════════════════════════════════════════════════════════════════════════
🎪 10) SNACKBAR ANIMADO PERSONALIZADO
════════════════════════════════════════════════════════════════════════════ */

@Composable
private fun AnimatedSnackbar(data: SnackbarData) {
    Snackbar(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(),
        action = {
            data.visuals.actionLabel?.let { label ->
                TextButton(onClick = { data.performAction() }) {
                    Text(
                        label,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
        },
        dismissAction = {
            if (data.visuals.withDismissAction) {
                IconButton(onClick = { data.dismiss() }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            data.visuals.message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/* ════════════════════════════════════════════════════════════════════════════
📖 11) SECCIONES EDUCATIVAS Y NOTAS
════════════════════════════════════════════════════════════════════════════ */

@Composable
private fun SeccionTeoricaAvanzada() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "🎓 Guía de Aprendizaje",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            ConceptItem(
                emoji = "🎨",
                title = "Animaciones implementadas",
                description = "• Top bar con parallax y fade\n" +
                        "• Favorito con corazón latiendo\n" +
                        "• Shimmer effect en imágenes\n" +
                        "• Crossfade entre vistas\n" +
                        "• Entrada animada de items\n" +
                        "• Badge del carrito con rebote"
            )

            ConceptItem(
                emoji = "⚡",
                title = "Optimizaciones",
                description = "• derivedStateOf para cálculos costosos\n" +
                        "• remember para evitar recrear objetos\n" +
                        "• graphicsLayer para transformaciones\n" +
                        "• Flow con debounce en búsqueda"
            )

            ConceptItem(
                emoji = "🎯",
                title = "Patrones de diseño",
                description = "• Single Source of Truth (SSOT)\n" +
                        "• Unidirectional Data Flow\n" +
                        "• Composición sobre herencia\n" +
                        "• State hoisting"
            )
        }
    }
}

@Composable
private fun ConceptItem(emoji: String, title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "$emoji $title",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun SeccionNotasFinales() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "💡 Mejores Prácticas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Divider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))

            BestPracticeItem(
                "✅",
                "Usa animate*AsState para valores simples (Float, Color, Dp)"
            )
            BestPracticeItem(
                "✅",
                "AnimatedVisibility para aparecer/desaparecer elementos"
            )
            BestPracticeItem(
                "✅",
                "updateTransition para animar múltiples propiedades sincronizadas"
            )
            BestPracticeItem(
                "✅",
                "graphicsLayer en vez de Modifier.scale/rotate para mejor performance"
            )
            BestPracticeItem(
                "⚠️",
                "No anides contenedores con scroll (LazyColumn dentro de LazyColumn)"
            )
            BestPracticeItem(
                "⚠️",
                "Evita lógica pesada en composables; usa ViewModels en producción"
            )
            BestPracticeItem(
                "💡",
                "Los efectos visuales deben tener propósito, no ser decorativos"
            )
        }
    }
}

@Composable
private fun BestPracticeItem(icon: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            lineHeight = 20.sp
        )
    }
}

/* ════════════════════════════════════════════════════════════════════════════
👁️ 12) PREVIEW
════════════════════════════════════════════════════════════════════════════ */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo4_InteraccionAvanzada() {
    JetpackComposeKotlinTheme {
        Capitulo4_InteraccionAvanzada()
    }
}

/*
════════════════════════════════════════════════════════════════════════════════

🎉 ¡FIN DEL CAPÍTULO 3!

📚 LO QUE APRENDISTE:
─────────────────────
✅ Animaciones avanzadas con Compose
✅ Estado derivado y optimización de recomposiciones
✅ Búsqueda con debounce usando Flow
✅ Transiciones suaves entre layouts
✅ Efectos visuales (shimmer, parallax, fade)
✅ Patrones de arquitectura (SSOT, UDF)
✅ Composables reutilizables y bien documentados

🚀 PRÓXIMOS PASOS:
─────────────────
• Implementar ViewModels reales con lifecycle
• Agregar Room para persistencia de datos
• Implementar navegación con Navigation Compose
• Agregar tests unitarios y de UI
• Explorar Canvas para dibujos custom
• Implementar gestos avanzados (swipe, drag)

💬 RECURSOS ADICIONALES:
───────────────────────
• Documentación oficial: developer.android.com/jetpack/compose
• Codelabs: developer.android.com/codelabs
• Compose Samples: github.com/android/compose-samples

════════════════════════════════════════════════════════════════════════════════
*/