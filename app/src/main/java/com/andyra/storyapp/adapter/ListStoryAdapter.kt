package com.andyra.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.databinding.ItemListStoryBinding
import com.andyra.storyapp.ui.views.story.DetailStoryActivity
import com.bumptech.glide.Glide

class ListStoryAdapter :
    PagingDataAdapter<ListStoryResponse, ListStoryAdapter.LisViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LisViewHolder {
        val mBinding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LisViewHolder(mBinding)
    }

    override fun onBindViewHolder(mHolder: LisViewHolder, mPosition: Int) {
        val mItem = getItem(mPosition)
        if (mItem != null) {
            mHolder.dataBinding(mItem)
            mHolder.itemView.setOnClickListener()
            {
                val mContext = mHolder.itemView.context
                val move = Intent(mContext, DetailStoryActivity::class.java)
                move.putExtra(DetailStoryActivity.EXTRA_NAME, mItem.name)
                move.putExtra(DetailStoryActivity.EXTRA_DATE, mItem.createdAt)
                move.putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, mItem.description)
                move.putExtra(DetailStoryActivity.EXTRA_IMAGE, mItem.photoUrl)
                mContext.startActivity(move)
            }

        }
    }

    class LisViewHolder(private val mBinding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun dataBinding(mItem: ListStoryResponse) {
            mBinding.apply {
                mItem.apply {
                    Glide.with(itemView.context).load(R.drawable.default_user).circleCrop()
                        .into(imvItemStoryProfile)
                    Glide.with(itemView.context).load(photoUrl).into(imvItemImageStory)
                    tvItemName.text = name
                    tvItemStory.text = description
                    tvItemStoryCreatedAt.text = createdAt
                }
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryResponse>() {
            override fun areItemsTheSame(oldStory: ListStoryResponse, newItem: ListStoryResponse): Boolean {
                return oldStory == newItem
            }

            override fun areContentsTheSame(oldStory: ListStoryResponse, newItem: ListStoryResponse): Boolean {
                return oldStory.id == newItem.id
            }
        }
    }
}