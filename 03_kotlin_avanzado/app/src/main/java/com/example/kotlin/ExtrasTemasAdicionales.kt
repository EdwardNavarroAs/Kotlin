package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 6 (KOTLIN AVANZADO)
COMPORTAMIENTO DE OBJETOS, IGUALDAD, CASTEOS Y SMART CASTS
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO

- Entender la diferencia entre igualdad estructural y referencial.
- Sobrescribir correctamente `equals()` y `hashCode()` en clases personalizadas.
- Comprender el propósito de `toString()` y su personalización.
- Usar los operadores `is`, `!is`, `as`, `as?` y `?:` (Elvis) para verificar y convertir tipos.
- Aplicar smart cast (conversión inteligente de tipos) en condiciones seguras.
- Cerrar el ciclo de los pilares fundamentales de la Programación Orientada a Objetos en Kotlin.

────────────────────────────────────────────────────────────
1. IGUALDAD EN KOTLIN: == vs ===
────────────────────────────────────────────────────────────

- `==` → Compara **contenido** (igualdad estructural).
     Internamente llama a `equals()` del objeto.
- `===` → Compara **referencias** (si apuntan al mismo objeto en memoria).

Ejemplo:
    val a = "Hola"
    val b = "Hola"
    val c = a
    println(a == b)   // true → mismo contenido
    println(a === b)  // false → distintas referencias
    println(a === c)  // true  → misma referencia
*/

/*
────────────────────────────────────────────────────────────
2. SOBRESCRIBIR equals() Y hashCode()
────────────────────────────────────────────────────────────

Reglas del contrato:
1. Si dos objetos son iguales (`a == b`), deben tener el mismo `hashCode()`.
2. Si dos objetos tienen distinto `hashCode()`, deben ser distintos.
3. equals() debe ser reflexivo, simétrico y transitivo.

El compilador genera ambos automáticamente para `data class`,
pero en clases normales podemos definirlos manualmente.
*/

class Persona(val nombre: String, val edad: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true               // misma referencia
        if (other !is Persona) return false           // tipo incompatible
        return nombre == other.nombre && edad == other.edad
    }

    override fun hashCode(): Int {
        return nombre.hashCode() * 31 + edad
    }

    override fun toString(): String {
        return "Persona(nombre='$nombre', edad=$edad)"
    }
}

/*
────────────────────────────────────────────────────────────
3. toString() PERSONALIZADO
────────────────────────────────────────────────────────────
Sirve para mostrar una representación legible del objeto, útil
para depuración o impresión en consola.
*/

/*
────────────────────────────────────────────────────────────
4. DEMOSTRACIÓN DE IGUALDAD, hashCode Y toString
────────────────────────────────────────────────────────────
*/

fun demoEqualsHashCode() {
    println("────────────────────────────────────")
    println("DEMO: equals(), hashCode() y toString()")
    println("────────────────────────────────────")

    val p1 = Persona("Laura", 30)
    val p2 = Persona("Laura", 30)
    val p3 = p1

    println("p1 == p2 → ${p1 == p2}")     // true: mismo contenido
    println("p1 === p2 → ${p1 === p2}")   // false: distinta referencia
    println("p1 === p3 → ${p1 === p3}")   // true: misma referencia
    println("p1.hashCode() = ${p1.hashCode()}")
    println("p2.hashCode() = ${p2.hashCode()}")
    println("p1.toString() = $p1")
}

/*
────────────────────────────────────────────────────────────
5. SMART CAST Y SAFE CAST
────────────────────────────────────────────────────────────

- `is` → Verifica si un objeto es de un tipo específico.
- `!is` → Verifica lo contrario.
- `as` → Casteo directo (puede lanzar excepción).
- `as?` → Safe cast (devuelve null si falla).
- Smart cast → Kotlin convierte automáticamente el tipo si la comprobación `is` fue exitosa.
*/

open class AnimalBase
class Gato(val nombre: String) : AnimalBase()
class Perro(val raza: String) : AnimalBase()

fun describirAnimal(animal: AnimalBase) {
    when (animal) {
        is Gato -> println("Es un gato llamado ${animal.nombre}") // Smart cast automático
        is Perro -> println("Es un perro de raza ${animal.raza}")
        else -> println("Animal desconocido")
    }
}

