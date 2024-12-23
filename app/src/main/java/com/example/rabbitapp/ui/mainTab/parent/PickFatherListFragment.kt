package com.example.rabbitapp.ui.mainTab.parent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.databinding.ContentHomeListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.utils.Gender
import com.example.rabbitapp.utils.MainViewModel

class PickFatherListFragment : Fragment() {
    private val args: PickFatherListFragmentArgs by navArgs()
    private var _binding: ContentHomeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

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
        viewModel.selectedFather = null
        val idList: MutableList<Long> = mutableListOf()
        if (args.rabbitId != 0L) {
            idList.add(args.rabbitId)
        }
        if (args.litterId != 0L) {
            idList.addAll(
                viewModel.getAllRabbitFromLitter(args.litterId)
                    .map { rabbit: Rabbit -> rabbit.id })
        }
        Log.d("PickFatherListFragment", "Excluded id list: $idList")
        binding.fragmentListRecyclerView.adapter =
            MainListAdapter(
                viewModel.getAllRabbitsExcept(
                    Gender.MALE,
                    idList
                ), object : OnSelectedItem {
                    override fun onItemClick(item: HomeListItem) {
                        viewModel.selectedFather = item as Rabbit
                        Log.d("PickFatherListFragment", "New father selected: $item")
                        activity?.onBackPressed()
                    }
                })
    }
}