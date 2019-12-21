package com.doctor.blue.superstorm.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(getLayoutId(), container, false)
        init(savedInstanceState)
        return view
    }

    abstract fun getLayoutId(): Int

    abstract fun init(savedInstanceState: Bundle?)
}