package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 4 (KOTLIN AVANZADO)
HERENCIA, SUPERCLASES, super, OVERRIDE Y CLASES ABSTRACTAS
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO

- Comprender cómo funciona la herencia en Kotlin.
- Entender por qué las clases y miembros son `final` por defecto y cómo habilitar la herencia con `open`.
- Practicar la sobrescritura de métodos y propiedades con `override`.
- Usar `super` para invocar comportamiento de la superclase.
- Distinguir clases concretas vs. abstractas y cuándo elegir una u otra.
- Ver el orden de inicialización en una jerarquía (constructores + `init`).
- Aplicar polimorfismo: tratar objetos concretos como su tipo base.

IMPORTANTE EN KOTLIN
- Por defecto, las clases y sus miembros son `final` (no se pueden heredar/sobrescribir).
- Para permitir herencia, marca la clase como `open`.
- Para permitir sobrescritura, marca el miembro como `open`.
- La palabra `override` indica que estamos redefiniendo un miembro `open` de la superclase.
- Puedes "cerrar" una sobrescritura con `final override` para impedir nuevas sobrescrituras.
*/


/*
────────────────────────────────────────────────────────────
1. FUNDAMENTOS DE HERENCIA Y CLASES "open"
────────────────────────────────────────────────────────────

- "Superclase" (o clase base): clase de la que se hereda.
- "Subclase" (o clase derivada): clase que hereda de otra.

Sintaxis general:
    open class SuperClase { ... }           // Debe ser open para poder heredarse
    class SubClase(...) : SuperClase(...)   // Subclase que extiende SuperClase

Regla: si la superclase tiene constructor (primario), la subclase debe llamarlo.
*/

open class SerVivo(open val nombre: String) {

    // Bloque init de la superclase: se ejecuta antes que el de la subclase
    init {
        println("[SerVivo.init] Nace un ser vivo llamado: $nombre")
    }

    // Miembro "open": permite sobrescritura en clases derivadas
    open fun describir() {
        println("Soy un ser vivo y me llamo $nombre.")
    }
}

/*
Subclase que hereda de SerVivo:
- Debe invocar al constructor de la superclase con `: SerVivo(nombre)`
- Puede añadir propiedades y métodos propios.
- Puede sobrescribir miembros marcados como `open`.
*/
class Animal(nombre: String, var edad: Int) : SerVivo(nombre) {

    init {
        println("[Animal.init] $nombre es un animal de $edad años.")
    }

    // Sobrescribimos el método describir() y llamamos al comportamiento base con super
    override fun describir() {
        super.describir() // Invoca la versión de la superclase
        println("Además, soy un animal y tengo $edad años.")
    }
}


/*
────────────────────────────────────────────────────────────
2. ORDEN DE INICIALIZACIÓN EN HERENCIA
────────────────────────────────────────────────────────────

Orden cuando se crea un objeto de la subclase:
1) Se evalúan los argumentos del constructor de la subclase.
2) Se llama al constructor (primario) de la superclase.
3) Se ejecutan inicializadores y bloques `init` de la superclase.
4) Se ejecutan inicializadores y bloques `init` de la subclase.

Demostración práctica con trazas impresas.
*/

open class EmpleadoBase(open val id: Int) {

    // Inicializador de propiedad en superclase
    val creadoEnBase: Long = System.currentTimeMillis().also {
        println("[EmpleadoBase.prop] id=$id, timestamp=$it")
    }

    init {
        println("[EmpleadoBase.init] Preparando empleado base (id=$id)")
    }

    open fun presentacion() {
        println("Empleado base con id=$id")
    }
}

class EmpleadoTiempoCompleto(
    override val id: Int,
    val nombre: String,
    var salario: Double
) : EmpleadoBase(id) {

    val creadoEnDerivada: Long = System.currentTimeMillis().also {
        println("[EmpleadoTiempoCompleto.prop] nombre=$nombre, timestamp=$it")
    }

    init {
        println("[EmpleadoTiempoCompleto.init] Empleado $nombre con salario $salario creado.")
    }

    override fun presentacion() {
        super.presentacion()
        println("Soy $nombre y gano $salario al mes.")
    }
}


