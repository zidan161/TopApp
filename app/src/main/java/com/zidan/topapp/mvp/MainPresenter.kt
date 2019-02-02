package com.zidan.topapp.mvp

import android.content.Context
import com.zidan.topapp.R
import com.zidan.topapp.data.Makanan

class MainPresenter(private val view: MainView) {

    fun initDataFood(ctx: Context) {
        val name = ctx.resources.getStringArray(R.array.name_top_food)
        val price = ctx.resources.getStringArray(R.array.price_top_food)
        val gojek = ctx.resources.getStringArray(R.array.price_gojek_food)
        val image = ctx.resources.obtainTypedArray(R.array.image_top_food)
        val items: MutableList<Makanan> = mutableListOf()
        for (i in name.indices) {
            items.add(Makanan(name[i], price[i], gojek[i], image.getResourceId(i, 0), 0))
        }
        image.recycle()
        view.setItems(items)
    }

    fun initDataDrink(ctx: Context) {
        val name = ctx.resources.getStringArray(R.array.name_top_drink)
        val price = ctx.resources.getStringArray(R.array.price_top_drink)
        val gojek = ctx.resources.getStringArray(R.array.price_gojek_drink)
        val image = ctx.resources.obtainTypedArray(R.array.image_top_drink)
        val items: MutableList<Makanan> = mutableListOf()
        for (i in name.indices) {
            items.add(Makanan(name[i], price[i], gojek[i], image.getResourceId(i, 0), 0))
        }
        image.recycle()
        view.setItems(items)
    }

    fun initDataOther(ctx: Context) {
        val name = ctx.resources.getStringArray(R.array.name_top_other)
        val price = ctx.resources.getStringArray(R.array.price_top_other)
        val gojek = ctx.resources.getStringArray(R.array.price_gojek_other)
        val image = ctx.resources.obtainTypedArray(R.array.image_top_other)
        val items: MutableList<Makanan> = mutableListOf()
        for (i in name.indices) {
            items.add(Makanan(name[i], price[i], gojek[i], image.getResourceId(i, 0), 0))
        }
        image.recycle()
        view.setItems(items)
    }
}