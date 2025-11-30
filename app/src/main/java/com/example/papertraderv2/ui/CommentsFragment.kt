package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.adapters.CommentAdapter
import com.example.papertraderv2.data.ForumRepository
import com.example.papertraderv2.databinding.FragmentCommentsBinding
import com.example.papertraderv2.models.ForumComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val repo = ForumRepository()
    private lateinit var adapter: CommentAdapter
    private val comments = mutableListOf<ForumComment>()

    private var postId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postId = arguments?.getString("postId") ?: ""

        adapter = CommentAdapter(comments)
        binding.commentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.commentsRecycler.adapter = adapter

        binding.btnSendComment.setOnClickListener {
            val text = binding.commentInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            addComment(text)
        }

        loadComments()
    }

    private fun addComment(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val comment = ForumComment(
                    postId = postId,
                    author = "User", // placeholder
                    content = text
                )
                repo.createComment(postId, comment)

                requireActivity().runOnUiThread {
                    binding.commentInput.text.clear()
                    Toast.makeText(requireContext(), "Comment added", Toast.LENGTH_SHORT).show()
                    loadComments() // refresh list
                }

            } catch (e: Exception) {
                e.printStackTrace()
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadComments() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val list = repo.getComments(postId)

                requireActivity().runOnUiThread {
                    comments.clear()
                    comments.addAll(list)
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}