package com.example.rabbitapp.ui.mainTab.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rabbitapp.databinding.SelectButtonLayoutBinding
import com.example.rabbitapp.utils.Gender

class PickButtonFragment(private val gender: Gender?, private val startSelect: StartSelect) :
    Fragment() {
    private var _binding: SelectButtonLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectButtonLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (binding.defaultButton.isEnabled) {
            binding.defaultButton.setOnClickListener {
                startSelect.select(gender)
            }
        }
    }
}