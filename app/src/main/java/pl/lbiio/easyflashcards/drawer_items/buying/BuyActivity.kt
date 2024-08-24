package pl.lbiio.easyflashcards.drawer_items.buying


import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityBuyBinding
import pl.lbiio.easyflashcards.model.SharedPreferencesViewModel
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class BuyActivity : AppCompatActivity() {
    private var listener: OnBottomSheetCallbacks? = null
    private val sharedPreferencesViewModel: SharedPreferencesViewModel by viewModels()
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityBuyBinding
    private lateinit var disposable: Disposable
    private var userInputSubject: BehaviorSubject<String?>? = BehaviorSubject.create()


    fun setOnBottomSheetCallbacks(onBottomSheetCallbacks: OnBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks
    }

    fun closeBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun openBottomSheet() {
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    private fun configureBackdrop() {
        val fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment)
        fragment?.view?.let {
            BottomSheetBehavior.from(it).let { bs ->
                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        listener?.onStateChanged(bottomSheet, newState)
                    }
                })
                bs.state = BottomSheetBehavior.STATE_COLLAPSED
                mBottomSheetBehavior = bs
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.buyToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        configureBackdrop()

        val languages = resources.getStringArray(R.array.languages)
        val currencies = resources.getStringArray(R.array.currencies)
        val languagesAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            languages
        )
        val currenciesAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            currencies
        )
        (binding.nativeLanguage as? AutoCompleteTextView)?.setAdapter(languagesAdapter)
        (binding.foreignLanguage as? AutoCompleteTextView)?.setAdapter(languagesAdapter)
        (binding.preferredCurrency as? AutoCompleteTextView)?.setAdapter(currenciesAdapter)

        binding.nativeLanguage.setText(sharedPreferencesViewModel.getNativeLanguage())
        binding.foreignLanguage.setText(sharedPreferencesViewModel.getForeignLanguage())
        binding.maximumPrice.setText(sharedPreferencesViewModel.getMaximumPrice().toString())
        binding.preferredCurrency.setText(sharedPreferencesViewModel.getCurrency())

        binding.applySearchingSettings.setOnClickListener {
            if (binding.nativeLanguage.text.toString() != "" && binding.foreignLanguage.text.isNotEmpty() && binding.maximumPrice.text!!.isNotEmpty() && binding.maximumPrice.text.toString()
                    .toInt() != 0 && binding.preferredCurrency.text.isNotEmpty()
            ) {
                sharedPreferencesViewModel.setNativeLanguage(binding.nativeLanguage.text.toString())
                sharedPreferencesViewModel.setForeignLanguage(binding.foreignLanguage.text.toString())
                sharedPreferencesViewModel.setMaximumPrice(
                    binding.maximumPrice.text.toString().toInt()
                )
                sharedPreferencesViewModel.setCurrency(binding.preferredCurrency.text.toString())
                openBottomSheet()
            } else {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_product_menu, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                lifecycleScope.launch(Dispatchers.Main) {
                    SharedPreferencesViewModel.mutablePhrase.value = query
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                userInputSubject!!.onNext(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        disposable = userInputSubject!!
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe {
                lifecycleScope.launch(Dispatchers.Main) {
                    SharedPreferencesViewModel.mutablePhrase.value = it
                }
            }

    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
