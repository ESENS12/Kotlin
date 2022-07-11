package kr.esens.mvi_architecture_example



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kr.esens.mvi_architecture_example.adapter.MainAdapter
import kr.esens.mvi_architecture_example.api.ApiHelperImpl
import kr.esens.mvi_architecture_example.api.RetrofitBuilder
import kr.esens.mvi_architecture_example.intent.MainIntent
import kr.esens.mvi_architecture_example.model.User
import kr.esens.mvi_architecture_example.util.ViewModelFactory
import kr.esens.mvi_architecture_example.viewmodel.MainViewModel
import kr.esens.mvi_architecture_example.viewstate.MainState

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private var adapter = MainAdapter(arrayListOf())

    private val buttonFetchUser : Button by lazy{
        findViewById(R.id.buttonFetchUser)
    }

    private val progressBar : ProgressBar by lazy{
        findViewById(R.id.progressBar)
    }

    private val recyclerView : RecyclerView by lazy{
        findViewById(R.id.recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        setupViewModel()
        observeViewModel()
        setupClicks()
    }

    private fun setupClicks() {
        buttonFetchUser.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.userIntent.send(MainIntent.FetchUser)
            }
        }
    }


    private fun setupUI() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.run {
            addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        recyclerView.adapter = adapter
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this,ViewModelFactory(
            ApiHelperImpl(
                RetrofitBuilder.apiService
            )
        ))[MainViewModel::class.java]

//        mainViewModel = ViewModelProvider.of(
//            this,
//            ViewModelFactory(
//                ApiHelperImpl(
//                    RetrofitBuilder.apiService
//                )
//            )
//        ).get(MainViewModel::class.java)

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            mainViewModel.state.collect {
                when (it) {
                    is MainState.Idle -> {

                    }
                    is MainState.Loading -> {
                        buttonFetchUser.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }

                    is MainState.Users -> {
                        progressBar.visibility = View.GONE
                        buttonFetchUser.visibility = View.GONE
                        renderList(it.user)
                    }
                    is MainState.Error -> {
                        progressBar.visibility = View.GONE
                        buttonFetchUser.visibility = View.VISIBLE
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun renderList(users: List<User>) {
        recyclerView.visibility = View.VISIBLE
        users.let { listOfUsers -> listOfUsers.let { adapter.addData(it) } }
        adapter.notifyDataSetChanged()
    }

}