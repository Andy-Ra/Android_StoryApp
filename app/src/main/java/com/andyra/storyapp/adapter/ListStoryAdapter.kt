package com.andyra.storyapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andyra.storyapp.R
import com.andyra.storyapp.data.remote.story.ListStoryResponse
import com.andyra.storyapp.databinding.ItemListStoryBinding
import com.andyra.storyapp.ui.views.story.DetailStoryActivity
import com.bumptech.glide.Glide

class ListStoryAdapter(private val mList: ArrayList<ListStoryResponse>) :
    RecyclerView.Adapter<ListStoryAdapter.LisViewHolder>() {
    private lateinit var mBinding: ItemListStoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LisViewHolder {
        mBinding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LisViewHolder(mBinding)
    }

    override fun onBindViewHolder(mHolder: LisViewHolder, mposition: Int) {
        val (_, name, createdAt, description, photoUrl) = mList[mposition]
        Log.e(this@ListStoryAdapter.toString(), "ara $name")
        mHolder.apply {
            mBinding.apply {
                Glide.with(itemView.context).load(R.drawable.default_user).circleCrop()
                    .into(imvItemStoryProfile)
                Glide.with(itemView.context).load(photoUrl).into(imvItemImageStory)
                tvItemName.text = name
                tvItemStory.text = description
                tvItemStoryCreatedAt.text = createdAt
            }
        }

        mHolder.itemView.setOnClickListener {
            val mContext = mHolder.itemView.context
            val move = Intent(mContext, DetailStoryActivity::class.java)
            move.putExtra(DetailStoryActivity.EXTRA_NAME, mList[mposition].name)
            move.putExtra(DetailStoryActivity.EXTRA_DATE, mList[mposition].createdAt)
            move.putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, mList[mposition].description)
            move.putExtra(DetailStoryActivity.EXTRA_IMAGE, mList[mposition].photoUrl)
            mContext.startActivity(move)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int = mList.size


    inner class LisViewHolder(mBinding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(mBinding.root)

}