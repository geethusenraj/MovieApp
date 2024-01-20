package com.example.movieapp.uimodules.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.BR
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.common.AppConstants.Companion.TOP_RATED
import com.example.movieapp.data.api.MoviesItem
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.databinding.FragmentTopRatedMovieListBinding
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.uimodules.category.adapter.MovieListAdapter
import com.example.movieapp.utils.Utils
import com.example.movieapp.viewmodels.TopRatedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieRatingFragment :
    BaseFragment<FragmentTopRatedMovieListBinding, TopRatedViewModel>(),
    CategoryNavigator, View.OnClickListener {

    private val mViewModel: TopRatedViewModel by viewModels()
    private lateinit var mBinding: FragmentTopRatedMovieListBinding
    lateinit var movieListAdapter: MovieListAdapter

    companion object {
        fun newInstance(categoryId: Int?, selectedTab: Int): MovieRatingFragment {
            val args = Bundle()
            categoryId?.let { args.putInt("category_id", it) }
            args.putInt("selected_tab", selectedTab)
            val fragment = MovieRatingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setDataBinding(inflater, container)
        mBinding = getViewDataBinding()
        mViewModel.setNavigator(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        mBinding.fab.setOnClickListener(this)
        movieListAdapter = MovieListAdapter(requireContext())
        mBinding.rvMovieList.apply {
            adapter = movieListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        initObservers()
        if (activity?.let { Utils.isNetworkConnected(it) } == true) {
            mViewModel.getCategoriesMovies(arguments?.getInt("category_id"))
        } else {
            activity?.resources?.let { showErrorDialog(it.getString(R.string.no_internet), "") }
        }
    }

    private fun initObservers() {
        mViewModel.movieList.observe(this) { response ->
            dismissProgress()
            if (response?.data != null && response.data.success == true) {
                response.data.movies?.let { movieList ->
                    if (movieList.isEmpty()) {
                        updateUiVisibility(View.VISIBLE, View.GONE)
                    } else {
                        updateUiVisibility(View.GONE, View.VISIBLE)
                        val sortedList = if (arguments?.getInt("selected_tab") == TOP_RATED) {
                            ArrayList(movieList)
                                .sortedWith(compareByDescending<MoviesItem?> { it?.movieRating })
                        } else {
                            ArrayList(movieList)
                                .sortedWith(compareBy { it?.movieRating })
                        }
                        movieListAdapter.updateMovieList(sortedList)
                    }
                }

            }
        }
    }

    private fun updateUiVisibility(textVisibility: Int, listVisibility: Int) {
        mBinding.tvNoContent.visibility = textVisibility
        mBinding.rvMovieList.visibility = listVisibility
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_top_rated_movie_list
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): TopRatedViewModel {
        return mViewModel
    }

    override fun showErrorResponse(result: NetworkResponse<MoviesResponse>?) {
        dismissProgress()
        result?.message?.let { showErrorDialog(it,activity?.resources?.getString(R.string.error)) }
    }

    override fun showProgress() {
        showProgress(resources.getString(R.string.progress_loading_text))
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> openUploadMovieScreen()
        }
    }

    private fun openUploadMovieScreen() {
        if (view != null) {
            findNavController().navigate(R.id.fragment_upload_movies)
        }
    }
}
