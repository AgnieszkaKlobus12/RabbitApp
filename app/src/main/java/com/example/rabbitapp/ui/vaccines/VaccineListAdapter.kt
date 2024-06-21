package com.example.rabbitapp.ui.vaccines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.Vaccine

class VaccineListAdapter(
    private val values: List<Vaccine>,
    private val onSelectedVaccine: OnSelectedVaccine
) :
    RecyclerView.Adapter<VaccineListAdapter.VaccineListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineListItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.vaccine_list_item, parent, false)
        return VaccineListItemView(itemView)
    }

    override fun onBindViewHolder(holder: VaccineListItemView, position: Int) {
        val item = values[position]
        holder.name.text = item.name
        holder.itemView.setOnClickListener { onSelectedVaccine.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class VaccineListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val name: TextView = iv.findViewById(R.id.vaccine_list_item_name)
    }

}