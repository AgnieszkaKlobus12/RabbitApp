package com.example.rabbitapp.ui.mainTab

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.ui.mainTab.add.OnSelected
import com.example.rabbitapp.utils.RabbitDetails

class MainListAdapter(
    private val values: List<HomeListItem>,
    private val onSelected: OnSelected
) :
    RecyclerView.Adapter<MainListAdapter.HomeListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return HomeListItemView(itemView)
    }

    override fun onBindViewHolder(holder: HomeListItemView, position: Int) {
        val item = values[position]
        setPictureToSelectedOrDefault(item, holder.image)
        holder.age.text = RabbitDetails.getAge(item.birth)
        holder.itemView.setOnClickListener { onSelected.onItemClick(item) }
    }

    override fun getItemCount(): Int = values.size

    inner class HomeListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val image: ImageView = iv.findViewById(R.id.home_list_item_picture)
        val name: TextView = iv.findViewById(R.id.home_list_item_name)
        val age: TextView = iv.findViewById(R.id.home_list_item_age)
    }

    private fun setPictureToSelectedOrDefault(item: HomeListItem, image: ImageView) {
        if (!item.imagePath.isNullOrEmpty()) {
            image.setImageBitmap(BitmapFactory.decodeFile(item.imagePath))
        } else if (item is Rabbit) {
            image.setImageResource(R.drawable.rabbit_back)
        } else {
            image.setImageResource(R.drawable.rabbit_2_back)
        }
    }

}