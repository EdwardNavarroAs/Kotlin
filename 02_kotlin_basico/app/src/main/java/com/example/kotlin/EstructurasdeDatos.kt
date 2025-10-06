package com.example.kotlin


/*
────────────────────────────────────────────────────────────
CAPÍTULO 6: Arreglos, Listas, Mapas y Conjuntos (Set) en Kotlin
────────────────────────────────────────────────────────────

En este capítulo aprenderemos a trabajar con las estructuras de datos
más comunes en Kotlin: los arreglos (`Array`), las listas (`List`),
los mapas (`Map`) y los conjuntos (`Set`).

Estas estructuras permiten almacenar y manipular colecciones de datos.

────────────────────────────────────────────────────────────
1. ARRAY (Arreglo)
────────────────────────────────────────────────────────────
- Estructura de datos que almacena una cantidad fija de elementos.
- Los elementos se acceden por índice (comenzando desde 0).
- Todos los elementos deben ser del mismo tipo.

Declaración:
val numeros = arrayOf(1, 2, 3, 4, 5)

También se puede especificar el tipo:
val nombres: Array<String> = arrayOf("Ana", "Luis", "Carlos")

────────────────────────────────────────────────────────────
2. LIST (Lista)
────────────────────────────────────────────────────────────
- Es similar a un array, pero más flexible.
- Puede ser mutable o inmutable.
- Kotlin diferencia entre:
  - List (inmutable): no se puede modificar.
  - MutableList (mutable): se puede agregar, eliminar o modificar elementos.

Declaración:
val lista = listOf("uno", "dos", "tres") // inmutable
val listaMutable = mutableListOf("a", "b", "c") // mutable

────────────────────────────────────────────────────────────
3. MAP (Mapa)
────────────────────────────────────────────────────────────
- Almacena pares clave → valor.
- Las claves deben ser únicas.
- También hay `Map` (inmutable) y `MutableMap` (mutable).

Declaración:
val edades = mapOf("Ana" to 25, "Luis" to 30) // inmutable
val inventario = mutableMapOf("manzanas" to 10, "bananas" to 5) // mutable

────────────────────────────────────────────────────────────
4. SET (Conjunto)
────────────────────────────────────────────────────────────
- Colección de elementos únicos (no permite duplicados).
- No garantiza un orden específico.
- También hay `Set` (inmutable) y `MutableSet` (mutable).

Declaración:
val conjunto = setOf("manzana", "pera", "manzana") // inmutable
val conjuntoMutable = mutableSetOf(1, 2, 3) // mutable
────────────────────────────────────────────────────────────
*/

fun main() {

    // ───────────────────────────────────────────────
    // ARRAYS
    // ───────────────────────────────────────────────

    val numeros = arrayOf(10, 20, 30, 40, 50)

    println("Acceso por índice en Array:")
    println("Primer número: ${numeros[0]}")
    println("Tercer número: ${numeros[2]}")

    println("Tamaño del arreglo: ${numeros.size}")

    println("Recorrer Array:")
    for (n in numeros) {
        println("Elemento: $n")
    }

    // ───────────────────────────────────────────────
    // LISTAS INMUTABLES
    // ───────────────────────────────────────────────

    val colores = listOf("Rojo", "Verde", "Azul")

    println("\nLista de colores (inmutable):")
    for (color in colores) {
        println(color)
    }

    println("Primer color: ${colores[0]}")
    println("¿Contiene Azul?: ${colores.contains("Azul")}")
    println("Tamaño de la lista: ${colores.size}")

    // ───────────────────────────────────────────────
    // LISTAS MUTABLES
    // ───────────────────────────────────────────────

    val tareas = mutableListOf("Estudiar", "Leer", "Programar")

    tareas.add("Dormir") // Agregar
    tareas.remove("Leer") // Eliminar
    tareas[0] = "Revisar Kotlin" // Modificar

    println("\nLista de tareas mutable:")
    for (tarea in tareas) {
        println(tarea)
    }

    // ───────────────────────────────────────────────
    // MAPAS INMUTABLES
    // ───────────────────────────────────────────────

    val edades = mapOf(
        "Ana" to 25,
        "Luis" to 30,
        "Carlos" to 28
    )

    println("\nMapa de edades (inmutable):")
    println("Edad de Luis: ${edades["Luis"]}")
    println("¿Contiene a Ana?: ${"Ana" in edades}")

    // ───────────────────────────────────────────────
    // MAPAS MUTABLES
    // ───────────────────────────────────────────────

    val inventario = mutableMapOf(
        "Manzanas" to 10,
        "Bananas" to 5
    )

    inventario["Manzanas"] = 15         // Modificar valor
    inventario["Peras"] = 8             // Agregar nuevo par
    inventario.remove("Bananas")        // Eliminar clave

    println("\nInventario actualizado (MutableMap):")
    for ((fruta, cantidad) in inventario) {
        println("$fruta: $cantidad unidades")
    }

    // ───────────────────────────────────────────────
    // SET (CONJUNTO) INMUTABLE
    // ───────────────────────────────────────────────

    val conjuntoInmutable = setOf("manzana", "pera", "manzana")
    // Kotlin elimina automáticamente los duplicados.

    println("\nConjunto inmutable (sin duplicados):")
    for (elemento in conjuntoInmutable) {
        println(elemento)
    }

    println("¿Contiene 'pera'?: ${conjuntoInmutable.contains("pera")}")

    // ───────────────────────────────────────────────
    // SET (CONJUNTO) MUTABLE
    // ───────────────────────────────────────────────

    val conjuntoMutable = mutableSetOf(1, 2, 3)

    conjuntoMutable.add(3) // No se agrega porque ya existe
    conjuntoMutable.add(4) // Se agrega nuevo
    conjuntoMutable.remove(2) // Se elimina

    println("\nConjunto mutable actualizado:")
    for (numero in conjuntoMutable) {
        println(numero)
    }
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:
────────────────────────────────────────────────────────────
- `Array` permite almacenar datos del mismo tipo con tamaño fijo.
- `List` es una colección ordenada. Usar `MutableList` si necesitas modificarla.
- `Map` almacena información en forma de pares clave → valor.
- `Set` almacena elementos únicos sin duplicados. Usar `MutableSet` si necesitas modificarlo.
- Kotlin distingue claramente entre colecciones mutables e inmutables.
- Estas estructuras son fundamentales para representar conjuntos de datos y manipularlos.
*/
