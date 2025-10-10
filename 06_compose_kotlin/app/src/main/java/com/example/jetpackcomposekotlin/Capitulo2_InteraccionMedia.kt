package com.example.jetpackcomposekotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 2 — INTERACCIÓN INTERMEDIA: LISTAS DINÁMICAS + DISEÑO VISUAL
────────────────────────────────────────────────────────────

VERSIÓN: 100% ESTABLE (SIN APIs EXPERIMENTALES)

OBJETIVO DEL CAPÍTULO
──────────────────────
Construir una mini app tipo **Catálogo Interactivo** que combine:
• Lista dinámica (LazyColumn) con ítems visualmente ricos (Card + imagen).
• Estado global de colección con `mutableStateListOf` (reactivo/declarativo).
• Formulario de alta de ítems (TextField + Button).
• Acciones en ítems: favorito ❤️ / eliminar 🗑 / “deshacer” con Snackbar.
• Diseño Material 3: colores, elevación, espaciados, jerarquía visual.

Por el camino, profundizamos en:
• Cómo Compose recomponen listas cuando cambia el estado.
• Cómo cargar **imágenes desde URL** con **Coil** (AsyncImage).
• Cómo estructurar pantallas en capas con **Scaffold** y feedback con **Snackbar**.
• Cómo crear **chips personalizados** sin usar APIs experimentales.

────────────────────────────────────────────────────────────
MAPA DEL CAPÍTULO (ÍNDICE)
────────────────────────────────────────────────────────────
1) Modelo de datos (Product) y utilidades de imagen.
2) Capitulo2_ListasYEstados(): orquestador con Scaffold.
3) TopBarPersonalizada(): barra superior estable y centrada.
4) SeccionTeoricaListas(): teoría pedagógica de estado y listas.
5) SeccionAgregarProducto(...): formulario con validación y UX.
6) SeccionListaProductos(...): LazyColumn + Card + AsyncImage + acciones.
7) ItemProductoCard(...): pieza reutilizable de UI (composable item).
8) CustomChip(...): chip estable (Surface+Row) para estados/categorías.
9) SeccionResumenYNotas(): repaso pedagógico y buenas prácticas.
10) PreviewCapitulo2_ListasYEstados(): vista previa.

────────────────────────────────────────────────────────────
GLOSARIO RÁPIDO
────────────────────────────────────────────────────────────
• mutableStateListOf<T> → lista observable: al mutarla, la UI se recompone.
• LazyColumn → lista eficiente que crea/recicla ítems bajo demanda.
• Coil (AsyncImage) → carga imágenes desde URL de forma declarativa.
• SnackbarHostState → canal oficial para mostrar Snackbars en Scaffold.
• Hoisting de estado → el padre guarda el estado y pasa callbacks a los hijos.

────────────────────────────────────────────────────────────
*/

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetpackcomposekotlin.ui.theme.JetpackComposeKotlinTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

/* ───────────────────────────────────────────────────────────
1) MODELO DE DATOS Y UTILIDADES
───────────────────────────────────────────────────────────── */

/*
DATA CLASS — Product
Representa un producto simple para el catálogo:
• id          → identificador único (local) para gestionar acciones.
• name        → nombre visible del producto.
• price       → precio (Double para permitir decimales).
• imageUrl    → URL de imagen (usamos Picsum para demo).
• isFavorite  → estado visual que podemos alternar.
*/
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

/*
UTILIDAD — generar URL de imagen
Para demos, usamos Picsum con un “seed” (id) para que la imagen
se mantenga estable por producto, garantizando variedad y consistencia.
*/
private fun imageUrlFor(id: Int): String =
    "https://picsum.photos/seed/$id/800/600" // tamaño generoso (se ajusta con ContentScale)

