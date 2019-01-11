package com.zidan.topapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zidan.topapp.R
import com.zidan.topapp.data.Makanan
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.food_list.*

class ListAdapter (private val ctx: Context, private val items: List<Makanan>): RecyclerView.Adapter<ListAdapter.ListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.food_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.bindItem(items[position])
    }

    class ListHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindItem(item: Makanan){
            tv_name.text = item.name
            tv_count.text = item.count.toString()
        }
    }
}
