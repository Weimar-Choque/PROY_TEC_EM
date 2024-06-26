package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Verificar el rol del usuario al iniciar AdminActivity
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        if (role != "admin") {
                            // Si el usuario ya no es administrador, redirigir a CategoryActivity
                            val intent = Intent(this@AdminActivity, CategoryActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // No se encontró el documento o no existe
                        showToast("No se pudo obtener el rol del usuario.")
                        signOutAndRedirectToLogin()
                    }
                }
                .addOnFailureListener { _ ->
                    // Error al obtener el rol del usuario
                    showToast("Error al obtener el rol del usuario.")
                    signOutAndRedirectToLogin()
                }
        }

        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)
        btnViewOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
    }
    // Configurar la barra de menú
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_list, menu)
        return true
    }

    // Manejar la selección de ítems del menú

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Cerrar sesión de Firebase
                mAuth.signOut()
                // Redirigir a la actividad de Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun signOutAndRedirectToLogin() {
        // Cerrar sesión y redirigir a LoginActivity
        mAuth.signOut()
        val intent = Intent(this@AdminActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
