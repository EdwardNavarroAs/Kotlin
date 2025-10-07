package com.example.kotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 5 (KOTLIN AVANZADO)
INTERFACES: CONTRATOS, IMPLEMENTACIONES POR DEFECTO,
MÚLTIPLE HERENCIA DE COMPORTAMIENTO, super<Interface>,
DELEGACIÓN (by) Y FUN INTERFACES (SAM)
────────────────────────────────────────────────────────────

OBJETIVO DEL CAPÍTULO
- Comprender qué es una interfaz y para qué sirve.
- Declarar métodos y propiedades en interfaces (abstractos y con implementación por defecto).
- Implementar múltiples interfaces en una clase y resolver conflictos con super<Interface>.
- Distinguir interfaces de clases abstractas (cuándo usar cada una).
- Conocer la visibilidad en interfaces (public / private; no hay protected).
- Aplicar delegación de interfaces con la sintaxis `by`.
- Usar "fun interface" (SAM) para trabajar con lambdas como implementaciones de interfaz.
- Consolidar con una demostración ejecutable en main().

IDEA CLAVE
- Una interfaz define un **contrato** (qué se puede hacer), no un estado interno.
- Las interfaces **no tienen backing fields**: pueden declarar propiedades, pero sin almacenamiento interno.
- Pueden incluir implementación por defecto en métodos (y getters), pero no inicializadores de propiedades.
*/


/*
────────────────────────────────────────────────────────────
1. QUÉ ES UNA INTERFAZ
────────────────────────────────────────────────────────────

- Estructura que define comportamientos (funciones) y propiedades sin estado.
- Las clases que "implementan" una interfaz se comprometen a proveer ese comportamiento.
- Permiten **herencia múltiple de comportamiento** (una clase puede implementar varias interfaces).
- Las interfaces pueden tener **métodos con implementación por defecto**.
*/

interface Imprimible {
    fun imprimir() // método abstracto: sin implementación aquí
}

/*
────────────────────────────────────────────────────────────
2. MÉTODOS CON IMPLEMENTACIÓN POR DEFECTO Y AYUDANTES PRIVADOS
────────────────────────────────────────────────────────────

- Las interfaces pueden tener métodos con cuerpo (default).
- Pueden declarar funciones privadas para reutilizarlas dentro de la interfaz.
- No existe `protected` en interfaces.
*/

interface Logeable {
    fun log(mensaje: String) {
        println("[${etiqueta()}] $mensaje")
    }

    // Método privado utilitario: solo puede usarse dentro de la interfaz.
    private fun etiqueta(): String = "LOG"
}

/*
────────────────────────────────────────────────────────────
3. PROPIEDADES EN INTERFACES (SIN BACKING FIELD)
────────────────────────────────────────────────────────────

- Se permiten propiedades, pero sin almacenamiento interno.
- Deben ser:
  a) abstractas (la clase las provee), o
  b) con getters por defecto que deriven su valor de otras propiedades/métodos.

- NO se permiten inicializadores del tipo `val x: Int = 10` dentro de una interfaz,
  porque implicarían un backing field (estado) que la interfaz no posee.
*/

interface Identificable {
    val id: String // abstracta: la clase debe proveerla
}

interface ConNombre {
    val nombre: String                            // abstracta
    val nombreEnMayusculas: String get() = nombre.uppercase() // getter por defecto (sin field)
}

/*
────────────────────────────────────────────────────────────
4. IMPLEMENTAR VARIAS INTERFACES EN UNA CLASE
────────────────────────────────────────────────────────────
*/

class Documento(
    override val id: String,
    override val nombre: String,
    val contenido: String
) : Imprimible, Logeable, Identificable, ConNombre {

    override fun imprimir() {
        log("Imprimiendo documento $id")
        println("Documento: $nombreEnMayusculas → $contenido")
    }
}

/*
────────────────────────────────────────────────────────────
5. MÚLTIPLES INTERFACES CON MÉTODOS EN CONFLICTO
super<Interface>.método() PARA RESOLVERLOS
────────────────────────────────────────────────────────────

- Si dos interfaces proveen un método con el mismo nombre y firma,
  la clase que las implementa DEBE sobrescribirlo.
- Dentro de esa sobrescritura, se puede llamar a implementaciones específicas con:
  super<NombreDeInterfaz>.metodo()
*/

interface A {
    fun demo() { println("Demo desde A") }
}

interface B {
    fun demo() { println("Demo desde B") }
}

class C : A, B {
    override fun demo() {
        // Resolución explícita del conflicto
        super<A>.demo()
        super<B>.demo()
        println("Demo combinada en C")
    }
}

/*
────────────────────────────────────────────────────────────
6. INTERFACES QUE EXTIENDEN OTRAS INTERFACES
────────────────────────────────────────────────────────────
*/

interface Persistente : Identificable, Logeable {
    fun guardar()
    fun cargar(): Boolean {
        log("Cargando recurso con id=$id")
        return true
    }
}

class UsuarioPersistente(
    override val id: String,
    val usuario: String
) : Persistente {
    override fun guardar() {
        log("Guardando usuario '$usuario' con id=$id")
    }
}

