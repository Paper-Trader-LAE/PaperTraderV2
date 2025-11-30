package com.example.papertraderv2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.papertraderv2.R
import com.example.papertraderv2.adapters.ForumPostAdapter
import com.example.papertraderv2.data.ForumRepository
import com.example.papertraderv2.databinding.FragmentForumBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val repo = ForumRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.forumRecycler.layoutManager = LinearLayoutManager(requireContext())

        // Go to create post screen
        binding.btnCreatePost.setOnClickListener {
            findNavController().navigate(R.id.createPostFragment)
        }

        loadPosts()
    }

    private fun loadPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val posts = repo.getPosts()

                requireActivity().runOnUiThread {
                    binding.forumRecycler.adapter =
                        ForumPostAdapter(posts) { post ->
                            val bundle = Bundle().apply {
                                putString("postId", post.id)
                            }
                            findNavController().navigate(R.id.commentsFragment, bundle)
                        }
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