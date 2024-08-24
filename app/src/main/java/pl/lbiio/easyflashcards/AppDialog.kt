package pl.lbiio.easyflashcards

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import javax.inject.Inject

interface OnPositiveButtonClickListener{
    fun onPositiveButtonClick()
}
interface OnNegativeButtonClickListener{
    fun onNegativeButtonClick()
}

class AppDialog
    @Inject constructor(private val ctx: Context,
                        private val title: String,
                        private val message: String,
                        private val allowNegativeButton: Boolean,
                        private val view: View?,
                        private var onPositiveButtonClickListener: OnPositiveButtonClickListener ?= null,
                        private var onNegativeButtonClickListener: OnNegativeButtonClickListener ?= null
     ) :
    AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(ctx)
        val dialog = if (allowNegativeButton) {
            builder.setTitle(title)
                .setView(view)
                .setMessage(message)
                .setPositiveButton(
                    "OK"
                ) { _: DialogInterface?, _: Int ->
                    onPositiveButtonClickListener?.onPositiveButtonClick()
                }
                .setNegativeButton(
                    "CANCEL"
                ) { _: DialogInterface?, _: Int ->
                    onNegativeButtonClickListener?.onNegativeButtonClick()
                }
                .create()
        } else {
            builder.setTitle(title)
                .setView(view)
                .setMessage(message)
                .setPositiveButton(
                    "OK"
                ) { _: DialogInterface?, _: Int ->
                    onPositiveButtonClickListener?.onPositiveButtonClick()
                }
                .create()
        }
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_back_ground)
        return dialog
    }

    fun setOnNegativeButtonClickListener(listener: OnNegativeButtonClickListener) {
        onNegativeButtonClickListener = listener
    }

    fun setOnPositiveButtonClickListener(listener: OnPositiveButtonClickListener) {
        onPositiveButtonClickListener = listener
    }
}