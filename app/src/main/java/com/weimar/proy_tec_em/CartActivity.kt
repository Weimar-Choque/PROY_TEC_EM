    package com.weimar.proy_tec_em

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import java.util.Date

    class CartActivity : AppCompatActivity() {

        private lateinit var rvCartItems: RecyclerView
        private lateinit var cartItemAdapter: CartItemAdapter
        private lateinit var mAuth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_cart)

            mAuth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            rvCartItems = findViewById(R.id.rvCartItems)
            rvCartItems.layoutManager = LinearLayoutManager(this)
            cartItemAdapter = CartItemAdapter(this, mutableListOf()) { updateCartTotals() }
            rvCartItems.adapter = cartItemAdapter

            loadCartItems()
        }

        private fun loadCartItems() {
            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                db.collection("users").document(userId).collection("cart")
                    .get()
                    .addOnSuccessListener { documents ->
                        val cartItems = documents.map { document ->
                            document.toObject(CartItem::class.java)
                        }
                        cartItemAdapter.updateCartItems(cartItems)
                        calculateTotals(cartItems)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al cargar artículos del carrito: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        private fun calculateTotals(cartItems: List<CartItem>) {
            var subtotal = 0.0
            cartItems.forEach { item ->
                subtotal += item.price * item.quantity
            }
            val tax = subtotal * 0.1 // Example tax calculation
            val shipping = 5.0 // Example fixed shipping cost
            val total = subtotal + tax + shipping

            findViewById<TextView>(R.id.tvSubtotal).text = "Subtotal: $${"%.2f".format(subtotal)}"
            findViewById<TextView>(R.id.tvTax).text = "Tax: $${"%.2f".format(tax)}"
            findViewById<TextView>(R.id.tvShipping).text = "Shipping: $${"%.2f".format(shipping)}"
            findViewById<TextView>(R.id.tvTotal).text = "Total: $${"%.2f".format(total)}"

            findViewById<Button>(R.id.btnCheckout).setOnClickListener {
                // Logic for proceeding to checkout
                Toast.makeText(this, "Procediendo al pago", Toast.LENGTH_SHORT).show()
                // Guardar el pedido en Firestore
                saveOrder(cartItems, total)
                // Simulando la verificacion del pago
                val timer = object : Thread() {
                    override fun run() {
                        try {
                            sleep(2000)
                            runOnUiThread {
                                Toast.makeText(this@CartActivity, "Pago verificado correctamente!", Toast.LENGTH_SHORT).show()
                                // Iniciar la última actividad
                                val intent = Intent(this@CartActivity, PagoFinalActivity::class.java)
                                startActivity(intent)
                            }
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
                timer.start()
            }
        }

        private fun saveOrder(cartItems: List<CartItem>, total: Double) {
            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                // Obtener datos del cliente desde Firestore
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val nameuser = document.getString("name") ?: "Cliente desconocido"
                            val email = document.getString("email") ?: "Email desconocido"

                            // Crear el objeto del pedido con los datos del cliente
                            val order = hashMapOf(
                                "userId" to userId,
                                "nameuser" to nameuser,
                                "email" to email,
                                "items" to cartItems,
                                "total" to total,
                                "timestamp" to Date()
                            )

                            // Guardar el pedido en la colección "orders"
                            db.collection("orders")
                                .add(order)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Pedido guardado correctamente.", Toast.LENGTH_SHORT).show()
                                    // Limpiar carrito del usuario
                                    clearUserCart(userId)
                                    // Ir a la actividad de pago final
                                    val intent = Intent(this, PagoFinalActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, "Error al guardar el pedido: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "No se encontraron datos del usuario.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error al obtener datos del usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        private fun clearUserCart(userId: String) {
            db.collection("users").document(userId).collection("cart")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
        }

        private fun updateCartTotals() {
            calculateTotals(cartItemAdapter.getCartItems())
        }
    }
