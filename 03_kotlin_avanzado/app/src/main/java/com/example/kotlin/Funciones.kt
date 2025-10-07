package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 2 (KOTLIN AVANZADO):
FUNCIONES AVANZADAS Y PROGRAMACIÓN FUNCIONAL EN KOTLIN
────────────────────────────────────────────────────────────

En este capítulo se estudian los conceptos avanzados relacionados
con las funciones en Kotlin, incluyendo el uso de funciones anónimas,
sobrecarga, funciones de orden superior y otras herramientas que
permiten escribir código más expresivo y reutilizable.

────────────────────────────────────────────────────────────
1. SOBRECARGA DE FUNCIONES (FUNCTION OVERLOADING)
────────────────────────────────────────────────────────────
Kotlin permite definir varias funciones con el mismo nombre,
siempre y cuando sus parámetros sean diferentes en tipo o cantidad.

Esto se conoce como **sobrecarga de funciones**.
*/

fun sumar(a: Int, b: Int): Int {
    return a + b
}

fun sumar(a: Double, b: Double): Double {
    return a + b
}

fun sumar(a: Int, b: Int, c: Int): Int {
    return a + b + c
}

/*
La sobrecarga mejora la legibilidad y permite usar el mismo nombre
para operaciones conceptualmente similares.
────────────────────────────────────────────────────────────
2. FUNCIONES CON NÚMERO VARIABLE DE ARGUMENTOS (vararg)
────────────────────────────────────────────────────────────
La palabra clave `vararg` permite recibir una cantidad variable
de parámetros del mismo tipo.
*/

fun promedio(vararg numeros: Double): Double {
    var suma = 0.0
    for (n in numeros) {
        suma += n
    }
    return if (numeros.isNotEmpty()) suma / numeros.size else 0.0
}

/*
`vararg` facilita operaciones sobre listas de valores sin necesidad
de crear un arreglo explícito.
────────────────────────────────────────────────────────────
3. FUNCIONES LAMBDA (FUNCIONES ANÓNIMAS)
────────────────────────────────────────────────────────────
Una **lambda** es una función sin nombre (anónima) que puede
asignarse a una variable o pasarse como argumento.

Sintaxis básica:
    val nombre = { parámetros -> cuerpo }

Ejemplo:
    val saludar = { nombre: String -> println("Hola, $nombre") }
*/

val saludar = { nombre: String -> println("Hola, $nombre") }

/*
Las lambdas son expresiones, por lo que pueden devolver valores
de forma implícita (la última línea es el resultado).
────────────────────────────────────────────────────────────
4. FUNCIONES DE ORDEN SUPERIOR
────────────────────────────────────────────────────────────
Una **función de orden superior** es aquella que recibe otra función
como parámetro, o devuelve una función como resultado.

Esto permite una programación más modular y expresiva.
*/

fun operar(a: Int, b: Int, operacion: (Int, Int) -> Int): Int {
    return operacion(a, b)
}

/*
Ejemplo de uso:
    operar(5, 3, { x, y -> x + y })
    operar(10, 2) { x, y -> x * y }
────────────────────────────────────────────────────────────
5. REFERENCIAS DE FUNCIÓN (::)
────────────────────────────────────────────────────────────
Si ya existe una función definida, se puede pasar como parámetro
usando el operador de referencia `::`.

Ejemplo:
    ::sumar   → referencia a la función sumar()
*/

fun multiplicar(a: Int, b: Int): Int = a * b

/*
────────────────────────────────────────────────────────────
6. FUNCIONES INLINE
────────────────────────────────────────────────────────────
La palabra clave `inline` sugiere al compilador que copie el código
de la función en cada llamada, en lugar de generar una llamada normal.

Esto mejora el rendimiento cuando se usan muchas funciones pequeñas
(lambdas o de orden superior).
*/

inline fun ejecutarOperacion(a: Int, b: Int, operacion: (Int, Int) -> Int): Int {
    return operacion(a, b)
}

