package com.cpm.g1.theacmeelectronicsshop.ui.basket

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.cpm.g1.theacmeelectronicsshop.MainActivity
import com.cpm.g1.theacmeelectronicsshop.R
import com.cpm.g1.theacmeelectronicsshop.ui.BasketHelper
import java.nio.file.Files.delete

class BasketFragment : Fragment() {
    private val dbHelper by lazy { BasketHelper(context) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_basket, container, false)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tempAddProducts()

        val productList = view.findViewById<ListView>(R.id.basket_sv)
        val basketCursor = dbHelper.getAll()
        val mainActivity = activity as MainActivity
        mainActivity.startManagingCursor(basketCursor)

        // Basket Adapter
        productList.adapter = ProductAdapter(basketCursor)
        productList.emptyView = view.findViewById(R.id.empty_list)
    }

    private fun tempAddProducts() {
        dbHelper.insert(
            "iPad Pro (11'' - 128 GB - Gray)",
            "Apple",
            "The Apple iPad Pro is a 12.9-inch touch screen tablet PC that is larger and offers higher resolution than Apple's other iPad models. The iPad Pro was scheduled to debut in November 2015, running the iOS 9 operating system. Apple unveiled the device at a September 2015 event in San Francisco.",
            909.99F,
            1,
        )
        dbHelper.insert(
            "iPad Pro (13'' - 128 GB - Gray)",
            "Apple",
            "The Apple iPad Pro is a 12.9-inch touch screen tablet PC that is larger and offers higher resolution than Apple's other iPad models. The iPad Pro was scheduled to debut in November 2015, running the iOS 9 operating system. Apple unveiled the device at a September 2015 event in San Francisco.",
            909.99F,
            1,
        )
    }

    inner class ProductAdapter(c: Cursor) : CursorAdapter(activity, c,true) {

        override fun newView(ctx: Context, c: Cursor, parent: ViewGroup): View {
            val row: View = layoutInflater.inflate(R.layout.product_row, parent, false)
            val priceText = getString(R.string.product_price, dbHelper.getPrice(c))
            val plusButton = row.findViewById<ImageButton>(R.id.plus_button)
            val minusButton = row.findViewById<ImageButton>(R.id.minus_button)
            val deleteButton = row.findViewById<ImageButton>(R.id.product_delete)

            row.findViewById<TextView>(R.id.name_text).text = dbHelper.getName(c)
            row.findViewById<TextView>(R.id.price_text).text = priceText
            row.findViewById<TextView>(R.id.brand_text).text = dbHelper.getBrand(c)
            row.findViewById<TextView>(R.id.product_quantity).text = dbHelper.getQuantity(c)
            //val image = row.findViewById<ImageView>(R.id.product_image)
            //image.setImageResource(R.drawable.ic_test)

            deleteButton.setOnClickListener{ onDeleteClickListener(c) }
            plusButton.setOnClickListener{ onPlusClickListener(row, c) }
            minusButton.setOnClickListener{ onMinusClickListener(row, c) }

            return row
        }

        @Suppress("DEPRECATION")
        private fun onDeleteClickListener(c: Cursor){
            dbHelper.delete(dbHelper.getId(c))
            c.requery()
            notifyDataSetChanged()
        }

        private fun onPlusClickListener(row: View, c: Cursor) {
            val quantityText = row.findViewById<TextView>(R.id.product_quantity)
            val newQuantity = quantityText.text.toString().toInt() + 1
            quantityText.text = newQuantity.toString()
            dbHelper.updateQuantity(dbHelper.getId(c), newQuantity)
        }

        private fun onMinusClickListener(row: View, c: Cursor){
            val quantityText = row.findViewById<TextView>(R.id.product_quantity)
            val newQuantity = quantityText.text.toString().toInt() - 1

            if(newQuantity == 0){
                onDeleteClickListener(c)
            }
            else{
                quantityText.text = newQuantity.toString()
                dbHelper.updateQuantity(dbHelper.getId(c), newQuantity)
            }

        }

        override fun bindView(view: View?, context: Context?, c: Cursor?) {
        }
    }

}