/* ───────────────────────────────────────────────────────────
2) FUNCIÓN PRINCIPAL — Orquestador con Scaffold
─────────────────────────────────────────────────────────────

PROPÓSITO
• Mantener el estado global de la lista (mutableStateListOf<Product>).
• Estructurar la pantalla con Scaffold: top bar, snackbar host, FAB, contenido.
• Orquestar callbacks: añadir, alternar favorito, eliminar, deshacer.

DECISIONES
• Usamos rememberSaveable + mutableStateListOf para persistir “lo básico”.
  (En escenarios reales, migrarías a ViewModel para sobrevida de proceso.)
• El FAB lanza el formulario de alta (en este ejemplo, agrega un demo rápido).
*/
@Composable
fun Capitulo2_ListasYEstados() {
    val context = LocalContext.current

    // SnackbarHostState para feedback (global en esta pantalla)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Lista reactiva de productos — la UI se recompone al mutarla
    val productos = rememberSaveable(saver = listSaver(
        save = { list -> list.flatMap { p -> listOf(p.id, p.name, p.price, p.imageUrl, if (p.isFavorite) 1 else 0) } },
        restore = { flat ->
            flat.chunked(5).map { chunk ->
                Product(
                    id = (chunk[0] as Number).toInt(),
                    name = chunk[1] as String,
                    price = (chunk[2] as Number).toDouble(),
                    imageUrl = chunk[3] as String,
                    isFavorite = ((chunk[4] as Number).toInt() == 1)
                )
            }.toMutableStateList()
        }
    )) {
        // Estado inicial (demo) — tres productos ficticios
        mutableStateListOf(
            Product(1, "Auriculares Pro", 59.99, imageUrlFor(1)),
            Product(2, "Mouse Inalámbrico", 24.50, imageUrlFor(2)),
            Product(3, "Teclado Mecánico", 99.00, imageUrlFor(3), isFavorite = true)
        )
    }

    // Para "deshacer" eliminaciones: guardamos el último eliminado
    var ultimoEliminado: Product? by remember { mutableStateOf(null) }

    // Función: agregar producto
    fun agregarProducto(name: String, price: Double) {
        val newId = (productos.maxOfOrNull { it.id } ?: 0) + 1
        val nuevo = Product(newId, name, price, imageUrlFor(newId))
        productos.add(0, nuevo) // lo insertamos al principio para apreciar el cambio
        scope.launch { snackbarHostState.showSnackbar("Producto agregado: $name") }
    }

    // Función: alternar favorito
    fun toggleFavorite(id: Int) {
        val idx = productos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val p = productos[idx]
            productos[idx] = p.copy(isFavorite = !p.isFavorite) // reemplazo in place → recompone
        }
    }

    // Función: eliminar (con opción “Deshacer”)
    fun eliminarProducto(id: Int) {
        val idx = productos.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val p = productos.removeAt(idx)
            ultimoEliminado = p
            scope.launch {
                val res = snackbarHostState.showSnackbar(
                    message = "Eliminado: ${p.name}",
                    actionLabel = "DESHACER",
                    withDismissAction = true
                )
                if (res == SnackbarResult.ActionPerformed) {
                    // El usuario hizo click en DESHACER → restauramos
                    productos.add(idx.coerceIn(0, productos.size), p)
                    ultimoEliminado = null
                }
            }
        }
    }

    Scaffold(
        topBar = { TopBarPersonalizada2(title = "Capítulo 2 — Catálogo Interactivo") },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = { /* FAB aquí */ }
    ) { innerPadding ->

        // LazyColumn principal: la única lista desplazable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // deja espacio al FAB
        ) {
            item { SeccionTeoricaListas() }
            item { SeccionAgregarProducto(onAdd = { nombre, precio -> agregarProducto(nombre, precio) }) }
            item {
                SeccionListaProductos(
                    productos = productos,
                    onToggleFavorite = { id -> toggleFavorite(id) },
                    onDelete = { id -> eliminarProducto(id) }
                )
            }
            item { SeccionResumenYNotas() }
        }
    }
}

/* ───────────────────────────────────────────────────────────
3) TOP BAR PERSONALIZADA — 100% estable
─────────────────────────────────────────────────────────────

• Reutilizamos el patrón del Cap. 1: Surface + Row + Text.
• Ventaja: cero dependencias experimentales y total control estético.
*/
@Composable
fun TopBarPersonalizada2(title: String) {
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
    }
}

/* ───────────────────────────────────────────────────────────
4) SECCIÓN TEÓRICA — Estado y listas reactivas
───────────────────────────────────────────────────────────── */
@Composable
private fun SeccionTeoricaListas() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Listas reactivas en Compose", style = MaterialTheme.typography.titleMedium)
            Text(
                "En Compose, una lista observable (mutableStateListOf) hace que la UI se " +
                        "recomponga automáticamente cuando añadimos, eliminamos o modificamos ítems. " +
                        "No hay que notificar adaptadores manualmente: la UI es una función del estado."
            )
            Text(
                "• LazyColumn renderiza solo las filas visibles (rendimiento). " +
                        "• Cada ítem debería tener un id estable para evitar recomposiciones innecesarias. " +
                        "• La mutación in place (reemplazar un elemento por su copia) dispara la recomposición."
            )
        }
    }
}

