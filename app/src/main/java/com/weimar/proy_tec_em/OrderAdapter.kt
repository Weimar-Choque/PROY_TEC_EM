package com.weimar.proy_tec_em

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val context: Context, private var orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderNameUser: TextView = itemView.findViewById(R.id.tvOrderNameUser)
        private val tvOrderEmail: TextView = itemView.findViewById(R.id.tvOrderEmail)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        private val tvOrderTimestamp: TextView = itemView.findViewById(R.id.tvOrderTimestamp)
        private val rvOrderItems: RecyclerView = itemView.findViewById(R.id.rvOrderItems)

        fun bind(order: Order) {
            tvOrderNameUser.text = "Nombre: ${order.nameuser}"
            tvOrderEmail.text = "Email: ${order.email}"
            tvOrderTotal.text = "Total: $${order.total}"
            tvOrderTimestamp.text = "Fecha: ${order.timestamp}"

            rvOrderItems.layoutManager = LinearLayoutManager(context)
            val orderItemAdapter = OrderItemAdapter(context, order.items)
            rvOrderItems.adapter = orderItemAdapter
        }
    }
}
