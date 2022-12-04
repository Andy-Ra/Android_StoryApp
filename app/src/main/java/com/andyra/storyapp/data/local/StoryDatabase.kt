package com.andyra.storyapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andyra.storyapp.data.remote.story.ListStoryResponse


@Database(
    entities = [ListStoryResponse::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDatabase : RoomDatabase() {
    abstract fun mStoryDao(): StoryDao
    abstract fun mRemoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getStoryDatabase(context: Context): StoryDatabase {
            if (INSTANCE == null) {
                synchronized(StoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        StoryDatabase::class.java, "DB_Story"
                    ).build()
                }
            }
            return INSTANCE as StoryDatabase
        }
    }

}