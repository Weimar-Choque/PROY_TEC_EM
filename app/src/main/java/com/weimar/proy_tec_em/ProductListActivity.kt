package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject

class ProductListActivity : AppCompatActivity() {

    private lateinit var rvProductList: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val category = intent.getStringExtra("category") ?: ""

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Configurar RecyclerView
        rvProductList = findViewById(R.id.rvProductList)
        rvProductList.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(this, productList)
        rvProductList.adapter = productAdapter

        // Configurar el listener de estado de autenticación
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // Redirigir al LoginActivity si el usuario no está autenticado
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        // Obtener productos
        fetchProducts()
        fetchProductsByCategory(category)
    }

    private fun fetchProductsByCategory(category: String) {
        db.collection("products")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                handleSuccess(result)
            }
            .addOnFailureListener { exception ->
                Log.w("ProductListActivity", "Error al obtener documentos: ", exception)
            }
    }

    private fun handleSuccess(result: QuerySnapshot) {
        productList.clear()
        for (document in result) {
            val product = document.toObject(Product::class.java)
            productList.add(product)
        }
        productAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(authListener)
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

    // Obtener productos de Firestore
    private fun fetchProducts() {
        db.collection("products").get()
            .addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val product = document.toObject<Product>()
                    productList.add(product)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w("ProductListActivity", "Error al obtener documentos:", e)
                Toast.makeText(this, "No se pudieron cargar los productos", Toast.LENGTH_SHORT).show()
            }
    }
}
