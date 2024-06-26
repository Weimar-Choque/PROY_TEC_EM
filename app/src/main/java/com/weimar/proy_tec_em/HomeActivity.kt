package com.weimar.proy_tec_em

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val btnContinue: Button = findViewById(R.id.btnContinue)
        btnContinue.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad para que no se pueda regresar a ella con el botón de retroceso
        }
    }
}

