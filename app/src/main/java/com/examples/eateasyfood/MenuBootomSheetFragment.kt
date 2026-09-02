package com.examples.eateasyfood

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eateasyfood.databinding.FragmentMenuBootomSheetBinding
import com.examples.eateasyfood.adaptar.MenuAdapter
import com.examples.eateasyfood.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*

class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBootomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Gắn layout cho fragment
        binding = FragmentMenuBootomSheetBinding.inflate(inflater, container, false)

        // Thiết lập lắng nghe sự kiện click cho nút quay lại
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        // Lấy danh sách món từ Firebase
        retrieveMenuItem()

        return binding.root
    }

    // Phương thức để lấy danh sách món từ Firebase database
    private fun retrieveMenuItem() {
        // Khởi tạo Firebase database
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        // Đính kèm một trình nghe để đọc dữ liệu tại địa chỉ cụ thể trong Firebase
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lặp qua từng nút con
                for (foodSnapshot in snapshot.children) {
                    // Deserialize dữ liệu thành đối tượng MenuItem
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        // Thêm đối tượng MenuItem vào danh sách
                        menuItems.add(it)
                    }
                }
                // Thiết lập adapter cho RecyclerView
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi việc lấy dữ liệu bị hủy bỏ
                Log.e("MenuBottomSheetFragment", "Lỗi khi lấy danh sách món ăn: ${error.message}")
            }
        })
    }

    // Phương thức để thiết lập adapter cho RecyclerView
    private fun setAdapter() {
        if (menuItems.isNotEmpty()) {
            // Tạo và thiết lập MenuAdapter
            val adapter = MenuAdapter(menuItems, requireContext())
            binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.menuRecyclerView.adapter = adapter
            // Log thông báo khi lấy danh sách món thành công
            Log.d("MenuBottomSheetFragment", "Danh sách món ăn đã được tải thành công")
        } else {
            // Log thông báo khi không có món nào trong danh sách
            Log.d("MenuBottomSheetFragment", "Không có món nào trong danh sách")
        }
    }
}
