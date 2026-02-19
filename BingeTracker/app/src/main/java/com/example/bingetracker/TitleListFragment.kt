package com.example.bingetracker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration

class TitleListFragment : Fragment() {

    private val viewModel: TitleViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TitleAdapter
    private lateinit var emptyText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_title_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerTitles)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)

        emptyText = view.findViewById(R.id.textEmpty)

        adapter = TitleAdapter(
            emptyList(),
            onItemClick = { title ->
                val bundle = Bundle().apply { putLong("titleId", title.id) }
                findNavController().navigate(R.id.titleEditFragment, bundle)
            },
            onItemLongClick = { title ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Brisanje")
                    .setMessage("Želiš li obrisati \"${title.name}\"?")
                    .setPositiveButton("Obriši") { _, _ ->
                        viewModel.delete(title)
                    }
                    .setNegativeButton("Odustani", null)
                    .show()
            }
        )

        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonAdd: Button = view.findViewById(R.id.buttonAddTitle)
        buttonAdd.setOnClickListener {
            // bez bundle-a → titleId će biti -1 → create mode
            findNavController().navigate(R.id.titleEditFragment)
        }

        viewModel.allTitles.observe(viewLifecycleOwner) { titles ->
            // konstante za prefs
            val prefsName = "bingetracker_prefs"
            val keyDefaultFilter = "default_filter"

            val prefs = requireContext()
                .getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            val filter = prefs.getString(keyDefaultFilter, "ALL")

            val filtered = when (filter) {
                "WATCHING" -> titles.filter { it.status == "WATCHING" }
                "PLANNED" -> titles.filter { it.status == "PLANNED" }
                else -> titles // ALL → svi, uključujući COMPLETED
            }

            adapter.submitList(filtered)

            if (filtered.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyText.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}