/* ───────────────────────────────────────────────────────────
5) SECCIÓN — Agregar producto (formulario)
─────────────────────────────────────────────────────────────

OBJETIVO
• Demostrar entradas controladas (TextField) + validación rápida.
• Buena UX: errores visibles, deshabilitar botón hasta que sea válido.
*/
@Composable
private fun SeccionAgregarProducto(
    onAdd: (name: String, price: Double) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var priceText by rememberSaveable { mutableStateOf("") } // guardamos como texto; validamos a Double

    val nameOk = name.trim().length >= 3
    val priceOk = priceText.toDoubleOrNull()?.let { it > 0.0 } == true
    val formOk = nameOk && priceOk

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Agregar producto", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre (mín. 3 caracteres)") },
                singleLine = true,
                isError = name.isNotBlank() && !nameOk,
                supportingText = {
                    if (name.isNotBlank() && !nameOk) Text("Nombre demasiado corto")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it.replace(',', '.') }, // soporta coma → punto
                label = { Text("Precio (> 0)") },
                singleLine = true,
                isError = priceText.isNotBlank() && !priceOk,
                supportingText = {
                    if (priceText.isNotBlank() && !priceOk) Text("Introduce un número válido")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = formOk,
                    onClick = {
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        onAdd(name.trim(), price)
                        // limpiamos el formulario
                        name = ""
                        priceText = ""
                    }
                ) { Text("Agregar") }

                OutlinedButton(onClick = { name = ""; priceText = "" }) { Text("Limpiar") }
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
6) SECCIÓN — Lista dinámica (LazyColumn)
─────────────────────────────────────────────────────────────

• Renderiza una lista de Product con celdas Card visualmente ricas.
• Delegamos el diseño del ítem en ItemProductoCard (responsabilidad única).
*/
@Composable
private fun SeccionListaProductos(
    productos: List<Product>,
    onToggleFavorite: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Productos (${productos.size})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            // Recorremos la lista de productos sin crear otro contenedor con scroll
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                productos.forEach { p ->
                    ItemProductoCard(
                        product = p,
                        onToggleFavorite = { onToggleFavorite(p.id) },
                        onDelete = { onDelete(p.id) }
                    )
                }
            }
        }
    }
}


/* ───────────────────────────────────────────────────────────
7) ITEM — Card visual de producto con imagen + acciones
─────────────────────────────────────────────────────────────

DISEÑO
• Imagen superior (AsyncImage de Coil) con ContentScale.Crop.
• Contenido: nombre, precio, chip de categoría/estado (custom).
• Acciones: favorito ❤️ (toggle) y eliminar 🗑.
*/
@Composable
private fun ItemProductoCard(
    product: Product,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // IMAGEN: Compose + Coil (carga desde URL)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop // recorte para que llene el ancho sin deformarse
            )

            // CONTENIDO: título, precio, chip y barra de acciones
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

                // Chip de estado: favorito o estándar (custom chip)
                val chipLabel = if (product.isFavorite) "Favorito" else "Producto"
                CustomChip(
                    label = chipLabel,
                    selected = product.isFavorite,
                    onClick = onToggleFavorite
                )

                // Acciones con iconos: favorito / eliminar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (product.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (product.isFavorite)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/* ───────────────────────────────────────────────────────────
8) CHIP PERSONALIZADO — 100% estable
─────────────────────────────────────────────────────────────

Por qué custom:
• Algunas variantes de chips en Material3 pueden ser experimentales.
• Con Surface + Row + Text logramos un chip visual, accesible y estable.

Diseño:
• Fondo según estado (selected → primaryContainer).
• Texto con color de contraste del tema.
• Forma redondeada y paddings cómodos para el dedo.
*/
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
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pequeño indicador circular (solo decoración)
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
9) SECCIÓN RESUMEN — Notas de buenas prácticas
───────────────────────────────────────────────────────────── */
@Composable
private fun SeccionResumenYNotas() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Resumen y buenas prácticas", style = MaterialTheme.typography.titleMedium)
            Text(
                "• Usa mutableStateListOf para listas reactivas; la UI se recompone sola. \n" +
                        "• Prefiere LazyColumn para listas largas; define claves estables (key) por ítem. \n" +
                        "• Para imágenes remotas, usa Coil (AsyncImage) en Compose; ContentScale.Crop para cubrir. \n" +
                        "• Mantén un estado global en el padre y pasa callbacks a los ítems (hoisting). \n" +
                        "• Ofrece feedback inmediato con Snackbar (y acciones como DESHACER). \n" +
                        "• Evita lógica pesada dentro de composables; considera ViewModel/casos de uso en apps reales."
            )
        }
    }
}

/* ───────────────────────────────────────────────────────────
10) PREVIEW — Vista previa de la pantalla completa
───────────────────────────────────────────────────────────── */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo2_ListasYEstados() {
    JetpackComposeKotlinTheme {
        Capitulo2_ListasYEstados()
    }
}
