package com.example.movieapp.uimodules.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.api.MovieCategoriesItem
import com.example.movieapp.databinding.ItemMovieCategoriesBinding
import com.example.movieapp.utils.setImageWithGlide

class MovieCategoryAdapter(val context: Context, private val listener: CategoryClickLister) :
    RecyclerView.Adapter<MovieCategoryAdapter.MovieCategoryViewHolder>() {


    private var movieCategoryList: List<MovieCategoriesItem?> = ArrayList()

    inner class MovieCategoryViewHolder(val binding: ItemMovieCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCategoryViewHolder {
        val binding =
            ItemMovieCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return movieCategoryList.size
    }

    override fun onBindViewHolder(holder: MovieCategoryViewHolder, position: Int) {
        with(holder) {
            this.binding.layoutCategory.setOnClickListener { listener.onCategorySelected(movieCategoryList[position]) }
            with(movieCategoryList[position]) {
                this?.categoryName?.let { binding.tvMovieCategory.text = it }
                this?.categoryImage?.let {
                    setImageWithGlide(
                        context,
                        binding.ivMovieIcon,
                        it,
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_icon_corrupted_placeholder
                            )
                        },
                        binding.pbView
                    )
                }
            }
        }
    }

    fun updateMovieList(movieCategories: List<MovieCategoriesItem?>) {
        this.movieCategoryList = movieCategories
        notifyDataSetChanged()
    }

    interface CategoryClickLister {
        fun onCategorySelected(movieCategoriesItem: MovieCategoriesItem?)
    }

}