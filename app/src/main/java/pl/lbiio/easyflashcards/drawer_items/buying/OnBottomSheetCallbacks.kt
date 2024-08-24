package pl.lbiio.easyflashcards.drawer_items.buying

import android.view.View

interface OnBottomSheetCallbacks {
    fun onStateChanged(bottomSheet: View, newState: Int)
}