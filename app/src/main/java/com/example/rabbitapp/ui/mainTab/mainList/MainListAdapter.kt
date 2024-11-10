package com.example.rabbitapp.ui.mainTab.mainList

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rabbitapp.R
import com.example.rabbitapp.model.entities.HomeListItem
import com.example.rabbitapp.model.entities.Rabbit
import com.example.rabbitapp.utils.RabbitDetails

class MainListAdapter(
    private var values: List<HomeListItem>,
    private val onSelectedItem: OnSelectedItem
) :
    RecyclerView.Adapter<MainListAdapter.HomeListItemView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListItemView {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return HomeListItemView(itemView)
    }

    fun getData(): List<HomeListItem> {
        return values
    }

    fun updateData(newItems: List<HomeListItem>) {
        values = newItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HomeListItemView, position: Int) {
        val item = values[position]
        setPictureToSelectedOrDefault(item, holder.image)
        holder.name.text = item.name
        holder.age.text = RabbitDetails.getAge(item.birth)
        holder.itemView.setOnClickListener { onSelectedItem.onItemClick(item) }
        if (item.deathDate != null) {
            holder.background.rootView.setBackgroundColor(Color.parseColor("#808080"))
        }
        if (item.cageNumber != null) {
            holder.cageNumber.text = item.cageNumber.toString()
        } else {
            holder.cageNumber.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = values.size

    inner class HomeListItemView(iv: View) : RecyclerView.ViewHolder(iv) {
        val image: ImageView = iv.findViewById(R.id.home_list_item_picture)
        val name: TextView = iv.findViewById(R.id.home_list_item_name)
        val age: TextView = iv.findViewById(R.id.home_list_item_age)
        val cageNumber: TextView = iv.findViewById(R.id.home_list_item_cage_number)
        val background: LinearLayout = iv.findViewById(R.id.home_list_item_background)
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