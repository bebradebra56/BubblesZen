package com.bubbzeniac.apssof.egrf.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication
import com.bubbzeniac.apssof.egrf.presentation.ui.load.BubblesZenLoadFragment
import org.koin.android.ext.android.inject

class BubblesZenV : Fragment(){

    private lateinit var bubblesZenPhoto: Uri
    private var bubblesZenFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val bubblesZenTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        bubblesZenFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        bubblesZenFilePathFromChrome = null
    }

    private val bubblesZenTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            bubblesZenFilePathFromChrome?.onReceiveValue(arrayOf(bubblesZenPhoto))
            bubblesZenFilePathFromChrome = null
        } else {
            bubblesZenFilePathFromChrome?.onReceiveValue(null)
            bubblesZenFilePathFromChrome = null
        }
    }

    private val bubblesZenDataStore by activityViewModels<BubblesZenDataStore>()


    private val bubblesZenViFun by inject<BubblesZenViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bubblesZenDataStore.bubblesZenView.canGoBack()) {
                        bubblesZenDataStore.bubblesZenView.goBack()
                        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "WebView can go back")
                    } else if (bubblesZenDataStore.bubblesZenViList.size > 1) {
                        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "WebView can`t go back")
                        bubblesZenDataStore.bubblesZenViList.removeAt(bubblesZenDataStore.bubblesZenViList.lastIndex)
                        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "WebView list size ${bubblesZenDataStore.bubblesZenViList.size}")
                        bubblesZenDataStore.bubblesZenView.destroy()
                        val previousWebView = bubblesZenDataStore.bubblesZenViList.last()
                        bubblesZenAttachWebViewToContainer(previousWebView)
                        bubblesZenDataStore.bubblesZenView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (bubblesZenDataStore.bubblesZenIsFirstCreate) {
            bubblesZenDataStore.bubblesZenIsFirstCreate = false
            bubblesZenDataStore.bubblesZenContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return bubblesZenDataStore.bubblesZenContainerView
        } else {
            return bubblesZenDataStore.bubblesZenContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "onViewCreated")
        if (bubblesZenDataStore.bubblesZenViList.isEmpty()) {
            bubblesZenDataStore.bubblesZenView = BubblesZenVi(requireContext(), object :
                BubblesZenCallBack {
                override fun bubblesZenHandleCreateWebWindowRequest(bubblesZenVi: BubblesZenVi) {
                    bubblesZenDataStore.bubblesZenViList.add(bubblesZenVi)
                    Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "WebView list size = ${bubblesZenDataStore.bubblesZenViList.size}")
                    Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "CreateWebWindowRequest")
                    bubblesZenDataStore.bubblesZenView = bubblesZenVi
                    bubblesZenVi.bubblesZenSetFileChooserHandler { callback ->
                        bubblesZenHandleFileChooser(callback)
                    }
                    bubblesZenAttachWebViewToContainer(bubblesZenVi)
                }

            }, bubblesZenWindow = requireActivity().window).apply {
                bubblesZenSetFileChooserHandler { callback ->
                    bubblesZenHandleFileChooser(callback)
                }
            }
            bubblesZenDataStore.bubblesZenView.bubblesZenFLoad(arguments?.getString(
                BubblesZenLoadFragment.BUBBLES_ZEN_D) ?: "")
//            ejvview.fLoad("www.google.com")
            bubblesZenDataStore.bubblesZenViList.add(bubblesZenDataStore.bubblesZenView)
            bubblesZenAttachWebViewToContainer(bubblesZenDataStore.bubblesZenView)
        } else {
            bubblesZenDataStore.bubblesZenViList.forEach { webView ->
                webView.bubblesZenSetFileChooserHandler { callback ->
                    bubblesZenHandleFileChooser(callback)
                }
            }
            bubblesZenDataStore.bubblesZenView = bubblesZenDataStore.bubblesZenViList.last()

            bubblesZenAttachWebViewToContainer(bubblesZenDataStore.bubblesZenView)
        }
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "WebView list size = ${bubblesZenDataStore.bubblesZenViList.size}")
    }

    private fun bubblesZenHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        bubblesZenFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Launching file picker")
                    bubblesZenTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Launching camera")
                    bubblesZenPhoto = bubblesZenViFun.bubblesZenSavePhoto()
                    bubblesZenTakePhoto.launch(bubblesZenPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                bubblesZenFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun bubblesZenAttachWebViewToContainer(w: BubblesZenVi) {
        bubblesZenDataStore.bubblesZenContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            bubblesZenDataStore.bubblesZenContainerView.removeAllViews()
            bubblesZenDataStore.bubblesZenContainerView.addView(w)
        }
    }


}