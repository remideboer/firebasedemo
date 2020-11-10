package com.monolithical.firebasedemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.monolithical.firebasedemo.databinding.ActivityLoginBinding

/**
 * Eerst demo met google login
 */
class LoginActivity : AppCompatActivity() {
    // 1
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up viewbinding (niet in module gradle vergeten aan te zetten)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 2
        auth = FirebaseAuth.getInstance()
    }

    // 3
    override fun onStart() {
        super.onStart()
        // check of gebruiker is ingelogd, zo ja
        val user = auth.currentUser
        if(user != null){
            // user in ingelogd ga naar MainActivity
            // anders hier blijven
            // explicit intent nodig
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // ! finish de LoginActivity anders blijft deze op de stack staan
            finish()
        }

    }

}