package com.example.myfit_exercisecompanion.other

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.createSnackBar(
    message: String,
    length: Int = Snackbar.LENGTH_SHORT,
    okayAction: Boolean = false
): Snackbar {
    val snackBar = Snackbar.make(this, message, length)
    if (okayAction) snackBar.apply {
        setAction("Okay") { dismiss() }
    }
    return snackBar
}