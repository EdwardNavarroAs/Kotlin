package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3 (KOTLIN AVANZADO)
CLASES, PROPIEDADES, CONSTRUCTORES Y DATA CLASSES
────────────────────────────────────────────────────────────

INTRODUCCIÓN GENERAL

Kotlin es un lenguaje completamente orientado a objetos.
Esto significa que organiza el código en **clases**, que son estructuras
capaces de combinar datos (propiedades) y comportamientos (métodos).

La Programación Orientada a Objetos (POO) es un paradigma que permite
modelar el mundo real mediante **clases** y **objetos**.

- Una **clase** define la estructura o plantilla de algo.
- Un **objeto** es una instancia concreta de esa plantilla.
- Las **propiedades** representan datos (estado).
- Los **métodos** representan acciones o comportamientos.

En Kotlin, todo puede ser un objeto, incluso las funciones.
Las clases son esenciales para crear programas estructurados, seguros y reutilizables.

────────────────────────────────────────────────────────────
1. QUÉ ES UNA CLASE Y PARA QUÉ SIRVE
────────────────────────────────────────────────────────────

Una **clase** es una plantilla que define cómo será un tipo de objeto.
Contiene propiedades (atributos) y funciones (métodos).

Ejemplo básico:
    class Persona {
        var nombre: String = ""
        var edad: Int = 0
        fun presentarse() {
            println("Hola, soy $nombre y tengo $edad años.")
        }
    }

A partir de esta clase, podemos crear **instancias** (objetos reales):

    val persona1 = Persona()
    persona1.nombre = "Laura"
    persona1.edad = 25
    persona1.presentarse()
*/

class Persona {
    // Propiedades del objeto (atributos)
    var nombre: String = ""
    var edad: Int = 0

    // Método: acción que puede realizar la persona
    fun presentarse() {
        println("Hola, soy $nombre y tengo $edad años.")
    }
}

/*
────────────────────────────────────────────────────────────
2. PROPIEDADES: val, var Y DIFERENCIA ENTRE VARIABLE Y ATRIBUTO
────────────────────────────────────────────────────────────

En Kotlin, las propiedades se definen con `val` o `var` dentro de la clase:

- `val` → propiedad inmutable (solo lectura).
- `var` → propiedad mutable (se puede cambiar su valor).

Las propiedades se diferencian de las variables locales:
- Una propiedad pertenece a un objeto y tiene visibilidad dentro de la clase.
- Una variable local vive solo dentro de una función o bloque.

Ejemplo:
*/

class Persona2 {
    var nombre: String = ""
    var edad: Int = 0
}

/*
NOTA:
Si una propiedad no puede inicializarse al declararla,
se puede usar `lateinit var` (solo para tipos no primitivos):

    class Persona {
        lateinit var nombre: String
        var edad: Int = 0
    }

`lateinit` indica que la variable será inicializada más adelante,
pero garantiza al compilador que no quedará sin valor al usarse.
────────────────────────────────────────────────────────────
3. CONSTRUCTOR PRIMARIO
────────────────────────────────────────────────────────────

Un **constructor** es una función especial que se ejecuta al crear un objeto.
El **constructor primario** es la forma más concisa de inicializar
las propiedades de una clase.

Kotlin permite declarar el **constructor primario** directamente en la cabecera
de la clase, justo después del nombre.

Sintaxis:
    class Empleado(val nombre: String, var salario: Double)

- `val` crea una propiedad de solo lectura (no puede modificarse).
- `var` crea una propiedad mutable.
- No se necesitan asignaciones internas ni bloques adicionales.

Al usar `val` o `var` dentro del constructor, esas variables
se convierten automáticamente en propiedades de la clase.
Este constructor genera automáticamente las propiedades y su inicialización.
*/

class Empleado(val nombre: String, var salario: Double) {
    fun mostrarInformacion() {
        println("Empleado: $nombre → Salario actual: $salario")
    }
}

/*
Ventajas:
- El código es más corto y claro.
- Evita repetir la inicialización de variables dentro del cuerpo de la clase.
────────────────────────────────────────────────────────────
4. BLOQUE INIT (INICIALIZADOR)
────────────────────────────────────────────────────────────
Kotlin permite un bloque especial llamado `init`, que se ejecuta
inmediatamente después del constructor primario, es decir cuando se crea un objeto de la clase.
Se usa para validaciones o inicializaciones complementarias.

Se utiliza para:
- Validar datos.
- Imprimir mensajes de inicialización.
- Ejecutar lógica complementaria al crear el objeto.
*/

class Estudiante(val nombre: String, var nota: Double) {

    init {
        println("Estudiante creado: $nombre con nota $nota")

        // Validación automática al construir el objeto
        if (nota < 0.0 || nota > 5.0) {
            println("Advertencia: nota fuera del rango permitido (0–5).")
        }
    }

