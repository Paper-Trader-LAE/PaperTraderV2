package com.example.papertraderv2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private val apiKey = "d8ee075538ad4423bd620920b6bb24e5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val input = findViewById<EditText>(R.id.searchInput)
        val button = findViewById<Button>(R.id.searchButton)
        val card = findViewById<LinearLayout>(R.id.resultCard)
        val symbolText = findViewById<TextView>(R.id.stockSymbol)
        val priceText = findViewById<TextView>(R.id.stockPrice)

        button.setOnClickListener {

            val symbol = input.text.toString().trim()

            if (symbol.isEmpty()) {
                Toast.makeText(this, "Enter a Stock Symbol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            card.visibility = View.GONE

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.api.getPrice(apiKey, symbol)

                    symbolText.text = symbol.uppercase()
                    priceText.text = "$${response.price}"

                    card.visibility = View.VISIBLE

                } catch (e: Exception) {
                    Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}