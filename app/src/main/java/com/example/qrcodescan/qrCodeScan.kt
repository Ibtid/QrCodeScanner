package com.example.qrcodescan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class BarcodeAnalyser(
    val callback: (String) -> Unit
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class) override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0) {
                        callback(barcodes[0].displayValue.toString())
                    }
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        }
        imageProxy.close()
    }
}



@Composable
fun FourCornersWithBorder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Draw four corners with white border
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(
                    2.dp,
                    Color.White,
                    shape = RoundedCornerShape(
                        topStart = CornerSize(10.dp),
                        topEnd = CornerSize(10.dp),
                        bottomStart = CornerSize(10.dp),
                        bottomEnd = CornerSize(10.dp)
                    )
                )
        ) {
            Text(
                text = "Scan QR Code",
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
            // Content inside the box, you can customize this as needed
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScan() {


    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    val requestPermission = {
        when {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,  // Cast context to Activity
                android.Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // Request camera permission when the composable is first launched
    DisposableEffect(Unit) {
        requestPermission()
        onDispose {
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color(0xFFF0E7D4),
        topBar = {

        },
    ) { innerPadding ->
        QrCodeScanScrollContent()
    }
}


@kotlin.OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation", "UnrememberedMutableState")
@Composable
fun QrCodeScanScrollContent() {
    val scrollState = rememberScrollState()

    val scrollStateHorizontal = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE1BB8E))
        ) {
            Spacer(modifier = Modifier.height(90.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFF0E7D4), RoundedCornerShape(
                            topStart = 24.dp, topEnd = 24.dp, bottomStart = 0.dp, bottomEnd = 0.dp
                        )
                    )
                    .padding(16.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))
                Box(
                    modifier = Modifier
                        .size(
                            400.dp
                        ) // Adjust the width and height as needed for the NID card shape
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        ) // Adjust the corner radius for the card
                ) {
                    AndroidView(
                        { context ->
                            val cameraExecutor = Executors.newSingleThreadExecutor()
                            val previewView = PreviewView(context).also {
                                it.scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                            cameraProviderFuture.addListener({
                                val cameraProvider: ProcessCameraProvider =
                                    cameraProviderFuture.get()

                                val preview = Preview.Builder()
                                    .build()
                                    .also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }

                                val imageCapture = ImageCapture.Builder().build()

                                val imageAnalyzer = ImageAnalysis.Builder()
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor, BarcodeAnalyser { String ->
                                            Toast.makeText(context, String, Toast.LENGTH_SHORT)
                                                .show()
                                            GlobalStorage.addStringToList(String)
                                        })
                                    }

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    // Unbind use cases before rebinding
                                    cameraProvider.unbindAll()

                                    // Bind use cases to camera
                                    cameraProvider.bindToLifecycle(
                                        context as ComponentActivity,
                                        cameraSelector,
                                        preview,
                                        imageCapture,
                                        imageAnalyzer
                                    )

                                } catch (exc: Exception) {
                                    Log.e("DEBUG", "Use case binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(context))
                            previewView
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    FourCornersWithBorder()
                }

                Spacer(modifier = Modifier.height(50.dp))
                Text(text = "Result: ${GlobalStorage.scanned}")

            }
            //Saved
        }
    }

}
