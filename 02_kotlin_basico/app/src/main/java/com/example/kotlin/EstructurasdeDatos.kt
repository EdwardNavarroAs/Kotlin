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
    for (n in numeros) println("Elemento: $n")

    // ───────────────────────────────────────────────
    // LISTAS INMUTABLES
    // ───────────────────────────────────────────────
    val colores = listOf("Rojo", "Verde", "Azul")
    println("\nLista de colores (inmutable):")
    for (color in colores) println(color)
    println("Primer color: ${colores[0]}")
    println("¿Contiene Azul?: ${colores.contains("Azul")}")
    println("Tamaño de la lista: ${colores.size}")

    // ───────────────────────────────────────────────
    // LISTAS MUTABLES
    // ───────────────────────────────────────────────
    val tareas = mutableListOf("Estudiar", "Leer", "Programar")
    tareas.add("Dormir")
    tareas.remove("Leer")
    tareas[0] = "Revisar Kotlin"
    println("\nLista de tareas mutable:")
    for (tarea in tareas) println(tarea)

    // ───────────────────────────────────────────────
    // MAPAS INMUTABLES
    // ───────────────────────────────────────────────
    val edades = mapOf("Ana" to 25, "Luis" to 30, "Carlos" to 28)
    println("\nMapa de edades (inmutable):")
    println("Edad de Luis: ${edades["Luis"]}")
    println("¿Contiene a Ana?: ${"Ana" in edades}")

    // ───────────────────────────────────────────────
    // MAPAS MUTABLES
    // ───────────────────────────────────────────────
    val inventario = mutableMapOf("Manzanas" to 10, "Bananas" to 5)
    inventario["Manzanas"] = 15
    inventario["Peras"] = 8
    inventario.remove("Bananas")
    println("\nInventario actualizado (MutableMap):")
    for ((fruta, cantidad) in inventario) println("$fruta: $cantidad unidades")

    // ───────────────────────────────────────────────
    // SET (CONJUNTO) INMUTABLE
    // ───────────────────────────────────────────────
    val conjuntoInmutable = setOf("manzana", "pera", "manzana")
    println("\nConjunto inmutable (sin duplicados):")
    for (elemento in conjuntoInmutable) println(elemento)
    println("¿Contiene 'pera'?: ${conjuntoInmutable.contains("pera")}")

    // ───────────────────────────────────────────────
    // SET (CONJUNTO) MUTABLE
    // ───────────────────────────────────────────────
    val conjuntoMutable = mutableSetOf(1, 2, 3)
    conjuntoMutable.add(3)
    conjuntoMutable.add(4)
    conjuntoMutable.remove(2)
    println("\nConjunto mutable actualizado:")
    for (numero in conjuntoMutable) println(numero)

    // ───────────────────────────────────────────────
    // MÉTODOS FUNCIONALES Y OPERACIONES COMUNES
    // ───────────────────────────────────────────────

    // map → transforma elementos
    val cuadrados = listOf(1, 2, 3, 4).map { it * it }
    println("\nmap → cuadrados = $cuadrados")

    // filter → selecciona elementos según condición
    val pares = listOf(1, 2, 3, 4, 5, 6).filter { it % 2 == 0 }
    println("filter → pares = $pares")

    // zip → combina dos listas
    val nombres = listOf("Ana", "Luis", "Pedro")
    val edadesZip = listOf(25, 30, 28)
    val personas = nombres.zip(edadesZip)
    println("zip → $personas")

    // flatten → aplana lista de listas
    val listaDeListas = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    println("flatten → ${listaDeListas.flatten()}")

    // sorted → ordena ascendente
    val desordenados = listOf(5, 2, 4, 1, 3)
    println("sorted → ${desordenados.sorted()}")

    // reversed → invierte el orden
    println("reversed → ${desordenados.reversed()}")

    // distinct → elimina duplicados
    val duplicados = listOf(1, 2, 2, 3, 3, 4)
    println("distinct → ${duplicados.distinct()}")

    // forEach → recorrer colecciones
    println("\nforEach en tareas:")
    tareas.forEach { println("- $it") }

    // groupBy → agrupar por clave
    val palabras = listOf("casa", "carro", "sol", "silla")
    val agrupadasPorLongitud = palabras.groupBy { it.length }
    println("groupBy (por longitud): $agrupadasPorLongitud")

    // any → al menos uno cumple
    val hayMayores = edades.values.any { it > 26 }
    println("any → ¿alguien mayor de 26?: $hayMayores")

    // all → todos cumplen
    val todosMayoresDe20 = edades.values.all { it > 20 }
    println("all → ¿todos mayores de 20?: $todosMayoresDe20")
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
- Las funciones `map`, `filter`, `zip`, `flatten`, `sorted`, `distinct`, `groupBy`, etc.,
  permiten transformar y consultar las colecciones de forma declarativa y expresiva.
*/
