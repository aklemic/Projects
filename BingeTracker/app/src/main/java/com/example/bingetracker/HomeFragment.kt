package com.example.bingetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonOpenSettings: Button = view.findViewById(R.id.buttonOpenSettings)
        buttonOpenSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        val buttonOpenList: Button = view.findViewById(R.id.buttonOpenList)
        buttonOpenList.setOnClickListener {
            findNavController().navigate(R.id.titleListFragment)
        }
    }
}
