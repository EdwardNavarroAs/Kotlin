package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 2: Tipos de Datos en Kotlin
────────────────────────────────────────────────────────────

¿QUÉ ES UN TIPO DE DATO?

Los tipos de datos (data types) determinan qué clase de información puede
almacenarse en una variable: números, texto, booleanos, etc.

Kotlin es un lenguaje fuertemente tipado, lo que significa que el tipo
de cada variable está definido (de forma explícita o inferida).

────────────────────────────────────────────────────────────
TIPOS DE DATOS PRIMARIOS EN KOTLIN
────────────────────────────────────────────────────────────

1. NÚMEROS ENTEROS
─────────────────────────────────────────────
- Byte  → 8 bits      → Rango: -128 a 127
- Short → 16 bits     → Rango: -32.768 a 32.767
- Int   → 32 bits     → Rango: ±2 mil millones (valor por defecto)
- Long  → 64 bits     → Se usa sufijo `L` (ej. 100000L)

2. NÚMEROS DECIMALES (PUNTO FLOTANTE)
─────────────────────────────────────────────
- Float  → 32 bits → Menor precisión (~6-7 dígitos decimales). Requiere sufijo `f`.
- Double → 64 bits → Mayor precisión (~15-16 dígitos). Valor por defecto para decimales.

DIFERENCIA ENTRE FLOAT Y DOUBLE:
- Float → Menor precisión, menos memoria. Útil para sensores, gráficos simples.
- Double → Mayor precisión, recomendado para cálculos matemáticos.

3. BOOLEANOS
─────────────────────────────────────────────
- Boolean → Solo dos posibles valores: `true` o `false`

4. CARACTERES
─────────────────────────────────────────────
- Char → Representa un único carácter. Se declara con comillas simples: 'A'

5. STRINGS (CADENAS DE TEXTO)
─────────────────────────────────────────────
- String → Cadena de caracteres entre comillas dobles: "Hola mundo"
           Kotlin lo trata como un objeto inmutable.

────────────────────────────────────────────────────────────
EJEMPLOS PRÁCTICOS
────────────────────────────────────────────────────────────
*/

fun main() {

    // ────────────────
    // NÚMEROS ENTEROS
    // ────────────────
    val edad: Int = 30
    val dias: Byte = 100
    val maximo: Short = 32_000
    val poblacion: Long = 7_800_000_000L // sufijo obligatorio para Long

    // ────────────────
    // DECIMALES (Float y Double)
    // ────────────────
    val temperatura: Float = 36.6f   // sufijo f necesario
    val pi: Double = 3.1415926535   // mayor precisión

    println("Temperatura (Float): $temperatura")
    println("Valor de Pi (Double): $pi")

    // ────────────────
    // BOOLEANOS
    // ────────────────
    val esEstudiante: Boolean = true
    val tieneTrabajo: Boolean = false
    println("¿Es estudiante?: $esEstudiante - ¿Tiene trabajo?: $tieneTrabajo")

    // ────────────────
    // CARACTERES
    // ────────────────
    val letra: Char = 'K'
    println("Letra inicial: $letra")

    // ────────────────
    // STRINGS (CADENAS DE TEXTO)
    // ────────────────

    // Declaración
    val nombre: String = "Kotlin"
    println("Nombre del lenguaje: $nombre")

    // Concatenación
    val saludo = "Hola, " + nombre
    println(saludo)

    // Interpolación de cadenas
    val edadUsuario = 25
    println("Edad del usuario: $edadUsuario años")

    // Acceder a propiedades
    println("Longitud del texto '$nombre': ${nombre.length}")

    // Literales multilínea con triple comillas
    val textoLargo = """
        Este es un texto
        que ocupa varias líneas.
        Se usa """ + "\"\"\"" + """ para definirlo.
    """.trimIndent()
    println(textoLargo)

    // Funciones comunes sobre strings
    println(nombre.uppercase()) // "KOTLIN"
    println(nombre.lowercase()) // "kotlin"
    println(nombre.contains("lin")) // true

    // ────────────────
    // INFERENCIA DE TIPO
    // ────────────────
    val ciudad = "Popayán" // Kotlin infiere: String
    val altura = 1.78       // Kotlin infiere: Double
    val activo = true       // Boolean

    println("Ciudad: $ciudad - Altura: $altura - ¿Activo?: $activo")

    // ────────────────
    // CONVERSIÓN EXPLÍCITA DE TIPOS
    // ────────────────

    val numeroInt = 42
    val numeroDouble = numeroInt.toDouble()
    val textoNumerico = "123"
    val convertido = textoNumerico.toInt()

    println("Int → Double: $numeroDouble")
    println("String → Int: $convertido")

    // Kotlin NO permite conversiones implícitas como en otros lenguajes

    // ────────────────
    // CONVERSIÓN DE CHAR A INT (ASCII)
    // ────────────────
    val caracter = 'A'
    val codigoAscii = caracter.code
    println("Carácter '$caracter' → Código ASCII: $codigoAscii")
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES DEL CAPÍTULO
────────────────────────────────────────────────────────────

- Kotlin es fuertemente tipado: cada variable tiene un tipo definido.
- Los números enteros y decimales tienen tamaños y precisiones distintas.
- Float y Double son distintos en precisión. Double es el más usado por defecto.
- Strings son objetos inmutables que se pueden concatenar e interpolar.
- El compilador puede inferir el tipo, pero también se puede declarar explícitamente.
- Las conversiones de tipos deben hacerse de forma explícita (por ejemplo: toInt()).
- Kotlin incluye herramientas útiles como `.uppercase()`, `.length`, `.contains()`.

────────────────────────────────────────────────────────────
*/