/*
────────────────────────────────────────────────────────────
7. FUNCIONES FOR EACH, MAP Y FILTER
────────────────────────────────────────────────────────────
Las colecciones en Kotlin incluyen funciones que aplican lambdas
a sus elementos.

- forEach → ejecuta una acción sobre cada elemento.
- map → transforma cada elemento y devuelve una nueva lista.
- filter → selecciona elementos que cumplan una condición.
*/

fun ejemploColecciones() {
    val numeros = listOf(1, 2, 3, 4, 5)

    println("forEach:")
    numeros.forEach { println(it) }

    println("\nmap:")
    val cuadrados = numeros.map { it * it }
    println(cuadrados)

    println("\nfilter:")
    val pares = numeros.filter { it % 2 == 0 }
    println(pares)
}

/*
────────────────────────────────────────────────────────────
8. FUNCIONES CON ÁMBITO DE APLICACIÓN (run, let, apply, also, with)
────────────────────────────────────────────────────────────
Estas funciones permiten ejecutar bloques de código sobre un objeto
sin necesidad de repetir su nombre.

- `let` → se usa para ejecutar código sobre un objeto no nulo.
- `run` → combina inicialización y retorno de un valor.
- `apply` → configura propiedades de un objeto.
- `also` → ejecuta una acción adicional (side effect).
- `with` → similar a run, pero recibe el objeto como argumento.
*/

data class Usuario(var nombre: String, var edad: Int)

fun ejemploScopeFunctions() {
    val usuario = Usuario("Carlos", 30)

    // apply: configurar propiedades
    usuario.apply {
        nombre = "Andrés"
        edad = 28
    }

    // let: ejecutar si no es nulo
    usuario.let {
        println("Usuario: ${it.nombre}, Edad: ${it.edad}")
    }

    // run: ejecutar operaciones y devolver resultado
    val mensaje = usuario.run {
        "Nombre en mayúsculas: ${nombre.uppercase()}"
    }
    println(mensaje)

    // also: realizar acción sin modificar el objeto
    usuario.also {
        println("Objeto revisado: $it")
    }
}

/*
────────────────────────────────────────────────────────────
EJECUCIÓN Y DEMOSTRACIÓN COMPLETA
────────────────────────────────────────────────────────────
*/

fun main() {

    println("────────────────────────────────────")
    println("EJEMPLOS DE FUNCIONES AVANZADAS EN KOTLIN")
    println("────────────────────────────────────\n")

    // Sobrecarga
    println("Suma Int: ${sumar(4, 5)}")
    println("Suma Double: ${sumar(3.5, 2.5)}")
    println("Suma Triple: ${sumar(1, 2, 3)}")

    // vararg
    println("\nPromedio vararg: ${promedio(4.0, 6.0, 8.0, 10.0)}")

    // Lambda
    saludar("Laura")

    // Función de orden superior
    val resultadoSuma = operar(5, 3) { x, y -> x + y }
    val resultadoMultiplicacion = operar(5, 3, ::multiplicar)
    println("\nOrden superior:")
    println("Suma = $resultadoSuma, Multiplicación = $resultadoMultiplicacion")

    // Inline
    val res = ejecutarOperacion(10, 2) { a, b -> a / b }
    println("\nResultado inline: $res")

    // ForEach, map, filter
    println()
    ejemploColecciones()

    // Scope functions
    println()
    ejemploScopeFunctions()
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:
────────────────────────────────────────────────────────────
1. Kotlin permite **sobrecargar funciones** con distintos parámetros.
2. `vararg` admite un número variable de argumentos.
3. Las **lambdas** son funciones anónimas que pueden asignarse o pasarse.
4. Las **funciones de orden superior** aceptan o retornan funciones.
5. El operador `::` permite pasar referencias a funciones existentes.
6. Las funciones **inline** optimizan el rendimiento de lambdas frecuentes.
7. Las colecciones ofrecen funciones funcionales (`forEach`, `map`, `filter`).
8. Las **funciones de ámbito** (`let`, `run`, `apply`, `also`, `with`)
   simplifican la manipulación de objetos.
9. Kotlin integra la programación funcional de manera natural y concisa.

────────────────────────────────────────────────────────────
*/
