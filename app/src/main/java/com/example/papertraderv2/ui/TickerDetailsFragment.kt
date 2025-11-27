package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.papertraderv2.R

class TickerDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ticker_details, container, false)

        val title = view.findViewById<TextView>(R.id.tickerTitle)
        val ticker = arguments?.getString("ticker") ?: "Unknown"

        title.text = ticker

        return view
    }
}