package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3: Operadores en Kotlin
────────────────────────────────────────────────────────────

¿Qué es un operador?

En programación, los operadores son símbolos especiales que se utilizan
para realizar operaciones sobre variables y valores. Kotlin ofrece una
variedad de operadores similares a otros lenguajes como Java, pero con
algunas características adicionales.

────────────────────────────────────────────────────────────
TIPOS DE OPERADORES EN KOTLIN
────────────────────────────────────────────────────────────

1. Operadores Aritméticos
- Se utilizan para realizar operaciones matemáticas.

+   → Suma
-   → Resta
*   → Multiplicación
/   → División
%   → Módulo (resto de la división)

++  → Incremento (suma 1)
--  → Decremento (resta 1)

2. Operadores de Asignación
- Se usan para asignar valores a variables.

=   → Asignación
+=  → a += b → a = a + b
-=  → a -= b → a = a - b
*=  → a *= b → a = a * b
/=  → a /= b → a = a / b
%=  → a %= b → a = a % b

3. Operadores de Comparación
- Comparan dos valores y devuelven un Boolean (true o false).

==  → Igual
!=  → Distinto
>   → Mayor que
<   → Menor que
>=  → Mayor o igual
<=  → Menor o igual

4. Operadores Lógicos
- Se usan para combinar expresiones booleanas.

&&  → AND lógico → true si ambas condiciones son true
||  → OR lógico  → true si al menos una es true
!   → NOT lógico → invierte el valor (true → false, false → true)

────────────────────────────────────────────────────────────
*/

fun main() {

    // OPERADORES ARITMÉTICOS
    val a = 10
    val b = 3

    println("Suma: a + b = ${a + b}")
    println("Resta: a - b = ${a - b}")
    println("Multiplicación: a * b = ${a * b}")
    println("División (entera): a / b = ${a / b}")
    println("Módulo (resto): a % b = ${a % b}")

    val c = 10.0
    val d = 3.0
    println("División con decimales: c / d = ${c / d}")

    // OPERADORES DE ASIGNACIÓN
    var x = 5

    x += 3  // x = x + 3
    x -= 1  // x = x - 1
    x *= 2  // x = x * 2
    x /= 4  // x = x / 4
    x %= 2  // x = x % 2

    println("Resultado final de x: $x")

    // OPERADORES DE COMPARACIÓN
    val m = 7
    val n = 5

    println("¿m == n? → ${m == n}")   // igualdad
    println("¿m != n? → ${m != n}")   // desigualdad
    println("¿m > n?  → ${m > n}")    // mayor que
    println("¿m < n?  → ${m < n}")    // menor que
    println("¿m >= n? → ${m >= n}")   // mayor o igual
    println("¿m <= n? → ${m <= n}")   // menor o igual

    // OPERADORES LÓGICOS
    val aEsMayor = a > b      // true
    val bEsMenor = b < a      // true

    println("a > b && b < a → ${aEsMayor && bEsMenor}")
    println("a < b || b < a → ${a < b || b < a}")
    println("!(a < b) → ${!(a < b)}")

    // OPERADORES UNARIOS (pre y post incremento/decremento)
    var z = 10
    println("z original: $z")
    println("++z (pre-incremento): ${++z}")
    println("z++ (post-incremento): ${z++}") // luego sube
    println("z actual: $z")
    println("--z (pre-decremento): ${--z}")
    println("z-- (post-decremento): ${z--}") // luego baja
    println("z actual: $z")

    // OPERADORES DE RANGO Y PERTENENCIA
    val enRango = 5 in 1..10
    println("¿5 está entre 1 y 10? → $enRango")

    val noEnRango = 15 !in 1..10
    println("¿15 no está entre 1 y 10? → $noEnRango")

    println("Rango ascendente:")
    for (i in 1..5) print("$i ")
    println()

    println("Rango descendente:")
    for (i in 5 downTo 1) print("$i ")
    println()

    println("Rango con pasos de 2:")
    for (i in 0..10 step 2) print("$i ")
    println()

    // ─────────────────────────────────────────────────────
    // OPERADORES ESPECIALES DE KOTLIN
    // ─────────────────────────────────────────────────────
    /*
    Estos operadores fueron creados para manejar valores nulos de manera segura,
    evitando errores comunes (NullPointerException).

    1. Operador Safe Call (?.)
       Permite acceder a propiedades o métodos de una variable solo si no es null.
       Si es null, devuelve null sin lanzar error.

    2. Operador Elvis (?:)
       Permite asignar un valor alternativo si la expresión de la izquierda es null.

    3. Not-null Assertion (!!)
       Fuerza el acceso a una variable nullable. Si es null lanza NullPointerException.
    */

    // Safe Call Operator (?.)
    val nombre: String? = null
    println("Longitud segura (?.): ${nombre?.length}") // null-safe → imprime null

    // Elvis Operator (?:)
    val saludo = nombre ?: "Invitado" // si nombre es null, usa "Invitado"
    println("Bienvenido, $saludo")

    // Not-null Assertion (!!)
    val textoSeguro: String? = "Kotlin"
    println("Longitud obligatoria (!!): ${textoSeguro!!.length}") // funciona porque no es null

    // Si se hace sobre null lanza excepción:
    // val textoNulo: String? = null
    // println(textoNulo!!.length) // NullPointerException

    /*
    Conclusión:
    - ?. se usa para acceder de forma segura sin error si es null.
    - ?: devuelve un valor por defecto cuando es null.
    - !! fuerza acceso (usar solo si estás seguro de que no es null).
    */
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES:

- Kotlin ofrece una amplia gama de operadores para distintos propósitos:
  matemáticos, lógicos, de comparación, de asignación y especiales.
- A diferencia de otros lenguajes, Kotlin no permite conversiones implícitas:
  por ejemplo, no se puede sumar un Int y un Double sin convertir uno.
- El operador Elvis (?:) y el safe call (?.) permiten manejar nulos de forma segura.
- El operador !! debe usarse con precaución ya que puede lanzar excepción.
- Los rangos (`in`, `..`, `downTo`, `step`) facilitan la lectura de bucles.
- Es importante entender la diferencia entre pre y post incremento/decremento.

────────────────────────────────────────────────────────────
*/
