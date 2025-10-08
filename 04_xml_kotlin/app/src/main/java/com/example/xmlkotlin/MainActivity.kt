package com.example.xmlkotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 1 (INTEGRACIÓN XML + KOTLIN)
CONEXIÓN E INTERACCIÓN BÁSICA CON LA PLANTILLA XML MODERNA
────────────────────────────────────────────────────────────

OBJETIVO DE ESTE ARCHIVO:
- Conectar cada vista de la plantilla XML con Kotlin usando findViewById().
- Leer valores ingresados por el usuario (texto, selecciones, switches, sliders).
- Reaccionar a eventos (onClick, onCheckedChange, onChange del Slider).
- Modificar la UI en tiempo real (texto, visibilidad, color de imagen).
- Simular un "proceso" mostrando un ProgressBar y actualizando el resultado.

ESTRUCTURA:
1) setContentView() vincula la Activity con el layout XML.
2) findViewById() obtiene referencias a las vistas por su id.
3) Listeners registran acciones del usuario.
4) Al presionar "Enviar", se:
   - Valida el nombre
   - Lee género, suscripción, modo oscuro y edad (Slider)
   - Muestra ProgressBar (simulación breve)
   - Actualiza TextView de resultado
   - Cambia el tinte del logo según selección
   - Muestra un Toast de confirmación

────────────────────────────────────────────────────────────
AÑADIDO NUEVO: EDGE-TO-EDGE Y AJUSTE DE WINDOW INSETS
────────────────────────────────────────────────────────────
Android Studio (desde 2023 en adelante) incluye por defecto el modo "edge-to-edge",
que permite que la aplicación use toda la pantalla, incluyendo debajo del notch o
de las barras de navegación del sistema.

- enableEdgeToEdge() → activa el modo de pantalla completa moderna.
- ViewCompat.setOnApplyWindowInsetsListener() → ajusta márgenes automáticamente
  para que el contenido no quede tapado por barras del sistema.

⚠ IMPORTANTE: El layout raíz de activity_main.xml debe tener:
    android:id="@+id/main"
────────────────────────────────────────────────────────────
AÑADIDO NUEVO: VIEW BINDING Y GLIDE
────────────────────────────────────────────────────────────
- View Binding: es el método moderno para acceder a las vistas sin usar
  findViewById(). Evita errores y mejora la legibilidad. Se activa en
  build.gradle con:
        buildFeatures {
            viewBinding true
        }

- Glide: es una librería moderna para cargar imágenes locales o desde Internet
  dentro de un ImageView, con caché y transformaciones visuales. Se agrega en
  build.gradle:
        implementation 'com.github.bumptech.glide:glide:4.16.0'
        annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'

────────────────────────────────────────────────────────────
*/

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import android.content.res.ColorStateList
import android.widget.RadioGroup
import android.widget.ImageView

// Nuevos imports necesarios para el modo moderno
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// NUEVOS IMPORTS AÑADIDOS
import com.bumptech.glide.Glide                       // Librería para manejo de imágenes
import com.example.xmlkotlin.databinding.ActivityMainBinding // Clase generada automáticamente por View Binding

class MainActivity : AppCompatActivity() {

    // ViewBinding: referencia moderna al layout (en lugar de usar solo findViewById)
    private lateinit var binding: ActivityMainBinding

