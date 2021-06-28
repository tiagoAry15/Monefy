package com.example.monefy.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.monefy.R
import com.example.monefy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mName: EditText
    private lateinit var mTelefone: EditText
    private lateinit var mEmail: EditText
    private lateinit var mPassword: EditText
    private lateinit var mPasswordConfirm: EditText
    private lateinit var mSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mName = findViewById(R.id.register_edittext_name)
        mTelefone = findViewById(R.id.register_edittext_telefone)
        mEmail = findViewById(R.id.register_edittext_email)
        mPassword = findViewById(R.id.register_edittext_password)
        mPasswordConfirm = findViewById(R.id.register_edittext_passwordConfirm)

        mSignUp = findViewById(R.id.register_button_register)
        mSignUp.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v?.id) {
            R.id.register_button_register -> {
                val name = mName.text.toString()
                val telefone = mTelefone.text.toString()
                val email = mEmail.text.toString()
                val password = mPassword.text.toString()
                val passwordConfirm = mPasswordConfirm.text.toString()

                var isFormFilled = true

                if (name.isEmpty()) {
                    mName.error = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if (telefone.isEmpty()) {
                    mTelefone.error = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if (email.isEmpty()) {
                    mEmail.error = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if (password.isEmpty()) {
                    mPassword.error = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if (passwordConfirm.isEmpty()) {
                    mPasswordConfirm.error = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if (isFormFilled) {
                    if (password != passwordConfirm) {
                        mPasswordConfirm.error = "As senhas não coincidem"
                        return
                    }
                    val dialog = ProgressDialog(this)
                    dialog.setTitle("ToDoList")
                    dialog.isIndeterminate = true
                    dialog.show()

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            dialog.dismiss()
                            val handler = Handler(Looper.getMainLooper())
                            if (it.isSuccessful) {
                                val user = User(name, email, telefone)

                                val ref = mDatabase.getReference("users/${mAuth.uid!!}")
                                    ref.setValue(user)
                                handler.post{
                                    Toast.makeText(applicationContext,
                                        "Usuário cadastrado com sucesso",
                                        Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                            else{
                                handler.post{
                                    Toast.makeText(applicationContext,
                                        it.exception?.message,
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }


            }
        }
    }
}