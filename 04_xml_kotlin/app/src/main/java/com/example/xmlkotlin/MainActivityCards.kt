package com.example.xmlkotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3 — INTEGRACIÓN AVANZADA DE XML + KOTLIN
────────────────────────────────────────────────────────────
OBJETIVOS DEL CAPÍTULO:
────────────────────────────────────────────────────────────
• Practicar el acceso moderno a vistas con ViewBinding.
• Manipular componentes de Material Design 3:
  - ChipGroup (selección múltiple)
  - MaterialCardView (resumen dinámico)
  - ImageButton (acción rápida)
• Aplicar Glide para cambiar imágenes según entradas del usuario.
• Simular un flujo de formulario con validación, feedback visual
  y actualización dinámica de la interfaz.
────────────────────────────────────────────────────────────
ESTRUCTURA:
────────────────────────────────────────────────────────────
1) Configuración inicial (Edge-to-Edge + ViewBinding)
2) Referencia y configuración de vistas.
3) Listeners y acciones del usuario.
4) Actualización dinámica de UI y Glide.
5) Reset de formulario con ImageButton.
────────────────────────────────────────────────────────────
*/

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.xmlkotlin.databinding.ActivityMainCardsBinding
import com.google.android.material.chip.Chip

class MainActivityCards : AppCompatActivity() {

    // ViewBinding → acceso moderno a las vistas del layout (sin findViewById)
    private lateinit var binding: ActivityMainCardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        ─────────────────────────────────────────────────────────────
        1️⃣ CONFIGURACIÓN INICIAL — Edge-to-Edge + Binding
        ─────────────────────────────────────────────────────────────
        enableEdgeToEdge(): usa toda la pantalla (oculta barras).
        binding = ActivityMainCardsBinding.inflate(layoutInflater):
        infla el layout "activity_main_cards.xml" y lo asocia a la actividad.
        ─────────────────────────────────────────────────────────────
        */
        enableEdgeToEdge()
        binding = ActivityMainCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste automático de padding en pantallas con notch o barra
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        ─────────────────────────────────────────────────────────────
        2️⃣ REFERENCIAS A VISTAS (todo mediante Binding)
        ─────────────────────────────────────────────────────────────
        */
        val ivLogo = binding.ivLogo
        val tvTitulo = binding.tvTitulo
        val tilNombre = binding.tilNombre
        val etNombre = binding.etNombre
        val rgGenero = binding.rgGenero
        val rbMasculino = binding.rbMasculino
        val rbFemenino = binding.rbFemenino
        val cbSuscripcion = binding.cbSuscripcion
        val swModoOscuro = binding.swModoOscuro
        val sliderEdad = binding.sliderEdad
        val btnEnviar = binding.btnEnviar
        val progressEnvio = binding.progressEnvio
        val tvResultado = binding.tvResultado
        val chipGroup = binding.chipGroupIntereses
        val cardResumen = binding.cardResumen
        val tvResumenCard = binding.tvResumenCard
        val btnRefrescar = binding.btnRefrescar

        /*
        ─────────────────────────────────────────────────────────────
        3️⃣ CONFIGURACIÓN INICIAL — textos, imagen y visibilidad
        ─────────────────────────────────────────────────────────────
        */
        tvTitulo.text = "Formulario avanzado con Material Design"
        progressEnvio.visibility = View.GONE

