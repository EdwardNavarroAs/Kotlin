package com.example.kotlin

/*
──────────────────────────────────────────────
 CAPÍTULO 1: Variables y Sintaxis Básica en Kotlin
──────────────────────────────────────────────

 ¿Qué es Kotlin?
Kotlin es un lenguaje de programación moderno, conciso, seguro y 100% interoperable con Java.
Fue desarrollado por JetBrains y ahora es oficialmente recomendado por Google para el desarrollo de aplicaciones Android.

 Características clave:
- Sintaxis clara y concisa.
- Compatible con Java.
- Orientado a objetos y funcional.
- Seguridad frente a valores nulos (null safety).
- Soporte oficial de Android Studio.

 ¿Dónde se usa Kotlin?
- Desarrollo Android.
- Aplicaciones de backend (con frameworks como Ktor o Spring).
- Aplicaciones web con Kotlin/JS.
- Aplicaciones multiplataforma (Kotlin Multiplatform).

──────────────────────────────────────────────
Sintaxis básica de Kotlin
──────────────────────────────────────────────

- Las funciones comienzan con la palabra clave `fun`.
- No se necesita punto y coma `;` al final de las líneas (aunque es válido).
- Las variables se definen con `val` (inmutable) o `var` (mutable).
- Los tipos se infieren automáticamente, aunque puedes especificarlos.

Ejemplo básico:
*/

fun main() {
    println("¡Hola, Kotlin!") // Imprime un saludo en consola

    /*
    ──────────────────────────────────────────────
    VARIABLES EN KOTLIN
    ──────────────────────────────────────────────

    val → Valor inmutable (como una constante)
    var → Valor mutable (puede cambiar)

    Kotlin infiere automáticamente el tipo, pero también puedes declararlo explícitamente.
    */

    // Declaración con inferencia de tipo
    val nombre = "Ana"              // Tipo String
    val edad = 30                   // Tipo Int
    val altura = 1.65               // Tipo Double
    val esEstudiante = true         // Tipo Boolean

    println("Nombre: $nombre")
    println("Edad: $edad")
    println("Altura: $altura m")
    println("¿Es estudiante? $esEstudiante")

    // Declaración con tipo explícito
    val apellido: String = "Pérez"
    val peso: Float = 60.5f
    val inicial: Char = 'A'

    println("Apellido: $apellido")
    println("Peso: $peso kg")
    println("Inicial: $inicial")

    // Variable mutable
    var contador = 10
    println("Contador inicial: $contador")
    contador = 15
    println("Contador actualizado: $contador")

    /*
    ──────────────────────────────────────────────
    CONCATENACIÓN Y TEMPLATE STRINGS
    ──────────────────────────────────────────────

    Puedes unir texto con el operador `+`, o usar strings interpolados con `\$`.
    */

    val producto = "Café"
    val precio = 2.5

    println("Producto: " + producto + " - Precio: " + precio)
    println("Producto: $producto cuesta $precio euros")

    /*
    ──────────────────────────────────────────────
    CONVERSIÓN DE TIPOS (Type Casting)
    ──────────────────────────────────────────────

    Kotlin no realiza conversiones automáticas entre tipos numéricos.
    Debes usar funciones como `toInt()`, `toFloat()`, etc.
    */

    val numeroComoString = "42"
    val numeroConvertido = numeroComoString.toInt()
    println("Número convertido: $numeroConvertido")

    val doubleAInt = 3.14.toInt()
    println("Double convertido a Int: $doubleAInt")

    /*
    Si intentas convertir un String no numérico con toInt(), dará error.
    Ejemplo:
    val texto = "Hola"
    val falla = texto.toInt() // Generará excepción NumberFormatException
    */

    /*
    ──────────────────────────────────────────────
    BONUS: NULL SAFETY (Seguridad frente a nulos)
    ──────────────────────────────────────────────

    Kotlin obliga a manejar los valores nulos.
    Una variable puede ser nullable si se declara con `?`
    */

    var nombreOpcional: String? = null
    println("Nombre opcional: $nombreOpcional")

    nombreOpcional = "Carlos"
    println("Nombre actualizado: ${nombreOpcional?.uppercase()}") // Safe call operator

    // También puedes usar el operador Elvis `?:`
    val longitudNombre = nombreOpcional?.length ?: 0
    println("Longitud del nombre: $longitudNombre")
}

