package com.andyra.storyapp.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.andyra.storyapp.R
import com.andyra.storyapp.adapter.ListStoryAdapter
import com.andyra.storyapp.databinding.ActivityMainBinding
import com.andyra.storyapp.databinding.CustomMainActionBarLayoutBinding
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.ui.viewmodel.StoryViewModel
import com.andyra.storyapp.ui.viewmodel.ViewModelFactory
import com.andyra.storyapp.ui.views.story.UserLocationActivity
import com.andyra.storyapp.ui.views.story.PostStoryActivity
import com.andyra.storyapp.ui.views.user.LoginActivity
import com.andyra.storyapp.util.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActionBarBinding: CustomMainActionBarLayoutBinding
    private lateinit var mSessionPreference: SessionPreference
    private lateinit var mListStoryAdapter: ListStoryAdapter

    private val mStoryVM: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }
    private val mLoading = LoadingDialog(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mActionBarBinding = CustomMainActionBarLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = mActionBarBinding.root

        mSessionPreference = SessionPreference(this)


        mBinding.swpRefreshStory.setOnRefreshListener {
            onRefresh()
        }

        mBinding.imbAddStory.setOnClickListener {
            intent(PostStoryActivity::class.java)
        }

        mLoading.isLoading(true)
        getListStory()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                logOut()
                true
            }
            R.id.menu_maps -> {
                intent(UserLocationActivity::class.java)
                true
            }
            else -> true
        }
    }

    override fun onRefresh() {
        mBinding.apply {
            swpRefreshStory.isRefreshing = true
            mListStoryAdapter.refresh()
            Timer().schedule(2000) {
                swpRefreshStory.isRefreshing = false
                rvListStory.smoothScrollToPosition(0)
            }
        }
    }

    private fun logOut() {
        mSessionPreference.setSession("")
        intent(LoginActivity::class.java)
        finish()
    }

    private fun getListStory() {
        mListStoryAdapter = ListStoryAdapter()
        mBinding.apply {
            rvListStory.setHasFixedSize(true)
            rvListStory.layoutManager = LinearLayoutManager(root.context)
            rvListStory.adapter = mListStoryAdapter
        }

        val mTokenID = StringBuilder("Bearer ").append(mSessionPreference.getSession()).toString()
        mStoryVM.listStory(mTokenID).observe(this
        ) {
            mListStoryAdapter.submitData(lifecycle, it)
        }

        mLoading.isLoading(false)
    }
}