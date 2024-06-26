package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvResetPassword: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        tvResetPassword = findViewById(R.id.tvResetPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signIn(email, password)
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvResetPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            showToast("Por favor ingrese el correo electrónico")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor introduzca una dirección de correo electrónico válida")
            return false
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Por favor, ingrese contraseña")
            return false
        }

        if (password.length < 8) {
            showToast("La contraseña debe tener al menos 8 caracteres")
            return false
        }

        return true
    }

    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val role = document.getString("role")
                                    if (role == "admin") {
                                        val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val intent = Intent(this@LoginActivity, CategoryActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    finish()
                                } else {
                                    Log.d("Iniciar sesiónActividad", "No existe tal documento")
                                    showToast("No se pudo obtener el rol del usuario.")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Iniciar sesiónActividad", "fracasar con ", exception)
                                showToast("Error al obtener el rol del usuario.")
                            }
                    }
                } else {
                    // Inicio de sesión fallido
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "No se encontró ninguna cuenta con este correo electrónico."
                        is FirebaseAuthInvalidCredentialsException -> "Contraseña invalida."
                        else -> "Error de autenticación: ${task.exception?.message}"
                    }
                    showToast(errorMessage)
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
