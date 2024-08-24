package pl.lbiio.easyflashcards.drawer_items

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.data.LeaderBoardItem
import pl.lbiio.easyflashcards.databinding.FragmentLeaderBoardBinding
import pl.lbiio.easyflashcards.model.UsersViewModel
import pl.lbiio.easyflashcards.recycle_adapters.LeaderBoardListAdapter

@AndroidEntryPoint
class LeaderBoardFragment : Fragment() {
    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var binding: FragmentLeaderBoardBinding
    private lateinit var recyclerViewLeaderBoardItems: RecyclerView
    private lateinit var leaderBoardAdapter: LeaderBoardListAdapter
    private var listOfPlayers: MutableList<LeaderBoardItem> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderBoardBinding.inflate(inflater, container, false)
        recyclerViewLeaderBoardItems = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                listOfPlayers = usersViewModel.getGeneralLeaderBoard().toMutableList()
            }
            leaderBoardAdapter = LeaderBoardListAdapter(listOfPlayers)
            Log.d("ilosc", listOfPlayers.size.toString())
            recyclerViewLeaderBoardItems.adapter = leaderBoardAdapter
            recyclerViewLeaderBoardItems.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewLeaderBoardItems.setHasFixedSize(true)
        }
    }
}