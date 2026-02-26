package com.bubbzeniac.apssof.egrf.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bubbzeniac.apssof.MainActivity
import com.bubbzeniac.apssof.R
import com.bubbzeniac.apssof.databinding.FragmentLoadBubblesZenBinding
import com.bubbzeniac.apssof.egrf.data.shar.BubblesZenSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BubblesZenLoadFragment : Fragment(R.layout.fragment_load_bubbles_zen) {
    private lateinit var bubblesZenLoadBinding: FragmentLoadBubblesZenBinding

    private val bubblesZenLoadViewModel by viewModel<BubblesZenLoadViewModel>()

    private val bubblesZenSharedPreference by inject<BubblesZenSharedPreference>()

    private var bubblesZenUrl = ""

    private val bubblesZenRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        bubblesZenSharedPreference.bubblesZenNotificationState = 2
        bubblesZenNavigateToSuccess(bubblesZenUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bubblesZenLoadBinding = FragmentLoadBubblesZenBinding.bind(view)

        bubblesZenLoadBinding.bubblesZenGrandButton.setOnClickListener {
            val bubblesZenPermission = Manifest.permission.POST_NOTIFICATIONS
            bubblesZenRequestNotificationPermission.launch(bubblesZenPermission)
        }

        bubblesZenLoadBinding.bubblesZenSkipButton.setOnClickListener {
            bubblesZenSharedPreference.bubblesZenNotificationState = 1
            bubblesZenSharedPreference.bubblesZenNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            bubblesZenNavigateToSuccess(bubblesZenUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bubblesZenLoadViewModel.bubblesZenHomeScreenState.collect {
                    when (it) {
                        is BubblesZenLoadViewModel.BubblesZenHomeScreenState.BubblesZenLoading -> {

                        }

                        is BubblesZenLoadViewModel.BubblesZenHomeScreenState.BubblesZenError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BubblesZenLoadViewModel.BubblesZenHomeScreenState.BubblesZenSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val bubblesZenNotificationState = bubblesZenSharedPreference.bubblesZenNotificationState
                                when (bubblesZenNotificationState) {
                                    0 -> {
                                        bubblesZenLoadBinding.bubblesZenNotiGroup.visibility = View.VISIBLE
                                        bubblesZenLoadBinding.bubblesZenLoadingGroup.visibility = View.GONE
                                        bubblesZenUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > bubblesZenSharedPreference.bubblesZenNotificationRequest) {
                                            bubblesZenLoadBinding.bubblesZenNotiGroup.visibility = View.VISIBLE
                                            bubblesZenLoadBinding.bubblesZenLoadingGroup.visibility = View.GONE
                                            bubblesZenUrl = it.data
                                        } else {
                                            bubblesZenNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        bubblesZenNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                bubblesZenNavigateToSuccess(it.data)
                            }
                        }

                        BubblesZenLoadViewModel.BubblesZenHomeScreenState.BubblesZenNotInternet -> {
                            bubblesZenLoadBinding.bubblesZenStateGroup.visibility = View.VISIBLE
                            bubblesZenLoadBinding.bubblesZenLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun bubblesZenNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_bubblesZenLoadFragment_to_bubblesZenV,
            bundleOf(BUBBLES_ZEN_D to data)
        )
    }

    companion object {
        const val BUBBLES_ZEN_D = "bubblesZenData"
    }
}