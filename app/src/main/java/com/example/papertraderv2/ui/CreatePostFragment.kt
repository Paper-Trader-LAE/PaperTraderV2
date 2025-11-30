package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.papertraderv2.data.ForumRepository
import com.example.papertraderv2.databinding.CreatePostFragmentBinding
import com.example.papertraderv2.models.ForumPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CreatePostFragment : Fragment() {

    private var _binding: CreatePostFragmentBinding? = null
    private val binding get() = _binding!!

    private val repo = ForumRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreatePostFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnSubmitPost.setOnClickListener {
            val title = binding.inputPostTitle.text.toString().trim()
            val content = binding.inputPostContent.text.toString().trim()

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(requireContext(), "Title and content required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitPost(title, content)
        }
    }

    private fun submitPost(title: String, content: String) {
        val post = ForumPost(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            author = "User", // placeholder
            timestamp = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                repo.createPost(post)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Posted!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}