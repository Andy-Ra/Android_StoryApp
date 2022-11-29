package com.andyra.storyapp.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.andyra.storyapp.R
import com.andyra.storyapp.adapter.ListStoryAdapter
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.databinding.ActivityMainBinding
import com.andyra.storyapp.databinding.CustomMainActionBarLayoutBinding
import com.andyra.storyapp.preference.SessionPreference
import com.andyra.storyapp.ui.auth.StoryAuthentication
import com.andyra.storyapp.ui.viewmodel.StoryViewModel
import com.andyra.storyapp.ui.views.story.PostStoryActivity
import com.andyra.storyapp.ui.views.user.LoginActivity
import com.andyra.storyapp.util.LoadingDialog
import com.andyra.storyapp.util.intent

class MainActivity : AppCompatActivity(), StoryAuthentication {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mActionBarBinding: CustomMainActionBarLayoutBinding
    private lateinit var mSessionPreference: SessionPreference

    private val mStoryVM: StoryViewModel by viewModels()
    private val mLoading = LoadingDialog(this)

    private val mListStory = ArrayList<ListStoryResponse>()
    private var mToken = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mActionBarBinding = CustomMainActionBarLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = mActionBarBinding.root

        mStoryVM.mStoryAuthentication = this
        mSessionPreference = SessionPreference(this)


        mBinding.imbAddStory.setOnClickListener {
            intent(PostStoryActivity::class.java)
        }

        mLoading.isLoading(true)
        getToken()
        getListStory()
        mBinding.rvListStory.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logOut()
                true
            }
            else -> true
        }
    }

    private fun logOut() {
        mToken = ""
        mSessionPreference.setSession(mToken)
        intent(LoginActivity::class.java)
        finish()
    }


    override fun onSuccess(mListStoryResponse: ArrayList<ListStoryResponse>) {
        mListStory.clear()
        for(mStory in mListStoryResponse){
            mListStory.add(ListStoryResponse(
                mStory.id,
                mStory.name,
                mStory.createdAt,
                mStory.description,
                mStory.photoUrl
            ))
        }
        showRecyclerList()
        mLoading.isLoading(false)
    }

    private fun getToken() {
        mToken = mSessionPreference.getSession().toString()
    }


    private fun getListStory() {
        val mTokenId = StringBuilder("Bearer ").append(mSessionPreference.getSession()).toString()
        mStoryVM.listStory(mTokenId)
    }

    private fun showRecyclerList() {
        mBinding.apply {
            rvListStory.layoutManager = LinearLayoutManager(root.context)
            val mListStoryAdapter = ListStoryAdapter(mListStory)
            rvListStory.adapter = mListStoryAdapter
        }
    }
}