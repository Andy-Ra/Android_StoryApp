package com.andyra.storyapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andyra.storyapp.data.remote.story.ListStoryResponse

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStoryToLocal(mStory: List<ListStoryResponse>)

    @Query("SELECT * FROM story")
    fun getLocalStory(): PagingSource<Int, ListStoryResponse>

    @Query("DELETE FROM story")
    fun deleteLocalStory()

}