package com.example.movieapp.uimodules.category.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movieapp.common.AppConstants.Companion.LOW_RATED
import com.example.movieapp.common.AppConstants.Companion.TOP_RATED
import com.example.movieapp.uimodules.category.MovieRatingFragment

class PagerAdapter(val context: Fragment, private val arguments: Bundle?) :
    FragmentStateAdapter(context) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment = when (position) {
        TOP_RATED -> MovieRatingFragment.newInstance(arguments?.getInt("category_id"), TOP_RATED)
        LOW_RATED -> MovieRatingFragment.newInstance(arguments?.getInt("category_id"), LOW_RATED)
        else -> throw IllegalStateException("Invalid adapter position")
    }
}