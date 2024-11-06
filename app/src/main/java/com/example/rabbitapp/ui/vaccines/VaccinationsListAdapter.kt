package com.example.rabbitapp.ui.vaccines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.relations.Vaccinated
import com.example.rabbitapp.utils.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails

class VaccinationsListAdapter(
    private val viewModel: MainListViewModel,
    private val values: List<Vaccinated>,
    private val onSelectedItem: OnSelectedVaccination
) :
    RecyclerView.Adapter<VaccinationsListAdapter.VaccinationsListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationsListItemView {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.vaccination_list_item, parent, false)
        return VaccinationsListItemView(itemView)
    }

    override fun onBindViewHolder(holder: VaccinationsListItemView, position: Int) {
        val item = values[position]
        holder.name.text = viewModel.getVaccine(item.fkVaccine)?.name
        holder.date.text = RabbitDetails.getDateString(item.date)
        if (item.nextDoseDate != null) {
            holder.nextDate.text = item.nextDoseDate.let { RabbitDetails.getDateString(it) }
        } else {
            holder.nextDate.visibility = View.GONE
        }
        holder.doseNumber.text = item.doseNumber.toString()
        holder.itemView.setOnClickListener { onSelectedItem.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class VaccinationsListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val name: TextView = iv.findViewById(R.id.vaccination_list_item_vaccine_name)
        val date: TextView = iv.findViewById(R.id.vaccination_list_item_vaccination_date)
        val nextDate: TextView = iv.findViewById(R.id.vaccination_list_item_date_next)
        val doseNumber: TextView = iv.findViewById(R.id.vaccination_list_item_dose_number)

    }

}