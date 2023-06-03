package com.jesd_opsc_poe.chrono

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var tvReg: TextView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tvReg = findViewById(R.id.tvRegister)

        tvReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth

        val txtLoginEmail = findViewById<TextInputEditText>(R.id.txtLoginEmail)
        val txtLoginPassword = findViewById<TextInputEditText>(R.id.txtLoginPassword)
        val btnLogin = findViewById<AppCompatButton>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            val email = txtLoginEmail.text.toString()
            val password = txtLoginPassword.text.toString()

            //checks if fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {

                //login is performed (checks success and failure)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Authentication Successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, TimesheetActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Missing Fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}