/*
────────────────────────────────────────────────────────────
3. OVERRIDE DE MÉTODOS Y PROPIEDADES
────────────────────────────────────────────────────────────

- Un miembro `open` en la superclase puede ser redefinido con `override`.
- Puedes sobrescribir tanto métodos como propiedades.

REGLAS ÚTILES PARA PROPIEDADES:
- Puedes sobrescribir un `val` de la superclase con un `val` o con un `var` en la subclase.
  (Un `var` agrega setter; sigue siendo compatible con el getter del `val` base.)
- No puedes sobrescribir un `var` con un `val` (perderías el setter).
*/

open class Figura(open val nombreFigura: String) {
    open fun area(): Double = 0.0
    open fun info() = "Figura: $nombreFigura, área=${area()}"
}

class Rectangulo(
    override var nombreFigura: String, // sobrescribimos val (base) con var (derivada): permitido
    var ancho: Double,
    var alto: Double
) : Figura(nombreFigura) {

    override fun area(): Double = ancho * alto

    override fun info(): String {
        // Ejemplo de uso de super.prop y super.metodo()
        val infoBase = super.info()
        return "$infoBase | Rectángulo de $ancho x $alto"
    }
}

/*
────────────────────────────────────────────────────────────
3.1. LA PALABRA CLAVE super
────────────────────────────────────────────────────────────

La palabra clave `super` se utiliza dentro de una subclase para acceder
a miembros (métodos o propiedades) definidos en la **superclase**.

Se usa en tres contextos principales:
1. **Invocar métodos de la superclase** (cuando fueron sobrescritos).
2. **Acceder a propiedades de la superclase** (cuando fueron redefinidas).
3. **Llamar explícitamente a constructores de la superclase** (en la cabecera).

────────────────────────────────────────────────────────────
3.1.1) super con MÉTODOS
────────────────────────────────────────────────────────────

Cuando una subclase sobrescribe un método pero desea conservar parte del
comportamiento original, puede invocar la versión de la superclase con `super.metodo()`.

Ejemplo:
*/

open class VehiculoBase {
    open fun encender() {
        println("Encendiendo vehículo genérico...")
    }
}

class Automovil : VehiculoBase() {
    override fun encender() {
        println("Verificando motor y batería...")
        super.encender() // Invoca la versión de la superclase
        println("Automóvil listo para arrancar.")
    }
}

/*
────────────────────────────────────────────────────────────
3.1.2) super con PROPIEDADES
────────────────────────────────────────────────────────────

Si una subclase sobrescribe una propiedad, puede seguir accediendo a la
versión original usando `super.propiedad`.

Ejemplo:
*/

open class Dispositivo {
    open val tipo: String = "Genérico"
    open fun info() = "Dispositivo tipo: $tipo"
}

class Computadora : Dispositivo() {
    override val tipo: String = "Computadora"

    override fun info(): String {
        // Acceso a la propiedad original de la superclase
        val base = super.tipo
        val actual = tipo
        return "Antes era '$base', ahora es '$actual'."
    }
}

/*
────────────────────────────────────────────────────────────
3.1.3) super en CONSTRUCTORES
────────────────────────────────────────────────────────────

Cuando una subclase define su propio constructor, debe llamar explícitamente
al constructor de la superclase en la cabecera usando `: SuperClase(...)`.

Ejemplo:
    open class Persona(open val nombre: String)
    class Estudiante(nombre: String, val carrera: String) : Persona(nombre)

El constructor de `Persona` se ejecuta antes del de `Estudiante`.

────────────────────────────────────────────────────────────
3.1.4) super<NombreDeClase> (caso especial)
────────────────────────────────────────────────────────────

En herencia múltiple (cuando una clase implementa varias interfaces que
definen el mismo método), se puede usar `super<Interface>.metodo()`
para indicar explícitamente de cuál interfaz se quiere heredar la implementación.

Ejemplo:
    interface A { fun mostrar() = println("Desde A") }
    interface B { fun mostrar() = println("Desde B") }

    class C : A, B {
        override fun mostrar() {
            super<A>.mostrar() // Invoca la versión de A
            super<B>.mostrar() // Invoca la versión de B
        }
    }

Este caso se verá más adelante en el capítulo sobre **interfaces**.
────────────────────────────────────────────────────────────
Resumen:
────────────────────────────────────────────────────────────
- `super.metodo()` → Invoca la versión base de un método.
- `super.propiedad` → Accede al valor de una propiedad base.
- `super(...)` → Invoca el constructor de la superclase.
- `super<Interface>.metodo()` → Selecciona implementación específica
  cuando hay herencia múltiple por interfaces.
────────────────────────────────────────────────────────────
*/

