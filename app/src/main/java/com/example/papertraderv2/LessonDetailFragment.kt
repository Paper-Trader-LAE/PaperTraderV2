package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.papertraderv2.data.ProgressRepository
import com.example.papertraderv2.databinding.FragmentLessonDetailBinding

class LessonDetailFragment : Fragment() {

    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!

    private var lessonKey: String = ""
    private var moduleKey: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lessonKey = arguments?.getString("lessonKey") ?: ""
        moduleKey = arguments?.getString("moduleKey") ?: ""

        val title = arguments?.getString("lessonTitle") ?: ""
        val summary = arguments?.getString("lessonSummary") ?: ""
        val content = arguments?.getString("lessonContent") ?: ""

        binding.lessonTitle.text = title
        binding.lessonSummary.text = summary
        binding.lessonContent.text = content

        val completed = ProgressRepository.isLessonCompleted(requireContext(), lessonKey)
        binding.btnMarkComplete.text = if (completed) "Completed" else "Mark Complete"

        binding.btnMarkComplete.setOnClickListener {
            ProgressRepository.setLessonCompleted(requireContext(), lessonKey, true)
            binding.btnMarkComplete.text = "Completed"

            parentFragmentManager.setFragmentResult("lesson_completed", Bundle().apply {
                putString("moduleKey", moduleKey)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}