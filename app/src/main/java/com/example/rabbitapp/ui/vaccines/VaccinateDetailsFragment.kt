package com.example.rabbitapp.ui.vaccines

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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentVaccinateDetailsBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VaccinateDetailsFragment : FragmentWithPicture() {

    private val args: VaccinateDetailsFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var vaccinated: Vaccinated? = null
    private var vaccine: Vaccine? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentVaccinateDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vaccinated_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_edit_vaccinated -> {
                view?.findNavController()
                    ?.navigate(
                        VaccinateDetailsFragmentDirections.actionNavigationVaccinatedDetailsToVaccinatedEditFragment(
                            0L, 0L, 0L, vaccinated!!.id
                        )
                    )
                true
            }

            R.id.navigation_delete_vaccinated -> {
                val alertDialog = requireActivity().let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            viewModel.deleteVaccinated(vaccinated!!.id)
                            if (vaccinated?.fkLitter != null) {
                                view?.findNavController()
                                    ?.navigate(VaccinateDetailsFragmentDirections.actionNavigationVaccinatedDetailsToLitterDetailsFragment())
                            } else {
                                view?.findNavController()
                                    ?.navigate(VaccinateDetailsFragmentDirections.actionNavigationVaccinatedDetailsToRabbitDetailsFragment())
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
        _binding = FragmentVaccinateDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        vaccinated = viewModel.getVaccinatedFromId(args.vaccinateId)

        if (vaccinated?.fkRabbit != null) {
            item = viewModel.getRabbitFromId(vaccinated!!.fkRabbit!!)
        } else {
            item = viewModel.getLitterFromId(vaccinated!!.fkLitter!!)
            Log.d("VaccinateFragment", "litter: $item")
        }
        vaccine = viewModel.getVaccine(vaccinated!!.fkVaccine)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVaccinateDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(vaccinated!!.date).format(dateFormatter)
        )
        if (vaccinated!!.nextDoseDate != null) {
            binding.fragmentVaccinateDateNextDose.text = Editable.Factory.getInstance().newEditable(
                LocalDate.ofEpochDay(vaccinated!!.nextDoseDate!!).format(dateFormatter)
            )
        } else {
            binding.fragmentVaccinateDateNextDose.text = getText(R.string.unknown)
        }
        binding.fragmentVaccinateDoseNumber.text = Editable.Factory.getInstance().newEditable(
            vaccinated!!.doseNumber.toString()
        )
        binding.vaccinatedDoseDescription.text = Editable.Factory.getInstance().newEditable(
            vaccinated!!.dose
        )
        binding.fragmentVaccinateRabbit.homeListItemAge.text = RabbitDetails.getAge(item!!.birth)
        binding.fragmentVaccinateRabbit.homeListItemName.text = item!!.name
        setPictureToSelectedOrDefault(
            binding.fragmentVaccinateRabbit.homeListItemPicture,
            item,
            R.drawable.rabbit_back
        )

        binding.fragmentVaccinateVaccineName.text = vaccine!!.name
        binding.fragmentVaccinateVaccineDescription.text = vaccine!!.description
    }

}