package com.example.rabbitapp.ui.mainList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rabbitapp.databinding.FragmentMainListBinding
import com.example.rabbitapp.model.entities.HomeListItem

class MainListFragment : Fragment() {

    private var _binding: FragmentMainListBinding? = null
    private val binding get() = _binding!!
    private var mainListViewModel: MainListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainListViewModel =
            ViewModelProvider(this)[MainListViewModel::class.java]
        _binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var rabbitsList = emptyList<HomeListItem>()
        mainListViewModel?.mainRabbitsItems?.observe(viewLifecycleOwner) { rabbitsData ->
            rabbitsData?.let { rabbitsList = it }
            binding.fragmentHomeRabbitList.adapter = MainListAdapter(rabbitsList)
        }

        binding.addNewMainButton.setOnClickListener(addOnClickListener())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addOnClickListener(): View.OnClickListener {
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
}