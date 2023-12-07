package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

data class ProductListItem(
    val productName: String,
    val checkBox: CheckBox,
)

class ProductsListAdapter(
    private var dataSet: List<ProductListItem>,
    private val onChecked: (ProductListItem) -> Unit,
    private val onUnChecked: (ProductListItem) -> Unit,
): RecyclerView.Adapter<ProductsListAdapter.ProductsListItemViewHolder>() {

    inner class ProductsListItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        var productName: TextView = view.findViewById(R.id.product_name)
        var productCheckBox: CheckBox = view.findViewById(R.id.product_check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsListItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.product_check_list_item, parent, false)
        return ProductsListItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ProductsListItemViewHolder, position: Int) {
        val item = dataSet[position]

        holder.productName.text = item.productName

        holder.productCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                onChecked(item)
            } else {
                onUnChecked(item)
            }
        }
    }
}
