package com.example.ecommerceapplication.data

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val itemId: String,
val productImage: List<String>,
val productName: String ,
val productPrice: String ,
val totalPrice: Number,
val totalQuantity: Int


): Parcelable
