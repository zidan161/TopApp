package com.zidan.topapp.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.*
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.util.Log
import android.view.KeyEvent
import android.widget.RadioButton
import com.zidan.topapp.*
import com.zidan.topapp.adapter.ListAdapter
import com.zidan.topapp.data.Makanan
import com.zidan.topapp.mvp.*
import kotlinx.android.synthetic.main.checkout_view.*
import org.jetbrains.anko.*
import java.text.DecimalFormat

@Suppress("DEPRECATION")
class CheckoutActivity : AppCompatActivity(), CheckoutView {

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mDevice: BluetoothDevice
    private lateinit var adapter: ListAdapter
    private lateinit var presenter: CheckoutPresenter
    private lateinit var discAlert: DialogInterface
    private lateinit var voucherAlert: DialogInterface
    private lateinit var loading: ProgressDialog
    private var isPromo: Boolean = false
    private var isGojek: Boolean = false
    private var isBluetoothOn: Boolean = false
    private var total: Int = 0
    private var defTotal: Int = 0
    private var meja: String = ""
    private var cash: String = ""
    private var discount: Int = 0
    private var diskon: Int = 0
    private var voucher: Int = 0
    private val selectedItems: MutableList<Makanan> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_view)
        presenter = CheckoutPresenter(this)

        val data = intent.getParcelableArrayListExtra<Makanan>("data")
        selectedItems.run {
            clear()
            addAll(data)
        }

        adapter = ListAdapter(this, selectedItems)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        setView()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(){
        val formatter = DecimalFormat("##,###,###")
        val totalString: String

        isGojek = intent.getBooleanExtra("gojek", false)
        if (isGojek) {
            for (i in selectedItems.indices){
                defTotal += selectedItems[i].count * selectedItems[i].gojekPrice.toInt() * 1000
            }
            total = defTotal
            totalString = formatter.format(total)
            tv_total.text = "Total: $totalString"
            meja_view.invisible()
        } else {
            defTotal = intent.getIntExtra("total", 0)
            total = defTotal
            totalString = formatter.format(total)
            tv_total.text = "Total: $totalString"
        }

        rg_button.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            meja = radioButton.text.toString() }


        cb_discount.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cb_voucher.isChecked = false
                voucher = 0
                discAlert = alert {
                    customView {
                        verticalLayout {
                            padding = dip(10)
                            textView {
                                text = "Masukkan Nominal Discount"
                                textSize = 20.toFloat()
                            }.lparams {
                                bottomMargin = dip(10)
                            }
                            editText {
                                inputType = InputType.TYPE_CLASS_NUMBER
                                setOnKeyListener { _, keyCode, event ->
                                    if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                                        diskon = text.toString().trim().toInt()
                                        if (diskon <= 100) {
                                            discount = total * diskon / 100
                                            total -= discount
                                            val disco = formatter.format(total)
                                            tv_total.text = "Total: $disco"
                                            tv_promo.text = "Disc: $diskon%"
                                        }
                                        discAlert.cancel()
                                        return@setOnKeyListener true
                                    }
                                    return@setOnKeyListener false
                                }
                            }
                        }
                    }
                }.show()
            } else {
                total = defTotal
                val string = formatter.format(total)
                tv_total.text = "Total: $string"
                tv_promo.text = ""
            }
        }

        cb_voucher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cb_discount.isChecked = false
                discount = 0
                voucherAlert = alert {
                    customView {
                        verticalLayout {
                            padding = dip(10)
                            textView {
                                text = "Masukkan Nominal Voucher"
                                textSize = 20.toFloat()
                            }.lparams {
                                bottomMargin = dip(10)
                            }
                            editText {
                                inputType = InputType.TYPE_CLASS_NUMBER
                                setOnKeyListener { _, keyCode, event ->
                                    if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                                        voucher = text.toString().trim().toInt()
                                        if (voucher <= total) {
                                            total -= voucher
                                            val vouce = formatter.format(total)
                                            tv_total.text = "Total: $vouce"
                                            tv_promo.text = "Voucher: $voucher"
                                        }
                                        voucherAlert.cancel()
                                        return@setOnKeyListener true
                                    }
                                    return@setOnKeyListener false
                                }
                            }
                        }
                    }
                }.show()
            } else {
                total = defTotal
                val string = formatter.format(total)
                tv_total.text = "Total: $string"
                tv_promo.text = ""
            }
        }

        edt_bayar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val bayar = edt_bayar.text.toString().trim().toInt()
                if (bayar > total){
                    tv_kembalian.text = "Kembalian: ${formatter.format(bayar - total)}"
                    cash = formatter.format(bayar)
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        btn_koki.setOnClickListener {
            presenter.printChefText("Meja: $meja")
        }

        btn_pesan.setOnClickListener {
            if (cb_discount.isChecked || cb_voucher.isChecked) isPromo = true
            val bayar = edt_bayar.text.toString().trim()

            if (!TextUtils.isEmpty(bayar)) {
                val kembalian = formatter.format(bayar.toInt() - total)
                presenter.printText(
                    mapOf(
                        "cash" to cash, "disc" to diskon, "vouc" to voucher, "change" to kembalian,
                        "total" to total
                    ), isGojek, "Meja: $meja"
                )
                presenter.toDatabase(selectedItems, isPromo, mapOf("disc" to discount, "vouc" to voucher), isGojek)

                startActivity<MainActivity>()
                presenter.closeBT()
                finish()
            } else {
                edt_bayar.error = "Field ini harus diisi"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        findBT()
        loading = indeterminateProgressDialog("Please Wait....")
        loading.show()
    }

    override fun onBackPressed() {
        selectedItems.clear()
        presenter.closeBT()
        super.onBackPressed()
    }

    private fun findBT() {

        try {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            if (mBluetoothAdapter == null) {
                toast("No bluetooth adapter available").show()
            }

            if (!mBluetoothAdapter.isEnabled) {
                val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetooth, 0)
            }

            val pairedDevices = mBluetoothAdapter.bondedDevices

            if (pairedDevices.size > 0) {

                for (device in pairedDevices) {

                    if (device.name == "BlueTooth Printer") {
                        mDevice = device
                        break
                    }
                }
            }

            Log.d("findBT", "Success")
            presenter.openBT(mDevice, selectedItems)

        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setBluetoothOn() {
        loading.cancel()
        toast("Printer is Ready").show()
        isBluetoothOn = true
    }

    override fun setBluetoothError() {
        alert {
            message = "Gagal menyiapkan printer\nApakah anda ingin menyiapkan ulang?"

            positiveButton("Ya"){
                presenter.openBT(mDevice, selectedItems)
            }
        }.show()
    }
}
