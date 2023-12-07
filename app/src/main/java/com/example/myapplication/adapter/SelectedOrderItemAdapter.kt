package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.OrderItemBinding
import com.example.myapplication.model.Order

class SelectedOrderItemAdapter(
    val onClick: (Order) -> Unit,
    val onClickRemove: (Order) -> Unit
): ListAdapter<Order, SelectedOrderItemAdapter.SelectedOrderItemViewHolder>(DiffCallBack) {

    inner class SelectedOrderItemViewHolder(private val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {

        val removeItemButton = binding.removeItemButton

        fun bind(order: Order) {
            binding.orderProductName.text = order.productName
            binding.orderQuantity.text = order.quantity.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedOrderItemViewHolder {
        val viewHolder = SelectedOrderItemViewHolder(OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))


        viewHolder.removeItemButton.setOnClickListener {
            onClickRemove(getItem(viewHolder.adapterPosition))
        }

        viewHolder.itemView.setOnClickListener {
            onClick(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: SelectedOrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.productName == newItem.productName
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }
        }
    }


}