/*
────────────────────────────────────────────────────────────
4. CONTROL DE HERENCIA CON final Y open override
────────────────────────────────────────────────────────────

- Una clase o miembro son `final` por defecto (cerrados).
- Para permitir herencia/sobrescritura: `open`.
- Puedes permitir sobrescribir y a la vez cerrar en el siguiente nivel con `final override`.

Ejemplo:
*/

open class Poligono {
    open fun lados(): Int = 0
    open fun descripcion(): String = "Polígono con ${lados()} lados."
}

open class Triangulo : Poligono() {
    final override fun lados(): Int = 3 // no podrá ser sobrescrito más abajo
    override fun descripcion(): String = "Triángulo: ${super.descripcion()}"
}

class TrianguloEscaleno : Triangulo() {
    // fun lados(): Int = 999  // ERROR de compilación, porque en Triangulo fue "final override"
    override fun descripcion(): String = "Triángulo escaleno (3 lados desiguales)."
}


/*
────────────────────────────────────────────────────────────
5. CONSTRUCTORES EN JERARQUÍAS: ENCADENAMIENTO
────────────────────────────────────────────────────────────

- La subclase debe invocar algún constructor de la superclase.
- Si la superclase tiene parámetros obligatorios, la subclase debe proporcionarlos.
- Los constructores secundarios de la subclase también deben encadenarse hacia el primario.

Demostración con constructores primario/secundarios + herencia:
*/

open class Cuenta(open val numero: String, saldoInicial: Double) {

    var saldo: Double = saldoInicial

    open fun depositar(monto: Double) {
        require(monto > 0) { "El depósito debe ser positivo." }
        saldo += monto
    }

    open fun retirar(monto: Double) {
        require(monto > 0) { "El retiro debe ser positivo." }
        require(monto <= saldo) { "Fondos insuficientes." }
        saldo -= monto
    }

    open fun info() = "Cuenta $numero → saldo=$saldo"
}

/*
Nota:
La función `require(condición)` es una herramienta estándar de Kotlin
para validar argumentos en tiempo de ejecución.

- Si la condición es falsa, lanza una excepción `IllegalArgumentException`.
- Su propósito es asegurar que los datos recibidos por una función
  cumplan las condiciones esperadas antes de continuar.

Ejemplo:
    require(x > 0) { "El valor debe ser positivo" }
*/

class CuentaCorriente(
    override val numero: String,
    saldoInicial: Double,
    val sobregiro: Double
) : Cuenta(numero, saldoInicial) {

    // Constructor secundario: asume sobregiro por defecto
    constructor(numero: String, saldoInicial: Double) : this(numero, saldoInicial, sobregiro = 0.0)

    override fun retirar(monto: Double) {
        require(monto > 0) { "El retiro debe ser positivo." }
        // Permite sobregiro hasta el límite
        require(monto <= saldo + sobregiro) { "Límite de sobregiro excedido." }
        saldo -= monto
    }

    override fun info() = "CuentaCorriente $numero → saldo=$saldo (sobregiro=$sobregiro)"
}


/*
────────────────────────────────────────────────────────────
6. VISIBILIDAD Y HERENCIA (recordatorio breve)
────────────────────────────────────────────────────────────

- `private`: visible solo dentro de la clase/archivo donde se declara.
- `protected`: visible en la clase y subclases (no fuera de ellas).
- `internal`: visible dentro del mismo módulo.
- `public`: visible desde cualquier lugar (por defecto).

Ejemplo: método auxiliar `protected` que solo la subclase puede reutilizar.
*/

open class ProcesadorPagos {

    fun ejecutarPago(monto: Double) {
        if (validar(monto)) {
            println("Pago aprobado por: ${autorizador()}. Monto: $monto")
        } else {
            println("Pago rechazado. Monto inválido: $monto")
        }
    }

