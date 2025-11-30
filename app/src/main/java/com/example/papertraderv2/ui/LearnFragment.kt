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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // ---- Your module list ----
        val modules = listOf(
            Module(
                thumbnailRes = R.drawable.thumb_basics,
                title = "Introduction to Trading",
                description = "Learn the basics of stocks, forex, and crypto.",
                progress = ProgressRepository.getProgress(requireContext(), "module1")
            ),
            Module(
                thumbnailRes = R.drawable.thumb_risk,
                title = "Risk Management",
                description = "Learn how to manage risk and protect your capital.",
                progress = ProgressRepository.getProgress(requireContext(), "module2")
            ),
            Module(
                thumbnailRes = R.drawable.thumb_technical,
                title = "Technical Analysis",
                description = "Charts, indicators, patterns, and trends.",
                progress = ProgressRepository.getProgress(requireContext(), "module3")
            ),
            Module(
                thumbnailRes = R.drawable.thumb_psychology,
                title = "Trading Psychology",
                description = "Mindset, discipline, and emotional control.",
                progress = ProgressRepository.getProgress(requireContext(), "module4")
            )
        )

        // RecyclerView Setup
        binding.modulesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.modulesRecycler.adapter = ModuleAdapter(modules) { module ->

            val bundle = Bundle().apply {
                putString("title", module.title)
                putString("desc", module.description)

                // Key must match your progress storage
                when (module.title) {
                    "Introduction to Trading" -> putString("key", "module1")
                    "Risk Management" -> putString("key", "module2")
                    "Technical Analysis" -> putString("key", "module3")
                    "Trading Psychology" -> putString("key", "module4")
                }
            }

            findNavController().navigate(R.id.moduleDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}