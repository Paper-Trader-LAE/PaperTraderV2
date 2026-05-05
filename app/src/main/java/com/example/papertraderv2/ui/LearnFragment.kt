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
import com.example.papertraderv2.models.Lesson
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

    private fun progressFor(lessons: List<Lesson>): Int {
        val completed = lessons.count {
            ProgressRepository.isLessonCompleted(requireContext(), it.key)
        }
        return if (lessons.isEmpty()) 0 else (completed * 100) / lessons.size
    }

    private fun lessonPath(moduleFolder: String, lessonFile: String): String {
        return "modules/$moduleFolder/lessons/$lessonFile.txt"
    }

    private fun loadModules() {
        val module1Lessons = listOf(
            Lesson("module1_l1", "What Is Trading?", "Understand what trading really is.", lessonPath("Introduction_to_Trading", "What_Is_Trading")),
            Lesson("module1_l2", "Stocks, Forex, Crypto, and Indices", "Learn the difference between major markets.", lessonPath("Introduction_to_Trading", "Stocks_Forex_Crypto_and_Indices")),
            Lesson("module1_l3", "Why Markets Move", "See what causes price changes.", lessonPath("Introduction_to_Trading", "Why_Markets_Move")),
            Lesson("module1_l4", "Trading vs Investing", "Know the difference between the two.", lessonPath("Introduction_to_Trading", "Trading_vs_Investing")),
            Lesson("module1_l5", "Long and Short Positions", "Learn how traders profit in both directions.", lessonPath("Introduction_to_Trading", "Long_and_Short_Positions")),
            Lesson("module1_l6", "Bid, Ask, and Spread", "Learn basic market pricing terms.", lessonPath("Introduction_to_Trading", "Bid_Ask_and_Spread")),
            Lesson("module1_l7", "Trading Sessions", "Understand when markets are most active.", lessonPath("Introduction_to_Trading", "Trading_Sessions")),
            Lesson("module1_l8", "Market Orders and Limit Orders", "Understand two major order types.", lessonPath("Introduction_to_Trading", "Market_Orders_and_Limit_Orders")),
            Lesson("module1_l9", "Volatility Basics", "Understand how fast markets move.", lessonPath("Introduction_to_Trading", "Volatility_Basics")),
            Lesson("module1_l10", "Building a Trading Foundation", "Set the right mindset from the beginning.", lessonPath("Introduction_to_Trading", "Building_a_Trading_Foundation"))
        )

        val module2Lessons = listOf(
            Lesson("module2_l1", "Why Risk Management Matters", "Learn why protecting capital comes first.", lessonPath("Risk_Management", "Why_Risk_Management_Matters")),
            Lesson("module2_l2", "What Is Position Size?", "Understand how much to trade.", lessonPath("Risk_Management", "What_Is_Position_Size")),
            Lesson("module2_l3", "Lot Size Basics", "Learn how lot sizes affect trades.", lessonPath("Risk_Management", "Lot_Size_Basics")),
            Lesson("module2_l4", "Using a Stop Loss", "Learn how to limit downside.", lessonPath("Risk_Management", "Using_a_Stop_Loss")),
            Lesson("module2_l5", "Using a Take Profit", "Know how to lock in gains.", lessonPath("Risk_Management", "Using_a_Take_Profit")),
            Lesson("module2_l6", "Risk-to-Reward Ratio", "Learn how to compare risk and payoff.", lessonPath("Risk_Management", "Risk_to_Reward_Ratio")),
            Lesson("module2_l7", "The 1% Rule", "Use a simple rule to avoid large losses.", lessonPath("Risk_Management", "The_1_Percent_Rule")),
            Lesson("module2_l8", "Drawdown and Recovery", "Understand account setbacks.", lessonPath("Risk_Management", "Drawdown_and_Recovery")),
            Lesson("module2_l9", "Overleveraging", "See why too much size is dangerous.", lessonPath("Risk_Management", "Overleveraging")),
            Lesson("module2_l10", "Risk Plan Example", "Learn what a simple plan looks like.", lessonPath("Risk_Management", "Risk_Plan_Example"))
        )

        val module3Lessons = listOf(
            Lesson("module3_l1", "Candlestick Basics", "Learn how candles show price movement.", lessonPath("Technical_Analysis", "Candlestick_Basics")),
            Lesson("module3_l2", "Bullish and Bearish Candles", "Read candle direction correctly.", lessonPath("Technical_Analysis", "Bullish_and_Bearish_Candles")),
            Lesson("module3_l3", "Support and Resistance", "Find key price levels.", lessonPath("Technical_Analysis", "Support_and_Resistance")),
            Lesson("module3_l4", "Trend Direction", "Know whether price is rising, falling, or ranging.", lessonPath("Technical_Analysis", "Trend_Direction")),
            Lesson("module3_l5", "Trendlines", "Use simple lines to follow price structure.", lessonPath("Technical_Analysis", "Trendlines")),
            Lesson("module3_l6", "Moving Averages", "Use an indicator to smooth price.", lessonPath("Technical_Analysis", "Moving_Averages")),
            Lesson("module3_l7", "RSI Basics", "Measure momentum and possible exhaustion.", lessonPath("Technical_Analysis", "RSI_Basics")),
            Lesson("module3_l8", "MACD Basics", "Learn another momentum tool.", lessonPath("Technical_Analysis", "MACD_Basics")),
            Lesson("module3_l9", "Chart Patterns", "Recognize repeating market behavior.", lessonPath("Technical_Analysis", "Chart_Patterns")),
            Lesson("module3_l10", "Combining Confluences", "Learn how stronger setups are built.", lessonPath("Technical_Analysis", "Combining_Confluences"))
        )

        val module4Lessons = listOf(
            Lesson("module4_l1", "Trading Psychology Overview", "Understand the mental side of trading.", lessonPath("Trading_Psychology", "Trading_Psychology_Overview")),
            Lesson("module4_l2", "Fear in Trading", "Learn how fear affects decisions.", lessonPath("Trading_Psychology", "Fear_in_Trading")),
            Lesson("module4_l3", "Greed in Trading", "See how greed damages discipline.", lessonPath("Trading_Psychology", "Greed_in_Trading")),
            Lesson("module4_l4", "Revenge Trading", "Avoid reacting emotionally after losses.", lessonPath("Trading_Psychology", "Revenge_Trading")),
            Lesson("module4_l5", "Overtrading", "Learn why too many trades can hurt performance.", lessonPath("Trading_Psychology", "Overtrading")),
            Lesson("module4_l6", "Patience and Discipline", "Build a calm trading routine.", lessonPath("Trading_Psychology", "Patience_and_Discipline")),
            Lesson("module4_l7", "Following a Trading Plan", "Trade with structure instead of emotion.", lessonPath("Trading_Psychology", "Following_a_Trading_Plan")),
            Lesson("module4_l8", "Handling Losing Streaks", "Stay stable during rough periods.", lessonPath("Trading_Psychology", "Handling_Losing_Streaks")),
            Lesson("module4_l9", "Confidence vs Ego", "Know the difference.", lessonPath("Trading_Psychology", "Confidence_vs_Ego")),
            Lesson("module4_l10", "Developing a Professional Mindset", "Think long term.", lessonPath("Trading_Psychology", "Developing_a_Professional_Mindset"))
        )

        val modules = listOf(
            Module("module1", "Introduction to Trading", "Learn the basics of stocks, forex, and crypto.", R.drawable.thumb_basics, module1Lessons, progressFor(module1Lessons)),
            Module("module2", "Risk Management", "Protect your capital and manage losses.", R.drawable.thumb_risk, module2Lessons, progressFor(module2Lessons)),
            Module("module3", "Technical Analysis", "Charts, indicators, trends, and patterns.", R.drawable.thumb_technical, module3Lessons, progressFor(module3Lessons)),
            Module("module4", "Trading Psychology", "Discipline, emotions, consistency.", R.drawable.thumb_psychology, module4Lessons, progressFor(module4Lessons))
        )

        binding.modulesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.modulesRecycler.adapter = ModuleAdapter(modules) { module ->
            val bundle = Bundle().apply {
                putString("key", module.key)
                putString("title", module.title)
                putString("desc", module.description)
                putParcelableArrayList("lessons", ArrayList(module.lessons))
            }
            findNavController().navigate(R.id.moduleDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}