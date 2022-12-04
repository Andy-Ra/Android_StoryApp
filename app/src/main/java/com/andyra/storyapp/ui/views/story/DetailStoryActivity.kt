package com.andyra.storyapp.ui.views.story

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.andyra.storyapp.R
import com.andyra.storyapp.databinding.CustomDetailActionBarLayoutBinding
import com.andyra.storyapp.databinding.ItemListStoryBinding
import com.andyra.storyapp.util.LoadingDialog
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var mBinding: ItemListStoryBinding
    private lateinit var mActionBarBinding: CustomDetailActionBarLayoutBinding

    private val mLoading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ItemListStoryBinding.inflate(layoutInflater)
        mActionBarBinding = CustomDetailActionBarLayoutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.customView = mActionBarBinding.root
        mLoading.isLoading(true)

        setDetail()

        mActionBarBinding.imbCloseDetailStory.setOnClickListener {
            finish()
        }
    }

    private fun setDetail() {
        mLoading.isLoading(false)
        mBinding.apply {
            Glide.with(this@DetailStoryActivity).load(R.drawable.default_user).circleCrop()
                .into(imvItemStoryProfile)
            Glide.with(this@DetailStoryActivity).load(
                intent.getStringExtra(EXTRA_IMAGE).toString()
            ).into(imvItemImageStory)
            tvItemName.text = intent.getStringExtra(EXTRA_NAME).toString()
            tvItemStoryCreatedAt.text = intent.getStringExtra(EXTRA_DATE).toString()
            tvItemStory.text = intent.getStringExtra(EXTRA_DESCRIPTION).toString()
        }
    }


    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_IMAGE = "extra_image"
    }
}