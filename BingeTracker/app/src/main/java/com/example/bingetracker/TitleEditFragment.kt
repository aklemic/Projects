package com.example.bingetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class TitleEditFragment : Fragment() {

    private val viewModel: TitleViewModel by activityViewModels()

    // -1L = create mode, bilo koji drugi ID = edit mode
    private var editingTitleId: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_title_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editName: EditText = view.findViewById(R.id.editName)
        val spinnerType: Spinner = view.findViewById(R.id.spinnerType)
        val spinnerStatus: Spinner = view.findViewById(R.id.spinnerStatus)
        val editRating: EditText = view.findViewById(R.id.editRating)
        val buttonSave: Button = view.findViewById(R.id.buttonSave)

        val types = listOf("FILM", "SERIES")
        val statuses = listOf("WATCHING", "COMPLETED", "PLANNED")

        spinnerType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            types
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerStatus.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statuses
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // 1) Pročitaj argument
        editingTitleId = arguments?.getLong("titleId", -1L) ?: -1L

        // 2) Ako je edit mode, nađi Title i popuni polja
        if (editingTitleId != -1L) {
            viewModel.allTitles.observe(viewLifecycleOwner) { titles ->
                val found = titles.firstOrNull { it.id == editingTitleId }
                if (found != null) {
                    // popuni polja iz baze
                    editName.setText(found.name)

                    val typeIndex = types.indexOf(found.type)
                    if (typeIndex >= 0) spinnerType.setSelection(typeIndex)

                    val statusIndex = statuses.indexOf(found.status)
                    if (statusIndex >= 0) spinnerStatus.setSelection(statusIndex)

                    if (found.rating != null) {
                        editRating.setText(found.rating.toString())
                    }
                }
            }
        }

        // 3) Spremanje – create ili update
        buttonSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val type = spinnerType.selectedItem as String
            val status = spinnerStatus.selectedItem as String
            val ratingText = editRating.text.toString().trim()
            val rating = if (ratingText.isEmpty()) null else ratingText.toInt()

            if (name.isNotEmpty()) {
                if (editingTitleId == -1L) {
                    // NOVI zapis
                    val title = Title(
                        name = name,
                        type = type,
                        status = status,
                        rating = rating
                    )
                    viewModel.insert(title)
                } else {
                    // AŽURIRANJE postojećeg
                    val updated = Title(
                        id = editingTitleId,
                        name = name,
                        type = type,
                        status = status,
                        rating = rating
                    )
                    viewModel.update(updated)
                }
                findNavController().navigateUp()
            }
        }
    }
}
