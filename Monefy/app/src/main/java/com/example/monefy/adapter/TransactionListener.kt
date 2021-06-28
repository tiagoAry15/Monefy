package com.example.monefy.adapter

import android.view.View

interface TransactionListener {
    fun onClick(v: View, position: Int)
    fun onLongClick(v: View, position: Int)
}