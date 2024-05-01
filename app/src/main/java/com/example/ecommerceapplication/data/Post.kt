package com.example.ecommerceapplication.data
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val postTitle: String = "",
    val postImages: List<String> = listOf(),
    val timestamp: Long = System.currentTimeMillis(),
    val shopName: String,
    val sellerID: String,
    val sellerName: String
    // Add more fields here that match the structure of your Firestore documents
): Parcelable {
    constructor(): this(
        postTitle = "",
        postImages = listOf(),
        timestamp = System.currentTimeMillis(),
        shopName = "",
        sellerID = "",
        sellerName = ""
    )
}



