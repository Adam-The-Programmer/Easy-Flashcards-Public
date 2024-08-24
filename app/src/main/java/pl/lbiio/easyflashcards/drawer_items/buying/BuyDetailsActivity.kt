package pl.lbiio.easyflashcards.drawer_items.buying

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.AppDialog
import pl.lbiio.easyflashcards.starter_activities.MainActivity
import pl.lbiio.easyflashcards.OnNegativeButtonClickListener
import pl.lbiio.easyflashcards.OnPositiveButtonClickListener
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.databinding.ActivityBuyDetailsBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.model.UsersViewModel

@AndroidEntryPoint
class BuyDetailsActivity : AppCompatActivity() {
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityBuyDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.activityBuyDetailsToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        val packageID = intent.getStringExtra("package_id").toString()

        lifecycleScope.launch(Dispatchers.Main) {
            val product = packagesViewModel.getProductDetails(packageID)
            product?.let {
                Picasso.get()
                    .load(it.path)
                    .into(binding.artwork)

                binding.name.text = it.name
                binding.amount.text = getString(R.string.amount_of_cards_to_buy, it.amount.toString())

                binding.downloads.text = it.downloads.toString()
                if(it.price==0){
                    binding.buy.visibility = GONE
                    binding.download.visibility = VISIBLE
                    binding.price.text = getString(R.string.free)
                }
                else{
                    binding.buy.visibility = VISIBLE
                    binding.download.visibility = GONE
                    binding.price.text = getString(R.string.full_price_to_pay, it.price.toString(), it.currency)
                }
                binding.rate.text = String.format("%.2f", ((it.acquiredPoints).toFloat()/(it.maxPoints.toFloat()/5)))

                binding.description.text = it.description
            }
        }


        binding.download.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = VISIBLE
                packagesViewModel.isInternetAvailable({
                    val pack = packagesViewModel.getBoughtPackage(packageID)
                    pack?.let {p->
                        packagesViewModel.addPackage(
                            3,
                            PackageToUpdateDTO(
                                p.packageID,
                                p.name,
                                p.frontLanguage,
                                p.backLanguage,
                                p.path
                            )
                        )

                        flashcardsViewModel.getBoughtFlashcards(packageID)?.forEach {card->
                            Log.d("fiszka", card.toString())
                            flashcardsViewModel.addFlashcard(
                                        3,
                                FlashcardToUpdateDTO(
                                    p.packageID,
                                    card.cardID,
                                    card.word,
                                    card.translations,
                                    card.explanations,
                                    card.phrases
                                )
                            )
                        }
                        usersViewModel.updateUserPoints(pack.packageID, 0, 1)
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = GONE
                        }
                        val intent = Intent(this@BuyDetailsActivity, MainActivity::class.java)
                        this@BuyDetailsActivity.startActivity(intent)
                    }

                    //WHERE `UIDForeign`="hCYCOjtB9EeFKh3gegqDkxKTC7w1"

                },{
                    val internetLossDialog = AppDialog(
                        this@BuyDetailsActivity,
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
                        supportFragmentManager,
                        getString(R.string.no_internet_connection_dialog_tag)
                    )
                })

            }
        }
    }

}