    protected fun autorizador(): String = "MotorInternoV1" // No accesible fuera de la jerarquía

    private fun validar(monto: Double): Boolean = monto > 0.0
}

class ProcesadorPagosAvanzado : ProcesadorPagos() {
    fun auditoria(monto: Double) {
        // println(autorizador()) // Sí es accesible aquí (protected), pero no fuera de la jerarquía
        println("Auditando pago con autorización de: ${autorizador()} y monto=$monto")
    }
}


/*
────────────────────────────────────────────────────────────
7. CLASES ABSTRACTAS
────────────────────────────────────────────────────────────

- Una clase abstracta NO se puede instanciar directamente.
- Puede contener:
  * Métodos abstractos (sin implementación) → deben implementarse en subclases.
  * Métodos concretos (con implementación) → se heredan normalmente.
  * Estado (propiedades) y lógica común para las subclases.
- Los miembros abstractos son `open` por naturaleza (no requieren `open` explícito).

¿Cuándo usarlas?
- Cuando quieres definir una **plantilla parcial** con comportamiento compartido y
  forzar a las subclases a completar ciertas piezas (métodos/properties abstractos).
- A diferencia de una interfaz (que veremos luego), una clase abstracta puede tener
  estado completo y lógica común compleja.

Ejemplo: cálculo de nómina con jerarquía abstracta.
*/

abstract class Trabajador(open val nombre: String) {

    // Método abstracto: cada tipo de trabajador define su cálculo
    abstract fun calcularPago(): Double

    // Método común (concreto) reutilizable por todas las subclases
    open fun resumen(): String = "Trabajador $nombre cobra ${calcularPago()} unidades."
}

class Asalariado(override val nombre: String, val salarioMensual: Double) : Trabajador(nombre) {
    override fun calcularPago(): Double = salarioMensual
    override fun resumen(): String = "Asalariado $nombre → pago mensual: ${calcularPago()}"
}

class PorHoras(
    override val nombre: String,
    val tarifaHora: Double,
    val horas: Int
) : Trabajador(nombre) {
    override fun calcularPago(): Double = tarifaHora * horas
    // No sobrescribimos resumen(): usa el de la clase abstracta base
}


/*
────────────────────────────────────────────────────────────
8. POLIMORFISMO
────────────────────────────────────────────────────────────

El término *polimorfismo* significa "muchas formas".
En la programación orientada a objetos, permite que una referencia
de tipo base (por ejemplo, `Trabajador`) apunte a objetos de
subclases diferentes (`Asalariado`, `PorHoras`, etc.) y ejecute el
comportamiento correcto en tiempo de ejecución.

Ventajas:
- Permite escribir código más genérico y reutilizable.
- Facilita extender el sistema agregando nuevas clases sin modificar el código base.
- Mejora la legibilidad y el mantenimiento del código.

Demostración: una lista de `Trabajador` con distintos tipos concretos.
*/

fun imprimirNomina(trabajadores: List<Trabajador>) {
    var total = 0.0
    for (t in trabajadores) {
        println(t.resumen())         // Llamada polimórfica
        total += t.calcularPago()    // Llamada polimórfica
    }
    println("Total a pagar: $total")
}

/*
────────────────────────────────────────────────────────────
COMPANION OBJECT (OBJETO DE COMPAÑÍA)
────────────────────────────────────────────────────────────

Un `companion object` se usa para definir valores o funciones
que pertenecen a la clase en general, no a cada objeto individual.

Se comporta como los “atributos estáticos” en otros lenguajes.
*/

class CalculadoraSimple {

    fun sumar(a: Int, b: Int): Int = a + b

    companion object {
        const val AUTOR = "Equipo Kotlin"  // constante de clase

        fun mostrarAutor() {
            println("Calculadora creada por $AUTOR")
        }
    }
}

/*
────────────────────────────────────────────────────────────
ENUM CLASS (ENUMERACIONES)
────────────────────────────────────────────────────────────

Un `enum class` define un conjunto fijo de valores posibles.
Son muy útiles para representar estados, tipos o categorías.
*/

enum class DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
}

