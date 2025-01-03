package com.reviling.filamentandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.reviling.filamentandroid.databinding.FragmentChooseBinding


class ChooseFragment : DialogFragment() {

    private var _binding: FragmentChooseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        val view = binding.root


        // Inflate the layout for this fragment
        return view
    }

}