    // Punto de entrada de la Activity en Android
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        1) VINCULAR LAYOUT XML CON ESTA ACTIVITY
        - enableEdgeToEdge() permite usar el área completa de la pantalla.
        - setContentView() infla el archivo XML y lo "pega" a esta pantalla.
        - A partir de aquí, los ids del XML están disponibles para findViewById().

        NUEVO:
        Ahora utilizamos también View Binding:
        - binding = ActivityMainBinding.inflate(layoutInflater)
        - setContentView(binding.root)
        Esto reemplaza la necesidad de buscar vistas por id en algunos casos.
        */
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        1.1) BLOQUE ViewCompat PARA AJUSTAR INSETS DEL SISTEMA
        Este fragmento garantiza que el contenido no se superponga con las barras
        del sistema (como el notch o la barra de gestos inferiores).
        El padding se ajusta automáticamente con WindowInsetsCompat.
        */
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        2) OBTENER REFERENCIAS A CADA VISTA (findViewById y ViewBinding)
        - Cada id proviene de activity_main.xml (debe coincidir exactamente).
        - Aquí combinamos ambos métodos para fines educativos.
        */
        val ivLogo        = binding.ivLogo                     //  View Binding
        val tvTitulo      = binding.tvTitulo                   //  View Binding
        val btnEnviar     = binding.btnEnviar                  //  View Binding
        val progressEnvio = binding.progressEnvio              //  View Binding
        val tvResultado   = binding.tvResultado                //  View Binding

        // Método tradicional (findViewById)
        val tilNombre     = findViewById<TextInputLayout>(R.id.tilNombre)
        val etNombre      = findViewById<TextInputEditText>(R.id.etNombre)
        val rgGenero      = findViewById<RadioGroup>(R.id.rgGenero)
        val rbMasculino   = findViewById<RadioButton>(R.id.rbMasculino)
        val rbFemenino    = findViewById<RadioButton>(R.id.rbFemenino)
        val cbSuscripcion = findViewById<CheckBox>(R.id.cbSuscripcion)
        val swModoOscuro  = findViewById<MaterialSwitch>(R.id.swModoOscuro)
        val sliderEdad    = findViewById<Slider>(R.id.sliderEdad)

        /*
        3) CONFIGURACIÓN INICIAL (opcional)
        - Puedes ajustar textos por defecto, visibilidad inicial, etc.
        */
        tvTitulo.text = "Formulario general de usuario"
        progressEnvio.visibility = View.GONE // Aseguramos que empiece oculto

        /*
        4) LISTENER DEL SWITCH (solo demo)
        - No activamos tema oscuro real; solo mostramos un Toast para practicar.
        */
        swModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Modo oscuro activado (demo)" else "Modo oscuro desactivado (demo)"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        /*
        5) LISTENER DEL SLIDER
        - Cada vez que el usuario mueva el control, podemos leer su valor (float).
        - Aquí solo mostramos un Toast breve para que el usuario sienta respuesta.
        */
        sliderEdad.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                Toast.makeText(this, "Edad seleccionada: ${value.toInt()} años", Toast.LENGTH_SHORT).show()
            }
        }

        /*
        6) LISTENER DEL BOTÓN "ENVIAR"
        - Flujo completo de lectura de datos + actualización de UI.
        */
        btnEnviar.setOnClickListener {

            // 6.1) LEER NOMBRE y VALIDAR (con TextInputLayout para mostrar error visual si falta)
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                tilNombre.error = "Por favor, ingresa tu nombre"
                etNombre.requestFocus()
                return@setOnClickListener
            } else {
                tilNombre.error = null // limpiamos error si todo bien
            }

            // 6.2) DETERMINAR GÉNERO SELECCIONADO (RadioGroup)
            val generoSeleccionado = when (rgGenero.checkedRadioButtonId) {
                rbMasculino.id -> "Masculino"
                rbFemenino.id  -> "Femenino"
                else           -> "No especificado"
            }

            // 6.3) LEER CHECKBOX (suscripción)
            val suscrito = if (cbSuscripcion.isChecked) "Sí" else "No"

            // 6.4) LEER SWITCH (modo oscuro - solo demostración)
            val modoOscuroTexto = if (swModoOscuro.isChecked) "Activado (demo)" else "Desactivado (demo)"

            // 6.5) LEER SLIDER (edad)
            val edadSeleccionada = sliderEdad.value.toInt()

            // 6.6) MOSTRAR UN PROGRESS BAR DE MANERA SIMULADA
            progressEnvio.visibility = View.VISIBLE
            btnEnviar.isEnabled = false  // bloqueamos el botón durante el "proceso"

            // 6.7) CONSTRUIR MENSAJE DE RESULTADO (texto multilínea)
            val resumen = """
                Nombre: $nombre
                Género: $generoSeleccionado
                Suscripción: $suscrito
                Modo oscuro: $modoOscuroTexto
                Edad (slider): $edadSeleccionada
            """.trimIndent()

            /*
            6.8) SIMULACIÓN DE PROCESO:
            - Usamos postDelayed() para "esperar" 900 ms y luego actualizar la UI.
            - Esto imita una operación de red o procesamiento corto.
            - Aquí también aplicamos GLIDE para cargar una imagen desde Internet.
            */
            progressEnvio.postDelayed({

                // Ocultamos progreso y reactivamos botón
                progressEnvio.visibility = View.GONE
                btnEnviar.isEnabled = true

                // Mostramos el resultado final
                tvResultado.text = resumen

                // Ajuste visual del color del boton (tinte) según género para reforzar feedback:
                val colorRes = when (generoSeleccionado) {
                    "Masculino" -> android.R.color.holo_blue_dark
                    "Femenino"  -> android.R.color.holo_red_light
                    else        -> android.R.color.darker_gray
                }

                btnEnviar.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, colorRes)
                )

                /*
                NUEVO BLOQUE: USO DE GLIDE
                - Carga una imagen remota en el ImageView (ivLogo)
                - Glide administra la memoria y la caché automáticamente.
                */
                val imageUrl = "https://images.alphacoders.com/130/1303667.jpg"
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person_black_24dp) // Imagen temporal mientras carga
                    .circleCrop() // Forma circular
                    .into(ivLogo)

                // Confirmación visual rápida (Toast)
                Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

            }, 900) // 900 ms ~ 0.9 s
        }
    }
}
