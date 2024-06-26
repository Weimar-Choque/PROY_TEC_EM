package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class CategoryActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Usar un GridLayoutManager para un diseño en cuadrícula
        recyclerView.adapter = CategoryAdapter(getCategories()) { category ->
            // Navegar a la actividad del catálogo con la categoría seleccionada
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra("category", category.name)
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

    private fun getCategories(): List<Category> {
        // Aquí podrías obtener las categorías desde una base de datos, una API, etc.
        return listOf(
            Category(R.drawable.alimentos, "Alimentos"),
            Category(R.drawable.accesorios, "Accesorios"),
            Category(R.drawable.higiene, "Higiene"),
            Category(R.drawable.salud, "Salud")
            // Añade más categorías según sea necesario
        )
    }
}
