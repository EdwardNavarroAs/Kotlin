package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 7 (KOTLIN AVANZADO)
FUNCIONES ESPECIALES Y UTILITARIAS EN KOTLIN
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
- Aprender las funciones nativas más importantes del lenguaje Kotlin.
- Comprender `require`, `check`, `assert` y sus diferencias.
- Usar funciones de alcance (`let`, `run`, `apply`, `also`, `with`) correctamente.
- Conocer funciones auxiliares: `takeIf`, `takeUnless`, `repeat`, `lazy`.
- Mejorar la claridad, legibilidad y seguridad del código Kotlin.

────────────────────────────────────────────────────────────
1. FUNCIONES DE VALIDACIÓN
────────────────────────────────────────────────────────────

Estas funciones aseguran condiciones antes o durante la ejecución.
Si no se cumplen, lanzan excepciones específicas.
*/

fun demoRequireCheckAssert() {
    println("────────────────────────────────────")
    println("DEMO: require(), check() y assert()")
    println("────────────────────────────────────")

    val edad = 20
    val saldo = 500.0

    // require(): valida argumentos o entradas de funciones
    require(edad >= 18) { "Debe ser mayor de edad" }

    // check(): valida estado interno del programa
    check(saldo >= 0) { "El saldo no puede ser negativo" }

    // assert(): se usa en depuración (puede desactivarse en producción)
    assert(edad > 0) { "La edad debe ser positiva" }

    println("Validaciones completadas correctamente.")
}

/*
────────────────────────────────────────────────────────────
2. FUNCIONES DE ALCANCE (Scope Functions)
────────────────────────────────────────────────────────────
Permiten ejecutar bloques sobre un objeto sin repetir su nombre.

Resumen rápido:

| Función | Devuelve | Contexto | Uso común |
|----------|-----------|----------|------------|
| let      | Resultado lambda | it | Transformaciones seguras, null safety |
| run      | Resultado lambda | this | Ejecutar bloques sobre un objeto |
| apply    | El mismo objeto | this | Configurar propiedades (builders) |
| also     | El mismo objeto | it | Efectos secundarios (logging, debug) |
| with     | Resultado lambda | this | Usar un objeto sin repetir su nombre |

────────────────────────────────────────────────────────────
*/

data class Usuario(var nombre: String, var correo: String)

fun demoScopeFunctions() {
    println("────────────────────────────────────")
    println("DEMO: funciones de alcance")
    println("────────────────────────────────────")

    val usuario = Usuario("Laura", "laura@mail.com")

    // let: se usa para transformar o ejecutar algo sobre un objeto
    usuario.let {
        println("Nombre en mayúsculas: ${it.nombre.uppercase()}")
    }

    // apply: modifica el objeto y lo devuelve
    val nuevo = usuario.apply {
        nombre = "Carlos"
        correo = "carlos@mail.com"
    }
    println("Usuario modificado con apply → $nuevo")

    // run: ejecuta un bloque y devuelve su resultado
    val longitud = usuario.run {
        println("Ejecutando run sobre: $nombre")
        correo.length
    }
    println("Longitud del correo: $longitud")

    // also: ideal para efectos secundarios o depuración
    usuario.also {
        println("Auditando objeto → $it")
    }

    // with: similar a run, pero se usa fuera del objeto
    with(usuario) {
        println("Con with → nombre: $nombre, correo: $correo")
    }
}

/*
────────────────────────────────────────────────────────────
3. takeIf y takeUnless
────────────────────────────────────────────────────────────
- `takeIf { condición }` → devuelve el objeto solo si cumple la condición, o null.
- `takeUnless { condición }` → lo contrario: devuelve el objeto si NO cumple.
*/

fun demoTakeIf() {
    println("────────────────────────────────────")
    println("DEMO: takeIf() y takeUnless()")
    println("────────────────────────────────────")

    val numero = 42
    val resultado1 = numero.takeIf { it % 2 == 0 }       // 42 (es par)
    val resultado2 = numero.takeUnless { it > 100 }      // 42 (no supera 100)
    val resultado3 = numero.takeIf { it > 100 } ?: 0     // null → usa Elvis (0)

    println("takeIf par → $resultado1")
    println("takeUnless menor a 100 → $resultado2")
    println("takeIf con Elvis → $resultado3")
}

/*
────────────────────────────────────────────────────────────
4. repeat(n)
────────────────────────────────────────────────────────────
Ejecuta un bloque un número determinado de veces.
*/

fun demoRepeat() {
    println("────────────────────────────────────")
    println("DEMO: repeat(n)")
    println("────────────────────────────────────")

    repeat(3) { i ->
        println("Iteración número ${i + 1}")
    }
}

/*
────────────────────────────────────────────────────────────
5. by lazy (INICIALIZACIÓN DIFERIDA)
────────────────────────────────────────────────────────────
Permite inicializar una propiedad solo cuando se usa por primera vez.
Ideal para operaciones costosas o recursos que no siempre se necesitan.
*/

val configuracion: String by lazy {
    println("Inicializando configuración…")
    "Modo de producción"
}

fun demoLazy() {
    println("────────────────────────────────────")
    println("DEMO: by lazy")
    println("────────────────────────────────────")

    println("Configuración aún no usada.")
    println("Configuración: $configuracion")
    println("Configuración usada nuevamente: $configuracion")
}

/*
────────────────────────────────────────────────────────────
6. EJECUCIÓN GENERAL
────────────────────────────────────────────────────────────
*/

fun main() {
    demoRequireCheckAssert()
    println()
    demoScopeFunctions()
    println()
    demoTakeIf()
    println()
    demoRepeat()
    println()
    demoLazy()
    println()
    println("✔ Fin de las demostraciones del Capítulo 7")
}

/*
────────────────────────────────────────────────────────────
7. CONCLUSIONES
────────────────────────────────────────────────────────────
1. `require()` valida argumentos; lanza IllegalArgumentException.
2. `check()` valida estados internos; lanza IllegalStateException.
3. `assert()` se usa para depuración; puede desactivarse.
4. Las *scope functions* (`let`, `run`, `apply`, `also`, `with`)
   evitan repetir nombres y mejoran la legibilidad del código.
5. `takeIf` / `takeUnless` simplifican condiciones y filtrados.
6. `repeat(n)` ejecuta bloques repetitivos con claridad.
7. `by lazy` optimiza inicializaciones tardías sin control manual.
────────────────────────────────────────────────────────────
PRÓXIMOS TEMAS (opcional)
────────────────────────────────────────────────────────────
- Delegación de propiedades avanzada (`observable`, `vetoable`).
- Operadores sobrecargables (`operator fun plus`, etc.).
- Corrutinas y funciones suspendidas.
- Extensiones (`fun String.capitalizar()`) y lambdas avanzadas.
────────────────────────────────────────────────────────────
*/
