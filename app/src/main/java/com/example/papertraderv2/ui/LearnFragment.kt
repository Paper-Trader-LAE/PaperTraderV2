package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.R
import com.example.papertraderv2.adapters.ModuleAdapter
import com.example.papertraderv2.data.ProgressRepository
import com.example.papertraderv2.databinding.FragmentLearnBinding
import com.example.papertraderv2.models.Module

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadModules()
    }

    private fun loadModules() {

        val modules = listOf(

            // ---------------- MODULE 1 ----------------
            Module(
                key = "module1",
                title = "Introduction to Trading",
                description = "Learn the basics of stocks, forex, and crypto.",
                content = """
                    Welcome to your first lesson!

                    In this module, you will learn:

                    • What stocks are  
                    • What forex is  
                    • What crypto is  
                    • Why markets move  
                    • The difference between long-term investing and trading  

                    Understanding these core concepts will help you build a strong foundation
                    before entering any financial market.

                    Tap "Mark Complete" below once you're ready.
                """.trimIndent(),
                thumbnailRes = R.drawable.thumb_basics,
                progress = ProgressRepository.getProgress(requireContext(), "module1")
            ),

            // ---------------- MODULE 2 ----------------
            Module(
                key = "module2",
                title = "Risk Management",
                description = "Protect your capital and manage losses.",
                content = """
                    Risk management is the MOST important part of trading.

                    In this module, you will learn:

                    • What risk really means  
                    • Lot sizes explained  
                    • Stop-loss vs. Take-profit  
                    • Risk-to-reward ratios  
                    • Position sizing strategies  

                    Real traders focus on risk. Not profits.
                """.trimIndent(),
                thumbnailRes = R.drawable.thumb_risk,
                progress = ProgressRepository.getProgress(requireContext(), "module2")
            ),

            // ---------------- MODULE 3 ----------------
            Module(
                key = "module3",
                title = "Technical Analysis",
                description = "Charts, indicators, trends, and patterns.",
                content = """
                    Technical analysis helps traders predict future price movement.

                    In this module, you will learn:

                    • Candlestick basics  
                    • Support & resistance  
                    • Trend lines  
                    • RSI, MACD, Moving Averages  
                    • Chart patterns (flags, triangles, head & shoulders)  
                """.trimIndent(),
                thumbnailRes = R.drawable.thumb_technical,
                progress = ProgressRepository.getProgress(requireContext(), "module3")
            ),

            // ---------------- MODULE 4 ----------------
            Module(
                key = "module4",
                title = "Trading Psychology",
                description = "Discipline, emotions, consistency.",
                content = """
                    Psychology affects everything in trading.

                    In this module, you will learn:

                    • Emotional discipline  
                    • Greed & fear  
                    • Overtrading  
                    • Building a trading plan  
                    • Avoiding revenge trading  

                    Master your emotions → Master the charts.
                """.trimIndent(),
                thumbnailRes = R.drawable.thumb_psychology,
                progress = ProgressRepository.getProgress(requireContext(), "module4")
            )
        )

        binding.modulesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.modulesRecycler.adapter = ModuleAdapter(modules) { module ->

            val bundle = Bundle().apply {
                putString("key", module.key)
                putString("title", module.title)
                putString("desc", module.description)
                putString("content", module.content)
            }

            findNavController().navigate(R.id.moduleDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}