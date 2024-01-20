package com.example.movieapp.uimodules.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.BR
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.api.AllMovieCategoryResponse
import com.example.movieapp.data.api.MovieCategoriesItem
import com.example.movieapp.databinding.FragmentDashboardBinding
import com.example.movieapp.datamanager.PreferenceConnector
import com.example.movieapp.datamanager.PreferenceManager
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.uimodules.MainActivity
import com.example.movieapp.uimodules.dashboard.adapter.MovieCategoryAdapter
import com.example.movieapp.utils.Utils
import com.example.movieapp.viewmodels.DashboardFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardFragmentViewModel>(),
    DashboardFragmentNavigator, MovieCategoryAdapter.CategoryClickLister {

    private val mViewModel: DashboardFragmentViewModel by viewModels()
    private lateinit var mBinding: FragmentDashboardBinding
    lateinit var movieCategoryAdapter: MovieCategoryAdapter
    private lateinit var mainActivity: MainActivity

    @Inject
    lateinit var preferenceManager: PreferenceManager

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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    private fun initView() {
        movieCategoryAdapter = MovieCategoryAdapter(requireContext(), this)
        mBinding.rvMovieCategory.apply {
            adapter = movieCategoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            //addItemDecoration(AdaptiveSpacingItemDecoration(0,true))
            setHasFixedSize(true)
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.supportActionBar?.title = activity?.resources?.getString(R.string.app_name)
        initObservers()
        if (activity?.let { Utils.isNetworkConnected(it) } == true) {
            mViewModel.getMovieCategories()
        } else {
            activity?.resources?.let { showErrorDialog(it.getString(R.string.no_internet), "") }
        }
    }

    private fun initObservers() {
        mViewModel.movieCategoryList.observe(this) {
            dismissProgress()
            if (it?.data != null && it.data.success == true) {
                it.data.movieCategories?.let { it1 -> movieCategoryAdapter.updateMovieList(it1) }

            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): DashboardFragmentViewModel {
        return mViewModel
    }

    override fun showErrorResponse(result: NetworkResponse<AllMovieCategoryResponse>?) {
        dismissProgress()
        result?.message?.let { showErrorDialog(it, activity?.resources?.getString(R.string.error)) }
    }

    override fun showProgress() {
        showProgress(resources.getString(R.string.progress_loading_text))
    }

    override fun onCategorySelected(movieCategoriesItem: MovieCategoriesItem?) {
        if (movieCategoriesItem != null) {
            openMoviesInCategoryScreen(movieCategoriesItem.id, movieCategoriesItem.categoryName)
        }
    }


    private fun openMoviesInCategoryScreen(id: Int?, categoryName: String?) {
        if (view != null) {
            val bundle = Bundle()
            id?.let {
                bundle.putInt("category_id", it)
                preferenceManager.writeInt(PreferenceConnector.CATEGORY_ID, it)
            }
            categoryName?.let {
                bundle.putString("category", it)
                preferenceManager.writeString(PreferenceConnector.CATEGORY_NAME, it)
            }
            findNavController().navigate(R.id.fragment_movies_in_category, bundle)

        }
    }
}
