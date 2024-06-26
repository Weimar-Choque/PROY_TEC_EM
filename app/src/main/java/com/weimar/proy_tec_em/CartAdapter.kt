package com.weimar.proy_tec_em

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class CartItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Int = 0,
    val imageUrl: String = ""
)

class CartItemAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    inner class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.ivCartItemProductImage)
        val productName: TextView = view.findViewById(R.id.tvCartItemProductName)
        val productPrice: TextView = view.findViewById(R.id.tvCartItemProductPrice)
        val productQuantity: TextView = view.findViewById(R.id.tvCartItemProductQuantity)
        val btnIncreaseQuantity: Button = view.findViewById(R.id.btnIncreaseQuantity)
        val btnDecreaseQuantity: Button = view.findViewById(R.id.btnDecreaseQuantity)
        val btnRemoveItem: Button = view.findViewById(R.id.btnRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.productName.text = cartItem.name
        holder.productPrice.text = "$${cartItem.price}"
        holder.productQuantity.text = "Quantity: ${cartItem.quantity}"
        Glide.with(context).load(cartItem.imageUrl).into(holder.productImage)

        holder.btnIncreaseQuantity.setOnClickListener {
            cartItems[position].quantity++
            notifyItemChanged(position)
            updateCartItemInFirestore(cartItems[position])
            onCartUpdated()
        }

        holder.btnDecreaseQuantity.setOnClickListener {
            if (cartItems[position].quantity > 1) {
                cartItems[position].quantity--
                notifyItemChanged(position)
                updateCartItemInFirestore(cartItems[position])
                onCartUpdated()
            }
        }

        holder.btnRemoveItem.setOnClickListener {
            removeCartItemFromFirestore(cartItems[position], position)
            cartItems.removeAt(position)
            notifyItemRemoved(position)
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    private fun updateCartItemInFirestore(cartItem: CartItem) {
        userId?.let { userId ->
            db.collection("users").document(userId).collection("cart").document(cartItem.productId)
                .set(cartItem)
                .addOnSuccessListener {
                    // Successfully updated item in Firestore
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                    Toast.makeText(context, "Failed to update item: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeCartItemFromFirestore(cartItem: CartItem, position: Int) {
        userId?.let { userId ->
            db.collection("users").document(userId).collection("cart").document(cartItem.productId)
                .delete()
                .addOnSuccessListener {
                    // Successfully removed item from Firestore
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                }
                .addOnFailureListener { e ->
                    // Handle any errors here
                    Toast.makeText(context, "Failed to remove item: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
