package com.weimar.proy_tec_em

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrdersActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        rvOrders = findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(this, mutableListOf())
        rvOrders.adapter = orderAdapter

        loadOrders()
    }

    private fun loadOrders() {
        db.collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                val orders = documents.map { document ->
                    document.toObject(Order::class.java)
                }
                orderAdapter.updateOrders(orders)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar pedidos: ${exception.message}", Toast.LENGTH_SHORT).show()
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
}
