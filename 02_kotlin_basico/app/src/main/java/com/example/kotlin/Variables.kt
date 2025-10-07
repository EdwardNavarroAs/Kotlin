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

    /*
    ──────────────────────────────────────────────
    VARIABLES AVANZADAS: GLOBALES, CONSTANTES Y ASIGNACIÓN TARDÍA
    ──────────────────────────────────────────────

    En Kotlin puedes declarar variables fuera de cualquier función.
    Estas se llaman variables top-level (globales para ese archivo).

    - `const val`: crea una constante en tiempo de compilación.
      Solo se permite a nivel top-level o dentro de objetos/companion.
    - `by lazy`: inicializa un valor inmutable solo cuando se usa.
    - `lateinit var`: inicializa más tarde una variable mutable no nula.
      Solo es válido en propiedades de clase, no en variables locales.
    */

    println("\nEjemplo de constante global y variable global:")
    println("Máximo de usuarios permitidos: $MAX_USUARIOS")
    contadorGlobal++
    println("Contador global actualizado: $contadorGlobal")

    println("\nEjemplo de inicialización perezosa (by lazy):")
    println("Antes de usar configCargada")
    println("configCargada = $configCargada") // aquí se inicializa por primera vez

    println("\nEjemplo de lateinit var (en clase):")
    val usuario = Usuario()
    usuario.inicializarNombre("Pedro")
    println("Usuario lateinit: ${usuario.nombre}")
}

/*
──────────────────────────────────────────────
Variables globales y constantes declaradas fuera de main()
──────────────────────────────────────────────
*/

// Constante de tiempo de compilación (solo top-level u objetos)
const val MAX_USUARIOS = 100

// Variable global mutable (evitar en proyectos grandes si no es necesario)
var contadorGlobal = 0

// Variable global inmutable (se asigna una sola vez)
val nombreAplicacion = "MiApp"

// Inicialización perezosa global (no se evalúa hasta que se use)
val configCargada: String by lazy {
    println("Cargando configuración global...")
    "Configuración lista"
}

// Clase para demostrar lateinit var (asignación tardía)
class Usuario {
    // lateinit solo se permite en propiedades mutables (var) no nulas
    lateinit var nombre: String
    fun inicializarNombre(valor: String) {
        nombre = valor // asignación tardía
    }
}

/*
──────────────────────────────────────────────
CONCLUSIONES:
──────────────────────────────────────────────
- `val`: valor inmutable (una sola asignación).
- `var`: valor mutable (puede cambiar).
- Variables top-level: se declaran fuera de funciones y son globales para ese archivo.
- `const val`: crea constantes en tiempo de compilación (solo top-level u objetos).
- `by lazy`: inicialización diferida para valores inmutables.
- `lateinit var`: inicialización tardía para variables mutables (en clases).
- Buenas prácticas: usar constantes y val cuando sea posible, y variables globales con moderación.
*/
