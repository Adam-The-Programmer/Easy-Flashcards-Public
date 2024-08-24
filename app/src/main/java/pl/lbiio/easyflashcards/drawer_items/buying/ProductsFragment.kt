package pl.lbiio.easyflashcards.drawer_items.buying

import android.content.Intent
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.AppDialog
import pl.lbiio.easyflashcards.OnNegativeButtonClickListener
import pl.lbiio.easyflashcards.OnPositiveButtonClickListener
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.SharedPackageToBuy
import pl.lbiio.easyflashcards.databinding.FragmentProductsBinding
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.model.SharedPreferencesViewModel
import pl.lbiio.easyflashcards.recycle_adapters.ItemToBuyListAdapter
import pl.lbiio.easyflashcards.recycle_adapters.OnBuyClickListener

@AndroidEntryPoint
class ProductsFragment : Fragment(), OnBottomSheetCallbacks {
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val sharedPreferencesViewModel: SharedPreferencesViewModel by viewModels()
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var productsAdapter: ItemToBuyListAdapter
    private var listOfPackagesToBuys: List<SharedPackageToBuy> = emptyList()
    private lateinit var binding: FragmentProductsBinding
    private var currentState: Int = BottomSheetBehavior.STATE_COLLAPSED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        recyclerViewProducts = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        productsAdapter = ItemToBuyListAdapter(listOfPackagesToBuys)
        (activity as BuyActivity).setOnBottomSheetCallbacks(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            filterImage.setOnClickListener {
                if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                    (activity as BuyActivity).closeBottomSheet()
                } else {
                    (activity as BuyActivity).openBottomSheet()
                }
            }
        }
        SharedPreferencesViewModel.mutablePhrase.observe(viewLifecycleOwner){
            Log.d("fraza", it)
            search(it)
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        currentState = newState
        binding.apply {
            when (newState) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    Log.d("stan", "collapsed")
                    filterImage.setImageResource(R.drawable.ic_collapse)
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    Log.d("stan", "expanded")
                    filterImage.setImageResource(R.drawable.ic_expand)
                }
            }
        }
    }

    private fun search(value: String){
        lifecycleScope.launch(Dispatchers.IO) {
            packagesViewModel.isInternetAvailable({
                Log.d("isInternetAvailable", "tak")
                listOfPackagesToBuys = packagesViewModel.searchForPackages(
                    sharedPreferencesViewModel.getForeignLanguage(),
                    sharedPreferencesViewModel.getNativeLanguage(),
                    sharedPreferencesViewModel.getCurrency(),
                    sharedPreferencesViewModel.getMaximumPrice(),
                    value
                )?: emptyList()
                Log.d("listOfPackagesToBuys", listOfPackagesToBuys.toString())
                withContext(Dispatchers.Main){
                    productsAdapter = ItemToBuyListAdapter(listOfPackagesToBuys)
                    recyclerViewProducts.adapter = productsAdapter
                    recyclerViewProducts.layoutManager =
                        LinearLayoutManager(requireContext())
                    recyclerViewProducts.setHasFixedSize(true)
                    productsAdapter.updateList(listOfPackagesToBuys)
                    productsAdapter.setOnBuyClickListener(
                        object : OnBuyClickListener {
                            override fun onBuyClick(
                                packageID: String,
                                path: String,
                                name: String,
                                amount: Int,
                                downloads: Int,
                                maxPoints: Int,
                                acquiredPoints: Int,
                                price: Int,
                                currency: String
                            ) {
                                val intent = Intent(
                                    requireContext(),
                                    BuyDetailsActivity::class.java
                                )
                                intent.putExtra("package_id", packageID)
                                startActivity(intent)
                            }

                        }
                    )
                }
            }, {
                val internetLossDialog = AppDialog(
                    requireContext(),
                    getString(R.string.no_internet_connection_dialog_title),
                    getString(R.string.no_internet_connection_dialog_content),
                    true,
                    null
                )
                internetLossDialog.setOnPositiveButtonClickListener(
                    object : OnPositiveButtonClickListener {
                        override fun onPositiveButtonClick() {
                            internetLossDialog.dismiss()
                        }
                    }
                )
                internetLossDialog.setOnNegativeButtonClickListener(
                    object : OnNegativeButtonClickListener {
                        override fun onNegativeButtonClick() {
                            internetLossDialog.dismiss()
                        }

                    }
                )
                internetLossDialog.show(
                    childFragmentManager,
                    getString(R.string.no_internet_connection_dialog_tag)
                )
            })

        }

    }
}