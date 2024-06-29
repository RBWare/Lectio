package me.ash.reader.unreddit.data.model

import me.ash.reader.unreddit.util.DateUtil
import me.ash.reader.unreddit.util.extension.formatNumber

data class User(
    val isSuspended: Boolean,

    val displayName: String,

    val title: String?,

    val over18: Boolean,

    val icon: String?,

    val url: String?,

    val publicDescription: String?,

    val postKarma: Int,

    val commentKarma: Int,

    val created: Long
) {
    fun getPostKarmaCount(): String {
        return postKarma.formatNumber()
    }

    fun getCommentKarmaCount(): String {
        return commentKarma.formatNumber()
    }

    fun getCakeDay(): String {
        return DateUtil.getFormattedDate(created)
    }
}
