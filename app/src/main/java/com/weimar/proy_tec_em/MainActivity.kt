package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            // Si no está autenticado, redirigir al LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Finalizar la MainActivity para que no vuelva atrás
        } else {
            // Si está autenticado, redirigir al ProductListActivity
            val intent = Intent(this@MainActivity, CategoryActivity::class.java)
            startActivity(intent)
            finish()  // Finalizar la MainActivity para que no vuelva atrás
        }
    }
}
