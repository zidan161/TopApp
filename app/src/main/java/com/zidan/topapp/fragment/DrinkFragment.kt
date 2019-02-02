package com.zidan.topapp.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.*
import com.zidan.topapp.adapter.MainAdapter
import com.zidan.topapp.data.Makanan
import com.zidan.topapp.mvp.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.*

/**
 * A simple [Fragment] subclass.
 *
 */
class DrinkFragment : Fragment(), MainView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var theAdapter: MainAdapter
    private lateinit var presenter: MainPresenter
    private val items: MutableList<Makanan> = mutableListOf()
    private lateinit var listener: FragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return UI {
            recyclerView = recyclerView {
                lparams(matchParent, wrapContent)
                padding = dip(5)
                layoutManager = LinearLayoutManager(ctx)
            }

            theAdapter = MainAdapter(ctx, items, { clickButton(it, "Minus") }, { clickButton(it, "Plus") })
            recyclerView.adapter = theAdapter

            presenter = MainPresenter(this@DrinkFragment)
            presenter.initDataDrink(ctx)

        }.view
    }

    override fun onAttach(context: Context?) {
        listener = activity as FragmentListener
        super.onAttach(context)
    }

    private fun clickButton(item: Makanan ,param: String){

        if (param == "Minus") {

            if(item.count > 0) listener.setTotal(item.price.toInt(), 1)

            if (item.count == 1) listener.removeSelectedItems(item)

        } else if (param == "Plus"){
            listener.setTotal(item.price.toInt(), 2)
            if (item.count == 0) listener.setSelectedItems(item)
        }
    }

    override fun setItems(item: List<Makanan>) {
        items.run {
            clear()
            addAll(item)
        }
        theAdapter.notifyDataSetChanged()
    }

    interface FragmentListener {

        fun setSelectedItems(item: Makanan)

        fun removeSelectedItems(item: Makanan)

        fun setTotal(num: Int, id: Int)
    }
}
