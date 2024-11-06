package com.example.rabbitapp.ui.mainTab.parent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rabbitapp.databinding.ContentHomeListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.MainListViewModel

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
        val idList: MutableList<Long> = mutableListOf()
        if (viewModel.selectedRabbit?.id != null) {
            idList.add(viewModel.selectedRabbit!!.id)
        }
        if (viewModel.selectedLitter?.id != null) {
            idList.addAll(
                viewModel.getAllRabbitFromLitter(viewModel.selectedLitter!!.id)
                    .map { rabbit: Rabbit -> rabbit.id })
        }
        binding.fragmentListRecyclerView.adapter =
            MainListAdapter(
                viewModel.getAllRabbitsExcept(
                    Gender.FEMALE,
                    idList
                ), object : OnSelectedItem {
                    override fun onItemClick(item: HomeListItem) {
                        viewModel.selectedMother = item as Rabbit
                        Log.d("PickMotherListFragment", "New mother selected: $item")
                        activity?.onBackPressed()
                    }
                })
    }

}