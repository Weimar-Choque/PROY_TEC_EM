package com.weimar.proy_tec_em

import java.util.Date

data class Order(
    val userId: String = "",
    val nameuser: String = "",
    val email: String = "",
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val timestamp: Date = Date()
)
