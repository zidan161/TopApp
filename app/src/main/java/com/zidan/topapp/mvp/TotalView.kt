package com.zidan.topapp.mvp

interface TotalView {

    fun setTotalSales(total: Int, totalNonGojek: Int, totalGojek: Int, promo: Int)
}