/*
────────────────────────────────────────────────────────────
6. SAFE CAST (as?) Y OPERADOR ELVIS (?:)
────────────────────────────────────────────────────────────

`as?` intenta convertir un objeto al tipo indicado.
Si no puede, devuelve null en lugar de lanzar excepción.

`?:` (Elvis) permite asignar un valor alternativo si algo es null.

Ejemplo:
    val obj: Any = "Hola"
    val texto: String? = obj as? String ?: "(no es texto)"
*/

fun demoSafeCast() {
    println("────────────────────────────────────")
    println("DEMO: safe cast (as?) y operador Elvis (?:)")
    println("────────────────────────────────────")

    val objetos: List<Any> = listOf("Hola", 42, Gato("Michi"), true)

    for (obj in objetos) {
        val texto: String? = obj as? String
        println("Intento castear $obj → ${texto ?: "(no es texto)"}")
    }
}

/*
────────────────────────────────────────────────────────────
7. APLICACIÓN EN LISTAS Y POLIMORFISMO
────────────────────────────────────────────────────────────
Combinando igualdad, smart cast y safe cast en colecciones mixtas.
*/

fun demoColeccionesPolimorficas() {
    println("────────────────────────────────────")
    println("DEMO: Smart cast y safe cast en colecciones mixtas")
    println("────────────────────────────────────")

    val animales: List<AnimalBase> = listOf(Gato("Luna"), Perro("Beagle"), Gato("Michi"))

    for (a in animales) describirAnimal(a)

    println("\nIntentando safe cast:")
    for (a in animales) {
        val gato = a as? Gato
        println("Resultado: ${gato?.nombre ?: "no es un gato"}")
    }
}

/*
────────────────────────────────────────────────────────────
8. EQUALS Y HASHCODE EN ESTRUCTURAS DE DATOS
────────────────────────────────────────────────────────────
Cuando se usan objetos como claves de Map o elementos de Set,
`equals()` y `hashCode()` deben estar correctamente implementados.
*/

fun demoEstructurasDeDatos() {
    println("────────────────────────────────────")
    println("DEMO: equals() y hashCode() en estructuras de datos")
    println("────────────────────────────────────")

    val p1 = Persona("Laura", 30)
    val p2 = Persona("Laura", 30)
    val p3 = Persona("Carlos", 40)

    val conjunto = hashSetOf(p1, p2, p3)
    println("Conjunto contiene:")
    conjunto.forEach { println(it) }

    val mapa = hashMapOf(p1 to "Diseñadora", p3 to "Gerente")
    println("\nProfesión de p2: ${mapa[p2]}") // usa equals/hashCode
}

/*
────────────────────────────────────────────────────────────
9. EJECUCIÓN GENERAL
────────────────────────────────────────────────────────────
*/

fun main() {
    demoEqualsHashCode()
    println()
    demoSafeCast()
    println()
    demoColeccionesPolimorficas()
    println()
    demoEstructurasDeDatos()
    println()
    println("✔ Fin de las demostraciones del Capítulo 6")
}

/*
────────────────────────────────────────────────────────────
10. CONCLUSIONES
────────────────────────────────────────────────────────────
1. `==` compara contenido; `===` compara referencias.
2. `equals()` y `hashCode()` deben mantener su contrato.
3. `toString()` mejora la legibilidad de los objetos en depuración.
4. `is` y `!is` verifican tipos; Kotlin hace smart cast automático al confirmar tipo.
5. `as?` realiza un cast seguro; si falla, devuelve null.
6. El operador Elvis (`?:`) ofrece valores alternativos ante nulls.
7. Smart cast y safe cast combinados permiten escribir código seguro y conciso.
8. Implementar correctamente equals/hashCode evita errores en colecciones y comparaciones.
────────────────────────────────────────────────────────────
PRÓXIMOS TEMAS
────────────────────────────────────────────────────────────
- Clases selladas (`sealed`) y jerarquías cerradas.
- Excepciones personalizadas y manejo de errores avanzado.
- Operadores sobrecargables (`operator fun`).
- Delegación de propiedades (`by lazy`, `observable`, etc.).
────────────────────────────────────────────────────────────
*/
