package com.example.rabbitapp.ui.matings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.relations.Mating
import com.example.rabbitapp.ui.mainTab.MainListViewModel
import com.example.rabbitapp.utils.RabbitDetails

class MatingListAdapter(
    private val viewModel: MainListViewModel,
    private val values: List<Mating>,
    private val onSelectedItem: OnSelectedMating
) :
    RecyclerView.Adapter<MatingListAdapter.MatingItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatingItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.mating_list_item, parent, false)
        return MatingItemView(itemView)
    }

    override fun onBindViewHolder(holder: MatingItemView, position: Int) {
        val item = values[position]
        holder.nameMother.text = item.fkMother?.let { viewModel.getRabbitFromId(it)?.name }
        holder.nameFather.text = item.fkFather?.let { viewModel.getRabbitFromId(it)?.name }
        holder.numbersMother.text = item.fkMother?.let { viewModel.getRabbitFromId(it)?.earNumber }
        holder.numbersFather.text = item.fkFather?.let { viewModel.getRabbitFromId(it)?.earNumber }
        holder.matingDate.text = RabbitDetails.getDateString(item.matingDate)
        holder.expectedBirthDate.text = RabbitDetails.getDateString(item.matingDate + 30)
        holder.itemView.setOnClickListener { onSelectedItem.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class MatingItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val nameMother: TextView = iv.findViewById(R.id.mating_list_item_name1)
        val nameFather: TextView = iv.findViewById(R.id.mating_list_item_name2)
        val numbersFather: TextView = iv.findViewById(R.id.mating_list_item_numbers_2)
        val numbersMother: TextView = iv.findViewById(R.id.mating_list_item_numbers_1)
        val matingDate: TextView = iv.findViewById(R.id.mating_list_item_mating_date)
        val expectedBirthDate: TextView = iv.findViewById(R.id.mating_list_item_birth_date)
    }

}