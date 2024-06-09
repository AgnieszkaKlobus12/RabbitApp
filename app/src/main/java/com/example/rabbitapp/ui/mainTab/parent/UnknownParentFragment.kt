package com.example.rabbitapp.ui.mainTab.parent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rabbitapp.databinding.UnknownParentLayoutBinding

class UnknownParentFragment :
    Fragment() {
    private var _binding: UnknownParentLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UnknownParentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

}