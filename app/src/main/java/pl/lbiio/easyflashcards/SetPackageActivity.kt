package pl.lbiio.easyflashcards

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.data.PackageToUpdate
import pl.lbiio.easyflashcards.databinding.ActivitySetPackageBinding
import pl.lbiio.easyflashcards.model.PackagesViewModel
import java.io.File


@AndroidEntryPoint
class SetPackageActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivitySetPackageBinding
    private lateinit var getContent: ActivityResultLauncher<String>
    private var packageId = ""
    private var status = -1
    private var packageToModify = PackageToUpdate("", "", "", "", "")
    @Volatile
    private var path=""
    private val packagesViewModel: PackagesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.setPackageToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = intent.getStringExtra("package_id") ?: ""
        status = intent.getIntExtra("status", -1)
        val languages = resources.getStringArray(R.array.languages)
        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            languages
        )
        (binding.nativeLanguage as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.foreignLanguage as? AutoCompleteTextView)?.setAdapter(adapter)

    }
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            if(packageId != ""){
                packageToModify = packagesViewModel.getPackageToModify(packageId)
                path = packageToModify.artwork
                Log.d("zdjecie", path)
                binding.packageName.setText(packageToModify.name)
                binding.nativeLanguage.setText(packageToModify.nativeLanguage)
                binding.foreignLanguage.setText(packageToModify.foreignLanguage)
                binding.artwork.setImageBitmap(
                    BitmapFactory.decodeFile(
                        File(packageToModify.artwork).absolutePath,
                        BitmapFactory.Options()
                    )
                )
            }

            binding.applyPackage.setOnClickListener {
                val packageName = binding.packageName.text.toString()
                val nativeLanguage = binding.nativeLanguage.text.toString()
                val foreignLanguage = binding.foreignLanguage.text.toString()
                if (path.isNotEmpty() && packageName.isNotEmpty() && nativeLanguage.isNotEmpty() && foreignLanguage.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                    if (packageId == "") {
                            packagesViewModel.addPackage(
                                1,
                                PackageToUpdateDTO(
                                    packageID = "${packagesViewModel.getCurrentUserId()}${System.currentTimeMillis()}",
                                    name = packageName,
                                    frontLanguage = nativeLanguage,
                                    backLanguage = foreignLanguage,
                                    path = path
                                )
                            )
                        finish()
                    } else {
                        packagesViewModel.updatePackage(
                            status,
                            PackageToUpdateDTO(
                                packageID = packageToModify.packageID,
                                name = packageName,
                                frontLanguage = nativeLanguage,
                                backLanguage = foreignLanguage,
                                path = path
                            )
                        )
                        finish()
                    }
                }
                } else {
                    Toast.makeText(this@SetPackageActivity, R.string.fill_form_notification, Toast.LENGTH_LONG).show()
                }
            }


            binding.addImage.setOnClickListener {
                //if (setupPermissions()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val photoPickerIntent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                            type = "image/*"
                        }
                        takeImageWithPhotoPicker.launch(photoPickerIntent)
                    }
                    else{
                        getContent.launch("image/*")
                    }
                //}
            }

            binding.dismissPackage.setOnClickListener {
                finish()
            }

        }


    getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            path = getFilePath(this, uri).toString()
            binding.artwork.setImageURI(uri)
            Log.d("path", path)
        }
    }
    }

    private val takeImageWithPhotoPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let { uri ->
            val uriConverted = convertContentUri(uri.toString())
            path = getFilePath(this, Uri.parse(uriConverted)).toString()

            binding.artwork.setImageBitmap(
                BitmapFactory.decodeFile(
                    File(path).absolutePath,
                    BitmapFactory.Options()
                )
            )
            Log.d("path", path)
        }
    }

    private fun convertContentUri(uriString: String): String? {
        val oldPrefix = "content://media/picker/0/com.android.providers.media.photopicker/media"
        val newPrefix = "content://media/external/images/media"

        if (uriString.startsWith(oldPrefix)) {
            return uriString.replaceFirst(oldPrefix, newPrefix)
        }

        return null
    }

    private fun getFilePath(context: Context, uri: Uri): String? {
        val isMediaDocument = uri.authority == "com.android.providers.media.documents"
        if (isMediaDocument) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                "video" -> {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                "audio" -> {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

}