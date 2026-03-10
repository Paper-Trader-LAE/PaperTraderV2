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

    private fun loadModules() {
        val module1Lessons = listOf(
            Lesson("module1_l1", "What Is Trading?", "Understand what trading really is.",
                "Trading is the process of buying and selling assets such as stocks, forex pairs, crypto, or ETFs to make a profit from price movement. Unlike long-term investing, trading usually focuses more on shorter timeframes and faster decisions."),
            Lesson("module1_l2", "Stocks, Forex, Crypto, and Indices", "Learn the difference between major markets.",
                "Stocks represent ownership in a company. Forex is the exchange of one currency for another. Crypto is digital currency traded on exchanges. Indices track a basket of assets, and many traders use ETFs like SPY or QQQ as index stand-ins."),
            Lesson("module1_l3", "Why Markets Move", "See what causes price changes.",
                "Markets move because of supply and demand. News, earnings, interest rates, sentiment, economic reports, and large institutional orders all affect whether buyers or sellers are stronger."),
            Lesson("module1_l4", "Trading vs Investing", "Know the difference between the two.",
                "Investing usually means holding assets for longer periods based on long-term growth. Trading focuses more on shorter moves, timing entries, and managing risk more actively."),
            Lesson("module1_l5", "Long and Short Positions", "Learn how traders profit in both directions.",
                "Going long means buying because you expect price to rise. Going short means selling because you expect price to fall. Traders can make money in both bullish and bearish markets."),
            Lesson("module1_l6", "Bid, Ask, and Spread", "Learn basic market pricing terms.",
                "The bid is the highest price a buyer is willing to pay. The ask is the lowest price a seller accepts. The spread is the gap between them, and it is a cost traders should always consider."),
            Lesson("module1_l7", "Trading Sessions", "Understand when markets are most active.",
                "Different markets are more active at different times. Forex has London and New York sessions. Stocks are most active during market open and around key economic or earnings events."),
            Lesson("module1_l8", "Market Orders and Limit Orders", "Understand two major order types.",
                "A market order executes immediately at the current best available price. A limit order only executes at a chosen price or better. Limit orders give more control but may not fill."),
            Lesson("module1_l9", "Volatility Basics", "Understand how fast markets move.",
                "Volatility measures how much price changes over time. High volatility can create more opportunity but also more risk. New traders should be careful when trading extremely volatile markets."),
            Lesson("module1_l10", "Building a Trading Foundation", "Set the right mindset from the beginning.",
                "A strong foundation comes from understanding markets, learning risk, and practicing consistency. Good traders do not rush. They build skill over time and focus on process before profit.")
        )

        val module2Lessons = listOf(
            Lesson("module2_l1", "Why Risk Management Matters", "Learn why protecting capital comes first.",
                "Risk management is what keeps traders in the game. A trader can be right often and still lose if risk is uncontrolled. Protecting your capital is more important than chasing profits."),
            Lesson("module2_l2", "What Is Position Size?", "Understand how much to trade.",
                "Position size is how large your trade is. It should be based on account size, stop loss distance, and how much you are willing to risk—not emotion or confidence."),
            Lesson("module2_l3", "Lot Size Basics", "Learn how lot sizes affect trades.",
                "Lot size determines trade volume. Even a small change in lot size can dramatically change profit and loss. New traders should stay small while learning."),
            Lesson("module2_l4", "Using a Stop Loss", "Learn how to limit downside.",
                "A stop loss is a pre-set exit point that closes a trade if price moves against you. It prevents one bad trade from turning into a major account loss."),
            Lesson("module2_l5", "Using a Take Profit", "Know how to lock in gains.",
                "A take profit automatically closes a trade when price reaches a target. It helps traders follow a plan and avoid holding too long out of greed."),
            Lesson("module2_l6", "Risk-to-Reward Ratio", "Learn how to compare risk and payoff.",
                "Risk-to-reward measures how much you stand to gain compared to how much you can lose. A setup risking 1 to make 2 has a 1:2 risk-to-reward ratio."),
            Lesson("module2_l7", "The 1% Rule", "Use a simple rule to avoid large losses.",
                "Many traders risk 1% or less of their account on a single trade. This makes it easier to survive losing streaks and stay emotionally stable."),
            Lesson("module2_l8", "Drawdown and Recovery", "Understand account setbacks.",
                "Drawdown is the reduction from a peak account value. The deeper the drawdown, the harder it is to recover. Preventing large drawdowns is key to long-term success."),
            Lesson("module2_l9", "Overleveraging", "See why too much size is dangerous.",
                "Overleveraging means using too much size relative to your account. It magnifies both wins and losses, but in most cases it causes traders to lose control emotionally and financially."),
            Lesson("module2_l10", "Risk Plan Example", "Learn what a simple plan looks like.",
                "A basic risk plan might include risking 1% per trade, using a stop loss on every trade, avoiding revenge trading, and stopping after reaching a daily loss limit.")
        )

        val module3Lessons = listOf(
            Lesson("module3_l1", "Candlestick Basics", "Learn how candles show price movement.",
                "Candlesticks show open, high, low, and close prices in a chosen period. They help traders quickly understand whether buyers or sellers were stronger."),
            Lesson("module3_l2", "Bullish and Bearish Candles", "Read candle direction correctly.",
                "Bullish candles usually close higher than they open, showing upward pressure. Bearish candles close lower than they open, showing downward pressure."),
            Lesson("module3_l3", "Support and Resistance", "Find key price levels.",
                "Support is an area where price tends to hold or bounce. Resistance is an area where price often struggles to move above. These levels help with entries and exits."),
            Lesson("module3_l4", "Trend Direction", "Know whether price is rising, falling, or ranging.",
                "An uptrend forms higher highs and higher lows. A downtrend forms lower highs and lower lows. Ranges move sideways between support and resistance."),
            Lesson("module3_l5", "Trendlines", "Use simple lines to follow price structure.",
                "Trendlines connect important highs or lows to show direction and momentum. They can also help highlight breakout or breakdown zones."),
            Lesson("module3_l6", "Moving Averages", "Use an indicator to smooth price.",
                "Moving averages show the average price over a set period. Traders use them to identify trend direction, dynamic support/resistance, and general momentum."),
            Lesson("module3_l7", "RSI Basics", "Measure momentum and possible exhaustion.",
                "RSI, or Relative Strength Index, measures the speed of price movement. High readings can suggest overbought conditions, while low readings can suggest oversold conditions."),
            Lesson("module3_l8", "MACD Basics", "Learn another momentum tool.",
                "MACD compares moving averages to show momentum and potential trend shifts. Traders often use crossovers and histogram changes to support their ideas."),
            Lesson("module3_l9", "Chart Patterns", "Recognize repeating market behavior.",
                "Common patterns include triangles, flags, double tops, double bottoms, and head-and-shoulders. Patterns do not guarantee an outcome, but they help organize trade ideas."),
            Lesson("module3_l10", "Combining Confluences", "Learn how stronger setups are built.",
                "Confluence means multiple reasons support one trade idea, such as trend direction, support/resistance, and a candle signal all lining up together.")
        )

        val module4Lessons = listOf(
            Lesson("module4_l1", "Trading Psychology Overview", "Understand the mental side of trading.",
                "Trading is not only technical. It is emotional. Fear, greed, impatience, and ego all affect decision-making, which is why psychology matters so much."),
            Lesson("module4_l2", "Fear in Trading", "Learn how fear affects decisions.",
                "Fear can make traders hesitate, exit too early, or avoid valid setups. Good planning reduces fear because the trader already knows the risk before entering."),
            Lesson("module4_l3", "Greed in Trading", "See how greed damages discipline.",
                "Greed causes traders to oversize, overtrade, ignore targets, or hold too long. A trading plan helps control greed by defining exits and size in advance."),
            Lesson("module4_l4", "Revenge Trading", "Avoid reacting emotionally after losses.",
                "Revenge trading happens when a trader tries to immediately win back losses without a good setup. This usually leads to more losses and emotional exhaustion."),
            Lesson("module4_l5", "Overtrading", "Learn why too many trades can hurt performance.",
                "Overtrading means taking too many trades, often from boredom, frustration, or lack of discipline. More trades does not mean more quality."),
            Lesson("module4_l6", "Patience and Discipline", "Build a calm trading routine.",
                "Patience means waiting for strong setups. Discipline means following the plan even when emotions push you to act differently."),
            Lesson("module4_l7", "Following a Trading Plan", "Trade with structure instead of emotion.",
                "A trading plan defines your setups, risk rules, entries, exits, and daily limits. It helps remove guesswork and keeps your process consistent."),
            Lesson("module4_l8", "Handling Losing Streaks", "Stay stable during rough periods.",
                "Losses are part of trading. A losing streak does not always mean your system is broken. Review your trades calmly before making changes."),
            Lesson("module4_l9", "Confidence vs Ego", "Know the difference.",
                "Confidence comes from preparation and repetition. Ego comes from needing to be right. Good traders trust their process but remain humble and adaptable."),
            Lesson("module4_l10", "Developing a Professional Mindset", "Think long term.",
                "A professional mindset focuses on consistency, review, discipline, and growth. Trading success is built through repeated good decisions over time—not one big trade.")
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