package pl.lbiio.easyflashcards

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.data.LeaderBoardItem
import pl.lbiio.easyflashcards.databinding.ActivityPackageLeaderBoardBinding
import pl.lbiio.easyflashcards.model.UsersViewModel
import pl.lbiio.easyflashcards.recycle_adapters.LeaderBoardListAdapter

@AndroidEntryPoint
class PackageLeaderBoardActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityPackageLeaderBoardBinding
    private var packageId = ""
    private lateinit var recyclerViewLeaderBoardItems: RecyclerView
    private lateinit var leaderBoardAdapter: LeaderBoardListAdapter
    private var listOfPlayers: MutableList<LeaderBoardItem> = mutableListOf()
    private val usersViewModel: UsersViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackageLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.packageLeaderBoardToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = intent.getStringExtra("package_id") ?: ""
        recyclerViewLeaderBoardItems = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                listOfPlayers = usersViewModel.getPackageLeaderBoard(packageId).toMutableList()
            }
            leaderBoardAdapter = LeaderBoardListAdapter(listOfPlayers)
            Log.d("ilosc", listOfPlayers.size.toString())
            recyclerViewLeaderBoardItems.adapter = leaderBoardAdapter
            recyclerViewLeaderBoardItems.layoutManager = LinearLayoutManager(this@PackageLeaderBoardActivity)
            recyclerViewLeaderBoardItems.setHasFixedSize(true)
        }
    }
}