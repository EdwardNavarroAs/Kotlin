package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 1 (KOTLIN AVANZADO):
CONVENCIONES DE NOMENCLATURA Y ESTILO DE CÓDIGO
────────────────────────────────────────────────────────────

Importancia de las convenciones de nombres:

Un programa no solo debe funcionar correctamente,
sino también ser fácil de leer, mantener y ampliar.
Para lograrlo, los desarrolladores deben seguir **estándares de estilo**
que hagan que el código sea coherente, predecible y entendible
por cualquier persona del equipo.

Kotlin, como lenguaje moderno, posee una guía oficial llamada
**Kotlin Coding Conventions (JetBrains)**, la cual define las reglas de estilo
para nombrar variables, funciones, clases, archivos y paquetes.
*/

// ─────────────────────────────────────────────────────────────
// Ejemplo de constante global (válido y compilable)
// ─────────────────────────────────────────────────────────────
const val MAX_LOGIN_ATTEMPTS = 3

/*
────────────────────────────────────────────────────────────
1. IDIOMA DEL CÓDIGO
────────────────────────────────────────────────────────────
- Todo el código, incluidos nombres de variables, funciones y clases,
  debe escribirse **en inglés**.
- Evita mezclar idiomas en un mismo proyecto.

  Ejemplo:
      val nombreUsuario = "Ana"
      val userName = "Ana"

  Razones:
  - El inglés es el estándar global en ingeniería de software.
  - Mejora la compatibilidad con bibliotecas, documentación y APIs.
  - Facilita la colaboración con desarrolladores de otros países.
────────────────────────────────────────────────────────────
2. NOMBRAR VARIABLES
────────────────────────────────────────────────────────────
- Usa **camelCase**: la primera palabra en minúscula y las siguientes
  comienzan con mayúscula.

  Ejemplo correcto:
      val numberOfStudents = 25

- Los nombres deben ser **claros y descriptivos**:
      val ns = 25
      val numberOfStudents = 25

- Evita notaciones de tipo (no usar prefijos como “str”, “int”, etc.).
- Usa sustantivos o frases nominales para representar **datos o entidades**.
────────────────────────────────────────────────────────────
3. NOMBRAR FUNCIONES
────────────────────────────────────────────────────────────
- También usan **camelCase**.
- El nombre debe describir una acción: por lo general, debe comenzar con un verbo.

  Ejemplo:
      fun calculateAverage()
      fun printReport()
      fun isValid()

- Funciones booleanas deben usar prefijos como `is`, `has`, `can`, `should`, etc.
────────────────────────────────────────────────────────────
4. NOMBRAR CONSTANTES Y VARIABLES GLOBALES
────────────────────────────────────────────────────────────
- Las constantes globales (`const val`) se escriben en
  **MAYÚSCULAS_CON_GUIONES_BAJOS**.

  Ejemplo:
      const val MAX_ATTEMPTS = 3
      const val BASE_URL = "https://api.example.com"

- Este formato las diferencia visualmente de variables normales.
────────────────────────────────────────────────────────────
5. NOMBRAR CLASES, INTERFACES Y ENUMS
────────────────────────────────────────────────────────────
- Se usa **PascalCase**: cada palabra inicia con mayúscula.

  Ejemplo:
      class UserAccount
      interface DataRepository
      enum class UserStatus { ACTIVE, INACTIVE }
────────────────────────────────────────────────────────────
6. NOMBRAR ARCHIVOS
────────────────────────────────────────────────────────────
- El nombre del archivo debe reflejar su contenido o responsabilidad.
────────────────────────────────────────────────────────────
7. NOMBRAR PAQUETES
────────────────────────────────────────────────────────────
- Siempre en minúsculas y sin guiones bajos.

  Ejemplo:
      package com.example.app
────────────────────────────────────────────────────────────
8. NOMBRAR PARÁMETROS
────────────────────────────────────────────────────────────
- Usa nombres descriptivos que indiquen claramente su propósito.
────────────────────────────────────────────────────────────
9. VARIABLES TEMPORALES O ITERADORES
────────────────────────────────────────────────────────────
- En bucles simples, se pueden usar `i`, `j`, `k` para índices.
────────────────────────────────────────────────────────────
10. EVITAR REDUNDANCIAS Y CONTEXTO REPETIDO
────────────────────────────────────────────────────────────
- No repitas información que ya está implícita por el contexto.
────────────────────────────────────────────────────────────
11. FUNCIONES PRIVADAS O INTERNAS
────────────────────────────────────────────────────────────
- En Kotlin no se usa el prefijo “_” (guion bajo) para denotar funciones privadas.
────────────────────────────────────────────────────────────
12. CONVENCIONES PARA TESTS
────────────────────────────────────────────────────────────
- Las funciones de prueba deben describir el comportamiento esperado.
────────────────────────────────────────────────────────────
13. CLARIDAD Y LONGITUD
────────────────────────────────────────────────────────────
- Busca equilibrio entre brevedad y significado.
────────────────────────────────────────────────────────────
14. CONSISTENCIA EN EL IDIOMA
────────────────────────────────────────────────────────────
- Usa **inglés en todo el proyecto**: variables, funciones, clases y archivos.
────────────────────────────────────────────────────────────
EJEMPLOS ILUSTRATIVOS
────────────────────────────────────────────────────────────
*/

fun main() {

    // Variables correctamente nombradas
    val userName = "Alice"
    val userAge = 25
    val accountBalance = 1200.50

    println("User: $userName, Age: $userAge, Balance: $accountBalance")

    // Función con nombre descriptivo
    fun calculateDiscount(price: Double, percentage: Double): Double {
        return price - (price * percentage / 100)
    }

    val discountedPrice = calculateDiscount(200.0, 15.0)
    println("Final price: $discountedPrice")

    // Uso de constante global declarada al nivel superior
    println("Max login attempts allowed: $MAX_LOGIN_ATTEMPTS")

    // Clase con nombre adecuado
    class Vehicle(val brand: String, val model: String) {
        fun displayInfo() = println("Vehicle: $brand $model")
    }

    val car = Vehicle("Toyota", "Corolla")
    car.displayInfo()

    // Variables temporales con nombres claros
    val userAges = listOf(20, 25, 30)
    for (age in userAges) {
        println("Age: $age")
    }

    // Ejemplo de mala práctica:
    // val e = listOf(20, 25, 30)
    // for (x in e) println(x)
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES Y BUENAS PRÁCTICAS
────────────────────────────────────────────────────────────
1. Usa inglés para todos los identificadores y comentarios técnicos.
2. Aplica las convenciones:
   - camelCase → variables, funciones y parámetros.
   - PascalCase → clases, interfaces y enums.
   - MAYÚSCULAS_CON_GUIONES_BAJOS → constantes globales.
3. Los nombres deben ser claros, descriptivos y coherentes con el contexto.
4. Evita abreviaciones innecesarias o nombres ambiguos.
5. No repitas información ya implícita (ejemplo: “userUserName”).
6. Las funciones deben representar acciones (verbos).
7. Las variables y clases deben representar entidades (sustantivos).
8. No uses guion bajo como prefijo para indicar privacidad.
9. Mantén consistencia en todo el proyecto (mismo idioma, mismo formato).
10. El mejor código es aquel que se entiende sin necesidad de comentarios adicionales.
────────────────────────────────────────────────────────────
*/
