package com.example.movieapp.uimodules

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.movieapp.BR
import com.example.movieapp.R
import com.example.movieapp.base.BaseActivity
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainActivityViewModel>(),
    MainActivityNavigator {

    private val mViewModel: MainActivityViewModel by viewModels()
    private var mBinding: ActivityMainBinding? = null
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = getViewDataBinding()
        mViewModel.setNavigator(this)
        supportActionBar?.elevation = 0F
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): MainActivityViewModel {
        return mViewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    fun showUpButton(isFromAddScreen : Boolean) {
        if (isFromAddScreen){
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_icon_close);
        }else{
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_icon_back)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    fun hideUpButton() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.label != "fragment_dashboard") {
            navController.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}