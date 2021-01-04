package com.zidan.topapp.mvp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.database.*
import com.zidan.topapp.*
import com.zidan.topapp.database.Makanan
import kotlinx.coroutines.*
import java.io.*
import java.util.*

class CheckoutPresenter(private val view: CheckoutView) {

    private lateinit var mSocket: BluetoothSocket
    private lateinit var mOutputStream: OutputStream
    private lateinit var mInputStream: InputStream
    private lateinit var mDevice: BluetoothDevice
    private lateinit var readBuffer: ByteArray
    private lateinit var selectedItems: List<Makanan>
    private var readBufferPosition: Int = 0
    private val uuidName = "00001101-0000-1000-8000-00805f9b34fb"
    @Volatile
    private var stopWorker: Boolean = false
    private var isPrinterReady: Boolean = false

    fun toDatabase(selectedItems: List<Makanan>, isPromo: Boolean, promo: Map<String, Int>, isGojek: Boolean) {

        val cal = Calendar.getInstance()

        val tahun = FirebaseDatabase.getInstance().reference.child(cal.time.toYear())
        val bulan = tahun.child(cal.time.toMonth())

        val pekan = bulan.child("Periode ${getPeriod()}")

        val hari = pekan.child(cal.time.toSimpleString())

        val pembelian = if (isGojek) hari.child("Gojek")
        else hari.child("NonGojek")

        for (i in selectedItems.indices) {
            val item = pembelian.child(selectedItems[i].name)

            val totalItems = if (isGojek) selectedItems[i].gojekPrice.toInt() * 1000
            else selectedItems[i].price.toInt() * 1000

            val allTotal = totalItems * selectedItems[i].count

            hari.runTransaction(object : Transaction.Handler {

                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {}

                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    var totalSales = mutableData.child("TotalSales").getValue(Int::class.java)

                    if (totalSales == null){
                        totalSales = allTotal
                    } else {
                        totalSales += allTotal
                    }

                    mutableData.child("TotalSales").value = totalSales
                    return Transaction.success(mutableData)
                }
            })

            pembelian.runTransaction(object : Transaction.Handler {

                override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {}

                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    var sales = mutableData.child("Sales").getValue(Int::class.java)

                    if (sales == null){
                        sales = allTotal
                    } else {
                        sales += allTotal
                    }

                    mutableData.child("Sales").value = sales
                    return Transaction.success(mutableData)
                }
            })

            item.runTransaction(object : Transaction.Handler {

                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    var jumlah = mutableData.child("Jumlah").getValue(Int::class.java)
                    var total = mutableData.child("Total").getValue(Int::class.java)

                    if (jumlah == null){
                        jumlah = selectedItems[i].count
                    } else {
                        jumlah += selectedItems[i].count
                    }

                    if (total == null){
                        total = allTotal
                    } else {
                        total += allTotal
                    }

                    mutableData.child("Jumlah").value = jumlah
                    mutableData.child("Total").value = total
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {}
            })
        }

        if (isPromo){

            hari.runTransaction(object : Transaction.Handler {

                override fun doTransaction(mutableData: MutableData): Transaction.Result {
                    var discount = mutableData.child("Promo").child("Discount").getValue(Int::class.java)
                    var voucher = mutableData.child("Promo").child("Voucher").getValue(Int::class.java)
                    var totalSales = mutableData.child("TotalSales").getValue(Int::class.java)!!

                    val disc = promo.getValue("disc")
                    val vouc = promo.getValue("vouc")

                    if (discount == null){
                        discount = disc
                    } else {
                        discount += disc
                    }

                    if (voucher == null){
                        voucher = vouc
                    } else {
                        voucher += vouc
                    }

                    val allPromo = disc + vouc
                    totalSales -= allPromo

                    mutableData.child("TotalSales").value = totalSales
                    mutableData.child("Promo").child("Discount").value = discount
                    mutableData.child("Promo").child("Voucher").value = voucher
                    return Transaction.success(mutableData)
                }

                override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {}
            })
        }
    }

    @Throws(IOException::class)
    fun openBT(device: BluetoothDevice, selectedItems: List<Makanan>) {

        this.selectedItems = selectedItems
        mDevice = device

        GetAsync().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAsync: AsyncTask<Unit, Unit, Boolean>() {

        override fun doInBackground(vararg params: Unit?): Boolean? {
            return try {
                val uuid = UUID.fromString(uuidName)
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid)
                mSocket.connect()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true) setAll()
            else view.setBluetoothError()
        }
    }

    private fun setAll() {
        mOutputStream = mSocket.outputStream
        mInputStream = mSocket.inputStream
        isPrinterReady = true
        view.setBluetoothOn()
        beginListenForData()
        Log.d("openBT", "Success")
    }

    private fun beginListenForData() {

        try {

            val delimiter: Byte = 10

            stopWorker = false
            readBufferPosition = 0
            readBuffer = ByteArray(1024)

            GlobalScope.launch(Dispatchers.Main) {

                val data = GlobalScope.async {

                    var where = ""

                    while (!stopWorker) {

                        try {

                            val bytesAvailable = mInputStream.available()

                            if (bytesAvailable > 0) {

                                val packetBytes = ByteArray(bytesAvailable)
                                mInputStream.read(packetBytes)

                                for (i in 0 until bytesAvailable) {

                                    val b = packetBytes[i]
                                    if (b == delimiter) {

                                        val encodedBytes = ByteArray(readBufferPosition)
                                        System.arraycopy(
                                            readBuffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.size
                                        )

                                        // specify US-ASCII encoding
                                        where = String(encodedBytes, charset("US-ASCII"))
                                        readBufferPosition = 0
                                        // tell the user data were sent to bluetooth printer device

                                    } else {
                                        readBuffer[readBufferPosition++] = b
                                    }
                                }
                            }

                        } catch (e: IOException) {
                            stopWorker = true
                        }
                    }
                    return@async where
                }.await()

                Log.d(data, "yes")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeBT(){
        if (mSocket.isConnected) {
            mSocket.close()
            mOutputStream.close()
        }
    }

    fun printText(pembayaran: Map<String, Any>, isGojek: Boolean, nomerKursi: String) {
        if (isPrinterReady) {
            TextToPrint(
                mOutputStream,
                selectedItems,
                nomerKursi
            ).setText(mSocket, pembayaran, isGojek)
        }
    }

    fun printChefText(nomerKursi: String){
        if (isPrinterReady){
            TextToPrint(mOutputStream, selectedItems, nomerKursi).setChefText()
        }
    }
}