package com.weimar.proy_tec_em

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var ivProductDetailImage: ImageView
    private lateinit var tvProductDetailName: TextView
    private lateinit var tvProductDetailPrice: TextView
    private lateinit var tvProductDetailDescription: TextView
    private lateinit var btnAddToCart: Button
    private lateinit var btnViewCart: Button
    private lateinit var progressBar: ProgressBar
    private val db = FirebaseFirestore.getInstance()
    private var productId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Inicializar vistas
        ivProductDetailImage = findViewById(R.id.ivProductDetailImage)
        tvProductDetailName = findViewById(R.id.tvProductDetailName)
        tvProductDetailPrice = findViewById(R.id.tvProductDetailPrice)
        tvProductDetailDescription = findViewById(R.id.tvProductDetailDescription)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnViewCart = findViewById(R.id.btnViewCart)
        progressBar = findViewById(R.id.progressBar)

        // Obtener productId de intent
        productId = intent.getStringExtra("productId")
        if (productId.isNullOrEmpty()) {
            handleInvalidProductId()
        } else {
            fetchProductDetails(productId!!)
        }

        // Listener para el botón de ver el carrito
        btnViewCart.setOnClickListener {
            viewCart()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchProductDetails(productId: String) {
        progressBar.visibility = View.VISIBLE
        db.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                val product = document.toObject<Product>()
                if (product != null) {
                    tvProductDetailName.text = product.name
                    tvProductDetailPrice.text = "$${product.price}"
                    tvProductDetailDescription.text = product.description
                    Glide.with(this).load(product.imageUrl).into(ivProductDetailImage)

                    // Listener para el botón de agregar al carrito
                    btnAddToCart.setOnClickListener {
                        addToCart(product)
                        Toast.makeText(this, "${product.name} Añadido al carrito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleProductNotFound()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                handleError(e)
            }
    }

    private fun addToCart(product: Product) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Debe iniciar sesión para agregar artículos a su carrito.", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = hashMapOf(
            "productId" to product.id,
            "name" to product.name,
            "price" to product.price,
            "imageUrl" to product.imageUrl,
            "quantity" to 1
        )

        db.collection("users").document(userId).collection("cart").document(product.id).set(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Producto añadido al carrito.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No se pudo agregar el producto al carrito: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun viewCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }

    private fun handleInvalidProductId() {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "ID del producto no válido", Toast.LENGTH_SHORT).show()
        finish() // Finaliza la actividad
    }


    private fun handleProductNotFound() {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Producto no encontrado.", Toast.LENGTH_SHORT).show()
        tvProductDetailName.text = "Producto no encontrado"
        tvProductDetailPrice.text = ""
        tvProductDetailDescription.text = ""
        ivProductDetailImage.setImageResource(R.drawable.ic_product_not_fount) // Imagen de marcador de posición
        btnAddToCart.visibility = View.GONE // Oculta el botón de agregar al carrito
    }

    private fun handleError(exception: Exception) {
        progressBar.visibility = View.GONE
        Toast.makeText(this, "No se pudieron cargar los detalles del producto: ${exception.message}", Toast.LENGTH_SHORT).show()
    }
}
