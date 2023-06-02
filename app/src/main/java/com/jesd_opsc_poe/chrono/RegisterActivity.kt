package com.jesd_opsc_poe.chrono

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
class RegisterActivity : AppCompatActivity() {

    private lateinit var tvLogIn: TextView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tvLogIn = findViewById(R.id.tvLogin)

        tvLogIn.setOnClickListener{
            val intent = Intent(this, TimesheetActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = Firebase.auth

        val txtRegisterEmail = findViewById<TextInputEditText>(R.id.txtRegisterEmail)
        val txtRegisterPassword = findViewById<TextInputEditText>(R.id.txtRegisterPassword)
        val btnRegister = findViewById<AppCompatButton>(R.id.btnRegister)

        btnRegister.setOnClickListener(){
            val email = txtRegisterEmail.text.toString()
            val password = txtRegisterPassword.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }





    }
}
