package com.example.jetpackcomposekotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3 — INTERACCIÓN AVANZADA EN COMPOSE + KOTLIN
────────────────────────────────────────────────────────────

VERSIÓN: 100% ESTABLE (SIN APIs EXPERIMENTALES)

OBJETIVOS AVANZADOS
────────────────────
• Construir una pantalla con:
  - Top bar “colapsable” (altura y elevación animadas por scroll)
  - Búsqueda con “debounce” (Flow + LaunchedEffect)
  - Filtros con chips personalizados (estables)
  - Ordenamiento (precio asc/desc)
  - Vista de lista ↔ cuadrícula (Crossfade)
  - Cards visuales con imagen (Coil), favoritos, agregar/eliminar
  - Contador de carrito animado
  - Snackbars con acción “DESHACER”
  - Bottom bar personalizada (estética Material 3 estable)

CONCEPTOS CLAVE
────────────────
• Estado derivado y memoización: `remember`, `derivedStateOf`
• Animaciones estables: `animate*AsState`, `Crossfade`, `animateContentSize`
• Scroll + animación de top bar: `LazyListState.firstVisibleItemScrollOffset`
• Búsqueda “debounce”: `snapshotFlow` + `debounce` + `distinctUntilChanged`
• Compose declarativo: UI = f(estado). Al cambiar estado, se recompone.

MAPA DEL ARCHIVO
────────────────
1) Modelos y utilidades (Product, Category)
2) Estado “tipo ViewModel” dentro del archivo (para simplificar el capítulo)
3) Pantalla principal: Capitulo3_InteraccionAvanzada()
   - Scaffold con TopBar colapsable, SnackbarHost, BottomBar y FAB
4) TopBarAvanzada(): altura y elevación animadas por scroll
5) Filtro y búsqueda: SearchBar + Chips
6) Switch de vista (lista ↔ grid) con Crossfade
7) Lista/Grid de productos: LazyColumn / LazyVerticalGrid*
8) ItemCard con animaciones (favorito, agregar, feedback)
9) BottomBar personalizada con contador animado del carrito
10) Preview

*Nota: Usamos LazyVerticalGrid SI está disponible en tu versión de foundation.
Si no, cae a lista tradicional. Está marcado en comentarios.
────────────────────────────────────────────────────────────
*/

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposekotlin.ui.theme.JetpackComposeKotlinTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/* ───────────────────────────────────────────────────────────
1) MODELOS Y UTILIDADES
───────────────────────────────────────────────────────────── */

// Categorías demostrativas para filtros
enum class Category { All, Audio, Perifericos, Almacenamiento, Iluminacion }

// Modelo de producto con categoría
data class Products(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val category: Category,
    val isFavorite: Boolean = false
)

// Generamos URL estable (Picsum) por id
private fun imageUrlFor(id: Int): String = "https://picsum.photos/seed/$id/800/600"

// Lista semilla “bonita” para iniciar
private fun seedProducts(): List<Products> = listOf(
    Products(1, "Auriculares Pro", 59.99, imageUrlFor(1), Category.Audio, isFavorite = true),
    Products(2, "Mouse Inalámbrico", 24.50, imageUrlFor(2), Category.Perifericos),
    Products(3, "Teclado Mecánico", 99.00, imageUrlFor(3), Category.Perifericos),
    Products(4, "SSD NVMe 1TB", 109.99, imageUrlFor(4), Category.Almacenamiento),
    Products(5, "Lámpara RGB Desk", 34.25, imageUrlFor(5), Category.Iluminacion),
    Products(6, "Soundbar Compacta", 79.90, imageUrlFor(6), Category.Audio),
)

/* ───────────────────────────────────────────────────────────
2) “VIEWMODEL” SIMPLE EN EL ARCHIVO (PROPÓSITO EDUCATIVO)
─────────────────────────────────────────────────────────────

En una app real, usarías un ViewModel (lifecycle) + repositorios.
Aquí alojamos estado y lógica en el composable superior para
mantener el capítulo autocontenido y legible (libro digital).
*/

