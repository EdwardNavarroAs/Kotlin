package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 7: Manejo de Errores en Kotlin (Try, Catch, Throw)
────────────────────────────────────────────────────────────

📌 ¿Qué es un error (excepción)?

Un error en tiempo de ejecución que detiene el programa se conoce
como una "excepción". Kotlin, como otros lenguajes modernos, permite
manejar estos errores para evitar que el programa se detenga
inesperadamente.

Kotlin provee mecanismos como:
- try / catch / finally
- throw (para lanzar errores)
- creación de excepciones personalizadas

────────────────────────────────────────────────────────────
1. try - catch
────────────────────────────────────────────────────────────

La forma básica de manejar errores es mediante un bloque `try` seguido
de uno o más bloques `catch`. Esto permite "atrapar" excepciones
y continuar con la ejecución.

Sintaxis:

try {
    // Código que puede lanzar un error
} catch (e: TipoDeExcepcion) {
    // Código que se ejecuta si ocurre ese error
}

────────────────────────────────────────────────────────────
2. finally (opcional)
────────────────────────────────────────────────────────────

Se ejecuta siempre, ocurra o no una excepción. Ideal para liberar
recursos (archivos, conexiones, etc).

try {
    // ...
} catch (...) {
    // ...
} finally {
    // Siempre se ejecuta
}

────────────────────────────────────────────────────────────
3. throw (lanzar errores)
────────────────────────────────────────────────────────────

Se utiliza para lanzar errores manualmente dentro del código.

Sintaxis:

throw NombreDeExcepcion("Mensaje descriptivo")

────────────────────────────────────────────────────────────
*/

fun main() {

    // ───────────────────────────────────────────────
    // EJEMPLO 1: try-catch con división por cero
    // ───────────────────────────────────────────────
    println("Ejemplo 1: División segura")

    try {
        val numerador = 10
        val denominador = 0
        val resultado = numerador / denominador
        println("Resultado: $resultado")
    } catch (e: ArithmeticException) {
        println("Error: División por cero no permitida.")
    }

    // ───────────────────────────────────────────────
    // EJEMPLO 2: finally
    // ───────────────────────────────────────────────
    println("\nEjemplo 2: finally siempre se ejecuta")

    try {
        val lista = listOf(1, 2, 3)
        println(lista[5]) // Esto lanza IndexOutOfBoundsException
    } catch (e: IndexOutOfBoundsException) {
        println("Error: índice fuera de rango.")
    } finally {
        println("Este bloque se ejecuta siempre.")
    }

    // ───────────────────────────────────────────────
    // EJEMPLO 3: throw personalizado
    // ───────────────────────────────────────────────
    println("\nEjemplo 3: uso de throw")

    val edad = -5
    try {
        if (edad < 0) {
            throw IllegalArgumentException("La edad no puede ser negativa.")
        }
        println("Edad válida: $edad")
    } catch (e: IllegalArgumentException) {
        println("Excepción lanzada: ${e.message}")
    }

    // ───────────────────────────────────────────────
    // EJEMPLO 4: Capturar excepción genérica
    // ───────────────────────────────────────────────
    println("\nEjemplo 4: captura genérica")


    try {
        val nombre: String? = null
        println(nombre!!.length) // Forzamos el error
    } catch (e: Exception) {
        println("Error detectado: ${e.javaClass.simpleName} - ${e.message}")
    }


    // ───────────────────────────────────────────────
    // EJEMPLO 5: Excepción personalizada
    // ───────────────────────────────────────────────

    println("\nEjemplo 5: excepción personalizada")

    try {
        verificarEdad(15)
    } catch (e: EdadNoPermitidaException) {
        println("Excepción capturada: ${e.message}")
    }
}

/*
Función que lanza una excepción personalizada si la edad < 18
*/
fun verificarEdad(edad: Int) {
    if (edad < 18) {
        throw EdadNoPermitidaException("Debes ser mayor de edad para continuar.")
    }
    println("Acceso permitido.")
}

/*
CLASE DE EXCEPCIÓN PERSONALIZADA
Hereda de Exception y permite definir errores propios.
*/
class EdadNoPermitidaException(mensaje: String) : Exception(mensaje)

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:
────────────────────────────────────────────────────────────

- Kotlin permite manejar errores de forma estructurada con try/catch.
- `finally` es útil para cerrar recursos o asegurar que algo siempre ocurra.
- Se pueden lanzar excepciones con `throw` para validar condiciones manuales.
- Kotlin permite definir tus propias clases de excepción para manejar casos específicos.
- Las excepciones ayudan a prevenir que el programa se caiga de forma abrupta.

COMPARACIÓN CON PYTHON:
- Python usa `try`, `except`, `finally` con lógica muy similar.
- Kotlin requiere declarar explícitamente el tipo de excepción a capturar.
- Kotlin tiene tipado fuerte y no permite pasar errores sin controlarlos.

────────────────────────────────────────────────────────────
*/
