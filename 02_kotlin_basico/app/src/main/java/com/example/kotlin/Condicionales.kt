package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 4: Condicionales en Kotlin
────────────────────────────────────────────────────────────

¿Qué son las estructuras condicionales?

Son herramientas del lenguaje que permiten que el flujo de ejecución
del programa cambie dependiendo de si ciertas condiciones son verdaderas
o falsas. Gracias a ellas, podemos hacer que el programa “pregunte” y
reaccione de diferente forma según el escenario.

En Kotlin existen principalmente dos mecanismos de condicionales:
- `if` / `else` / `else if`
- `when`

Además, en Kotlin los condicionales pueden usarse como **expresiones**
(es decir, que devuelven un valor), no solo como sentencias.

────────────────────────────────────────────────────────────
“if” – SENTENCIA Y EXPRESIÓN
────────────────────────────────────────────────────────────

1. If como sentencia (bloque de código que se ejecuta si la condición es true)

    if (condición) {
        // acciones si condición es verdadera
    }

2. If con else

    if (condición) {
        // acciones si condición es verdadera
    } else {
        // acciones si condición es falsa
    }

3. If con varios casos: else if

    if (cond1) {
        // acciones si cond1 es true
    } else if (cond2) {
        // acciones si cond2 es true (y cond1 era false)
    } else {
        // acciones si ninguna condición anterior se cumplió
    }

4. If como expresión (devuelve un valor). En este uso, el `else` es obligatorio.

    val resultado = if (cond) {
        valorSiTrue
    } else {
        valorSiFalse
    }

5. If como expresión en forma compacta (cuando solo una línea en cada rama):

    val mensaje = if (edad >= 18) "Adulto" else "Menor"

────────────────────────────────────────────────────────────
“when” – ALTERNATIVA MÁS ELEGANTE A IF / ELSE IF MÚLTIPLES
────────────────────────────────────────────────────────────

El `when` permite manejar múltiples casos de forma clara:

    when (valor) {
        caso1 -> acción1
        caso2 -> acción2
        else   -> acciónPorDefecto
    }

También se puede usar sin argumento, evaluando condiciones booleanas:

    when {
        cond1 -> acción1
        cond2 -> acción2
        else  -> acciónPorDefecto
    }

`when` también puede actuar como expresión que devuelve un valor.

────────────────────────────────────────────────────────────
EJEMPLIFICACIÓN COMPLETA
────────────────────────────────────────────────────────────
*/

fun main() {

    // ────────────────────────────
    // EJEMPLO “if / else / else if”
    // ────────────────────────────

    val hora = 15

    if (hora < 12) {
        println("Buenos días")
    } else if (hora < 18) {
        println("Buenas tardes")
    } else {
        println("Buenas noches")
    }

    // ────────────────────────────
    // If como expresión
    // ────────────────────────────

    val edad = 20
    val tipoPersona = if (edad >= 18) {
        "Adulto"
    } else {
        "Menor"
    }
    println("La persona es: $tipoPersona")

    // rama compacta (sin llaves)
    val tipoCorto = if (edad >= 18) "Adulto" else "Menor"
    println("Tipo (versión corta): $tipoCorto")

    // ────────────────────────────
    // Uso de `when` con valor
    // ────────────────────────────

    val dia = 3
    val nombreDia = when (dia) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6, 7 -> "Fin de semana"
        else -> "Día inválido"
    }
    println("Día $dia es $nombreDia")

    // ────────────────────────────
    // Uso de `when` sin argumento (con condiciones)
    // ────────────────────────────

    val temperatura = 28

    when {
        temperatura < 0 -> println("Hace mucho frío")
        temperatura in 0..20 -> println("Está fresco")
        temperatura in 21..30 -> println("Hace calor")
        else -> println("Hace mucho calor")
    }

    // ────────────────────────────
    // `when` como expresión que devuelve valor
    // ────────────────────────────

    val estado = "verde"
    val acción = when (estado) {
        "rojo" -> "Detener"
        "amarillo" -> "Precaución"
        "verde" -> "Avanzar"
        else -> "Estado desconocido"
    }
    println("Semáforo = $estado → acción: $acción")

    // ────────────────────────────
    // Uso combinado con `if` y `when`
    // ────────────────────────────

    val nota = 85
    val nivel = when {
        nota >= 90 -> "Excelente"
        nota >= 75 -> "Bueno"
        nota >= 50 -> "Regular"
        else -> "Insuficiente"
    }
    println("Nota: $nota → nivel: $nivel")
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:

- `if` es básico e indispensable: permite separar flujos según condiciones.
- En Kotlin, `if` puede actuar como expresión (devolver valor).
- `when` es más limpio para múltiples casos y puede usarse con o sin argumento.
- Ambos pueden usarse como expresiones, no solo como sentencias.
- Al usar `when`, puedes agrupar casos (ej. “6, 7 -> …”) o usar rangos.
- Las condicionales permiten que tu programa no sea lineal, sino que responda a distintos escenarios.

────────────────────────────────────────────────────────────
*/