@OptIn(FlowPreview::class)
@Composable
private fun rememberCatalogState(): CatalogState {
    val snackbarHostState = remember { SnackbarHostState() }

    // Lista reactiva “fuente de la verdad”
    val items = remember { mutableStateListOf<Products>().apply { addAll(seedProducts()) } }

    // Búsqueda (texto crudo) y flujo con debounce
    var search by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val searchFlow = remember { MutableStateFlow("") }

    // Filtros
    var selectedCategory by rememberSaveable { mutableStateOf(Category.All) }
    var sortAscending by rememberSaveable { mutableStateOf(true) }
    var gridMode by rememberSaveable { mutableStateOf(false) }

    // Carrito (simple contador animado)
    var cartCount by rememberSaveable { mutableStateOf(0) }

    // Manejo de snackbar
    val scope = rememberCoroutineScope()

    // Emitimos la búsqueda con debounce (300ms)
    LaunchedEffect(search) {
        searchFlow.emit(search.text)
    }
    val debouncedQuery by remember {
        searchFlow
            .debounce(300)
            .map { it.trim().lowercase() }
            .distinctUntilChanged()
    }.collectAsState(initial = "")

    // Resultado filtrado/ordenado derivado del estado
    val filteredSorted by remember(items, debouncedQuery, selectedCategory, sortAscending) {
        derivedStateOf {
            items.asSequence()
                .filter { p ->
                    (selectedCategory == Category.All || p.category == selectedCategory) &&
                            (debouncedQuery.isBlank() || p.name.lowercase().contains(debouncedQuery))
                }
                .sortedBy { if (sortAscending) it.price else -it.price }
                .toList()
        }
    }

    // Acciones
    fun addRandom() {
        val nextId = (items.maxOfOrNull { it.id } ?: 0) + 1
        val names = listOf("Micrófono USB", "Hub USB-C", "SSD 2TB", "Tira LED RGB", "Gamepad Pro")
        val cats = listOf(Category.Audio, Category.Perifericos, Category.Almacenamiento, Category.Iluminacion)
        val n = names.random()
        val c = cats.random()
        val price = (Random.nextInt(15, 250) + Random.nextDouble()).let { (it * 100.0).roundToInt() / 100.0 }
        items.add(0, Products(nextId, "$n ${Random.nextInt(100)}", price, imageUrlFor(nextId), c))
        scope.launch { snackbarHostState.showSnackbar("Producto agregado: $n") }
    }

    fun toggleFavorite(id: Int) {
        val i = items.indexOfFirst { it.id == id }
        if (i >= 0) {
            val p = items[i]
            items[i] = p.copy(isFavorite = !p.isFavorite)
        }
    }

    fun delete(id: Int) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val removed = items.removeAt(idx)
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = "Eliminado: ${removed.name}",
                    actionLabel = "DESHACER",
                    withDismissAction = true
                )
                if (res == SnackbarResult.ActionPerformed) {
                    items.add(idx.coerceIn(0, items.size), removed)
                }
            }
        }
    }

    fun addToCart() {
        cartCount += 1
        scope.launch {
            snackbarHostState.showSnackbar("Añadido al carrito ($cartCount)")
        }
    }

    return remember {
        CatalogState(
            snackbarHostState = snackbarHostState,
            items = items,
            search = { search },
            setSearch = { search = it },
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

private data class CatalogState(
    val snackbarHostState: SnackbarHostState,
    val items: MutableList<Products>,
    val search: () -> TextFieldValue,
    val setSearch: (TextFieldValue) -> Unit,
    val debouncedQuery: () -> String,
    val selectedCategory: () -> Category,
    val setCategory: (Category) -> Unit,
    val sortAscending: () -> Boolean,
    val toggleSort: () -> Unit,
    val gridMode: () -> Boolean,
    val toggleGrid: () -> Unit,
    val filteredSorted: () -> List<Products>,
    val addRandom: () -> Unit,
    val toggleFavorite: (Int) -> Unit,
    val delete: (Int) -> Unit,
    val addToCart: () -> Unit,
    val cartCount: () -> Int
)

/* ───────────────────────────────────────────────────────────
3) PANTALLA PRINCIPAL — CON ANIMACIONES DE SCROLL
───────────────────────────────────────────────────────────── */
@Composable
fun Capitulo3_InteraccionAvanzada() {
    val context = LocalContext.current
    val state = rememberCatalogState()

    // Estado de scroll para animar top bar (colapso)
    val listState = rememberLazyListState()

    // Altura y elevación animadas según scroll (simple y estable)
    val collapsed by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8 }
    }
    val barHeight by animateFloatAsState(targetValue = if (collapsed) 56f else 88f, animationSpec = tween(250), label = "barHeight")
    val barElevation by animateFloatAsState(targetValue = if (collapsed) 4f else 0f, animationSpec = tween(250), label = "barElevation")

    Scaffold(
        topBar = {
            TopBarAvanzada(
                title = "Capítulo 3 — Avanzado",
                heightPx = barHeight,
                elevationDp = barElevation,
                cartCount = state.cartCount(),
                onCartClick = { Toast.makeText(context, "Carrito: ${state.cartCount()}", Toast.LENGTH_SHORT).show() }
            )
        },
        snackbarHost = { SnackbarHost(state.snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { state.addRandom() }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        },
        bottomBar = {
            BottomBarAvanzada(
                gridMode = state.gridMode(),
                sortAscending = state.sortAscending(),
                onToggleGrid = { state.toggleGrid() },
                onToggleSort = { state.toggleSort() }
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
            contentPadding = PaddingValues(bottom = 100.dp) // espacio para FAB/bottom bar
        ) {
            // Teoría y guía
            item {
                SeccionTeoricaAvanzada()
            }

            // Búsqueda + filtros (chips)
            item {
                SearchAndFilters(
                    query = state.search(),
                    onQueryChange = state.setSearch,
                    selected = state.selectedCategory(),
                    onSelect = state.setCategory
                )
            }

            // Vista dinámica: lista o grid (Crossfade)
            item {
                ProductosCrossfadeSection(
                    gridMode = state.gridMode(),
                    products = state.filteredSorted(),
                    onToggleFavorite = state.toggleFavorite,
                    onDelete = state.delete,
                    onAddToCart = state.addToCart
                )
            }

            // Notas y cierre
            item { SeccionNotasFinales() }
        }
    }
}

/* ───────────────────────────────────────────────────────────
4) TOP BAR COLAPSABLE — altura/elevación animadas por scroll
───────────────────────────────────────────────────────────── */
@Composable
private fun TopBarAvanzada(
    title: String,
    heightPx: Float,
    elevationDp: Float,
    cartCount: Int,
    onCartClick: () -> Unit
) {
    Surface(
        tonalElevation = elevationDp.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightPx.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // “Badge” de carrito simple y estable
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCartClick() }
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    "Carrito: $cartCount",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
5) BÚSQUEDA + FILTROS CON CHIPS PERSONALIZADOS
───────────────────────────────────────────────────────────── */
@Composable
private fun SearchAndFilters(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    selected: Category,
    onSelect: (Category) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Barra de búsqueda simple
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar productos…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Filtros por categoría con chips personalizados (estables)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Category.values().forEach { cat ->
                CustomChip(
                    label = cat.name,
                    selected = selected == cat,
                    onClick = { onSelect(cat) }
                )
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
6) SECCIÓN CON CROSSFADE: LISTA ↔ GRID
─────────────────────────────────────────────────────────────

• Crossfade cambia suavemente entre dos layouts
• Evitamos APIs experimentales usando Foundation estable
*/


@Composable
private fun ProductosCrossfadeSection(
    gridMode: Boolean,
    products: List<Products>,
    onToggleFavorite: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resultados (${products.size})", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (gridMode) Icons.Filled.GridView else Icons.Filled.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))

            Crossfade(targetState = gridMode, label = "gridCrossfade") { isGrid ->
                if (!isGrid) {
                    // ✅ CORREGIDO: Column en vez de LazyColumn
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        products.forEach { p ->
                            ProductItemCard(
                                product = p,
                                onToggleFavorite = { onToggleFavorite(p.id) },
                                onDelete = { onDelete(p.id) },
                                onAddToCart = onAddToCart,
                                compact = true
                            )
                        }
                    }
                } else {
                    // ✅ Grid está bien (no anidado)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(180.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp), // Limita la altura
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(products.size, key = { products[it].id }) { index ->
                            val p = products[index]
                            ProductItemCard(
                                product = p,
                                onToggleFavorite = { onToggleFavorite(p.id) },
                                onDelete = { onDelete(p.id) },
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
/* ───────────────────────────────────────────────────────────
7) ITEM CARD CON ANIMACIONES Y ACCIONES
─────────────────────────────────────────────────────────────

Animaciones:
• Favorito → color del icono con animateFloatAsState + tint dinámico
• Card → animateContentSize (suaviza cambios por contenido)
• Imágenes con crossfade (Coil)
*/
@Composable
private fun ProductItemCard(
    product: Products,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit,
    compact: Boolean
) {
    val favAlpha by animateFloatAsState(
        targetValue = if (product.isFavorite) 1f else 0.5f,
        animationSpec = tween(200),
        label = "favAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(180)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 140.dp else 170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "USD ${"%,.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Chip de categoría (custom, estable)
                    CustomChip(
                        label = product.category.name,
                        selected = false,
                        onClick = {} // demostrativo
                    )

                    Row {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (product.isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.graphicsLayer(alpha = favAlpha)
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Button(onClick = onAddToCart) { Text("Agregar") }
                    }
                }
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
8) CHIP PERSONALIZADO — REUTILIZABLE Y ESTABLE
───────────────────────────────────────────────────────────── */
@Composable
private fun CustomChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        tonalElevation = if (selected) 2.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) MaterialTheme.colorScheme.primary else fg)
            )
            Spacer(Modifier.width(8.dp))
            Text(label, color = fg, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/* ───────────────────────────────────────────────────────────
9) BOTTOM BAR PERSONALIZADA — CON ACCIONES AVANZADAS
─────────────────────────────────────────────────────────────

• Estética Material 3 sin APIs experimentales.
• Acciones: alternar lista↔grid, alternar orden (precio asc/desc).
*/
@Composable
private fun BottomBarAvanzada(
    gridMode: Boolean,
    sortAscending: Boolean,
    onToggleGrid: () -> Unit,
    onToggleSort: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text("Filtros", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (sortAscending) "Precio ↑" else "Precio ↓", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onToggleSort) {
                    Icon(
                        imageVector = if (sortAscending) Icons.Filled.List else Icons.Filled.List,
                        contentDescription = "Ordenar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer(rotationZ = if (sortAscending) 0f else 180f)
                    )
                }
                IconButton(onClick = onToggleGrid) {
                    Icon(
                        imageVector = if (gridMode) Icons.Filled.GridView else Icons.Filled.List,
                        contentDescription = "Vista",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
10) SECCIONES TEÓRICAS / NOTAS
───────────────────────────────────────────────────────────── */
@Composable
private fun SeccionTeoricaAvanzada() {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Interacción avanzada: guía de lectura", style = MaterialTheme.typography.titleMedium)
            Text(
                "• La top bar cambia altura/elevación según el scroll (estado derivado + animaciones). " +
                        "• La búsqueda usa debounce con Flow para evitar recomponer por cada tecla. " +
                        "• Filtros con chips personalizados (estables) y ordenamiento por precio. " +
                        "• Crossfade alterna entre lista y grid con una transición suave. " +
                        "• Las cards usan Coil con crossfade; los favoritos animan su opacidad/color. " +
                        "• Snackbar con 'DESHACER' permite UX reversible; el bottom bar agrupa acciones globales."
            )
        }
    }
}

@Composable
private fun SeccionNotasFinales() {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Notas finales y buenas prácticas", style = MaterialTheme.typography.titleMedium)
            Text(
                "• No anides contenedores con scroll vertical (usa una sola LazyColumn o limita alturas). \n" +
                        "• Deriva estado computado con derivedStateOf para evitar cálculos costosos en recomposición. \n" +
                        "• Usa animate*AsState para microinteracciones (elevaciones, opacity, tamaños). \n" +
                        "• Crossfade para cambiar layouts sin saltos de contenido. \n" +
                        "• Evita lógica pesada en composables; en producción usa ViewModel y repositorios. \n" +
                        "• Usa colores y elevaciones del tema para accesibilidad y consistencia."
            )
        }
    }
}

/* ───────────────────────────────────────────────────────────
11) PREVIEW
───────────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo3_InteraccionAvanzada() {
    JetpackComposeKotlinTheme {
        Capitulo3_InteraccionAvanzada()
    }
}
