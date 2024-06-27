package com.example.rabbitapp.ui.matings

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentMatingDetailsBinding
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.ui.mainTab.HomeListItemFragment
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.RabbitDetails

class MatingDetailsFragment : Fragment() {

    private val args: MatingDetailsFragmentArgs by navArgs()

    private var _binding: FragmentMatingDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainListViewModel by activityViewModels()
    private val parentSelectService: ParentSelectService = ParentSelectService()

    private var mating: Mating? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatingDetailsBinding.inflate(inflater, container, false)
        mating = viewModel.getMating(args.matingId)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mating_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_birth_completed -> {
//                view?.findNavController()
//                    ?.navigate(
//                        RabbitDetailsFragmentDirections.actionRabbitDetailsFragmentToVaccineListFragment(
//                            viewModel.selectedRabbit!!.id
//                        )
//                    )
                true
            }

            R.id.navigation_edit_mating -> {
                view?.findNavController()
                    ?.navigate(
                        MatingDetailsFragmentDirections.actionMatingDetailsFragmentToEditMatingsFragment(
                            mating!!.id
                        )
                    )
                true
            }

            R.id.navigation_delete_mating -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deleteMating(mating!!.id)
                            view?.findNavController()
                                ?.navigate(R.id.action_matingDetailsFragment_to_matingsListFragment)
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setTitle(R.string.alert)
                        setMessage(R.string.confirm_delete_matting_message)
                    }
                    builder.create()
                }
                alertDialog.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentSelectService.displayParentOrUnknown(
            mating?.fkMother,
            mating?.fkFather,
            childFragmentManager,
            viewModel
        )
        displayLitterIfSelected()

        val formattedMatingDate = mating?.matingDate?.let { RabbitDetails.getDateString(it) }
        binding.addMatingDate.text =
            Editable.Factory.getInstance().newEditable(formattedMatingDate)

        if (mating?.fkLitter == null) {
            val formattedBirthDate = mating?.birthDate?.let { RabbitDetails.getDateString(it) }
            binding.matingDateBirth.text =
                Editable.Factory.getInstance().newEditable(formattedBirthDate)
            binding.matingActualDateBirth.visibility = View.GONE
            binding.matingDateActualBirthLabel.visibility = View.GONE
        } else {
            val formattedBirthDate = mating?.birthDate?.let { RabbitDetails.getDateString(it) }
            binding.matingActualDateBirth.text =
                Editable.Factory.getInstance().newEditable(formattedBirthDate)
            binding.matingDateBirth.visibility = View.GONE
            binding.matingDateBirthLabel.visibility = View.GONE
        }
    }

    private fun displayLitterIfSelected() {
        if (mating?.fkLitter != null) {
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            val selectedLitterFragment = HomeListItemFragment(viewModel.selectedLitter!!)
            transaction.replace(R.id.add_litter_fragment, selectedLitterFragment)
            transaction.commit()
        } else {
            binding.fragmentMatingIncludeLitter.root.visibility = View.GONE
        }
    }
}