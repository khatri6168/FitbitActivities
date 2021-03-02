package com.fitbit.fitbitapp.activities

import android.app.ActionBar
import android.app.Activity
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.fitbit.authentication.AuthenticationManager
import com.fitbit.fitbitapp.R
import com.fitbit.fitbitapp.activities.MainActivity
import com.fitbit.fitbitapp.adapters.UserDataPagerAdapter
import com.fitbit.fitbitapp.databinding.ActivityUserDataBinding

/**
 * MainActivity - It is a base activity to render User's data information
 */
class MainActivity : Activity() {
    private var binding: ActivityUserDataBinding? = null
    private var userDataPagerAdapter: UserDataPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_data)
        binding!!.setLoading(false)
        userDataPagerAdapter = UserDataPagerAdapter(this@MainActivity.fragmentManager)
        binding!!.viewPager.adapter = userDataPagerAdapter
        binding!!.viewPager.offscreenPageLimit = 0
        binding!!.viewPager.addOnPageChangeListener(
                object : SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) { // When swiping between pages, select the
// corresponding tab.
                        actionBar!!.setSelectedNavigationItem(position)
                    }
                })
        addTabs()
    }

    /**
     * Add tabs here. Initially it was created to show user's profile and Users Activities. Currently it only Shows User's Activities
     */
    private fun addTabs() {
        val actionBar = actionBar
        // Specify that tabs should be displayed in the action bar.
        actionBar!!.navigationMode = ActionBar.NAVIGATION_MODE_TABS
        val numberOfTabs = userDataPagerAdapter!!.count
        for (i in 0 until numberOfTabs) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(getString(userDataPagerAdapter!!.getTitleResourceId(i)))
                            .setTabListener(object : ActionBar.TabListener {
                                override fun onTabSelected(tab: ActionBar.Tab, ft: FragmentTransaction) {
                                    binding!!.viewPager.currentItem = i
                                }

                                override fun onTabUnselected(tab: ActionBar.Tab, ft: FragmentTransaction) {}
                                override fun onTabReselected(tab: ActionBar.Tab, ft: FragmentTransaction) {}
                            }))
        }
    }

    /**
     * To logout with a Fitbit account. It will redirect to Login Actiity.
     */
    fun onLogoutClick(view: View?) {
        binding!!.loading = true
        AuthenticationManager.logout(this)
    }

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}