    fun aprobado(): Boolean {
        return nota >= 3.0
    }
}

/*
────────────────────────────────────────────────────────────
5. CONSTRUCTORES SECUNDARIOS
────────────────────────────────────────────────────────────

Kotlin también permite definir **constructores secundarios**, usando la palabra clave `constructor`.

Estos se utilizan cuando:
- Necesitamos múltiples formas de crear un mismo tipo de objeto.
- Queremos definir valores por defecto o inicializaciones alternativas.

Regla: todo constructor secundario debe llamar al primario usando `this(...)`.
*/

class Vehiculo(val marca: String) {

    var modelo: String = "Desconocido"

    // Constructor secundario: crea un objeto con marca y modelo
    constructor(marca: String, modelo: String) : this(marca) {
        this.modelo = modelo
    }

    fun mostrarInfo() {
        println("Vehículo: $marca, Modelo: $modelo")
    }
}

/*
Uso:
    val v1 = Vehiculo("Toyota")
    val v2 = Vehiculo("Mazda", "CX-5")

En el ejemplo anterior:
- El primer constructor solo establece la marca.
- El segundo constructor establece tanto la marca como el modelo.
────────────────────────────────────────────────────────────
6. MODIFICADORES DE VISIBILIDAD
────────────────────────────────────────────────────────────
Los modificadores controlan qué partes del código pueden acceder a una clase
o a sus miembros.

1. `public` (por defecto) → visible desde cualquier parte del programa.
2. `private` → visible solo dentro de la clase o archivo.
3. `protected` → visible en la clase y sus subclases.
4. `internal` → visible solo dentro del mismo módulo o paquete.

Ejemplo práctico:
*/

class CuentaBancaria(private val numeroCuenta: String, var saldo: Double) {

    fun depositar(monto: Double) {
        if (monto > 0) {
            saldo += monto
            println("Depósito de $monto realizado. Nuevo saldo: $saldo")
        } else {
            println("Monto inválido.")
        }
    }

    fun retirar(monto: Double) {
        if (monto <= saldo) {
            saldo -= monto
            println("Retiro de $monto realizado. Saldo restante: $saldo")
        } else {
            println("Fondos insuficientes.")
        }
    }

    // Esta función solo puede ser usada dentro de la clase
    private fun mostrarNumeroCuenta() {
        println("Número interno de cuenta: $numeroCuenta")
    }
}

/*
────────────────────────────────────────────────────────────
7. MÉTODOS (FUNCIONES MIEMBRO)
────────────────────────────────────────────────────────────
Un **método** es una función que pertenece a una clase.
Se usa para expresar comportamientos relacionados con los datos de esa clase.
*/

class Calculadora {

    fun sumar(a: Int, b: Int): Int {
        return a + b
    }

    fun restar(a: Int, b: Int): Int {
        return a - b
    }

    fun multiplicar(a: Int, b: Int): Int = a * b // forma compacta
}

/*
────────────────────────────────────────────────────────────
8. GET Y SET PERSONALIZADOS
────────────────────────────────────────────────────────────
Cada propiedad (`var`) tiene por defecto un **getter** (lectura)
y un **setter** (escritura) automáticos.

Sin embargo, pueden personalizarse para controlar cómo se leen o modifican los valores.
El campo interno se llama `field` (nombre reservado dentro del setter/getter).

Ejemplo:
*/

class Producto {

    var nombre: String = ""
        // getter: define cómo se obtiene el valor
        get() = field.uppercase()  // Siempre se devuelve en mayúsculas

    var precio: Double = 0.0
        // setter: define cómo se asigna el valor
        set(value) {
            // Validación: no permitir precios negativos
            field = if (value < 0) {
                println("Advertencia: el precio no puede ser negativo. Se establecerá en 0.0.")
                0.0
            } else {
                value
            }
        }
}

/*
────────────────────────────────────────────────────────────
9. DATA CLASS (CLASES DE DATOS)
────────────────────────────────────────────────────────────
Las **data classes** están diseñadas para representar estructuras de datos,
como modelos de base de datos, respuestas de API o registros de información.

El compilador genera automáticamente funciones muy útiles:
- `toString()` → representación textual del objeto.
- `equals()` → comparación por contenido.
- `hashCode()` → soporte para estructuras de datos (mapas, sets).
- `copy()` → permite clonar objetos cambiando algunos valores.
- `componentN()` → permite desestructurar el objeto (como en Python).

Requisitos:
- Debe tener al menos un parámetro en el constructor primario.
- No puede ser `abstract`, `open`, `sealed` ni `inner`.
*/

data class Cliente(val nombre: String, val correo: String, val activo: Boolean)

