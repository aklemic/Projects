package com.example.bingetracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    companion object {
        private const val PREFS_NAME = "bingetracker_prefs"
        private const val KEY_DEFAULT_FILTER = "default_filter"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroupFilter)
        val radioAll: RadioButton = view.findViewById(R.id.radioFilterAll)
        val radioWatching: RadioButton = view.findViewById(R.id.radioFilterWatching)
        val radioPlanned: RadioButton = view.findViewById(R.id.radioFilterPlanned)

        val prefs = requireContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // učitaj spremljeni filter (default ALL)
        when (prefs.getString(KEY_DEFAULT_FILTER, "ALL")) {
            "ALL" -> radioAll.isChecked = true
            "WATCHING" -> radioWatching.isChecked = true
            "PLANNED" -> radioPlanned.isChecked = true
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val value = when (checkedId) {
                R.id.radioFilterAll -> "ALL"
                R.id.radioFilterWatching -> "WATCHING"
                R.id.radioFilterPlanned -> "PLANNED"
                else -> "ALL"
            }

            prefs.edit()
                .putString(KEY_DEFAULT_FILTER, value)
                .apply()
        }
    }
}
