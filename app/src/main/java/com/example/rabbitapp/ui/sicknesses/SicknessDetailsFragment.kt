package com.example.rabbitapp.ui.sicknesses

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentSickDetailsBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Sickness
import com.example.rabbitapp.model.entities.relations.Sick
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SicknessDetailsFragment : FragmentWithPicture() {

    private val args: SicknessDetailsFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var sick: Sick? = null
    private var sickness: Sickness? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentSickDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sick_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_edit_sick -> {
                view?.findNavController()
                    ?.navigate(
                        SicknessDetailsFragmentDirections.navigationSickDetailsToAddSicknessFragment(
                            0L, 0L, 0L,
                            sick!!.id
                        )
                    )
                true
            }

            R.id.navigation_delete_sick -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deleteSick(sick!!.id)
                            if (sick?.fkLitter != null) {
                                view?.findNavController()
                                    ?.navigate(R.id.navigation_sick_details_to_litterDetailsFragment)
                            } else {
                                view?.findNavController()
                                    ?.navigate(R.id.navigation_sick_details_to_rabbitDetailsFragment)
                            }
                        }
                        setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                        }
                        setTitle(R.string.alert)
                        setMessage(R.string.confirm_delete_sick_message)
                    }
                    builder.create()
                }
                alertDialog.show()
                true
            }

            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSickDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sick = viewModel.getSick(args.sickId)
        item = if (sick!!.fkRabbit != null) {
            sick!!.fkRabbit?.let { viewModel.getRabbitFromId(it) }
        } else {
            sick!!.fkLitter?.let { viewModel.getLitterFromId(it) }
        }
        sickness = sick!!.fkSickness.let { viewModel.getSickness(it) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentDetailsSicknessStartDate.text = Editable.Factory.getInstance().newEditable(
            sick?.let { LocalDate.ofEpochDay(it.startDate).format(dateFormatter) }
        )
        if (sick?.endDate != null) {
            binding.fragmentDetailsSicknessEndDate.text =
                Editable.Factory.getInstance().newEditable(
                    sick?.endDate?.let { LocalDate.ofEpochDay(it).format(dateFormatter) }
                )
        }

        binding.fragmentDetailsSicknessRabbit.homeListItemAge.text =
            RabbitDetails.getAge(item!!.birth)
        binding.fragmentDetailsSicknessRabbit.homeListItemName.text = item!!.name
        setPictureToSelectedOrDefault(
            binding.fragmentDetailsSicknessRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.fragmentDetailsSicknessSicknessName.text = sickness!!.name
        binding.fragmentDetailsSicknessSicknessTreatment.text = sickness!!.treatment
        binding.fragmentDetailsSicknessSicknessSymptoms.text = sickness!!.symptoms
    }

}