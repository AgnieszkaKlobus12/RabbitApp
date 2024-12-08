package com.example.rabbitapp.ui.matings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.databinding.ContentHomeListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.ui.mainTab.mainList.MainListAdapter
import com.example.rabbitapp.ui.mainTab.mainList.OnSelectedItem
import com.example.rabbitapp.utils.MainViewModel

class PickLitterListFragment : Fragment() {
    private val args: PickLitterListFragmentArgs by navArgs()
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
        binding.fragmentListRecyclerView.adapter =
            MainListAdapter(
                viewModel.getAllLitters(), object : OnSelectedItem {
                    override fun onItemClick(item: HomeListItem) {
                        view.findNavController()
                            .navigate(
                                PickLitterListFragmentDirections.actionPickLitterListFragmentToAddMattingFragment(
                                    args.matingId,
                                    args.motherId,
                                    args.fatherId,
                                    item.id
                                )
                            )
                    }
                })
    }
}