package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.papertraderv2.data.ProgressRepository
import com.example.papertraderv2.databinding.FragmentModuleDetailBinding

class ModuleDetailFragment : Fragment() {

    private var _binding: FragmentModuleDetailBinding? = null
    private val binding get() = _binding!!

    private var key: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val title = arguments?.getString("title") ?: ""
        val desc = arguments?.getString("desc") ?: ""
        key = arguments?.getString("key") ?: ""

        binding.moduleTitle.text = title
        binding.moduleDescription.text = desc

        binding.btnMarkComplete.setOnClickListener {
            ProgressRepository.setProgress(requireContext(), key, 100)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}