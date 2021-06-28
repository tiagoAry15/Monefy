package com.example.monefy.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monefy.R
import com.example.monefy.adapter.TransactionAdapter
import com.example.monefy.model.Transaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRecycleExpense: RecyclerView
    private lateinit var mRecycleRevenue: RecyclerView
    private lateinit var transactionAdapter:TransactionAdapter
    private var mRevenueList = mutableListOf<Transaction>()
    private var mExpenseList = mutableListOf<Transaction>()
    private lateinit var mBalance: TextView
    private lateinit var mUserName: TextView
    private lateinit var mRegistTransation: FloatingActionButton
    private var ExTotal = 0.0
    private var RevTotal = 0.0
    private var Total = 0.0
    private  val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecycleExpense = findViewById(R.id.main_recyclerView_Expenses)
        mRecycleRevenue = findViewById(R.id.main_recyclerView_Revenues)
        mBalance = findViewById(R.id.main_textView_balance)
        mUserName = findViewById(R.id.main_textview_username)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRegistTransation = findViewById(R.id.main_floating_registform)
        mRegistTransation.setOnClickListener(this)




    }
    override fun onStart() {
        super.onStart()
        Total = 0.0
        ExTotal = 0.0
        RevTotal = 0.0
        val dialog = ProgressDialog(this)
        dialog.setTitle("Carregando")
        dialog.isIndeterminate = true
        dialog.show()
        val UserName = mDatabase.reference.child("/users/${mAuth.uid!!}/name").get()
            .addOnSuccessListener {
                Log.i("App", "${it.value}")
                mUserName.text = ("Olá ${it.value}")
            }
            .addOnCanceledListener {
                Log.i("App", "erro")
            }
            val ExpenseQuery = mDatabase.reference.child("users/${mAuth.uid}/Expenses").orderByKey()
            val RevenueQuery = mDatabase.reference.child("users/${mAuth.uid}/Revenues").orderByKey()
            val Expensejust5 =  mDatabase.reference.child("users/${mAuth.uid}/Expenses").orderByKey().limitToLast(5)
            val Revenuejust5 =  mDatabase.reference.child("users/${mAuth.uid}/Revenues").orderByKey().limitToLast(5)

    Expensejust5.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var expenseList = mutableListOf<Transaction>()
            snapshot.children.forEach {
                val expense = it.getValue(Transaction::class.java)
                expenseList.add(expense!!)
            }
            mExpenseList.clear()
            mExpenseList.addAll(expenseList)
            handler.post {
                transactionAdapter = TransactionAdapter(mExpenseList)
                val llm = LinearLayoutManager(applicationContext)
                llm.stackFromEnd = true
                llm.reverseLayout = true
                mRecycleExpense.apply {
                    adapter = transactionAdapter
                    layoutManager = llm

                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })
    Revenuejust5.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var revenueList = mutableListOf<Transaction>()
            snapshot.children.forEach {
                val revenue = it.getValue(Transaction::class.java)
                revenueList.add(revenue!!)
            }
            mRevenueList.clear()
            mRevenueList.addAll(revenueList)
            handler.post {
                transactionAdapter = TransactionAdapter(mRevenueList)
                val llm = LinearLayoutManager(applicationContext)
                llm.stackFromEnd = true
                llm.reverseLayout = true
                mRecycleRevenue.apply {
                    adapter = transactionAdapter
                    layoutManager = llm
                }
                dialog.dismiss()
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    })
    ExpenseQuery.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var expenseList = mutableListOf<Transaction>()
            snapshot.children.forEach {
                val revenue = it.getValue(Transaction::class.java)
                expenseList.add(revenue!!)
                ExTotal += revenue.valor

            }
            val Total = RevTotal - ExTotal
            mBalance.text = (NumberFormat.getCurrencyInstance().format(Total))
            handler.post {


            }

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
    RevenueQuery.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                val revenue = it.getValue(Transaction::class.java)
                RevTotal += revenue!!.valor

            }
            val Total = RevTotal - ExTotal
            mBalance.text = (NumberFormat.getCurrencyInstance().format(Total))

        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })







    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.main_floating_registform ->{
                val it = Intent(applicationContext,TransactionFormActivity::class.java)
                startActivity(it)
            }
        }
    }
}