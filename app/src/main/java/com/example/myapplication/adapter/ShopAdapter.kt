package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ShopsItemBinding
import com.example.myapplication.model.ShopUser

class ShopAdapter(
    private val onClick: (ShopUser) -> Unit
): ListAdapter<ShopUser, ShopAdapter.ShopListHolder>(DiffCallBack) {

    inner class ShopListHolder(private val binding: ShopsItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClick(getItem(adapterPosition))
            }
        }
        fun bind(shopUser: ShopUser) {
            binding.shopName.text = shopUser.shop.name
            binding.shopDisc.text = shopUser.shop.shopType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListHolder {
        val viewHolder = ShopListHolder(ShopsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        viewHolder.itemView.setOnClickListener {
            onClick(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: ShopListHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<ShopUser>() {
            override fun areItemsTheSame(oldItem: ShopUser, newItem: ShopUser): Boolean {
                return oldItem.shopUserName == newItem.shopUserName
            }

            override fun areContentsTheSame(oldItem: ShopUser, newItem: ShopUser): Boolean {
                return oldItem == newItem
            }

        }
    }
}