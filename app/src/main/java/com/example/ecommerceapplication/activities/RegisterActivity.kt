package com.example.ecommerceapplication.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerceapplication.R
import com.example.ecommerceapplication.viewmodel.RegisterSharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    // Use by viewModels delegate to automatically create and retain the ViewModel
    private val sharedViewModel: RegisterSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
}
