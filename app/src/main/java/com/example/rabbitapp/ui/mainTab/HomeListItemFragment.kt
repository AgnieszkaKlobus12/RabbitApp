package com.example.rabbitapp.ui.mainTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rabbitapp.databinding.HomeListItemBinding
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.RabbitDetails.Companion.getAge

class HomeListItemFragment(val rabbit: Rabbit) : Fragment() {

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
}