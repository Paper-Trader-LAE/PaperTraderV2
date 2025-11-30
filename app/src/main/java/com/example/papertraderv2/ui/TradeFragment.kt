package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.papertraderv2.databinding.FragmentTradeBinding
import com.example.papertraderv2.data.AppDatabase
import com.example.papertraderv2.models.Trade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TradeFragment : Fragment() {

    private var _binding: FragmentTradeBinding? = null
    private val binding get() = _binding!!

    private var symbol: String = ""
    private var action: String = ""
    private var livePrice: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTradeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        symbol = arguments?.getString("symbol") ?: ""
        action = arguments?.getString("action") ?: ""

        binding.tradeSymbol.text = "$action $symbol"

        // Passed price from ticker page? (Optional)
        livePrice = arguments?.getDouble("livePrice") ?: 0.0
        if (livePrice > 0) {
            binding.tradePrice.text = "$${"%.4f".format(livePrice)}"
        }

        binding.tradeConfirmBtn.setOnClickListener {
            executeTrade()
        }
    }

    private fun executeTrade() {
        val qty = binding.tradeQuantity.text.toString().toDoubleOrNull()

        if (qty == null || qty <= 0) {
            Toast.makeText(requireContext(), "Enter a valid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val total = livePrice * qty

        val trade = Trade(
            symbol = symbol,
            action = action,
            quantity = qty,
            price = livePrice,
            total = total
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(requireContext())
                .tradeDao()
                .insertTrade(trade)

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "$action Complete!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}