package com.zidan.topapp.mvp

import com.google.firebase.database.*
import com.zidan.topapp.*
import java.util.*

class TotalPresenter(private val view: TotalView) {

    fun getTotalSales(){

        val cal = Calendar.getInstance()
        var totalSales = 0
        var totalNonGojek = 0
        var totalGojek = 0
        var promo = 0

        val bulan = FirebaseDatabase.getInstance().reference.child(cal.time.toYear()).child(cal.time.toMonth())

        val hari = bulan.child("Pekan Ke ${cal.get(Calendar.WEEK_OF_MONTH)}").child(cal.time.toSimpleString())

        hari.runTransaction(object : Transaction.Handler {

            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                view.setTotalSales(totalSales, totalNonGojek, totalGojek, promo)
            }

            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                var sales = mutableData.child("TotalSales").getValue(Int::class.java)
                var nonGojek = mutableData.child("NonGojek").child("Sales").getValue(Int::class.java)
                var gojek = mutableData.child("Gojek").child("Sales").getValue(Int::class.java)
                var disc = mutableData.child("Promo").child("Discount").getValue(Int::class.java)
                var vouc = mutableData.child("Promo").child("Voucher").getValue(Int::class.java)

                if (sales == null) sales = 0
                if (nonGojek == null) nonGojek = 0
                if (gojek == null) gojek = 0
                if (disc == null) disc = 0
                if (vouc == null) vouc = 0

                totalSales = sales
                totalNonGojek = nonGojek
                totalGojek = gojek
                promo = disc + vouc

                return Transaction.success(mutableData)
            }
        })
    }
}