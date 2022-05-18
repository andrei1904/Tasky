package com.example.tasky.navigation

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.tasky.R

class FragmentNavigation(activity: Activity) {

    private val _activity: Activity = activity
    private val _container = R.id.fragment_container_view

    fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        animEnter: Int = 0,
        animExit: Int = 0,
        animPopEnter: Int = 0,
        animPopExit: Int = 0
    ) {
        val transaction = (_activity as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit)
        transaction.replace(_container, fragment)
        when (addToBackStack) {
            true -> transaction.addToBackStack(fragment.tag)
            false -> return
        }
        transaction.commit()
    }

    fun addFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = (_activity as FragmentActivity).supportFragmentManager.beginTransaction()
        transaction.add(_container, fragment)
        when (addToBackStack) {
            true -> transaction.addToBackStack(fragment.tag)
            false -> return
        }
        transaction.commit()
    }

    fun popBackStack() {
        (_activity as FragmentActivity).supportFragmentManager.popBackStack()
    }

}
