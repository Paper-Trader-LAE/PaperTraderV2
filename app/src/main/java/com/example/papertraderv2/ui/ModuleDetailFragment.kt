package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.R
import com.example.papertraderv2.adapters.LessonAdapter
import com.example.papertraderv2.data.ProgressRepository
import com.example.papertraderv2.databinding.FragmentModuleDetailBinding
import com.example.papertraderv2.models.Lesson

class ModuleDetailFragment : Fragment() {

    private var _binding: FragmentModuleDetailBinding? = null
    private val binding get() = _binding!!

    private var moduleKey: String = ""
    private var lessons: ArrayList<Lesson> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModuleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        moduleKey = arguments?.getString("key") ?: ""
        val title = arguments?.getString("title") ?: ""
        val desc = arguments?.getString("desc") ?: ""

        binding.moduleTitle.text = title
        binding.moduleDescription.text = desc

        lessons = arguments?.getParcelableArrayList("lessons") ?: arrayListOf()

        refreshLessons()

        parentFragmentManager.setFragmentResultListener("lesson_completed", viewLifecycleOwner) { _, _ ->
            refreshLessons()
        }
    }

    private fun refreshLessons() {
        val completedKeys = lessons
            .map { it.key }
            .filter { ProgressRepository.isLessonCompleted(requireContext(), it) }
            .toSet()

        val progress = if (lessons.isEmpty()) 0 else ((completedKeys.size * 100) / lessons.size)
        binding.progressBar.progress = progress
        binding.progressText.text = "$progress% Complete"

        binding.lessonsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.lessonsRecycler.adapter = LessonAdapter(lessons, completedKeys) { lesson ->
            val bundle = Bundle().apply {
                putString("moduleKey", moduleKey)
                putString("lessonKey", lesson.key)
                putString("lessonTitle", lesson.title)
                putString("lessonSummary", lesson.summary)
                putString("lessonContent", lesson.content)
            }
            findNavController().navigate(R.id.lessonDetailFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}