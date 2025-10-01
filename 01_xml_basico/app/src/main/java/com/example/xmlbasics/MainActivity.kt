package com.example.xmlbasics

// Importación de clases necesarias para trabajar con actividades y UI moderna
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    // Método que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita el modo edge-to-edge (pantalla completa sin bordes)
        enableEdgeToEdge()

        // Cargar layout de la interfaz actual
        // 🔹 Capítulo 1: Fundamentos de interfaces en XML
        // setContentView(R.layout.basic_interface)

        // 🔹 Capítulo 2: ScrollView – Diseño deslizable en Android
        setContentView(R.layout.basic_scrollview)

        // Ajuste automático de padding para respetar las barras del sistema (status bar, nav bar)
        // Evita que los elementos se solapen con la barra superior/inferior del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
