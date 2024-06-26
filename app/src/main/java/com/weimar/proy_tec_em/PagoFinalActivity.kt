package com.weimar.proy_tec_em

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PagoFinalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_final)
        //Mostrar mensaje de confirmacion
        val textViewConfirmacion = findViewById<TextView>(R.id.textViewConfirmacion)
        textViewConfirmacion.text = "Pago realizado con exito!"

    }
}