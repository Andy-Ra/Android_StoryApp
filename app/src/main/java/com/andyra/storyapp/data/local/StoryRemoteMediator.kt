package com.andyra.storyapp.data.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.Loading.endOfPaginationReached
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.andyra.storyapp.api.ApiServices
import com.andyra.storyapp.data.remote.story.ListStoryResponse


@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val mApiServices: ApiServices,
    private val mDatabase: StoryDatabase,
    private var mToken: String
) : RemoteMediator<Int, ListStoryResponse>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryResponse>
    ): MediatorResult {
        val mPage = when (loadType) {
            LoadType.REFRESH -> {
                INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            val mResponseData =
                mApiServices.getListStory(mPage, state.config.pageSize, mToken)
                    .body()!!.listStory

            mDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    mDatabase.mRemoteKeysDao().deleteRemoteKeys()
                    mDatabase.mStoryDao().deleteLocalStory()
                }
                val mPrevKey = if (mPage == 1) null else mPage - 1
                val mNextKey = if (endOfPaginationReached) null else mPage + 1
                val mKeys = mResponseData!!.map {
                    RemoteKeys(id = it.id, prevKey = mPrevKey, nextKey = mNextKey)
                }
                mDatabase.mRemoteKeysDao().setRemoteKeys(mKeys)
                mDatabase.mStoryDao().addStoryToLocal(mResponseData)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStoryResponse>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { data ->
                mDatabase.mRemoteKeysDao().getRemoteKeysId(data.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStoryResponse>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { data ->
                mDatabase.mRemoteKeysDao().getRemoteKeysId(data.id)
            }
    }


    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}