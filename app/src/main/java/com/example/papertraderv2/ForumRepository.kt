package com.example.papertraderv2.data

import com.example.papertraderv2.models.ForumComment
import com.example.papertraderv2.models.ForumPost
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ForumRepository {

    private val db = FirebaseFirestore.getInstance()
    private val postsRef = db.collection("forum_posts")

    suspend fun createPost(post: ForumPost) {
        if (post.id.isBlank()) {
            postsRef.add(post).await()
        } else {
            postsRef.document(post.id).set(post).await()
        }
    }

    suspend fun getPosts(): List<ForumPost> {
        val snapshot = postsRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(ForumPost::class.java)
    }

    suspend fun createComment(postId: String, comment: ForumComment) {
        postsRef
            .document(postId)
            .collection("comments")
            .add(comment)
            .await()

        postsRef.document(postId)
            .update("commentCount", FieldValue.increment(1))
            .await()
    }

    suspend fun getComments(postId: String): List<ForumComment> {
        val snapshot = postsRef
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()

        return snapshot.toObjects(ForumComment::class.java)
    }

    fun upvotePost(postId: String) {
        postsRef.document(postId)
            .update("upvotes", FieldValue.increment(1))
    }

    fun downvotePost(postId: String) {
        postsRef.document(postId)
            .update("downvotes", FieldValue.increment(1))
    }
}