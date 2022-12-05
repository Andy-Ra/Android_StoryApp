package com.andyra.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.andyra.storyapp.adapter.ListStoryAdapter
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.data.remote.story.StoryResponse
import com.andyra.storyapp.repository.StoryRepository
import com.andyra.storyapp.ui.MainCoroutineRule
import com.andyra.storyapp.utils.DataDummy
import com.andyra.storyapp.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    var mInstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mMainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var mStoryViewModel: StoryViewModel
    private lateinit var mStoryRepository: StoryRepository

    @Before
    fun setup(){
        mStoryRepository = Mockito.mock(StoryRepository::class.java)
    }

    @Test
    fun `when get List Story is Successful`() = runTest {
        val mDummyStory = DataDummy.generateDummyShowStoryResponse()
        val mData: PagingData<ListStoryResponse> = PagingTestStorySources.snapshot(mDummyStory)
        val mStory = MutableLiveData<PagingData<ListStoryResponse>>()

        mStory.postValue(mData)
        Mockito.`when`(mStoryViewModel.listStory(TOKEN)).thenReturn(mStory)

        val mActualStoryResponse: PagingData<ListStoryResponse> =
            mStoryViewModel.listStory(TOKEN).getOrAwaitValue()

        val mDiffer = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        mDiffer.submitData(mActualStoryResponse)

        verify(mStoryViewModel).listStory(TOKEN)
        assertNotNull(mDiffer.snapshot())
        assertEquals(mDummyStory.size, mDiffer.snapshot().size)
    }

    @Test
    fun `when Post Story is Successful`() = runTest{
        val mDummyPostStory = DataDummy.generateDummyPostStoryResponse()
        val mDescription = "Hello my friend".toRequestBody("text/plain".toMediaType())
        val mFile = Mockito.mock(File::class.java)
        val mRequestImageFile = mFile.asRequestBody("image/jpg".toMediaTypeOrNull())
        val mImageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "nameFile",
            mRequestImageFile
        )

        val mExpectedStory = MutableLiveData<StoryResponse>()
        val mCDStory = CompletableDeferred(mExpectedStory)
        mExpectedStory.postValue(mDummyPostStory)

        Mockito.`when`(mStoryViewModel.postStory(TOKEN, mImageMultipart, mDescription, LOCATION.latitude, LOCATION.longitude)).thenReturn(mCDStory)
        val mActualStory = mStoryViewModel.postStory(TOKEN, mImageMultipart, mDescription, LOCATION.latitude, LOCATION.longitude).run { mExpectedStory.getOrAwaitValue() }

        verify(mStoryViewModel).postStory(TOKEN, mImageMultipart, mDescription, LOCATION.latitude, LOCATION.longitude)
        assertNotNull(mActualStory)
        assertEquals(mExpectedStory.value, mActualStory)
    }

    @Test
    fun `when get List Story with Map is Successful`() = runTest{
        val mDummyStory = DataDummy.generateDummyShowMapsStory()
        val mExpectedStory = MutableLiveData<StoryResponse>()
        val mCDStory = CompletableDeferred(mExpectedStory)
        mExpectedStory.postValue(mDummyStory)

        Mockito.`when`(mStoryViewModel.getLocation(TOKEN, PAGE)).thenReturn(mCDStory)
        val mActualStory = mStoryViewModel.getLocation(TOKEN, PAGE).run { mExpectedStory.getOrAwaitValue() }

        verify(mStoryViewModel).getLocation(TOKEN, PAGE)
        assertNotNull(mActualStory)
        assertEquals(mExpectedStory.value, mActualStory)
    }

    companion object {
        private const val TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVlzRzdNMEc0aXFZb21nY28iLCJpYXQiOjE2NzAxNTQ5ODd9.hjY6EGFZA4QOdbiFtjz1uY7rphFnL1dJCtsvbwr0xsQ"
        private val LOCATION = LatLng(84.9999817, 89.9999999)
        private const val PAGE = 1
    }
}


class PagingTestStorySources:
    PagingSource<Int, LiveData<List<ListStoryResponse>>>() {
    companion object {
        fun snapshot(mItems: List<ListStoryResponse>): PagingData<ListStoryResponse> {
            return PagingData.from(mItems)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryResponse>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryResponse>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}


val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
