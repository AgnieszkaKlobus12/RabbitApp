package com.example.rabbitapp.ui.mainTab.add.pickParent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rabbitapp.databinding.ContentHomeListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.MainListAdapter
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.ui.mainTab.add.OnSelected
import com.example.rabbitapp.utils.Gender

class PickMotherListFragment : Fragment() {
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
        viewModel.selectedMother = null

        binding.fragmentListRecyclerView.adapter =
            MainListAdapter(viewModel.getAllRabbits(Gender.FEMALE), object : OnSelected {
                override fun onItemClick(item: HomeListItem) {
                    viewModel.selectedMother = item as Rabbit
                    activity?.onBackPressed()
                }
            })
    }

}