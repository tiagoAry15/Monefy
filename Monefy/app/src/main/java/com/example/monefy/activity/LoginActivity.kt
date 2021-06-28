package com.example.monefy.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.monefy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mRegister: TextView
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mLoginSignIn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        mDatabase = FirebaseDatabase.getInstance()
        mAuth = FirebaseAuth.getInstance()
        mEmail = findViewById(R.id.login_edittext_email)
        mPassword = findViewById(R.id.login_edittext_password)
        mLoginSignIn = findViewById(R.id.login_button_entrar)
        mRegister = findViewById(R.id.login_textview_register)
        mRegister.setOnClickListener(this)
        mLoginSignIn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_textview_register -> {
                val it = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(it)
            }
            R.id.login_button_entrar -> {
                val email = mEmail.text.toString()
                val password = mPassword.text.toString()

                var isLoginFilled = true

                if (email.isEmpty()) {
                    mEmail.error = "Este campo não pode estar vazio"
                    isLoginFilled = false

                }

                if (password.isEmpty()) {
                    mPassword.error = "Este campo não pode estar vazio"
                    isLoginFilled = false

                }

                if (isLoginFilled) {
                    val dialog = ProgressDialog(LoginActivity@ this)
                    dialog.setTitle("ToDoList")
                    dialog.isIndeterminate = true
                    dialog.show()

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        dialog.dismiss()
                        if (it.isSuccessful) {
                            val it = Intent(applicationContext, MainActivity::class.java)
                            it.putExtra("Email",email)
                            startActivity(it)
                            finish()
                        } else {
                            showToastMessage("Usuário ou senha incorretos")
                        }

                    }


                }


            }
        }
    }

    private fun showToastMessage(text: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
        }
    }
}