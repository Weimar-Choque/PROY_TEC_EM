package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var adminRadioButton: RadioButton
    private lateinit var adminCodeEditText: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializar vistas
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        roleRadioGroup = findViewById(R.id.roleRadioGroup)
        adminRadioButton = findViewById(R.id.adminRadioButton)
        adminCodeEditText = findViewById(R.id.adminCodeEditText)

        // Configurar evento de cambio en el RadioGroup
        roleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.adminRadioButton) {
                adminCodeEditText.visibility = View.VISIBLE
            } else {
                adminCodeEditText.visibility = View.GONE
            }
        }

        // Configurar evento click del botón de registro
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val role = if (adminRadioButton.isChecked) "admin" else "client"
            val adminCode = adminCodeEditText.text.toString().trim()

            if (validateInput(name, email, password, confirmPassword, role, adminCode)) {
                if (role == "admin") {
                    if (adminCode.isNullOrEmpty() || !verifyAdminCode(adminCode)) {
                        showToast("Código de administrador no válido.")
                        return@setOnClickListener
                    }
                }
                // Proceder con el registro del usuario
                registerUser(name,email, password, role)
            }
        }
    }

    private fun registerUser(name: String,email: String, password: String, role: String) {
        // Crear nuevo usuario con Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
                        // Guardar información del usuario en Firestore
                        saveUserToFirestore(userId, name, email, role)
                    } else {
                        Log.e("RegisterActivity", "Error: UID de usuario es nulo")
                        showToast("Error inesperado. Por favor, inténtelo de nuevo.")
                    }
                    // Enviar correo de verificación
                    mAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(this@RegisterActivity, "Registro exitoso. Por favor revise su correo electrónico para verificación.", Toast.LENGTH_SHORT).show()
                                val intent = if (role == "admin") {
                                    Intent(this@RegisterActivity, AdminActivity::class.java)
                                } else {
                                    Intent(this@RegisterActivity, CategoryActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@RegisterActivity, "No se pudo enviar el correo electrónico de verificación: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Registro fallido
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "No se encontró ninguna cuenta con este correo electrónico."
                        is FirebaseAuthInvalidCredentialsException -> "Contraseña invalida."
                        is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
                        else -> "Registro fallido: ${task.exception?.message}"
                    }
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(userId: String, name: String, email: String, role: String) {
        val user = hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "role" to role)

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Usuario guardado en Firestore con ID: $userId")
                Toast.makeText(this@RegisterActivity, "Usuario guardado en Firestore.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Error al guardar usuario en Firestore: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Error al guardar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(name: String,email: String, password: String, confirmPassword: String, role: String, adminCode: String): Boolean {
        if (name.isEmpty()) {
            showToast("Por favor ingrese su nombre.")
            return false
        }

        if (email.isEmpty()) {
            showToast("Por favor ingrese el correo electrónico.")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor ingrese un correo electrónico válido.")
            return false
        }

        if (password.isEmpty()) {
            showToast("Por favor, ingrese contraseña.")
            return false
        }

        if (password.length < 8) {
            showToast("La contraseña debe tener al menos 8 caracteres.")
            return false
        }

        if (password != confirmPassword) {
            showToast("Las contraseñas no coinciden.")
            return false
        }

        if (role == "admin" && adminCode.isEmpty()) {
            showToast("Por favor ingrese un código de administrador.")
            return false
        }

        return true
    }

    private fun verifyAdminCode(code: String): Boolean {
        val validAdminCodes = listOf("admin123", "supersecretcode")
        return validAdminCodes.contains(code)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
