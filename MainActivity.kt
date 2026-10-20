package com.kareth.smartstockassistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import java.io.File

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null
    private var cameraPermissionRequest: PermissionRequest? = null
    private var pendingCameraUri: Uri? = null
    private var pendingPhotoFile: File? = null
    private val cameraRequestCode = 4102
    private val fileRequestCode = 4103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        webView = WebView(this)
        webView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setContentView(webView)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = true
            mediaPlaybackRequiresUserGesture = false
        }
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest) =
                assetLoader.shouldInterceptRequest(request.url)
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                fileCallback?.onReceiveValue(null)
                fileCallback = callback
                val picker = Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = params.acceptTypes.firstOrNull { it.isNotBlank() } ?: "image/*"
                }
                val intents = mutableListOf<Intent>()
                if (params.isCaptureEnabled && picker.type.startsWith("image/")) {
                    val photo = File(cacheDir, "product-${System.currentTimeMillis()}.jpg")
                    pendingPhotoFile = photo
                    pendingCameraUri = FileProvider.getUriForFile(
                        this@MainActivity,
                        "$packageName.files",
                        photo
                    )
                    intents += Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, pendingCameraUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        clipData = android.content.ClipData.newUri(contentResolver, "Product photo", pendingCameraUri)
                    }
                }
                startActivityForResult(Intent.createChooser(picker, "Choose product photo").apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
                }, fileRequestCode)
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                val isLocalApp = request.origin.host == "appassets.androidplatform.net"
                val wantsCamera = request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                if (!isLocalApp || !wantsCamera) {
                    request.deny()
                    return
                }
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                } else {
                    cameraPermissionRequest?.deny()
                    cameraPermissionRequest = request
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        cameraRequestCode
                    )
                }
            }
        }
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
    }

    @Deprecated("Legacy callback required for WebChromeClient file chooser")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileRequestCode) {
            val selected = when {
                resultCode != RESULT_OK -> null
                data?.data != null -> data.data
                pendingCameraUri != null && pendingPhotoFile?.exists() == true -> pendingCameraUri
                else -> null
            }
            fileCallback?.onReceiveValue(selected?.let { arrayOf(it) })
            fileCallback = null
            pendingCameraUri = null
            pendingPhotoFile = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraRequestCode) {
            val request = cameraPermissionRequest ?: return
            cameraPermissionRequest = null
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED &&
                request.origin.host == "appassets.androidplatform.net" &&
                request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
            ) {
                request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
            } else {
                request.deny()
            }
        }
    }

    @Deprecated("Use system back handling on newer Android versions")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        fileCallback?.onReceiveValue(null)
        cameraPermissionRequest?.deny()
        webView.destroy()
        super.onDestroy()
    }
}
