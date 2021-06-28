package com.example.monefy.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.monefy.R
import com.example.monefy.model.Transaction
import java.text.NumberFormat

class TransactionAdapter (val transactions: List<Transaction>):
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    private var listener:TransactionListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction,parent,false)
        return TransactionViewHolder(itemView,listener)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.TransactionDescription.text = transactions[position].nome
        holder.TransactionValue.text = transactions[position].valor.toString()

        if(transactions[position].gain == true){
            holder.TransactionValue.text = ("+ ${NumberFormat.getCurrencyInstance().format(holder.TransactionValue.text.toString().toDouble())}")
            holder.TransactionValue.setTextColor(Color.GREEN)
        }
        else{
            holder.TransactionValue.text = ("- ${NumberFormat.getCurrencyInstance().format(holder.TransactionValue.text.toString().toDouble())}")
            holder.TransactionValue.setTextColor(Color.RED)
        }


    }

    override fun getItemCount(): Int {
        return transactions.size
    }
    class TransactionViewHolder(itemView: View, listener:TransactionListener?): ViewHolder(itemView){
        val TransactionDescription: TextView = itemView.findViewById(R.id.item_textview_description)
        val TransactionValue: TextView = itemView.findViewById(R.id.item_textview_Value)
        init {
            itemView.setOnClickListener{
                listener?.onClick(it,adapterPosition)
            }
            itemView.setOnLongClickListener {
                listener?.onLongClick(it, adapterPosition)
                true
            }
        }

    }
}