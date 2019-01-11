package com.zidan.topapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Makanan (val name: String, val price: String, val gojekPrice: String, val image: Int, var count: Int) : Parcelable