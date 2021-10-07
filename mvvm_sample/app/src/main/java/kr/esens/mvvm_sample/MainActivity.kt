package kr.esens.mvvm_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import kr.esens.mvvm_sample.model.MainViewModel;
import androidx.databinding.DataBindingUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.esens.mvvm_sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val model: MainViewModel by viewModels();
    private lateinit var binding : ActivityMainBinding;
    private lateinit var btn_increase : FloatingActionButton;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.lifecycleOwner = this;
        binding.viewModel = model;
        btn_increase = findViewById(R.id.fab_main);
    }
}