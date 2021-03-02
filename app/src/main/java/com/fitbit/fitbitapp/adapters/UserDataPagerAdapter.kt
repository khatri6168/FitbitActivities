package com.fitbit.fitbitapp.adapters

import android.app.Fragment
import android.app.FragmentManager
import androidx.legacy.app.FragmentPagerAdapter
import com.fitbit.authentication.AuthenticationManager
import com.fitbit.authentication.Scope
import com.fitbit.fitbitapp.fragments.ActivitiesFragment
import com.fitbit.fitbitapp.fragments.InfoFragment
import java.util.*

/**
 * UserDataPagerAdapter - An adapter to show multiple Fragments in a view pager manner. Currently it will only show Activities fragment in it.
 */
class UserDataPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    private val fragments: MutableList<InfoFragment<*>> = ArrayList()
    private fun containsScope(scope: Scope): Boolean {
        return AuthenticationManager.getCurrentAccessToken().scopes.contains(scope)
    }

    /*@Override
    public Fragment getItem(int position) {
        if (position >= fragments.size()) {
            return null;
        }

        return fragments.get(position);
    }*/
/*@Override
    public Fragment getItem(int position) {
        if (position >= fragments.size()) {
            return null;
        }

        return fragments.get(position);
    }*/
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    fun getTitleResourceId(index: Int): Int {
        return fragments[index].titleResourceId
    } /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }*/

    /*@Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }*/
    init {
        fragments.clear()
        if (containsScope(Scope.activity)) {
            fragments.add(ActivitiesFragment())
        }
    }
}