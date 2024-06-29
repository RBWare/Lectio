package me.ash.reader.unreddit.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import me.ash.reader.unreddit.data.model.Comment
import me.ash.reader.unreddit.data.model.db.PostEntity
import me.ash.reader.unreddit.data.model.db.Profile
import me.ash.reader.unreddit.data.model.db.Subscription
import me.ash.reader.unreddit.data.repository.PostListRepository
import me.ash.reader.unreddit.data.repository.PreferencesRepository
import me.ash.reader.unreddit.util.extension.latest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

open class BaseViewModel(
    preferencesRepository: PreferencesRepository,
    private val postListRepository: PostListRepository
) : ViewModel() {

    val currentProfile: SharedFlow<Profile> = preferencesRepository.getCurrentProfile().map {
        postListRepository.getProfile(it)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    protected val historyIds: Flow<List<String>> = currentProfile.flatMapLatest {
        postListRepository.getHistoryIds(it.id)
    }

    protected val subscriptions: Flow<List<Subscription>> = currentProfile.flatMapLatest {
        postListRepository.getSubscriptions(it.id)
    }

    protected val subscriptionsNames: Flow<List<String>> = currentProfile.flatMapLatest {
        postListRepository.getSubscriptionsNames(it.id)
    }

    protected val savedPostIds: Flow<List<String>> = currentProfile.flatMapLatest {
        postListRepository.getSavedPostIds(it.id)
    }

    fun insertPostInHistory(postId: String) {
        viewModelScope.launch {
            currentProfile.latest?.let {
                postListRepository.insertPostInHistory(postId, it.id)
            }
        }
    }

    fun toggleSavePost(post: PostEntity) {
        viewModelScope.launch {
            currentProfile.latest?.let {
                if (post.saved) {
                    postListRepository.unsavePost(post, it.id)
                } else {
                    postListRepository.savePost(post, it.id)
                }
            }
        }
    }

    fun toggleSaveComment(comment: Comment.CommentEntity) {
        viewModelScope.launch {
            currentProfile.latest?.let {
                if (comment.saved) {
                    postListRepository.unsaveComment(comment, it.id)
                } else {
                    postListRepository.saveComment(comment, it.id)
                }
            }
        }
    }
}
