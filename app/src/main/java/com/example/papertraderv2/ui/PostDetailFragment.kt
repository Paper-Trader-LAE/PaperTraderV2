package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.adapters.CommentAdapter
import com.example.papertraderv2.data.ForumRepository
import com.example.papertraderv2.databinding.PostDetailFragmentBinding
import com.example.papertraderv2.models.ForumComment
import com.example.papertraderv2.models.ForumPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment() {

    private var _binding: PostDetailFragmentBinding? = null
    private val binding get() = _binding!!

    private val repo = ForumRepository()
    private var postId: String = ""

    private val commentList = mutableListOf<ForumComment>()
    private lateinit var adapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PostDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        postId = arguments?.getString("postId") ?: ""

        binding.commentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = CommentAdapter(commentList)
        binding.commentsRecycler.adapter = adapter

        loadPost()
        loadComments()

        binding.btnSendComment.setOnClickListener {
            val text = binding.inputComment.text.toString().trim()
            if (text.isNotBlank()) {
                addComment(text)
                binding.inputComment.text.clear()
            }
        }
    }

    private fun loadPost() {
        CoroutineScope(Dispatchers.IO).launch {
            val posts = repo.getPosts()
            val post = posts.find { it.id == postId } ?: return@launch

            requireActivity().runOnUiThread {
                binding.postDetailTitle.text = post.title
                binding.postDetailContent.text = post.content
            }
        }
    }

    private fun loadComments() {
        CoroutineScope(Dispatchers.IO).launch {
            val comments = repo.getComments(postId)

            requireActivity().runOnUiThread {
                commentList.clear()
                commentList.addAll(comments)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun addComment(text: String) {
        val comment = ForumComment(
            postId = postId,
            author = "User",
            content = text
        )

        CoroutineScope(Dispatchers.IO).launch {
            repo.createComment(postId, comment)
            loadComments()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}