package com.example.rabbitapp.ui.mainTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.HomeListItemBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails.Companion.getAge

class HomeListItemFragment(val item: HomeListItem) : FragmentWithPicture() {

    private var _binding: HomeListItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeListItemBinding.inflate(inflater, container, false)
        binding.homeListItemAge.text = getAge(item.birth)
        binding.homeListItemName.text = item.name
        setPictureToSelectedOrDefault(
            binding.homeListItemPicture,
            viewModel.selectedLitter,
            R.drawable.rabbit_2_back
        )
        return binding.root
    }

}