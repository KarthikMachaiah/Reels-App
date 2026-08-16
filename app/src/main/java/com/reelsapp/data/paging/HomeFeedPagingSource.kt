package com.reelsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.reelsapp.data.model.HomeFeedCard
import kotlinx.coroutines.delay

class HomeFeedPagingSource : PagingSource<Int, HomeFeedCard>() {

    override fun getRefreshKey(state: PagingState<Int, HomeFeedCard>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeFeedCard> {
        val page = params.key ?: 1
        return try {
            // Simulate network latency for internet pagination fetch
            delay(1000)

            val categories = listOf("Trending", "AI Art", "Cinematic", "Nature", "Urban", "Cyberpunk", "Music Beats", "Shorts")
            val itemsPerPage = params.loadSize.coerceAtMost(10)
            
            val newCards = (1..itemsPerPage).map { index ->
                val globalId = (page - 1) * 10 + index
                val category = categories[globalId % categories.size]
                HomeFeedCard(
                    id = "card_$globalId",
                    title = "Discover $category Story #$globalId",
                    author = "@creator_$globalId",
                    imageUrl = "https://picsum.photos/seed/$globalId/1080/1920",
                    avatarUrl = "https://picsum.photos/seed/user_$globalId/200/200",
                    category = category,
                    likesCount = 1200 + globalId * 85
                )
            }

            val nextKey = if (newCards.isEmpty() || page >= 20) null else page + 1
            val prevKey = if (page == 1) null else page - 1

            LoadResult.Page(
                data = newCards,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
