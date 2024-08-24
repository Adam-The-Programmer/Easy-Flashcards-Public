package pl.lbiio.easyflashcards.games.memory

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.data.MemoryCard
import pl.lbiio.easyflashcards.databinding.FragmentMemoryContentBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import pl.lbiio.easyflashcards.recycle_adapters.MemoryCardAdapter

interface OnMemoryFinishedListener{
    fun onMemoryFinished(score: Int)
}

@AndroidEntryPoint
class MemoryContentFragment(private var onMemoryFinishedListener: OnMemoryFinishedListener?=null) : Fragment() {
    private var packageId = ""
    private var time=0
    private var score=0
    private lateinit var binding: FragmentMemoryContentBinding
    val dataList: MutableList<MemoryCard> = ArrayList()
    private val cardsViewModel: FlashcardsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemoryContentBinding.inflate(layoutInflater)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            packageId = requireArguments().getString("package_id")?:""
            Log.d("pakieta id", packageId)
        }

        lifecycleScope.launch(Dispatchers.Main) {

            var elements = emptyList<MemoryCard>()

            withContext(Dispatchers.IO){
                elements = cardsViewModel.getMemoryCards(packageId)
            }

            Log.d("elements", elements.toString())
            withContext(Dispatchers.Main){
                dataList.addAll(elements)
                Log.d("datalist przypisanie", dataList.toString())
            }

            Log.d("datalist", dataList.toString())
            val adapter = MemoryCardAdapter(requireContext(), dataList)
            adapter.setOnMemoryCardClickListener(
                object : MemoryCardAdapter.OnMemoryCardClickListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onMemoryCardClick(cardID: String, content: String) {
                        lifecycleScope.launch {
                            dataList.find { it.cardID == cardID && it.content == content }?.state = 1
                            adapter.notifyDataSetChanged()
                            val state = getStatusOfMatchingElement(dataList, cardID, content)
                            delay(500)
                            when (state) {
                                1 -> {
                                    makeBothUncoveredOriginal(dataList, cardID)
                                    adapter.notifyDataSetChanged()
                                    stopTimer()
                                }

                                0 -> {
                                    delay(1500)
                                    closeBothOriginal(dataList)
                                    adapter.notifyDataSetChanged()
                                }

                                else -> {
                                    Log.d("znaloeziono pare", "brak otwartego drugiego elementu")
                                }
                            }
                        }
                    }

                }
            )
            adapter.setOnGameFinishListener(object: MemoryCardAdapter.OnGameFinishListener{
                override fun onGameFinish() {
                    Log.d("koniec gry", "tak")
                    onMemoryFinishedListener?.onMemoryFinished(score)
                }

            })
            binding.recyclerView.adapter = adapter

        }


        lifecycleScope.launch {
            while(true){
                delay(100)
                time += 100
                formatTime()
            }
        }
    }

    private fun getStatusOfMatchingElement(dataList: MutableList<MemoryCard>, desiredId: String, content: String): Int?{
        if(dataList.count { it.state==1 }==1) return -1
        return dataList.find { it.cardID == desiredId && it.content!=content}?.state
    }

    private fun closeBothOriginal(dataList: MutableList<MemoryCard>) {
        dataList.forEach {
            if (it.state == 1) {
                it.state = 0
            }
        }
    }

    private fun makeBothUncoveredOriginal(dataList: MutableList<MemoryCard>, desiredId: String) {
        dataList.forEachIndexed { index, memoryCard ->
            if (memoryCard.cardID == desiredId) {
                dataList[index] = memoryCard.copy(state = 2)
            }
        }
    }

    fun setOnMemoryFinishedListener(listener: OnMemoryFinishedListener) {
        onMemoryFinishedListener = listener
    }

    private fun stopTimer(){
        score += (100-(time.toFloat()/1000)).toInt()
        binding.score.text = score.toString()
        time = 0
    }

    private fun formatTime(){
        val text = time.toFloat()/1000
        binding.time.text = text.toString()
    }

}