package pl.lbiio.easyflashcards.drawer_items

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.UserAward
import pl.lbiio.easyflashcards.databinding.FragmentAwardsBinding
import pl.lbiio.easyflashcards.model.UsersViewModel
import pl.lbiio.easyflashcards.recycle_adapters.UserAwardListAdapter

@AndroidEntryPoint
class AwardsFragment : Fragment() {
    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var binding: FragmentAwardsBinding
    private lateinit var recyclerViewAwards: RecyclerView
    private lateinit var awardsAdapter: UserAwardListAdapter
    private var listOfAwards: MutableList<UserAward> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAwardsBinding.inflate(inflater, container, false)
        recyclerViewAwards = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                listOfAwards = usersViewModel.getAllAwards().toMutableList()
            }
            awardsAdapter = UserAwardListAdapter(listOfAwards)
            Log.d("ilosc", listOfAwards.size.toString())
            recyclerViewAwards.adapter = awardsAdapter
            recyclerViewAwards.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewAwards.setHasFixedSize(true)
        }
    }
}