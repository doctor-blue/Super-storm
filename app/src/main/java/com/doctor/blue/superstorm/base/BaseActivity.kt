package com.doctor.blue.superstorm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        init(savedInstanceState)
    }
    abstract fun getLayoutId():Int

    abstract fun init(savedInstanceState: Bundle?)

}