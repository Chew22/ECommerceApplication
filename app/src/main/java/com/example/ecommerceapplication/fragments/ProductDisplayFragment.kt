package com.example.ecommerceapplication.fragment.Managing



import ProductAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceapplication.R
import com.example.ecommerceapplication.data.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class ProductDisplayFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private val productList: MutableList<Product> = mutableListOf()
    private val firestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.productRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Create and set up the adapter
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // In ProductDisplayFragment
        productAdapter.onClick = { selectedProduct ->
            val action = ProductDisplayFragmentDirections
                .actionProductdisplayFragmentToProductDetailViewFragment(selectedProduct)
            findNavController().navigate(action)
        }

        // Fetch products from Firestore
        fetchProducts()

        // Add Product FloatingActionButton
        val fabAddProduct: FloatingActionButton = view.findViewById(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            // Navigate to the product details fragment to add a new product
            findNavController().navigate(R.id.action_productdisplayFragment_to_productFragment)
        }

        return view
    }

    private fun fetchProducts() {
        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Get the sellerID
        val sellerID = currentUser?.uid ?: ""

        firestore.collection("Products")
            .whereEqualTo("sellerID", sellerID)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val newProducts = documents.map { it.toObject(Product::class.java) }
                    productAdapter.updateProducts(newProducts)
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("Firestore", "Error getting products", exception)
            }
    }

}