package com.examples.eateasyfood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.examples.eateasyfood.adaptar.MenuAdapter
import com.example.eateasyfood.databinding.FragmentSearchBinding
import com.google.firebase.database.FirebaseDatabase
import com.examples.eateasyfood.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

// Fragment hiển thị màn hình tìm kiếm món ăn
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        retrieveMenu()

        setupSearchView()

        showAllMenu()

        return binding.root
    }

    private fun retrieveMenu() {
        database = FirebaseDatabase.getInstance()
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem:: class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showAllMenu() {
        val filteredMenuItems = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItems)
    }

    private fun setAdapter(filteredMenuItem: List<MenuItem>) {
        adapter = MenuAdapter(filteredMenuItem,requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            // Xử lý sự kiện khi người dùng nhấn "Submit" trên bàn phím
            override fun onQueryTextSubmit(query: String): Boolean {
                // Lọc các mục menu dựa trên từ khóa tìm kiếm
                filterMenuItem(query)
                return true
            }

            // Xử lý sự kiện khi nội dung của SearchView thay đổi
            override fun onQueryTextChange(newText: String): Boolean {
                // Lọc các mục menu dựa trên từ khóa tìm kiếm mới
                filterMenuItem(newText)
                return true
            }
        })
    }

    // Phương thức lọc các mục menu dựa trên từ khóa tìm kiếm
    private fun filterMenuItem(query: String) {
        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }

        // Thông báo cho adapter biết dữ liệu đã thay đổi
       setAdapter(filteredMenuItems)
    }

    companion object {
    }
}
