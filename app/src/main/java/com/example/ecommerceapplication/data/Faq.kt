package com.example.ecommerceapplication.data

data class Faq(
    val title: String,
    val logo: Int,
    val desc: String,
    var isExpandable: Boolean = false
)
