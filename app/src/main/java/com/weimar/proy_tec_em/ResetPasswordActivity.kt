package com.weimar.proy_tec_em

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail)
        btnResetPassword = findViewById(R.id.btnResetPassword)


        // Configurar evento click del botón de reset password
        btnResetPassword.setOnClickListener {
            val email = etEmail.text.toString()
            if (validateInput(email)) {
                resetPassword(email)
            }

        }
    }

    private fun resetPassword(email: String) {


        // Enviar email de recuperación de contraseña
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Correo electrónico para restablecer contraseña enviado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()

                }
            }
    }
    private fun validateInput(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            showToast("Por favor ingrese el correo electrónico")
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Por favor introduzca una dirección de correo electrónico válida")
            return false
        }


        return true
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
