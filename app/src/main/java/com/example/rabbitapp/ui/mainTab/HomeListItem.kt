package com.example.rabbitapp.ui.mainTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rabbitapp.databinding.HomeListItemBinding
import com.example.rabbitapp.model.entities.Rabbit

class HomeListItem(val rabbit: Rabbit) : Fragment() {

    private var _binding: HomeListItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeListItemBinding.inflate(inflater, container, false)
        binding.homeListItemAge.text = getAge(rabbit.birth)
        binding.homeListItemName.text = rabbit.name
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAge(birthdate: Long): String {
        val epochDays = System.currentTimeMillis() / 86400000 - birthdate
        val days = epochDays % 30
        val months = epochDays / 30 % 12
        val years = epochDays / 365
        return "$years y $months m $days d"
    }
}