fun demoEnumClass() {
    println("────────────────────────────────────")
    println("DEMO ENUM CLASS — DÍAS DE LA SEMANA")
    println("────────────────────────────────────")

    val hoy = DiaSemana.MIERCOLES

    // Comparación directa
    if (hoy == DiaSemana.MIERCOLES) {
        println("Hoy es ${hoy.name}: ¡mitad de semana!")
    }

    // Recorrer todos los valores
    println("\nLista completa de días:")
    for (dia in DiaSemana.values()) {
        println("- $dia")
    }

    // Mostrar nombre y posición
    println("\nEl día ${hoy.name} ocupa la posición ${hoy.ordinal}.")
}

/*
────────────────────────────────────────────────────────────
9. EJECUCIÓN Y DEMOSTRACIÓN
────────────────────────────────────────────────────────────
*/

fun main() {

    println("────────────────────────────────────")
    println("HERENCIA, super, override y ABSTRACTAS — DEMO")
    println("────────────────────────────────────\n")

    // 1) Herencia básica + super
    val a1 = Animal("Luna", 4)
    a1.describir()
    println()

    // 2) Orden de inicialización (super → sub)
    val e1 = EmpleadoTiempoCompleto(id = 101, nombre = "Carlos", salario = 3200.0)
    e1.presentacion()
    println()

    // 3) Override de propiedades y métodos (val→var permitido)
    val r1 = Rectangulo(nombreFigura = "Rectángulo A", ancho = 5.0, alto = 3.0)
    println(r1.info())
    println()

    // 4) Control de herencia con final/open override
    val t1: Poligono = Triangulo()
    val t2: Poligono = TrianguloEscaleno()
    println(t1.descripcion())
    println(t2.descripcion())
    println()

    // 5) Constructores en jerarquías + secundaria encadenado
    val cc1 = CuentaCorriente(numero = "CC-001", saldoInicial = 1000.0, sobregiro = 300.0)
    val cc2 = CuentaCorriente(numero = "CC-002", saldoInicial = 500.0) // usa secundario con sobregiro=0.0
    cc1.retirar(1200.0)  // usa sobregiro
    println(cc1.info())
    println(cc2.info())
    println()

    // 6) Visibilidad y herencia (protected)
    val proc = ProcesadorPagosAvanzado()
    proc.ejecutarPago(200.0)  // usa motor interno (protected) en el flujo
    proc.auditoria(200.0)
    println()

    // 7) Clases abstractas + polimorfismo
    val nomina = listOf<Trabajador>(
        Asalariado("María", 3000.0),
        PorHoras("Jorge", tarifaHora = 25.0, horas = 120),
        PorHoras("Ana", tarifaHora = 40.0, horas = 80)
    )
    imprimirNomina(nomina)

    // Companion object
    CalculadoraSimple.mostrarAutor() // Acceso al companion object sin crear instancia

    // Enum class
    demoEnumClass()
}

/*
────────────────────────────────────────────────────────────
CONCLUSIONES
────────────────────────────────────────────────────────────
1. En Kotlin, **todo es final por defecto**; para heredar o sobrescribir debes usar `open`.
2. Usa `override` para redefinir miembros; puedes combinar con `final override` para cerrarlos.
3. El **orden de inicialización** en jerarquías es: primero superclase, luego subclase.
4. `super` permite reutilizar o extender la lógica de la superclase.
5. Puedes sobrescribir **propiedades**: un `val` base puede volverse `var` en la derivada (no al revés).
6. Las **clases abstractas** proveen plantilla + código compartido; obligan a implementar piezas clave.
7. El **polimorfismo** habilita escribir código genérico sobre la superclase, ejecutando comportamiento concreto.
8. Diseña jerarquías con responsabilidad clara; no fuerces herencia si composición es suficiente.
────────────────────────────────────────────────────────────
PRÓXIMOS TEMAS (en capítulos posteriores)
────────────────────────────────────────────────────────────
- Interfaces, conflictos de herencia múltiple y `super<Interface>.método()`.
- Jerarquías cerradas con `sealed` y patrones exhaustivos con `when`.
- Objetos de compañía (`companion object`) y miembros estáticos.
- Sobrecarga de operadores y data classes avanzadas.
- Generics y tipos con variantes.
────────────────────────────────────────────────────────────
*/
