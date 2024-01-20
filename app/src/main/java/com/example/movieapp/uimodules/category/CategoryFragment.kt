package com.example.movieapp.uimodules.category

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.movieapp.BR
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.api.MoviesResponse
import com.example.movieapp.databinding.FragmentMoviesInCategoryBinding
import com.example.movieapp.helper.NetworkResponse
import com.example.movieapp.uimodules.MainActivity
import com.example.movieapp.uimodules.category.adapter.PagerAdapter
import com.example.movieapp.viewmodels.CategoryViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesInCategoryFragment :
    BaseFragment<FragmentMoviesInCategoryBinding, CategoryViewModel>(),
    CategoryNavigator {

    private val mViewModel: CategoryViewModel by viewModels()
    private lateinit var mBinding: FragmentMoviesInCategoryBinding
    private var pagerAdapter: PagerAdapter? = null
    private lateinit var mainActivity: MainActivity

    var customView: ConstraintLayout? = null
    private var titles: Array<String> = arrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setDataBinding(inflater, container)
        mBinding = getViewDataBinding()
        mViewModel.setNavigator(this)
        titles = arrayOf(getString(R.string.top_rated), getString(R.string.low_rated))
        setUpViewPager()
        return mBinding.root
    }

    private fun setUpViewPager() {
        pagerAdapter = PagerAdapter(this, arguments)
        mBinding.viewPager.apply {
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 2
        }
        TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewPager,
            true, true
        ) { currentTab, currentPosition ->
            Log.d(
                "MoviesInCategoryFragment",
                "TabLayoutMediator : currentPosition : $currentPosition"
            )
        }.attach()
        createTabIcons(mBinding.tabLayout)
        mBinding.tabLayout.getTabAt(0)?.select()
        mBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    private fun createTabIcons(tabLayout: TabLayout) {
        for (index in titles.indices) {
            customView = LayoutInflater.from(requireActivity())
                .inflate(R.layout.notification_custom_tab, null) as ConstraintLayout
            customView?.findViewById<TextView>(R.id.tabName_Tv)?.text = titles[index]
            tabLayout.getTabAt(index)?.customView = customView
        }
    }


    private val onTabSelectedListener: TabLayout.OnTabSelectedListener = object :
        TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            selectedTabView(tab)

        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            unSelectedTabView(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    fun selectedTabView(tab: TabLayout.Tab) {
        val selected = tab.customView
        val iv_text = selected?.findViewById<View>(R.id.tabName_Tv) as TextView
        iv_text.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
    }

    fun unSelectedTabView(tab: TabLayout.Tab) {
        val selected = tab.customView
        val iv_text = selected?.findViewById<View>(R.id.tabName_Tv) as TextView
        iv_text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        arguments?.getString("category")
            ?.let { mainActivity.supportActionBar?.title = it }
        mainActivity.showUpButton(false)
    }

    override fun onPause() {
        super.onPause()
        mainActivity.supportActionBar?.title = ""
        mainActivity.hideUpButton()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_movies_in_category
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel

    }

    override fun getViewModel(): CategoryViewModel {
        return mViewModel
    }

    override fun showErrorResponse(result: NetworkResponse<MoviesResponse>?) {

    }

    override fun showProgress() {

    }
}