package com.example.xmlkotlin

/*
────────────────────────────────────────────────────────────
CAPÍTULO 3 (INTERACCIÓN AVANZADA CON XML + KOTLIN)
────────────────────────────────────────────────────────────

FUNCIONALIDADES COMPLETAS:
✔ ScrollView (contenido desplazable)
✔ Campo de fecha (MaterialDatePicker)
✔ Confirmación (AlertDialog)
✔ Feedback visual (Snackbar)
✔ Chips dinámicos (actualizan el resumen)
✔ Resumen visual y resultado textual
────────────────────────────────────────────────────────────
*/

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.card.MaterialCardView
import com.example.xmlkotlin.databinding.ActivityMainAdvancedBinding

class MainActivityAdvanced : AppCompatActivity() {

    private lateinit var binding: ActivityMainAdvancedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdvancedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste para notch y barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        ─────────────────────────────────────────────
        REFERENCIAS A LAS VISTAS (binding + clásicos)
        ─────────────────────────────────────────────
        */
        val ivLogo = binding.ivLogo
        val etNombre = binding.etNombre
        val tilNombre = binding.tilNombre
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val rgGenero = findViewById<RadioGroup>(R.id.rgGenero)
        val rbMasculino = findViewById<RadioButton>(R.id.rbMasculino)
        val rbFemenino = findViewById<RadioButton>(R.id.rbFemenino)
        val cbSuscripcion = findViewById<CheckBox>(R.id.cbSuscripcion)
        val swModoOscuro = findViewById<MaterialSwitch>(R.id.swModoOscuro)
        val sliderEdad = findViewById<Slider>(R.id.sliderEdad)
        val btnEnviar = binding.btnEnviar
        val progressEnvio = binding.progressEnvio
        val tvResultado = binding.tvResultado
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupIntereses)
        val tvResumenCard = findViewById<TextView>(R.id.tvResumenCard)
        val btnRefrescar = findViewById<ImageButton>(R.id.btnRefrescar)

        /*
        ─────────────────────────────────────────────
        IMAGEN INICIAL (descargada con Glide)
        ─────────────────────────────────────────────
        */
        val imageUrl = "https://images.alphacoders.com/130/1303667.jpg"
        Glide.with(this).load(imageUrl).circleCrop().into(ivLogo)

        /*
        ─────────────────────────────────────────────
        DATE PICKER → Selección de fecha de nacimiento
        ─────────────────────────────────────────────
        */
        etFecha.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.hint_birthdate))
                .build()
            datePicker.addOnPositiveButtonClickListener {
                etFecha.setText(datePicker.headerText)
            }
            datePicker.show(supportFragmentManager, "datePicker")
        }

        /*
        ─────────────────────────────────────────────
        CHIPS → Selección múltiple de intereses
        ─────────────────────────────────────────────
        */
        val actualizarResumen: () -> Unit = {
            val seleccionados = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) seleccionados.add(chip.text.toString())
            }
            tvResumenCard.text = if (seleccionados.isEmpty()) {
                getString(R.string.no_intereses)
            } else {
                getString(R.string.intereses_prefix) + " " +
                        seleccionados.joinToString(", ")
            }
        }
        chipGroup.setOnCheckedStateChangeListener { _, _ -> actualizarResumen() }

        /*
        ─────────────────────────────────────────────
        BOTÓN ENVIAR → Validación + Diálogo + Resultado
        ─────────────────────────────────────────────
        */
        btnEnviar.setOnClickListener {
            val nombre = etNombre.text?.toString()?.trim().orEmpty()
            if (nombre.isEmpty()) {
                tilNombre.error = getString(R.string.error_nombre_requerido)
                etNombre.requestFocus()
                return@setOnClickListener
            } else tilNombre.error = null

            val genero = when (rgGenero.checkedRadioButtonId) {
                rbMasculino.id -> getString(R.string.text_gender_male)
                rbFemenino.id -> getString(R.string.text_gender_female)
                else -> getString(R.string.text_gender_unknown)
            }

            val edad = sliderEdad.value.toInt()
            val fecha = etFecha.text.toString().ifEmpty { getString(R.string.text_no_definida) }
            val suscripcion = if (cbSuscripcion.isChecked)
                getString(R.string.text_si)
            else
                getString(R.string.text_no)

            val intereses = tvResumenCard.text.toString()

            val resumen = getString(
                R.string.resumen_datos,
                nombre, fecha, genero, edad, suscripcion, intereses
            )

            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_titulo_confirmar))
                .setMessage(getString(R.string.dialog_mensaje_confirmar))
                .setPositiveButton(getString(R.string.text_si)) { _, _ ->
                    progressEnvio.visibility = View.VISIBLE
                    btnEnviar.isEnabled = false

                    progressEnvio.postDelayed({
                        progressEnvio.visibility = View.GONE
                        btnEnviar.isEnabled = true
                        tvResultado.text = resumen
                        Snackbar.make(
                            binding.main,
                            getString(R.string.msg_enviado_ok),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }, 900)
                }
                .setNegativeButton(getString(R.string.text_no), null)
                .create()

            dialog.show()
        }

        /*
        ─────────────────────────────────────────────
        BOTÓN REFRESCAR → Reinicia todo el formulario
        ─────────────────────────────────────────────
        */
        btnRefrescar.setOnClickListener {
            etNombre.setText("")
            etFecha.setText("")
            rgGenero.clearCheck()
            cbSuscripcion.isChecked = false
            swModoOscuro.isChecked = false
            sliderEdad.value = 25f
            chipGroup.clearCheck()
            tvResultado.text = getString(R.string.msg_form_reiniciado)
            tvResumenCard.text = getString(R.string.msg_card_placeholder)
            Glide.with(this).load(imageUrl).circleCrop().into(ivLogo)

            Snackbar.make(
                btnRefrescar,
                getString(R.string.msg_refresh),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
