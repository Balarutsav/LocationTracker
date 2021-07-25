package com.appgiants.locationtracker.Activity

import android.os.Bundle
import com.appgiants.locationtracker.databinding.ActivityPermissionBinding
import com.cluttrfly.driver.ui.base.BaseActivity

class PermissionActivity : BaseActivity() {
    lateinit var binding:ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}