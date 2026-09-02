package com.examples.eateasyfood.adaptar

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eateasyfood.databinding.BuyAgainItemBinding

// Định nghĩa lớp BuyAgainAdapter kế thừa từ RecyclerView.Adapter với ViewHolder là BuyAgainViewHolder
class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private val context: Context
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    // Phương thức onCreateViewHolder tạo ViewHolder mới khi cần thiết
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        // Sử dụng View Binding để gắn layout
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    // Phương thức onBindViewHolder gắn dữ liệu vào ViewHolder
    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(
            buyAgainFoodName[position],
            buyAgainFoodPrice[position],
            buyAgainFoodImage[position]
        )
    }

    // Phương thức getItemCount trả về số lượng phần tử trong danh sách
    override fun getItemCount(): Int = buyAgainFoodName.size

    // Định nghĩa lớp BuyAgainViewHolder kế thừa từ RecyclerView.ViewHolder
    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Phương thức bind gắn dữ liệu vào các view trong item layout
        fun bind(foodName: String, foodPrice: String, foodImage: String) {

            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            val uri = Uri.parse(foodImage)
            Glide.with(context).load(uri).into(binding.buyAgainFoodImage)
        }
    }
}
