package com.example.ecommerceapplication.data

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val customerID: String = "",
    val orderStatus: String = "",
    val totalPrice: Float = 0f,
    val products: List<Item> = listOf(),
    val orderTotal: Double = 0.0 ,
    val orderDate: String = "",
    val orderTime: String = "",
    val orderId: String = "",
    val address: CurrentUser = CurrentUser(),
    var items: List<Item> = listOf()
): Parcelable
