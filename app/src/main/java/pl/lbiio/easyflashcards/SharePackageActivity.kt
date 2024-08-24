package pl.lbiio.easyflashcards


import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.PackageToBuyDTO
import pl.lbiio.easyflashcards.databinding.ActivitySharePackageBinding
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.model.UsersViewModel


@AndroidEntryPoint
class SharePackageActivity : AppCompatActivity() {
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var binding: ActivitySharePackageBinding
    private var packageId = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharePackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.sharePackageToolbar.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = Firebase.auth
        val currencies = resources.getStringArray(R.array.currencies)
        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            currencies
        )
        (binding.currency as? AutoCompleteTextView)?.setAdapter(adapter)
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putString("package_id", packageId)

        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d("id pakietu", packageId)
            val isPackageShared = packagesViewModel.checkIsShared(packageId)
            Log.d("czy udostepione", isPackageShared.toString())
            if (isPackageShared) {
                val sharedPackage = packagesViewModel.getPackageToShare(packageId)
                binding.priceLabel.text = getString(R.string.set_price_shared_package, sharedPackage.name)
                binding.descriptionLabel.text = getString(R.string.set_description_shared_package, sharedPackage.name)
                binding.price.setText(sharedPackage.price.toString())
                binding.currency.setText(sharedPackage.currency)
                binding.description.setText(sharedPackage.description)
            } else {
                val name = packagesViewModel.getPackageName(packageId)
                binding.priceLabel.text = getString(R.string.set_price_shared_package, name)
                binding.descriptionLabel.text = getString(R.string.set_description_shared_package, name)
            }


            binding.publish.setOnClickListener {
                    if (binding.description.text.toString()
                            .isEmpty() || binding.currency.text.toString()
                            .isEmpty() || binding.price.text.toString().isEmpty()
                    ) {
                        Toast.makeText(this@SharePackageActivity, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (!isPackageShared){
                                Log.d("udostępnianie", "tak")
                                packagesViewModel.sharePackage(
                                    PackageToBuyDTO(
                                        packageId,
                                        binding.description.text.toString(),
                                        binding.price.text.toString().toInt(),
                                        binding.currency.text.toString()
                                    )
                                )
                                usersViewModel.updateUserPoints(packageId, 0, 0)
                            }
                            else{
                                Log.d("aktualizowanie", "tak")
                                packagesViewModel.updateSharedPackage(
                                    PackageToBuyDTO(
                                        packageId,
                                        binding.description.text.toString(),
                                        binding.price.text.toString().toInt(),
                                        binding.currency.text.toString()
                                    )
                                )
                            }
                            withContext(Dispatchers.Main){
                                finish()
                            }
                        }
                    }
            }
        }
    }
}