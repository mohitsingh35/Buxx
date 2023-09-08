package com.ncs.tradezy

import android.content.Context
import androidx.core.content.edit
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)

    fun saveRecentSearch(search: String) {
        val searches = getRecentSearches().toMutableSet()
        searches.add(search)
        sharedPreferences.edit {
            putStringSet(RECENT_SEARCHES_KEY, searches)
        }
    }

    fun getRecentSearches(): Set<String> {
        return sharedPreferences.getStringSet(RECENT_SEARCHES_KEY, emptySet()) ?: emptySet()
    }

    fun clearRecentSearches() {
        sharedPreferences.edit {
            remove(RECENT_SEARCHES_KEY)
        }
    }

    companion object {
        private const val RECENT_SEARCHES_KEY = "recent_searches"
    }
}

