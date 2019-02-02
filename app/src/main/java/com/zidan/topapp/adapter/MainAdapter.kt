package com.zidan.topapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.*
import com.zidan.topapp.data.Makanan
import com.zidan.topapp.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.content_view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainAdapter(private val ctx: Context, private val items: List<Makanan>,
                  private val listener1: (Makanan) -> Unit, private val listener2: (Makanan) -> Unit): RecyclerView.Adapter<MainAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(ctx).inflate(
                R.layout.content_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bindItem(items[position], listener1, listener2)
    }

    class MainHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        @SuppressLint("SetTextI18n")
        fun bindItem(item: Makanan, listener1: (Makanan) -> Unit, listener2: (Makanan) -> Unit) {
            img_food.setImageResource(item.image)
            tv_name_food.text = item.name
            tv_price_food.text = "Rp ${item.price}.000"
            tv_jumlah.text = "${item.count}"

            if (item.count < 1) {
                btn_pesan.visible()
                layout_pesan.gone()
            } else {
                btn_pesan.gone()
                layout_pesan.visible()
            }

            btn_minus.onClick {

                if (item.count > 0) {
                    listener1(item)
                    item.count--
                    tv_jumlah.text = "${item.count}"
                }

                if (item.count < 1) {
                    layout_pesan.gone()
                    btn_pesan.visible()
                }
            }

            btn_plus.onClick {
                listener2(item)
                item.count++
                tv_jumlah.text = "${item.count}"
            }

            btn_pesan.onClick {
                listener2(item)
                item.count++
                tv_jumlah.text = "${item.count}"
                btn_pesan.gone()
                layout_pesan.visible()
            }
        }
    }
}
