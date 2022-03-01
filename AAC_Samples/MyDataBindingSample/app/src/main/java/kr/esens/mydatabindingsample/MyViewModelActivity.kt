package kr.esens.mydatabindingsample

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kr.esens.mydatabindingsample.data.MyViewModel
import kr.esens.mydatabindingsample.databinding.ViewmodelProfileBinding

class MyViewModelActivity : AppCompatActivity() {
    val TAG = MyViewModelActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        val binding : ViewmodelProfileBinding = DataBindingUtil.setContentView(this,R.layout.viewmodel_profile)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

}