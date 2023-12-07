package com.example.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.OrderItemBinding

class ChooseOrderItemAdapter(
    val onClick: (String) -> Unit
    ): ListAdapter<String, ChooseOrderItemAdapter.ChooseItemViewHolder>(DiffCallBack) {

    inner class ChooseItemViewHolder(val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(productName: String) {
            binding.orderProductName.text = productName
        }

        val adapterLayout = binding.root
        val quantityLayout = binding.quantityLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseItemViewHolder {
        val viewHolder = ChooseItemViewHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        viewHolder.quantityLayout.visibility = View.GONE

        viewHolder.adapterLayout.setOnClickListener {
             onClick(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ChooseItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}