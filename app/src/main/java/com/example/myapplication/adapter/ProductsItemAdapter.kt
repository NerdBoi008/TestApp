package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

data class ProductsItem(
    @DrawableRes val imageRes: Int,
    val productName: String
)

class ProductsItemAdapter(private var dataset: List<ProductsItem>): RecyclerView.Adapter<ProductsItemAdapter.ProductsItemViewHolder>() {

    class ProductsItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_item_image)
        val productName: TextView = view.findViewById(R.id.product_item_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.products_item, parent, false)

        return ProductsItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun setData(dataset: List<ProductsItem>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductsItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.productImage.setImageResource(item.imageRes)
        holder.productName.text = item.productName
//        holder.productImage.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in))
    }
}