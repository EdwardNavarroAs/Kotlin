package com.example.jetpackcompose

/*
────────────────────────────────────────────────────────────
CAPÍTULO 4 — LISTAS Y LAZYCOLUMN EN JETPACK COMPOSE
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
──────────────────────
Aprender a construir listas dinámicas y optimizadas con Jetpack Compose,
utilizando `LazyColumn` y `LazyRow`, sus equivalentes modernos a
`RecyclerView` en XML.

✔ Diferencias entre Column y LazyColumn.
✔ Cómo mostrar listas estáticas y dinámicas.
✔ Uso de items(), item() y itemsIndexed().
✔ Manejo de scroll automático e integrado.
✔ Introducción a LazyRow (scroll horizontal).
✔ Ejemplo completo con datos simulados (usuarios, productos, etc.).

────────────────────────────────────────────────────────────
CONCEPTO CLAVE: “LAZY” (CARGA PEREZOSA)
────────────────────────────────────────────────────────────
A diferencia de `Column`, que dibuja **todos los elementos a la vez**,
`LazyColumn` **solo renderiza los visibles en pantalla**, cargando
y destruyendo elementos según se hace scroll.

Esto mejora el rendimiento y el consumo de memoria, especialmente en
listas largas (100, 1000 o más elementos).

────────────────────────────────────────────────────────────
ESTRUCTURA DE ESTE ARCHIVO
────────────────────────────────────────────────────────────
1) Diferencia entre Column y LazyColumn.
2) Lista estática (valores fijos).
3) Lista dinámica (usando List<String>).
4) itemsIndexed para obtener índice y valor.
5) Ejemplo visual con datos de usuarios.
6) LazyRow (scroll horizontal).
7) Ejemplo completo de pantalla con scroll.
────────────────────────────────────────────────────────────
*/

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.jetpackcompose.ui.theme.JetpackComposeTheme
import androidx.compose.foundation.lazy.items

/*
────────────────────────────────────────────────────────────
SECCIÓN 1 — DIFERENCIA ENTRE COLUMN Y LAZYCOLUMN
────────────────────────────────────────────────────────────
- Column: renderiza todos los elementos (no recomendada para listas largas).
- LazyColumn: renderiza bajo demanda, ideal para grandes colecciones.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionColumnVsLazyColumn() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Column vs LazyColumn",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = """
Column dibuja todos los elementos al instante, incluso si no se ven.
LazyColumn solo dibuja los visibles, cargando el resto según scroll.
            """.trimIndent(),
            fontSize = 14.sp
        )
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 2 — LISTA ESTÁTICA CON COLUMN
────────────────────────────────────────────────────────────
(Cambio aplicado aquí)
Antes usábamos LazyColumn dentro de otra LazyColumn,
lo que causaba conflicto de medidas infinitas.
Ahora esta sección usa Column normal, ya que el contenido es corto.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionListaEstatica() {
    val frutas = listOf("Manzana", "Banano", "Cereza", "Durazno", "Kiwi")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Lista estática de frutas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        frutas.forEach { fruta ->
            Text(
                text = "• $fruta",
                fontSize = 16.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 3 — LISTA DINÁMICA CON COLUMN
────────────────────────────────────────────────────────────
(Cambio aplicado aquí)
Esta lista también es corta, por lo tanto se puede mostrar
completa dentro de una Column sin problemas.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionListaDinamica() {
    val animales = listOf("Perro", "Gato", "Conejo", "Loro", "Tortuga", "Hamster")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Lista dinámica (con índice)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        animales.forEachIndexed { index, animal ->
            Text(
                text = "${index + 1}. $animal",
                fontSize = 16.sp,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 4 — LISTA VISUAL DE USUARIOS (COLUMN + filas)
────────────────────────────────────────────────────────────
(Cambio aplicado aquí)
La lista se mantiene visualmente igual, pero ahora se muestra
completa en una Column, ya que los datos son pocos.
────────────────────────────────────────────────────────────
*/
data class Usuario(val nombre: String, val imagen: String)

@Composable
fun SeccionListaUsuarios() {
    val usuarios = listOf(
        Usuario("Ana López", "https://randomuser.me/api/portraits/women/44.jpg"),
        Usuario("Carlos Pérez", "https://randomuser.me/api/portraits/men/46.jpg"),
        Usuario("Lucía Torres", "https://randomuser.me/api/portraits/women/12.jpg"),
        Usuario("Miguel Castro", "https://randomuser.me/api/portraits/men/24.jpg"),
        Usuario("Sofía Morales", "https://randomuser.me/api/portraits/women/30.jpg")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Lista de usuarios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        usuarios.forEach { usuario ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(usuario.imagen),
                    contentDescription = usuario.nombre,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = usuario.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 5 — LISTA HORIZONTAL (LAZYROW)
────────────────────────────────────────────────────────────
Se mantiene igual: LazyRow no causa conflicto de scroll
porque su desplazamiento es horizontal.
────────────────────────────────────────────────────────────
*/
@Composable
fun SeccionLazyRow() {
    val colores = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Ejemplo de LazyRow (scroll horizontal)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(colores) { color ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color)
                )
            }
        }
    }
}

/*
────────────────────────────────────────────────────────────
SECCIÓN 6 — PANTALLA COMPLETA DEL CAPÍTULO 4
────────────────────────────────────────────────────────────
Ahora esta LazyColumn es la ÚNICA de toda la pantalla.
Todas las secciones internas usan Column, evitando el error.
────────────────────────────────────────────────────────────
*/
@Composable
fun Capitulo4_ListasYLazyColumn() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "CAPÍTULO 4 — LISTAS Y LAZYCOLUMN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp)
            }

            item { SeccionColumnVsLazyColumn() }
            item { SeccionListaEstatica() }
            item { SeccionListaDinamica() }
            item { SeccionListaUsuarios() }
            item { SeccionLazyRow() }
        }
    }
}

/*
────────────────────────────────────────────────────────────
VISTA PREVIA COMPLETA DEL CAPÍTULO 4
────────────────────────────────────────────────────────────
Permite visualizar todas las listas (LazyColumn y LazyRow) con scroll.
────────────────────────────────────────────────────────────
*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCapitulo4() {
    JetpackComposeTheme {
        Capitulo4_ListasYLazyColumn()
    }
}
