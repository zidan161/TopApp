package com.zidan.topapp.adapter

import android.support.v4.app.*
import com.zidan.topapp.fragment.*

class PageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> FoodFragment()
            1 -> DrinkFragment()
            else -> OtherFragment()
        }
    }

    override fun getCount() = 3

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "MAKANAN"
            1 -> "MINUMAN"
            else -> "LAIN-LAIN"
        }
    }
}