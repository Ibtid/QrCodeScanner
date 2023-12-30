package com.example.qrcodescan

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class YourApplication : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}

object GlobalStorage {
    private const val PREF_NAME = "YourAppPreferences"
    private const val KEY_STRING_SET = "storedStringSet"



    private val sharedPreferences: SharedPreferences
        get() = YourApplication.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    var scanned: String by mutableStateOf("")

    // Read stored string set from SharedPreferences
    private var storedStringSet: MutableSet<String>
        get() = sharedPreferences.getStringSet(KEY_STRING_SET, mutableSetOf()) ?: mutableSetOf()
        set(value) {
            sharedPreferences.edit {
                putStringSet(KEY_STRING_SET, value)
            }
        }

    // Function to add a string to the list if it doesn't exist
    fun addStringToList(newString: String) {
        if (!storedStringSet.contains(newString)) {
            storedStringSet.add(newString)
            // Update the stored set in SharedPreferences
            storedStringSet = storedStringSet
        } else scanned = "This qr code is used"
    }
}
