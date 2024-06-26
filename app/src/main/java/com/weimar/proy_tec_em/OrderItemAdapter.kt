package com.weimar.proy_tec_em

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderItemAdapter(private val context: Context, private var items: List<CartItem>) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(item: CartItem) {
            tvProductName.text = item.name
            tvProductQuantity.text = "Cantidad: ${item.quantity}"
            tvProductPrice.text = "Precio: $${item.price}"
        }
    }
}
