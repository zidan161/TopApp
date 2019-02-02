package com.zidan.topapp.mvp

import com.zidan.topapp.data.Makanan

interface MainView {

    fun setItems(item: List<Makanan>)
}