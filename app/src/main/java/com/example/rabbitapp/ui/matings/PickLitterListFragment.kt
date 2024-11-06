package com.example.rabbitapp.ui.matings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rabbitapp.databinding.ContentHomeListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.utils.MainListViewModel

class PickLitterListFragment : Fragment() {
    private var _binding: ContentHomeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContentHomeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedLitter = null
        binding.fragmentListRecyclerView.adapter =
            MainListAdapter(
                viewModel.getAllLitters(), object : OnSelectedItem {
                    override fun onItemClick(item: HomeListItem) {
                        viewModel.selectedLitter = item as Litter
                        activity?.onBackPressed()
                    }
                })
    }
}