/*
────────────────────────────────────────────────────────────
7. VISIBILIDAD EN INTERFACES
────────────────────────────────────────────────────────────

- Los miembros de una interfaz son `public` por defecto.
- Se permite `private` para funciones/propiedades auxiliares usadas por métodos default.
- `protected` NO está permitido en interfaces.
- No hay estado interno; por tanto, no hay inicializadores con field.
*/


/*
────────────────────────────────────────────────────────────
8. DELEGACIÓN DE INTERFACES (by)
────────────────────────────────────────────────────────────

- Permite que una clase "delegue" la implementación de una interfaz a otra instancia.
- Útil para componer comportamiento sin herencia, y para envolver componentes.

Sintaxis:
    class X(private val impl: I) : I by impl { ...extras... }
*/

interface Repositorio {
    fun getAll(): List<String>
}

class RepositorioMemoria(
    private val datos: MutableList<String> = mutableListOf()
) : Repositorio {
    override fun getAll(): List<String> = datos.toList()
    fun add(item: String) { datos.add(item) }
}

class ServicioReporte(
    private val repo: Repositorio
) : Repositorio by repo { // delega getAll() en repo
    fun resumen(): String = getAll().joinToString(separator = ", ")
}

/*
────────────────────────────────────────────────────────────
9. FUN INTERFACES (SAM) — INTERFACES DE UN SOLO MÉTODO
────────────────────────────────────────────────────────────

- Una `fun interface` tiene **un único método abstracto**.
- Se puede implementar con **lambdas** directamente (SAM conversion).
- Ideal para filtros, transformaciones y callbacks simples.
*/

fun interface Transformador {
    fun aplica(x: Int): Int
}

fun procesar(datos: List<Int>, t: Transformador): List<Int> =
    datos.map { x -> t.aplica(x) }

/*
────────────────────────────────────────────────────────────
10. INTERFACES VS CLASES ABSTRACTAS (CRITERIOS PRÁCTICOS)
────────────────────────────────────────────────────────────

Usa INTERFAZ cuando:
- Buscas un contrato sin estado (múltiples implementaciones posibles).
- Requieres herencia múltiple de comportamiento (varias interfaces).
- Quieres permitir implementación con lambdas (fun interface).

Usa CLASE ABSTRACTA cuando:
- Necesitas **estado compartido** y lógica común no trivial.
- Quieres un punto único de extensión en una jerarquía con implementación parcial.
- Debes controlar el ciclo de vida/plantilla con inicializadores y propiedades con backing field.
*/


/*
────────────────────────────────────────────────────────────
11. EJECUCIÓN Y DEMOSTRACIÓN
────────────────────────────────────────────────────────────
*/

fun main() {

    println("────────────────────────────────────")
    println("INTERFACES — DEMOSTRACIONES")
    println("────────────────────────────────────\n")

    // 1) Contrato + default methods + propiedades derivadas (sin field)
    val doc = Documento(id = "DOC-1", nombre = "reporte", contenido = "Resultados Q3")
    doc.imprimir()
    println()

    // 2) Múltiples interfaces en conflicto: super<Interface>.método()
    val c = C()
    c.demo()
    println()

    // 3) Interfaces que extienden interfaces + defaults que usan el contrato
    val u = UsuarioPersistente(id = "U-100", usuario = "edward")
    u.guardar()
    println("Carga exitosa: ${u.cargar()}")
    println()

    // 4) Delegación de interfaces (by)
    val repo = RepositorioMemoria().also {
        it.add("alpha")
        it.add("beta")
        it.add("gamma")
    }
    val servicio = ServicioReporte(repo)
    println("Resumen desde servicio (delegación): ${servicio.resumen()}")
    println()

    // 5) Fun interface (SAM): pasar lambdas como implementaciones de interfaz
    val datos = listOf(1, 2, 3, 4)
    val doble = Transformador { x -> x * 2 }     // lambda como implementación de la interfaz
    val cuadrado = Transformador { x -> x * x }  // otra implementación con lambda

    println("Procesar (doble):   ${procesar(datos, doble)}")
    println("Procesar (cuadrado):${procesar(datos, cuadrado)}")
    println()

    println("✔ Fin de la demo de Interfaces.")
}

/*
────────────────────────────────────────────────────────────
12. CONCLUSIONES
────────────────────────────────────────────────────────────
1. Las interfaces definen contratos y permiten herencia múltiple de comportamiento.
2. Pueden tener métodos con implementación por defecto y funciones privadas auxiliares.
3. Las propiedades en interfaces no tienen backing field; se declaran abstractas o con getters.
4. Cuando hay conflicto de métodos por herencia múltiple, usa super<Interface>.método().
5. La delegación (`by`) ayuda a componer comportamiento sin herencia de clases.
6. Las fun interfaces (SAM) permiten usar lambdas como implementaciones concisas.
7. Elige interfaz o clase abstracta según si necesitas contrato sin estado o plantilla con estado.
────────────────────────────────────────────────────────────
*/
