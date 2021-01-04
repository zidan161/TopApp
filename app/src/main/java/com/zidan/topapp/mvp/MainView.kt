package com.zidan.topapp.mvp

import com.zidan.topapp.database.Makanan

interface MainView {

    fun setItems(item: List<Makanan>)
}