        // Imagen inicial mediante Glide
        val imageUrl = "https://images.alphacoders.com/130/1303667.jpg"
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.outline_account_circle_24)
            .circleCrop()
            .into(ivLogo)

        /*
        ─────────────────────────────────────────────────────────────
        4️⃣ LISTENERS PRINCIPALES
        ─────────────────────────────────────────────────────────────
        */

        // Switch → simula el cambio de tema
        swModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Modo oscuro (simulado)" else "Modo claro (simulado)"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        // Slider → muestra la edad seleccionada
        sliderEdad.addOnChangeListener { _, value, fromUser ->
            if (fromUser) Toast.makeText(this, "Edad: ${value.toInt()} años", Toast.LENGTH_SHORT).show()
        }

        // CHIPGROUP → cada vez que cambia la selección, actualiza el resumen
        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            val seleccionados = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) seleccionados.add(chip.text.toString())
            }
            tvResumenCard.text = if (seleccionados.isEmpty()) {
                "Sin intereses seleccionados"
            } else {
                "Intereses: ${seleccionados.joinToString(", ")}"
            }
        }

        /*
        ─────────────────────────────────────────────────────────────
        5️⃣ BOTÓN ENVIAR — validación, procesamiento y feedback visual
        ─────────────────────────────────────────────────────────────
        */
        btnEnviar.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                tilNombre.error = "Por favor, ingresa tu nombre"
                etNombre.requestFocus()
                return@setOnClickListener
            } else tilNombre.error = null

            val generoSeleccionado = when (rgGenero.checkedRadioButtonId) {
                rbMasculino.id -> "Masculino"
                rbFemenino.id -> "Femenino"
                else -> "No especificado"
            }

            val suscrito = if (cbSuscripcion.isChecked) "Sí" else "No"
            val edadSeleccionada = sliderEdad.value.toInt()

            // Extraer intereses seleccionados
            val intereses = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) intereses.add(chip.text.toString())
            }
            val textoIntereses = if (intereses.isEmpty()) "Sin intereses" else intereses.joinToString(", ")

            // Simulación visual de envío
            progressEnvio.visibility = View.VISIBLE
            btnEnviar.isEnabled = false

            progressEnvio.postDelayed({
                progressEnvio.visibility = View.GONE
                btnEnviar.isEnabled = true

                // Mostrar resultado textual
                tvResultado.text = """
                    Nombre: $nombre
                    Género: $generoSeleccionado
                    Suscripción: $suscrito
                    Edad: $edadSeleccionada
                    Intereses: $textoIntereses
                """.trimIndent()

                // Actualizar tarjeta resumen
                tvResumenCard.text = "Resumen: $nombre ($edadSeleccionada años, $generoSeleccionado)\nIntereses: $textoIntereses"

                // Cambiar color del botón según género
                val colorRes = when (generoSeleccionado) {
                    "Masculino" -> android.R.color.holo_blue_dark
                    "Femenino" -> android.R.color.holo_red_light
                    else -> android.R.color.darker_gray
                }
                btnEnviar.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, colorRes)
                )

                // Actualizar imagen según género con Glide
                val url = when (generoSeleccionado) {
                    "Masculino" -> "https://4kwallpapers.com/images/wallpapers/tiger-dope-amoled-2560x2560-18370.jpg"
                    "Femenino" -> "https://i.pinimg.com/236x/38/f4/3d/38f43d4cf336f94b6ac7d10afe71993a.jpg"
                    else -> "https://images.alphacoders.com/130/1303667.jpg"
                }
                Glide.with(this).load(url).circleCrop().into(ivLogo)

                Toast.makeText(this, "Datos procesados correctamente", Toast.LENGTH_SHORT).show()
            }, 1000)
        }

        /*
        ─────────────────────────────────────────────────────────────
        6️⃣ BOTÓN REFRESCAR — limpiar y reiniciar el formulario
        ─────────────────────────────────────────────────────────────
        */
        btnRefrescar.setOnClickListener {
            etNombre.setText("")
            rgGenero.clearCheck()
            cbSuscripcion.isChecked = false
            swModoOscuro.isChecked = false
            sliderEdad.value = 25f
            chipGroup.clearCheck()
            tvResultado.text = "Formulario reiniciado."
            tvResumenCard.text = "Aquí aparecerá un resumen interactivo"

            // Restaurar imagen original
            Glide.with(this).load(imageUrl).circleCrop().into(ivLogo)

            Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show()
        }

        /*
        ─────────────────────────────────────────────────────────────
        7️⃣ MENSAJE INICIAL
        ─────────────────────────────────────────────────────────────
        */
        Toast.makeText(this, "Capítulo 3 cargado correctamente ✅", Toast.LENGTH_SHORT).show()
    }
}
