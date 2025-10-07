package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 7: FUNCIONES EN KOTLIN
────────────────────────────────────────────────────────────

¿Qué es una función?

Una **función** es un bloque de código reutilizable que realiza una tarea
específica. Las funciones permiten dividir el programa en partes lógicas
más pequeñas, facilitando la organización, legibilidad y mantenimiento del código.

En Kotlin, todas las funciones se definen con la palabra clave `fun`.

────────────────────────────────────────────────────────────
SINTAXIS GENERAL
────────────────────────────────────────────────────────────

fun nombreFuncion(parámetros): TipoDeRetorno {
    // cuerpo de la función
    return valor
}

Ejemplo simple:

fun saludar() {
    println("Hola desde una función")
}

────────────────────────────────────────────────────────────
1. FUNCIONES SIN PARÁMETROS Y SIN RETORNO
────────────────────────────────────────────────────────────
Cuando una función no recibe datos ni devuelve un valor, su tipo de retorno
es implícitamente `Unit` (equivalente a `void` en otros lenguajes).
*/

fun saludar() {
    println("Hola, bienvenido al curso de Kotlin")
}

/*
────────────────────────────────────────────────────────────
2. FUNCIONES CON PARÁMETROS
────────────────────────────────────────────────────────────
Los parámetros permiten enviar datos a la función.

Cada parámetro debe declararse con su nombre y tipo:
*/

fun mostrarEdad(nombre: String, edad: Int) {
    println("$nombre tiene $edad años")
}

/*
────────────────────────────────────────────────────────────
3. FUNCIONES CON RETORNO
────────────────────────────────────────────────────────────
Las funciones pueden devolver un valor utilizando la palabra clave `return`.
Se debe especificar el tipo de dato que se devuelve.
*/

fun sumar(a: Int, b: Int): Int {
    return a + b
}

/*
────────────────────────────────────────────────────────────
4. FUNCIONES COMO EXPRESIONES (UNA SOLA LÍNEA)
────────────────────────────────────────────────────────────
Si el cuerpo de la función tiene una sola instrucción, se puede escribir
en forma compacta, sin llaves ni `return` explícito.
*/

fun restar(a: Int, b: Int): Int = a - b

/*
────────────────────────────────────────────────────────────
5. PARÁMETROS CON VALOR POR DEFECTO
────────────────────────────────────────────────────────────
Una función puede definir valores por defecto para sus parámetros.
Si no se pasa ese argumento, se usa el valor predeterminado.
*/

fun saludarUsuario(nombre: String = "Invitado") {
    println("Hola, $nombre!")
}

/*
────────────────────────────────────────────────────────────
6. PARÁMETROS NOMBRADOS
────────────────────────────────────────────────────────────
Kotlin permite especificar los parámetros por nombre al llamar la función,
lo que mejora la claridad cuando hay varios argumentos.
*/

fun presentarPersona(nombre: String, edad: Int, ciudad: String) {
    println("$nombre tiene $edad años y vive en $ciudad")
}

/*
────────────────────────────────────────────────────────────
7. MÚLTIPLES PARÁMETROS Y LLAMADAS ANIDADAS
────────────────────────────────────────────────────────────
Una función puede recibir varios argumentos y también llamar a otras funciones.
*/

fun calcularPromedio(a: Double, b: Double, c: Double): Double {
    val suma = a + b + c
    return suma / 3
}

/*
────────────────────────────────────────────────────────────
8. FUNCIONES ANIDADAS
────────────────────────────────────────────────────────────
Podemos definir una función dentro de otra.
La función interna solo puede usarse dentro de la externa.
*/

fun procesoPrincipal() {

    fun pasoInterno() {
        println("Ejecutando paso interno...")
    }

    println("Inicio del proceso principal")
    pasoInterno()  // llamada a la función interna
    println("Fin del proceso principal")
}

/*
────────────────────────────────────────────────────────────
9. USO DE `Unit` Y FUNCIONES SIN RETORNO
────────────────────────────────────────────────────────────
Cuando una función no devuelve valor, su tipo de retorno es `Unit`.
No es necesario escribirlo explícitamente, aunque se puede:
*/

fun mostrarMensaje(): Unit {
    println("Esta función no devuelve ningún valor")
}

/*
────────────────────────────────────────────────────────────
EJECUCIÓN Y DEMOSTRACIÓN COMPLETA
────────────────────────────────────────────────────────────
*/

fun main() {

    println("────────────────────────────────────")
    println("EJEMPLOS DE FUNCIONES EN KOTLIN")
    println("────────────────────────────────────\n")

    // 1. Sin parámetros
    saludar()

    // 2. Con parámetros
    mostrarEdad("Laura", 25)

    // 3. Con retorno
    val resultadoSuma = sumar(10, 5)
    println("La suma es: $resultadoSuma")

    // 4. Función como expresión
    val resultadoResta = restar(20, 7)
    println("La resta es: $resultadoResta")

    // 5. Parámetro con valor por defecto
    saludarUsuario()
    saludarUsuario("Edward")

    // 6. Parámetros nombrados
    presentarPersona(nombre = "Carlos", edad = 30, ciudad = "Cali")

    // 7. Función con varias operaciones
    val promedio = calcularPromedio(4.5, 3.8, 5.0)
    println("El promedio es: $promedio")

    // 8. Función anidada
    procesoPrincipal()

    // 9. Función sin retorno explícito
    mostrarMensaje()
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:
────────────────────────────────────────────────────────────
- Una función se declara con `fun` seguida del nombre y sus parámetros.
- Si no devuelve valor, su tipo de retorno es `Unit` (puede omitirse).
- Puede devolver valores usando `return` y declarando el tipo de salida.
- Se pueden usar **valores por defecto** para parámetros.
- Los **parámetros nombrados** facilitan la lectura de las llamadas.
- Kotlin permite **funciones de una sola línea**, ideales para operaciones simples.
- También es posible definir **funciones dentro de otras** (anidadas).
- Las funciones mejoran la modularidad, legibilidad y reuso del código.

────────────────────────────────────────────────────────────
*/
