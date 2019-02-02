package com.zidan.topapp.activity

import android.app.ProgressDialog
import android.content.Context
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
import com.zidan.topapp.mvp.*
import java.text.DecimalFormat

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), TotalView,
    FoodFragment.FragmentListener, DrinkFragment.FragmentListener, OtherFragment.FragmentListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tool: Toolbar
    private lateinit var presenter: TotalPresenter
    private lateinit var loading: ProgressDialog
    private var total: Int = 0
    private val selectedItems: MutableList<Makanan> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TotalPresenter(this)

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
                        offscreenPageLimit = 2
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
        return when (item.itemId) {

            R.id.btn_pesan -> {
                startActivity<CheckoutActivity>(
                    "data" to selectedItems,
                    "total" to total * 1000,
                    "gojek" to false
                )
                true
            }

            R.id.btn_pesan_gojek -> {
                startActivity<CheckoutActivity>(
                    "data" to selectedItems,
                    "total" to total * 1000,
                    "gojek" to true
                )
                true
            }

            R.id.btn_cek_sales -> {
                presenter.getTotalSales()
                loading = indeterminateProgressDialog("Please Wait....")
                loading.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setTotalSales(total: Int, totalNonGojek: Int, totalGojek: Int, promo: Int) {
        val formatter = DecimalFormat("##,###,###")

        val setor = "setor"
        val nonGojek = "setorNonGojek"
        val gojek = "setorGojek"
        val setorPromo = "setorPromo"

        val preferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val setoranAwal = preferences.getInt(setor, 0)
        val setoranNonGojek = preferences.getInt(nonGojek, 0)
        val setoranGojek = preferences.getInt(gojek, 0)
        val setoranPromo = preferences.getInt(setorPromo, 0)

        val trueTotal = "NonGojek: Rp ${formatter.format(totalNonGojek - setoranNonGojek)}\n\n" +
                "Gojek: Rp ${formatter.format(totalGojek - setoranGojek)}\n\n" +
                "Promo: Rp ${formatter.format(promo - setoranPromo)}\n\n" +
                "Total: Rp ${formatter.format(total - setoranAwal)}"

        alert {
            message = trueTotal
            positiveButton("SETOR"){
                if (setoranAwal > 0){
                    preferences.edit().putInt(setor, 0).apply()
                    preferences.edit().putInt(nonGojek, 0).apply()
                    preferences.edit().putInt(gojek, 0).apply()
                    preferences.edit().putInt(setorPromo, 0).apply()
                }
                else {
                    preferences.edit().putInt(setor, total).apply()
                    preferences.edit().putInt(nonGojek, totalNonGojek).apply()
                    preferences.edit().putInt(gojek, totalGojek).apply()
                    preferences.edit().putInt(setorPromo, promo).apply()
                }
                it.cancel()
            }
            negativeButton("OK"){ it.cancel() }
        }.show()
        loading.cancel()
    }

    override fun setSelectedItems(item: Makanan) { selectedItems.add(item) }

    override fun removeSelectedItems(item: Makanan) { selectedItems.remove(item) }

    override fun setTotal(num: Int, id: Int){
        if (id == 1) total -= num
        else total += num
    }
}
