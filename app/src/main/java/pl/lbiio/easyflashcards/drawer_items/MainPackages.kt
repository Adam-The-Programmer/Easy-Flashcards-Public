package pl.lbiio.easyflashcards.drawer_items

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.*
import pl.lbiio.easyflashcards.data.PackageToDisplayInList
import pl.lbiio.easyflashcards.databinding.FragmentPackagesBinding
import pl.lbiio.easyflashcards.databinding.RatingDialogBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.recycle_adapters.OnOptionsClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnPackageClickListener
import pl.lbiio.easyflashcards.recycle_adapters.PackageListAdapter


@AndroidEntryPoint
class MainPackages : Fragment() {
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private lateinit var binding: FragmentPackagesBinding
    private lateinit var recyclerViewPackages: RecyclerView
    private lateinit var packageAdapter: PackageListAdapter
    private var listOfPackages: MutableList<PackageToDisplayInList> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_packages, container, false)
        recyclerViewPackages = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        packageAdapter = PackageListAdapter(listOfPackages)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
            Log.d("fragment start", "tak")
            checkAndLoadBackup()
            binding.addPackageFAB.setOnClickListener {
                val intent = Intent(requireContext(), SetPackageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                requireContext().startActivity(intent)
            }

    }

    private fun loadBackup(){
        binding.addPackageFAB.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = VISIBLE
            }
            val pakiety = packagesViewModel.getBackup()
            Log.d("wszystkie pakiety", pakiety.toString())
            Log.d("UID", packagesViewModel.getCurrentUserId())
            pakiety.forEach { pack ->
                Log.d("pakiet", pack.toString())
                packagesViewModel.addPackageFromBackupToRoom(pack)
                flashcardsViewModel.getBackupFlashcards(pack.packageID)
                    ?.forEach { card ->
                        flashcardsViewModel.addBackupFlashcard(card, pack.packageID)
                    }
            }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = GONE
                binding.addPackageFAB.isEnabled = true
            }
        }
    }

    private fun updateBoughtElements(){
        binding.addPackageFAB.isEnabled = false
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = VISIBLE
            }
            packagesViewModel.updateChangedPackages()
            packagesViewModel.getBoughtPackagesIds().forEach {
                Log.d("id kupionego pakietu", it)
                flashcardsViewModel.updateChangedCards(it)
            }
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = GONE
                binding.addPackageFAB.isEnabled = true
            }
        }
    }

    private fun checkAndLoadBackup(){
        lifecycleScope.launch(Dispatchers.IO) {
            packagesViewModel.isInternetAvailable({
                Log.d("checkAndLoadBackup", "wszedl")
                val isDatabaseEmpty = packagesViewModel.isDatabaseEmpty()
                Log.d("isDatabaseEmpty", isDatabaseEmpty.toString())
                val doesUserHaveBackup = packagesViewModel.doesUserHaveBackup()
                Log.d("doesUserHaveBackup", doesUserHaveBackup.toString())
                Log.d("UID", packagesViewModel.getCurrentUserId())
                withContext(Dispatchers.Main) {
                    if (isDatabaseEmpty && doesUserHaveBackup) {
                          Log.d("backup", "wszedl")
                          loadBackup()
                    }
                }
                withContext(Dispatchers.Main){
                    updateBoughtElements()
                }
                withContext(Dispatchers.Main){
                    observeViewStateUpdates()
                }
            }, {
                val connectionLostDialog = AppDialog(
                    requireContext(),
                    "There is no internet connection",
                    "CLick OK to Reload",
                    true,
                    null
                )
                connectionLostDialog.setOnPositiveButtonClickListener(
                    object : OnPositiveButtonClickListener {
                        override fun onPositiveButtonClick() {
                            checkAndLoadBackup()
                            connectionLostDialog.dismiss()
                        }
                    }
                )
                connectionLostDialog.setOnNegativeButtonClickListener(
                    object : OnNegativeButtonClickListener {
                        override fun onNegativeButtonClick() {
                            requireActivity().finish()
                            connectionLostDialog.dismiss()
                        }
                    }
                )
                view?.post {
                    connectionLostDialog.show(
                        childFragmentManager,
                        "connection lost dialog"
                    )
                }
            })
        }
    }


    //https://www.kodeco.com/22030171-reactive-streams-on-kotlin-sharedflow-and-stateflow#toc-anchor-010
    private fun observeViewStateUpdates() {
        listOfPackages.clear()
        packagesViewModel.loadAllPackages()
        viewLifecycleOwner.lifecycleScope.launch {
            packagesViewModel.allPackagesList.collect {
                listOfPackages = it.toMutableList()
                //listOfPackages.addAll(listOfPackages)
                populateList()
            }
        }
    }

    private fun populateList() {
        packageAdapter = PackageListAdapter(listOfPackages)
        Log.d("ilosc", listOfPackages.size.toString())
        recyclerViewPackages.adapter = packageAdapter
        recyclerViewPackages.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewPackages.setHasFixedSize(true)
        binding.amountInfo.text = getString(R.string.amount_of_packages, listOfPackages.size.toString())
        packageAdapter.updateList(listOfPackages)
        packageAdapter.setOnOptionsClickListener(
            object : OnOptionsClickListener {
                override fun onOptionsClick(
                    db_position: String,
                    list_position: Int,
                    status: Int,
                    view: View
                ) {
                    val popupMenu = PopupMenu(requireContext(), view)
                    if (status == 1) {
                        popupMenu.menuInflater.inflate(R.menu.own_package_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.remove_package -> {
                                    val removePackageDialog = AppDialog(
                                        context!!,
                                        getString(R.string.removing_package_dialog_title),
                                        getString(R.string.removing_package_dialog_content),
                                        true,
                                        null
                                    )
                                    removePackageDialog.setOnPositiveButtonClickListener(
                                        object : OnPositiveButtonClickListener {
                                            override fun onPositiveButtonClick() {
                                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                                    packagesViewModel.deletePackage(
                                                        db_position,
                                                        status
                                                    )
                                                    withContext(Dispatchers.Main) {
                                                        //listOfPackages.removeAt(list_position)
                                                        //packageAdapter.notifyItemRemoved(
                                                         //   list_position
                                                        //)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    removePackageDialog.setOnNegativeButtonClickListener(
                                        object : OnNegativeButtonClickListener {
                                            override fun onNegativeButtonClick() {
                                                removePackageDialog.dismiss()
                                            }

                                        }
                                    )
                                    removePackageDialog.show(
                                        childFragmentManager,
                                        getString(R.string.removing_package_dialog_tag)
                                    )
                                }

                                R.id.edit_package -> {
                                    val intent =
                                        Intent(requireContext(), SetPackageActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("package_id", db_position)
                                    requireContext().startActivity(intent)
                                }

                                R.id.share_package -> {
                                    val intent =
                                        Intent(requireContext(), SharePackageActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("package_id", db_position)
                                    intent.putExtra("status", status)
                                    requireContext().startActivity(intent)
                                }
                            }

                            true
                        }
                    }
                    else if (status == 2) {
                        popupMenu.menuInflater.inflate(R.menu.shared_package_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {

                                R.id.edit_package -> {
                                    val intent =
                                        Intent(requireContext(), SetPackageActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("package_id", db_position)
                                    requireContext().startActivity(intent)
                                }

                                R.id.update_sharing_package -> {
                                    val intent =
                                        Intent(requireContext(), SharePackageActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("package_id", db_position)
                                    intent.putExtra("status", status)
                                    requireContext().startActivity(intent)
                                }
                            }

                            true
                        }
                    }
                    else if(status == 3){
                        popupMenu.menuInflater.inflate(R.menu.bought_package_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.rate_package -> {
                                    lifecycleScope.launch {
                                        val currentRate =
                                            packagesViewModel.getPersonalRate(db_position)
                                        val builder = AlertDialog.Builder(requireContext())
                                        val inflater = layoutInflater
                                        builder.setTitle("Rate this package")
                                        val dialogBinding = RatingDialogBinding.inflate(inflater)
                                        val dialogLayout = dialogBinding.root
                                        dialogBinding.ratingBar.rating = currentRate.toFloat()
                                        val ratePackageDialog = AppDialog(
                                            context!!,
                                            getString(R.string.rating_package_dialog_title),
                                            getString(R.string.rating_package_dialog_content),
                                            true,
                                            dialogLayout
                                        )
                                        ratePackageDialog.setOnPositiveButtonClickListener(
                                            object : OnPositiveButtonClickListener {
                                                override fun onPositiveButtonClick() {
                                                    if (dialogBinding.ratingBar.rating.toInt() > 0) {
                                                        lifecycleScope.launch(Dispatchers.IO) {
                                                            packagesViewModel.ratePackage(
                                                                db_position,
                                                                dialogBinding.ratingBar.rating.toInt()
                                                            )
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            getString(R.string.rating_package_dialog_warn),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        )
                                        ratePackageDialog.setOnNegativeButtonClickListener(
                                            object : OnNegativeButtonClickListener {
                                                override fun onNegativeButtonClick() {
                                                    ratePackageDialog.dismiss()
                                                }
                                            }
                                        )
                                        ratePackageDialog.show(
                                            childFragmentManager,
                                            getString(R.string.rating_package_dialog_tag)
                                        )
                                    }
                                }
                            }
                            true
                        }
                    }

                    popupMenu.show()
                }
            }
        )

        packageAdapter.setOnPackageClickListener(
            object : OnPackageClickListener {
                override fun onPackageClick(
                    db_position: String,
                    nativeLanguage: String,
                    foreignLanguage: String,
                    status: Int
                ) {
                    Log.d("status pakietu", status.toString())
                    val intent = Intent(requireContext(), FlashCardsListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("package_id", db_position)
                    intent.putExtra("native_language", nativeLanguage)
                    intent.putExtra("foreign_language", foreignLanguage)
                    intent.putExtra("status", status)
                    requireContext().startActivity(intent)
                }
            }
        )
    }

}