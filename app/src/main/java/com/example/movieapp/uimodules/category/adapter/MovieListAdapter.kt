package com.example.movieapp.uimodules.category.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.data.api.MoviesItem
import com.example.movieapp.databinding.ItemMovieDetailsBinding
import com.example.movieapp.utils.setImageWithGlide

class MovieListAdapter(val context: Context) :
    RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {


    private var movieList: List<MoviesItem?> = ArrayList()

    inner class MovieListViewHolder(val binding: ItemMovieDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val binding =
            ItemMovieDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        with(holder) {
            with(movieList[position]) {
                this?.movieName?.let { binding.tvMovieTitle.text = it }
                this?.movieImage?.let {
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
                this?.movieDescription?.let { binding.tvMovieDescription.text = it }
                this?.movieRating?.let { binding.ratingBar.rating = it.toFloat() }
            }
        }
    }

    fun updateMovieList(movies: List<MoviesItem?>) {
        this.movieList = movies
        notifyDataSetChanged()
    }

}
