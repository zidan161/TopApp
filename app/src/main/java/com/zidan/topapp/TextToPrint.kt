package com.zidan.topapp

import android.bluetooth.BluetoothSocket
import com.zidan.topapp.data.Makanan
import java.io.OutputStream
import java.text.DecimalFormat
import java.util.*

class TextToPrint(private val mOutputStream: OutputStream, private val selectedItems: List<Makanan>, private val idInt: String) {

    private val formatDef = byteArrayOf(0x1B, 0x21, 0x00)
    private val formatBoldMed = byteArrayOf(0x1B, 0x21, 0x20)
    private val formatBoldBig = byteArrayOf(0x1B, 0x21, 0x10)

    private val center = byteArrayOf(0x1b, 'a'.toByte(), 0x01)

    fun setText(mSocket: BluetoothSocket, pembayaran: Map<String, Any>, isGojek: Boolean){

        val date = Calendar.getInstance().time
        val name = "NASI GORENG TOP"

        val text = "\n" +
                "       Ruko Wijaya Kusuma\n" +
                "      JL.Wijaya Kusuma no.40\n" +
                "     Mengelo Sooko Mojokerto\n" +
                "--------------------------------\n" +
                date.toSimpleString() +"     $idInt\n"+
                "--------------------------------\n" +
                "Menu                         QTY\n" +
                "--------------------------------\n" +
                allItems(selectedItems, pembayaran, isGojek) +
                "          Terima Kasih\n" +
                "        Selamat Menikmati\n" +
                " Berdoa Sebelum & Sesudah Makan\n\n\n\n"

        mOutputStream.write(center)
        mOutputStream.write(formatBoldMed)
        mOutputStream.write(name.toByteArray())
        mOutputStream.write(formatDef)
        mOutputStream.write(text.toByteArray())
        mSocket.close()
        mOutputStream.close()
    }

    fun setChefText(){

        val text =  "\n\n" +
                "$idInt\n" +
                "--------------------------------\n" +
                "Menu                         QTY\n" +
                "--------------------------------\n" +
                allChefItems(selectedItems)

        mOutputStream.write(text.toByteArray())
    }

    private fun allItems(selectedItems: List<Makanan>, pembayaran: Map<String, Any>, isGojek: Boolean): String {

        var textMenu = ""
        var totalAllItem = 0
        val formatter = DecimalFormat("##,###,###")

        val cash = pembayaran.getValue("cash").toString()
        val disc = pembayaran.getValue("disc")
        val vouc = pembayaran.getValue("vouc")
        val kembalian = pembayaran.getValue("change").toString()
        val finalTotal = pembayaran.getValue("total")

        for (i in selectedItems.indices){

            var totalPrice: Int
            var price: String

            if (!isGojek) {
                totalPrice = selectedItems[i].count * selectedItems[i].price.toInt()
                price = selectedItems[i].price
            } else {
                totalPrice = selectedItems[i].count * selectedItems[i].gojekPrice.toInt()
                price = selectedItems[i].gojekPrice
            }

            totalAllItem += totalPrice * 1000

            textMenu += toFormatString(selectedItems[i].name ,"%1$-29s") +
                    toFormatString(selectedItems[i].count.toString(), "%1$3s") +
                    "\n" +
                    toFormatString("@ ${price}K" ,"%1$-28s") +
                    toFormatString("${totalPrice}K", "%1$4s") +
                    "\n"
        }

        textMenu += "--------------------------------\n" +
                toFormatString("Price", "%1$-26s") +
                toFormatString(formatter.format(totalAllItem), "%1$6s") +
                "\n"+"                        --------\n" +
                toFormatString("Discount", "%1$-29s") +
                toFormatString("$disc%", "%1$3s") +
                "\n"+
                toFormatString("Voucher", "%1$-26s") +
                toFormatString(formatter.format(vouc), "%1$6s") +
                "\n"+"                        --------\n" +
                toFormatString("Total", "%1$-26s") +
                toFormatString(formatter.format(finalTotal), "%1$6s") +
                "\n"+
                toFormatString("Cash", "%1$-26s") +
                toFormatString(cash, "%1$6s") +
                "\n"+
                toFormatString("Change", "%1$-26s")

        textMenu += toFormatString(kembalian, "%1$6s")

        return textMenu
    }

    private fun allChefItems(selectedItems: List<Makanan>): String {

        var textMenu = ""

        for (i in selectedItems.indices) {

            textMenu += toFormatString(selectedItems[i].name, "%1$-29s") +
                    toFormatString(selectedItems[i].count.toString(), "%1$3s") +
                    "\n"
        }

        textMenu += "\n\n\n"

        return textMenu
    }
}