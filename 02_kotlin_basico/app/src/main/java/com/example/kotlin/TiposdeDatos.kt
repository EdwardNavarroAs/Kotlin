package com.example.kotlin

/*
────────────────────────────────────────────────────────────
 CAPÍTULO 2: Tipos de Datos en Kotlin
────────────────────────────────────────────────────────────

 ¿Qué es un tipo de dato?
Los tipos de datos (data types) determinan qué tipo de valores pueden
almacenarse en una variable. Kotlin es un lenguaje fuertemente tipado,
lo que significa que cada variable debe tener un tipo definido
(o inferido por el compilador).

────────────────────────────────────────────────────────────
 Tipos de Datos Primarios en Kotlin:
────────────────────────────────────────────────────────────

▶ Números Enteros:
- Byte (8 bits): -128 a 127
- Short (16 bits): -32,768 a 32,767
- Int (32 bits): el más usado por defecto
- Long (64 bits): usa sufijo L → ejemplo: 100000L

▶ Números Decimales:
- Float (32 bits con decimales, menos preciso, sufijo `f`)
- Double (64 bits con decimales, más preciso, usado por defecto)

  DIFERENCIA CLAVE:
- **Float**: ocupa menos memoria (32 bits) pero tiene menos precisión decimal
  (aprox. 6-7 cifras decimales).
- **Double**: ocupa más memoria (64 bits) y tiene mayor precisión decimal
  (aprox. 15-16 cifras decimales). Se usa por defecto en Kotlin.

▶ Boolean:
- Solo puede tener dos valores: `true` o `false`

▶ Char:
- Representa un solo carácter entre comillas simples, ej: 'A'

▶ String:
- Cadena de texto entre comillas dobles: "Hola Kotlin"

────────────────────────────────────────────────────────────
Ejemplos:
────────────────────────────────────────────────────────────
*/

fun main() {
    // ───────────────────────────────────────────────
    // NÚMEROS ENTEROS
    // ───────────────────────────────────────────────
    val edad: Int = 30
    val poblacion: Long = 7_800_000_000L // L obligatorio para Long
    val dias: Byte = 100
    val maximo: Short = 32_000

    // ───────────────────────────────────────────────
    // NÚMEROS DECIMALES (FLOAT vs DOUBLE)
    // ───────────────────────────────────────────────
    val temperatura: Float = 36.6f // Float (32 bits) requiere la 'f'
    val pi: Double = 3.141592653589793 // Double (64 bits, más precisión)

    println("Temperatura (Float): $temperatura")
    println("Pi (Double): $pi")

    // ───────────────────────────────────────────────
    // BOOLEANOS
    // ───────────────────────────────────────────────
    val esEstudiante: Boolean = true
    val tieneTrabajo: Boolean = false
    println("¿Estudiante? $esEstudiante - ¿Trabaja? $tieneTrabajo")

    // ───────────────────────────────────────────────
    // CARACTER Y STRING
    // ───────────────────────────────────────────────
    val letra: Char = 'K'
    val nombre: String = "Kotlin"
    println("Letra inicial: $letra - Lenguaje: $nombre")

    // ───────────────────────────────────────────────
    // INFERENCIA DE TIPO
    // ───────────────────────────────────────────────
    val ciudad = "Popayán"     // String
    val decimal = 24.5         // Double por defecto
    val activo = true          // Boolean

    println("Ciudad: $ciudad | Decimal: $decimal | ¿Activo?: $activo")

    // ───────────────────────────────────────────────
    //  CONVERSIÓN DE TIPOS
    // ───────────────────────────────────────────────
    val numeroInt = 10
    val numeroDouble = numeroInt.toDouble()
    val stringToInt = "123".toInt()

    println("Int → Double: $numeroDouble")
    println("String → Int: $stringToInt")

    //  Kotlin no convierte automáticamente entre tipos (como en otros lenguajes)

    // ───────────────────────────────────────────────
    // TRUCO: CHAR → ASCII
    // ───────────────────────────────────────────────
    val caracter = 'A'
    val ascii = caracter.code
    println("Carácter: $caracter → Código ASCII: $ascii")
}

/*
────────────────────────────────────────────────────────────
 CONCLUSIONES:
────────────────────────────────────────────────────────────
- Kotlin tiene una amplia variedad de tipos de datos.
- **Float** (32 bits) → menos preciso, usar cuando no necesitas alta precisión.
- **Double** (64 bits) → más preciso, se usa por defecto.
- El tipo Int es el entero más común.
- La inferencia automática facilita el desarrollo.
- Las conversiones de tipo deben ser explícitas (no automáticas).
*/
