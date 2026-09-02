package com.examples.eateasyfood.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.examples.eateasyfood.adaptar.MenuAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.eateasyfood.databinding.FragmentHomeBinding
import com.example.eateasyfood.R
import com.google.firebase.database.FirebaseDatabase
import com.examples.eateasyfood.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.examples.eateasyfood.MenuBottomSheetFragment

// Định nghĩa lớp HomeFragment kế thừa từ Fragment
class HomeFragment : Fragment() {
    // Khai báo biến binding sử dụng để liên kết view
    private lateinit var binding: FragmentHomeBinding
    // Khai báo biến database để truy cập Firebase Database
    private lateinit var database: FirebaseDatabase
    // Khai báo danh sách lưu trữ các mục menu
    private lateinit var menuItems: MutableList<MenuItem>

    // Phương thức onCreate được gọi khi Fragment được tạo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Phương thức onCreateView được gọi để tạo và trả về view của Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Liên kết view với FragmentHomeBinding
        binding = FragmentHomeBinding.inflate(inflater,container, false)
        // Thiết lập sự kiện khi nhấn vào nút "View All Menu"
        binding.viewAllMenu.setOnClickListener{
            val bottomSheetDialog= MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }

        // Gọi phương thức để lấy và hiển thị các mục phổ biến
        retrieveAnDisplayPopularItems()
        return binding.root
    }

    // Phương thức lấy và hiển thị các mục phổ biến từ cơ sở dữ liệu
    private fun retrieveAnDisplayPopularItems() {
        // Lấy tham chiếu đến cơ sở dữ liệu
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        // Thêm một sự kiện lắng nghe dữ liệu từ Firebase
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener {
            // Phương thức được gọi khi dữ liệu thay đổi
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItem:: class.java)
                    menuItem?.let{
                        menuItems.add(it)
                    }
                }
                randomPopularItems()
            }

            // Phương thức được gọi khi có lỗi xảy ra
            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }

    // Phương thức chọn ngẫu nhiên các mục phổ biến từ danh sách
    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subSetMenuItem = index.take(numItemToShow).map {
            menuItems[it]
        }
        setPopularItemsAdapter(subSetMenuItem)
    }

    // Phương thức thiết lập adapter cho RecyclerView để hiển thị các mục phổ biến
    private fun setPopularItemsAdapter(subSetMenuItem: List<MenuItem>) {
        val adapter = MenuAdapter(subSetMenuItem,requireContext())
        binding.PopulerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopulerRecyclerView.adapter = adapter
    }

    // Phương thức được gọi sau khi view được tạo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tạo danh sách hình ảnh cho image slider
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        // Thiết lập image slider
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {
            }

            override fun onItemSelected(position: Int) {

                val itemMessage = "Select Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
