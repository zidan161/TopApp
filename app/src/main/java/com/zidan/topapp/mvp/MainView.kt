package com.zidan.topapp.mvp

import com.zidan.topapp.data.Makanan

interface MainView {

    fun getItems(item: List<Makanan>)
}