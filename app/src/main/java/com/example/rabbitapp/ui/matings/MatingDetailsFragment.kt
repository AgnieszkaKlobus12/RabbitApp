package com.example.rabbitapp.ui.matings

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import com.example.rabbitapp.ui.mainTab.parent.ParentSelectService
import com.example.rabbitapp.utils.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate


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
        Log.d("MatingDetailsFragment", "onCreateView: $mating")
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.getEditable()) {
            inflater.inflate(R.menu.mating_details_menu, menu)
        }

        if (mating?.archived == true) {
            val item = menu.findItem(R.id.navigation_archive)
            item.setVisible(false)
        }
        if (mating?.fkLitter != null) {
            val item = menu.findItem(R.id.navigation_birth_completed)
            item.setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_birth_completed -> {
                view?.findNavController()
                    ?.navigate(
                        MatingDetailsFragmentDirections.actionMatingDetailsFragmentToAddLitterFragment(
                            mating!!.id
                        )
                    )
                true
            }

            R.id.navigation_archive -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            mating?.let { it1 -> viewModel.save(it1.copy(archived = true)) }
                            refresh()
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setTitle(R.string.alert)
                        setMessage(R.string.confirm_archive_matting_message)
                    }
                    builder.create()
                }
                alertDialog.show()
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
        handleExpirationAndArchie()

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

    private fun handleExpirationAndArchie() {
        if (mating?.archived == true) {
            binding.fragmentMatingDetailsArchived.visibility = View.VISIBLE
        } else {
            binding.fragmentMatingDetailsArchived.visibility = View.GONE
            if (LocalDate.ofEpochDay(mating!!.matingDate).plusDays(60)
                    .isBefore(LocalDate.now()) && mating!!.fkLitter == null
            ) {
                binding.overtimeMessage.visibility = View.VISIBLE
            } else {
                binding.overtimeMessage.visibility = View.GONE
            }
        }
    }

    private fun displayLitterIfSelected() {
        if (mating?.fkLitter != null) {
            val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
            val selectedLitterFragment = viewModel.getLitterFromId(mating!!.fkLitter!!)
                ?.let { HomeListItemFragment(it) }
            if (selectedLitterFragment != null) {
                transaction.replace(R.id.add_litter_fragment, selectedLitterFragment)
            }
            transaction.commit()
        } else {
            binding.fragmentMatingIncludeLitter.root.visibility = View.GONE
        }
    }

    private fun refresh() {
        fragmentManager?.beginTransaction()?.detach(this)?.commitNow();
        fragmentManager?.beginTransaction()?.attach(this)?.commitNow();
    }
}