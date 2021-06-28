package com.example.monefy.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.example.monefy.R
import com.example.monefy.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat

class TransactionFormActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mDescription: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mValue: EditText
    private lateinit var mRadioExpense: RadioButton
    private lateinit var mRadioRevenue: RadioButton
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mRegisterBtn: Button
    private lateinit var mDatabase: FirebaseDatabase
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mTaskId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_form)

        mDescription = findViewById(R.id.transform_plaintext_description)
        mRadioExpense = findViewById(R.id.transform_radio_expense)
        mRadioRevenue = findViewById(R.id.transform_radio_revenue)
        mRegisterBtn = findViewById(R.id.transform_button_register)
        mRadioGroup = findViewById(R.id.transform_radioGroup)
        mValue = findViewById(R.id.transform_edittext_value)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRegisterBtn.setOnClickListener(this)

        mValue.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            var current = ""
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s != current){
                    mValue.removeTextChangedListener(this)

                    var cleanString = s.toString().replace("""[$,.]""".toRegex(), "")
                    var parsed = cleanString.toDouble()
                    var formatted = NumberFormat.getCurrencyInstance().format((parsed/100))

                    current = formatted

                    mValue.setText(formatted)
                    mValue.setSelection(formatted.length)
                    mValue.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })




    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.transform_button_register ->{
                var isFormFilled = true
                val description = mDescription.text.toString()
                val value = mValue.text.toString().replace("""[$,.]""".toRegex(), "").toDouble()/100
                var expense = true

                if(description.isEmpty()){
                    mDescription.error  = "este campo não pode estar vazio"
                    isFormFilled = false
                }

                if(!mRadioExpense.isChecked && !mRadioRevenue.isChecked){
                    mRegisterBtn.error = "Coloque a transação como despesa ou receita"
                    isFormFilled = false
                }

                if(mRadioExpense.isChecked && !mRadioRevenue.isChecked){
                    expense = true
                }

                if(!mRadioExpense.isChecked && mRadioRevenue.isChecked){
                    expense = false
                }

                if(isFormFilled) {
                    if (expense) {
                        val transactionId = mDatabase.reference.child("/users/${mAuth.uid}/Expenses").push().key
                        val ref = mDatabase.getReference("/users/${mAuth.uid}/Expenses/${transactionId}")
                        val transaction = Transaction(description, value,false)
                        ref.setValue(transaction)
                        handler.post {
                            Toast.makeText(
                                applicationContext, "Despesa criada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        finish()
                    }
                    else{
                        val transactionId = mDatabase.reference.child("/users/${mAuth.uid}/Revenues").push().key
                        val ref = mDatabase.getReference("/users/${mAuth.uid}/Revenues//${transactionId}")
                        val transaction = Transaction(description, value,true)
                        ref.setValue(transaction)
                        Toast.makeText(
                            applicationContext, "Receita criada com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                }

            }
        }
    }

}