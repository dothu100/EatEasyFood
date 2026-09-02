package com.examples.eateasyfood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eateasyfood.R
import com.example.eateasyfood.databinding.FragmentNotificationBottomBinding
import com.examples.eateasyfood.adaptar.NotificationAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NotificationBottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notifications= listOf("Your order has been Canceled Successfully","Orders has been taken by driver taxi","Congrats your order placed")
        val notiticationImage= listOf(R.drawable.sademoji,R.drawable.truck_driver,R.drawable.congrats)
        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notiticationImage)
        )
        binding.notificationRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter=adapter
        return binding.root

    }

    companion object {

    }
}