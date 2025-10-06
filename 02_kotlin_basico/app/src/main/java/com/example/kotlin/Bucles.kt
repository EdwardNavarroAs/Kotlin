package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 5: Bucles (Loops) en Kotlin
────────────────────────────────────────────────────────────

¿Qué es un bucle?

Un bucle permite ejecutar un bloque de código varias veces, ya sea
una cantidad determinada o mientras se cumpla una condición.

Kotlin proporciona tres tipos principales de bucles:

1. while
2. do...while
3. for (incluye rangos, colecciones y arrays)

También existen instrucciones especiales como:

- break → salir completamente del bucle.
- continue → saltar a la siguiente iteración.

────────────────────────────────────────────────────────────
1. BUCLE WHILE
────────────────────────────────────────────────────────────
Evalúa la condición **antes** de ejecutar el bloque.

Sintaxis:

while (condición) {
    // bloque de código que se repite
}
*/

fun main() {

    var contador = 1
    println("Bucle while:")
    while (contador <= 5) {
        println("Contador = $contador")
        contador++
    }

    /*
    ────────────────────────────────────────────────────────
    2. BUCLE DO...WHILE
    ────────────────────────────────────────────────────────
    Evalúa la condición **después** de ejecutar el bloque.
    Se ejecuta al menos una vez, aunque la condición sea falsa.

    Sintaxis:

    do {
        // bloque de código
    } while (condición)
    */

    var i = 10
    println("\nBucle do...while:")
    do {
        println("i = $i")
        i--
    } while (i > 5)

    /*
    ────────────────────────────────────────────────────────
    3. BUCLE FOR
    ────────────────────────────────────────────────────────
    Se usa para recorrer rangos, arrays o listas.

    Sintaxis básica (rango ascendente):

    for (variable in inicio..fin) {
        // bloque
    }

    También existen:
    - downTo → para contar hacia atrás
    - step → para saltos personalizados
    */

    println("\nBucle for (1..5):")
    for (n in 1..5) {
        println("n = $n")
    }

    println("\nBucle for (5 downTo 1):")
    for (n in 5 downTo 1) {
        println("n = $n")
    }

    println("\nBucle for con step:")
    for (n in 0..10 step 2) {
        println("n = $n")
    }

    /*
    ────────────────────────────────────────────────────────
    Recorrer una lista o arreglo:
    ────────────────────────────────────────────────────────
    */

    val frutas = listOf("Manzana", "Banana", "Cereza")
    println("\nLista de frutas:")
    for (fruta in frutas) {
        println(fruta)
    }

    /*
    También se puede acceder con índices:
    */

    println("\nFrutas con índices:")
    for (indice in frutas.indices) {
        println("Índice $indice: ${frutas[indice]}")
    }

    /*
    ────────────────────────────────────────────────────────
    Recorrer un Map (diccionario en Python)
    ────────────────────────────────────────────────────────
    Un Map es una estructura clave → valor. Se recorre así:
    */

    val capitales = mapOf(
        "Colombia" to "Bogotá",
        "México" to "Ciudad de México",
        "Argentina" to "Buenos Aires"
    )

    println("\nRecorrer Map:")
    for ((pais, capital) in capitales) {
        println("La capital de $pais es $capital")
    }

    /*
    ────────────────────────────────────────────────────────
    4. break y continue
    ────────────────────────────────────────────────────────

    - break → finaliza el bucle inmediatamente.
    - continue → salta al siguiente ciclo, omitiendo lo que resta.
    */

    println("\nUso de break:")
    for (n in 1..10) {
        if (n == 5) break  // rompe el bucle cuando n = 5
        println(n)
    }

    println("\nUso de continue:")
    for (n in 1..5) {
        if (n == 3) continue  // omite el valor 3
        println(n)
    }

    /*
    ────────────────────────────────────────────────────────
    BUCLES ANIDADOS
    ────────────────────────────────────────────────────────
    Es posible tener bucles dentro de otros bucles.

    Ejemplo: imprimir una tabla de multiplicar (1 al 3)
    */

    println("\nTabla de multiplicar (1 al 3):")
    for (x in 1..3) {
        for (y in 1..3) {
            print("${x * y} \t")
        }
        println()
    }
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:
────────────────────────────────────────────────────────────
- Los bucles permiten automatizar tareas repetitivas.
- `while` evalúa antes; `do...while` después (mínimo una vez).
- `for` es muy versátil: sirve con rangos, listas, arreglos, etc.
- Se pueden recorrer mapas (clave → valor) como los diccionarios en Python.
- `break` y `continue` modifican el flujo del bucle.
- Kotlin permite estructuras de bucles compactas y legibles.
────────────────────────────────────────────────────────────
*/