/*
Ejemplo:
    val cliente1 = Cliente("Laura", "laura@mail.com", true)
    val cliente2 = cliente1.copy(nombre = "Carlos")

    println(cliente1)             // Usa toString automático
    println(cliente1 == cliente2) // Compara contenido, no referencia

Desestructuración:
    val (n, c, a) = cliente1
────────────────────────────────────────────────────────────
10. OBJECT (CLASES SINGLETON)
────────────────────────────────────────────────────────────
La palabra `object` define un **objeto único** (Singleton) que se crea solo una vez
y se usa globalmente.

Se utiliza para:
- Configuraciones globales.
- Constantes.
- Clases utilitarias (helpers).
*/

object Configuracion {
    const val VERSION = "1.0.0"

    fun mostrarVersion() {
        println("Versión actual del sistema: $VERSION")
    }
}

/*
────────────────────────────────────────────────────────────
11. COMPARACIÓN ENTRE class, data class Y object
────────────────────────────────────────────────────────────
| Tipo          | Propósito principal                   | Ejemplo |
|----------------|---------------------------------------|----------|
| class          | Modelo general con lógica             | Persona, CuentaBancaria |
| data class     | Almacenar datos, generar funciones    | Cliente, UsuarioDTO |
| object         | Crear un único objeto global          | Configuracion |
────────────────────────────────────────────────────────────
12. BUENAS PRÁCTICAS AL DEFINIR CLASES
────────────────────────────────────────────────────────────
1. Usa `val` siempre que sea posible (inmutabilidad = seguridad).
2. Declara propiedades `private` si no deben ser modificadas directamente.
3. Evita clases con demasiadas responsabilidades (principio de responsabilidad única).
4. Usa `data class` para representar entidades simples de datos (modelos).
5. Si el objeto no debe tener múltiples instancias, usa `object`.
6. Evita lógica compleja dentro del bloque `init` (solo validaciones ligeras).
7. Los nombres de clases deben usar **PascalCase** y ser claros (ej. `CuentaBancaria`, no `cuenta_bancaria`).
────────────────────────────────────────────────────────────
EJECUCIÓN Y DEMOSTRACIÓN
────────────────────────────────────────────────────────────
*/

fun main() {

    println("────────────────────────────────────")
    println("DEMONSTRACIÓN COMPLETA DE CLASES EN KOTLIN")
    println("────────────────────────────────────\n")

    // CLASE BÁSICA
    val persona = Persona()
    persona.nombre = "Laura"
    persona.edad = 25
    persona.presentarse()

    // CONSTRUCTOR PRIMARIO
    val empleado = Empleado("Carlos", 2500.0)
    empleado.mostrarInformacion()

    // BLOQUE INIT
    val estudiante = Estudiante("Sofía", 4.8)
    println("¿Aprobado? ${estudiante.aprobado()}")

    // CONSTRUCTOR SECUNDARIO
    val v1 = Vehiculo("Toyota")
    val v2 = Vehiculo("Mazda", "CX-5")
    v1.mostrarInfo()
    v2.mostrarInfo()

    // MÉTODOS
    val calc = Calculadora()
    println("Suma = ${calc.sumar(10, 5)}")
    println("Multiplicación = ${calc.multiplicar(3, 4)}")

    // GET/SET PERSONALIZADOS
    val producto = Producto()
    producto.nombre = "Laptop"
    producto.precio = -500.0
    println("Producto: ${producto.nombre}, Precio ajustado: ${producto.precio}")

    // DATA CLASS
    val cliente1 = Cliente("Laura", "laura@mail.com", true)
    val cliente2 = cliente1.copy(nombre = "Carlos")
    println("\nCliente 1: $cliente1")
    println("Cliente 2: $cliente2")
    println("¿Son iguales? ${cliente1 == cliente2}")

    val (nombre, correo, activo) = cliente1
    println("Desestructuración → $nombre, $correo, $activo")

    // OBJECT
    Configuracion.mostrarVersion()
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES
────────────────────────────────────────────────────────────
1. Una **clase** define propiedades y comportamientos de un tipo de objeto.
2. El **constructor primario** inicializa propiedades de forma concisa.
3. El **bloque init** permite validación e inicialización adicional.
4. Los **constructores secundarios** ofrecen flexibilidad en la creación de objetos.
5. Los **modificadores de visibilidad** permiten encapsular y proteger datos.
6. Los **métodos** representan acciones asociadas al estado del objeto.
7. Los **getters/setters personalizados** controlan cómo se acceden los datos.
8. Las **data classes** son ideales para representar modelos de información.
9. Los **object** permiten definir instancias únicas (patrón Singleton).
10. El diseño orientado a objetos en Kotlin promueve claridad, inmutabilidad
    y encapsulación, pilares de un código mantenible y profesional.
────────────────────────────────────────────────────────────
*/
