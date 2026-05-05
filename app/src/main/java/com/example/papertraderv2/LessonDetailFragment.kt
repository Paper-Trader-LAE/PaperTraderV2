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
        val lessonFilePath = arguments?.getString("lessonContent") ?: ""

        binding.lessonTitle.text = title
        binding.lessonSummary.text = summary
        binding.lessonContent.text = loadLessonFromAssets(lessonFilePath)

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

    private fun loadLessonFromAssets(filePath: String): String {
        return try {
            requireContext().assets.open(filePath).bufferedReader().use {
                it.readText()
            }
        } catch (e: Exception) {
            "Lesson content could not be loaded.\n\nMissing file path:\n$filePath"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}