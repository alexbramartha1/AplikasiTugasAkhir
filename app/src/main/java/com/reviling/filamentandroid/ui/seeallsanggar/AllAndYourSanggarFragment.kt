package com.reviling.filamentandroid.ui.seeallsanggar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reviling.filamentandroid.R
import com.reviling.filamentandroid.ViewModelFactory
import com.reviling.filamentandroid.databinding.FragmentAllAndYourSanggarBinding
import com.reviling.filamentandroid.ui.adapter.InstrumentAdapter
import com.reviling.filamentandroid.ui.seeallinstrument.SeeAllInstrumentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.reviling.filamentandroid.data.Result
import com.reviling.filamentandroid.ui.CustomItemDecoration
import com.reviling.filamentandroid.ui.CustomItemDecorationVertical
import com.reviling.filamentandroid.ui.adapter.SanggarAdapter
import com.reviling.filamentandroid.ui.adapter.SanggarSeeAllAdapter
import com.reviling.filamentandroid.ui.inputsanggar.InputDataSanggarActivity

class AllAndYourSanggarFragment : Fragment() {

    private var _binding: FragmentAllAndYourSanggarBinding? = null
    private val binding get() =_binding!!
    private lateinit var seeAllSanggarViewModel: SeeAllSanggarViewModel
    private var position: Int? = null
    private lateinit var sanggarAdapter: SanggarSeeAllAdapter
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllAndYourSanggarBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.showEverything.setOnClickListener {
            Log.d("buttonClick", "buttonClick")
            val intentAdd = Intent(requireContext(), InputDataSanggarActivity::class.java)
            startActivity(intentAdd)
        }

        lifecycleScope.launch {
            seeAllSanggarViewModel = withContext(Dispatchers.IO) {
                ViewModelFactory.getInstance(requireContext())
                    .create(SeeAllSanggarViewModel::class.java)
            }

            arguments?.let {
                position = it.getInt(ARG_POSITION)
            }

            binding.searchViewGamelan.setupWithSearchBar(binding.searchBarGamelan)

            binding.searchViewGamelan
                .editText
                .setOnEditorActionListener{ textView, actionId, event ->
                    binding.searchViewGamelan.hide()
                    val value = binding.searchViewGamelan.text.toString()
                    seeAllSanggarViewModel.getAllSanggarByName(value).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter
                                    sanggarAdapter.setListSanggarAll(result.data)
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }
                    Log.d("IsiSearchView", binding.searchViewGamelan.text.toString())
                    false
                }

            if (position == 1) {
                seeAllSanggarViewModel.getAllSanggar().observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                isLoading(true)
                            }

                            is Result.Success -> {
                                showToast("Data Loaded")
                                isLoading(false)

                                sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                                binding.rvSanggar.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                                binding.rvSanggar.setHasFixedSize(true)
                                binding.rvSanggar.adapter = sanggarAdapter
                                sanggarAdapter.setListSanggarAll(result.data)
                            }

                            is Result.Error -> {
                                showToast(result.error)
                                isLoading(false)
                            }
                        }
                    }
                }
                binding.addDataSanggar.visibility = View.GONE

            } else {
                seeAllSanggarViewModel.getSessionUser().observe(viewLifecycleOwner) { user ->
                    if (user.isLogin) {
                        userId = user.user_id
                    }

                    seeAllSanggarViewModel.getAllSanggarByUserId(userId.toString()).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    if (binding.rvSanggar.itemDecorationCount == 0) {
                                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_32dp)
                                        binding.rvSanggar.addItemDecoration(CustomItemDecorationVertical(spacingInPixels, false))
                                    }
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter
                                    sanggarAdapter.setListSanggarAll(result.data.sanggarData)
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }
                }

                binding.addDataSanggar.visibility = View.VISIBLE
            }

            binding.scrolling.setOnRefreshListener {
                arguments?.let {
                    position = it.getInt(ARG_POSITION)
                }

                if (position == 1) {
                    seeAllSanggarViewModel.getAllSanggar().observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    isLoading(true)
                                }

                                is Result.Success -> {
                                    showToast("Data Loaded")
                                    isLoading(false)

                                    sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                    binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                    binding.rvSanggar.setHasFixedSize(true)
                                    binding.rvSanggar.adapter = sanggarAdapter
                                    sanggarAdapter.setListSanggarAll(result.data)
                                }

                                is Result.Error -> {
                                    showToast(result.error)
                                    isLoading(false)
                                }
                            }
                        }
                    }

                    binding.addDataSanggar.visibility = View.GONE

                    binding.scrolling.isRefreshing = false
                } else {
                    seeAllSanggarViewModel.getSessionUser().observe(viewLifecycleOwner) { user ->
                        if (user.isLogin) {
                            userId = user.user_id
                        }

                        seeAllSanggarViewModel.getAllSanggarByUserId(userId.toString()).observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        isLoading(true)
                                    }

                                    is Result.Success -> {
                                        showToast("Data Loaded")
                                        isLoading(false)

                                        sanggarAdapter = SanggarSeeAllAdapter(requireContext())
                                        binding.rvSanggar.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                        binding.rvSanggar.setHasFixedSize(true)
                                        binding.rvSanggar.adapter = sanggarAdapter
                                        sanggarAdapter.setListSanggarAll(result.data.sanggarData)
                                    }

                                    is Result.Error -> {
                                        showToast(result.error)
                                        isLoading(false)
                                    }
                                }
                            }
                        }
                    }

                    binding.addDataSanggar.visibility = View.VISIBLE

                    binding.scrolling.isRefreshing = false
                }
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "position"
    }
}