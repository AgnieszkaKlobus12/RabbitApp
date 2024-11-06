package com.example.rabbitapp.ui.mainTab.mainList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMainListBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Litter
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.MainListViewModel

class MainListFragment : Fragment() {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearSelected()

        binding.fragmentHomeListInclude.fragmentListRecyclerView.adapter =
            MainListAdapter(viewModel.getAll(), object : OnSelectedItem {
                override fun onItemClick(item: HomeListItem) {
                    if (item is Rabbit) {
                        viewModel.selectedRabbit = item
                        view.findNavController()
                            .navigate(R.id.action_navigation_home_to_rabbitDetailsFragment)
                    } else {
                        viewModel.selectedLitter = item as Litter
                        view.findNavController()
                            .navigate(R.id.action_navigation_home_to_litterDetailsFragment)
                    }
                }
            })

        binding.addNewMainButton.setOnClickListener(showFloatingAddButtons())
        binding.addRabbitButton.setOnClickListener(moveToAddRabbit())
        binding.addLitterButton.setOnClickListener(moveToAddLitter())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFloatingAddButtons(): View.OnClickListener {
        return View.OnClickListener {
            if (binding.addLitterButton.visibility == View.GONE) {
                binding.addLitterButton.visibility = View.VISIBLE
            } else {
                binding.addLitterButton.visibility = View.GONE
            }
            if (binding.addRabbitButton.visibility == View.GONE) {
                binding.addRabbitButton.visibility = View.VISIBLE
            } else {
                binding.addRabbitButton.visibility = View.GONE
            }
        }
    }

    private fun moveToAddRabbit(): View.OnClickListener {
        return if (viewModel.getEditable()) {
            View.OnClickListener { view ->
                view.findNavController().navigate(R.id.action_navigation_home_to_addRabbitFragment)
            }
        } else {
            View.OnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun moveToAddLitter(): View.OnClickListener {
        return if (viewModel.getEditable()) {
            View.OnClickListener { view ->
                view.findNavController().navigate(R.id.action_navigation_home_to_addLitterFragment)
            }
        } else {
            View.OnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.non_editable),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}