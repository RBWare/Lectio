package me.ash.reader.unreddit.data.remote.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.ash.reader.unreddit.data.model.Sorting
import me.ash.reader.unreddit.data.remote.api.reddit.model.Child
import me.ash.reader.unreddit.data.remote.api.reddit.source.CurrentSource

class SearchUserDataSource(
    private val source: CurrentSource,
    private val query: String,
    private val sorting: Sorting
) : PagingSource<String, Child>() {

    override val keyReuseSupported: Boolean = true

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Child> {
        return try {
            val response = source.searchUser(
                query,
                sorting.generalSorting,
                sorting.timeSorting,
                params.key
            )
            val data = response.data

            LoadResult.Page(data.children, data.before, data.after)
        } catch (e: Exception) {
            Log.e("SearchUserDataSource", "Error", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Child>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}
