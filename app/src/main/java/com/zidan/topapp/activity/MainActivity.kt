package com.zidan.topapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.*
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.*
import com.zidan.topapp.R
import com.zidan.topapp.adapter.*
import com.zidan.topapp.data.Makanan
import com.zidan.topapp.fragment.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.*
import org.jetbrains.anko.support.v4.viewPager
import android.support.design.widget.AppBarLayout

class MainActivity : AppCompatActivity(),
    FoodFragment.FragmentListener, DrinkFragment.FragmentListener, OtherFragment.FragmentListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tool: Toolbar
    private var total: Int = 0
    private val selectedItems: MutableList<Makanan> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        relativeLayout {
            lparams(matchParent, matchParent)

            appBarLayout {
                R.id.main_appbar
                lparams(matchParent, wrapContent)
                fitsSystemWindows = true

                tool = toolbar {
                    backgroundColor = resources.getColor(R.color.colorPrimary)
                    imageView {
                        setImageResource(R.drawable.logo_nasgor_top)
                    }.lparams {
                        gravity = Gravity.CENTER
                    }
                }.lparams(matchParent, wrapContent){
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                }

                coordinatorLayout {
                    backgroundColor = resources.getColor(android.R.color.white)

                    appBarLayout {
                        R.id.main_appbar
                        lparams(matchParent, wrapContent)
                        fitsSystemWindows = true

                        tabLayout = tabLayout{
                            lparams(matchParent, dip(30))
                            fitsSystemWindows = true
                        }
                    }
                    viewPager = viewPager {
                        id = R.id.viewpager
                    }.lparams(matchParent, matchParent)
                    (viewPager.layoutParams as CoordinatorLayout.LayoutParams).behavior = AppBarLayout.ScrollingViewBehavior()
                }
            }
        }

        setSupportActionBar(tool)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        viewPager.adapter = PageAdapter(supportFragmentManager)

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.btn_pesan -> {
                startActivity<CheckoutActivity>(
                    "data" to selectedItems,
                    "total" to total*1000,
                    "gojek" to false)
                true
            }

            R.id.btn_pesan_gojek -> {
                startActivity<CheckoutActivity>(
                    "data" to selectedItems,
                    "total" to total*1000,
                    "gojek" to true)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setSelectedItems(item: Makanan){
        selectedItems.add(item)
    }

    override fun removeSelectedItems(item: Makanan){
        selectedItems.remove(item)
    }

    override fun setTotal(num: Int, id: Int){
        if (id == 1) total -= num
        else total += num
    }
}
