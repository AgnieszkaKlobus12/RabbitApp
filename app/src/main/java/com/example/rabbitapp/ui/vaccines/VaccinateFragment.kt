package com.example.rabbitapp.ui.vaccines

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rabbitapp.R
import com.example.rabbitapp.databinding.FragmentVaccinateBinding
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Vaccine
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.ui.mainTab.add.FragmentWithPicture
import com.example.rabbitapp.utils.RabbitDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class VaccinateFragment : FragmentWithPicture() {

    private val args: VaccinateFragmentArgs by navArgs()

    private var item: HomeListItem? = null
    private var vaccine: Vaccine? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private var _binding: FragmentVaccinateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaccinateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (args.rabbitId != 0L) {
            item = viewModel.getRabbitFromId(args.rabbitId)
            Log.d("VaccinateFragment", "rabbit: $item")
        } else {
            item = viewModel.getLitterFromId(args.litterId)
            Log.d("VaccinateFragment", "litter: $item")
        }
        vaccine = viewModel.getVaccine(args.vaccineId)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentVaccinateDate.text = Editable.Factory.getInstance().newEditable(
            LocalDate.ofEpochDay(item!!.birth).format(dateFormatter)
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

        binding.vaccinateSaveButton.setOnClickListener {
            viewModel.save(
                Vaccinated(
                    0L,
                    LocalDate.parse(binding.fragmentVaccinateDate.text.toString(), dateFormatter)
                        .toEpochDay(),
                    binding.vaccinatedDoseDescription.text.toString(),
                    args.rabbitId.takeIf { it != 0L },
                    args.litterId.takeIf { it != 0L },
                    vaccine!!.id
                )
            )
            if (args.rabbitId != 0L) {
                view.findNavController()
                    .navigate(R.id.action_navigation_vaccinate_to_rabbitDetailsFragment)
            } else {
                view.findNavController()
                    .navigate(R.id.action_navigation_vaccinate_to_litterDetailsFragment)
            }
        }
    }


}