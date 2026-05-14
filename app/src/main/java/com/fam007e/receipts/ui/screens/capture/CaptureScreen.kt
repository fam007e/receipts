package com.fam007e.receipts.ui.screens.capture

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.video.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.fam007e.receipts.domain.model.Person
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CaptureScreen(
    personId: Long? = null,
    onCaptureComplete: () -> Unit,
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        CameraContent(personId, viewModel, onCaptureComplete)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission required.")
        }
    }
}

@Composable
private fun CameraContent(
    initialPersonId: Long?,
    viewModel: CaptureViewModel,
    onCaptureComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val persons by viewModel.persons.collectAsState()
    
    var selectedPerson by remember { 
        mutableStateOf<Person?>(null) 
    }

    LaunchedEffect(persons, initialPersonId) {
        if (selectedPerson == null && initialPersonId != null) {
            selectedPerson = persons.find { it.id == initialPersonId }
        }
    }
    var isVideoMode by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val recorder = remember { 
        Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
    }
    val videoCapture: VideoCapture<Recorder> = remember { VideoCapture.withOutput(recorder) }
    var recording by remember { mutableStateOf<Recording?>(null) }
    
    val executor = remember { ContextCompat.getMainExecutor(context) }

    LaunchedEffect(lensFacing, isVideoMode) {
        val cameraProvider = context.getCameraProvider()
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                if (isVideoMode) videoCapture else imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            PersonSelector(
                persons = persons,
                selected = selectedPerson,
                onSelect = { selectedPerson = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flash/Switch toggle
                IconButton(onClick = { 
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) 
                        CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                }) {
                    Icon(Icons.Default.Cached, contentDescription = "Switch Camera", tint = Color.White)
                }

                // Shutter
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isVideoMode) Color.Red else Color.White)
                        .clickable {
                            if (isVideoMode) {
                                if (recording != null) {
                                    recording?.stop()
                                    recording = null
                                } else {
                                    recording = startRecording(
                                        context,
                                        videoCapture,
                                        executor
                                    ) { uri ->
                                        selectedPerson?.let { person ->
                                            viewModel.saveReceipt(
                                                personId = person.id,
                                                categoryId = null,
                                                mediaPath = uri.toString(),
                                                mediaType = "video",
                                                isPositive = false
                                            )
                                            onCaptureComplete()
                                        }
                                    }
                                }
                            } else {
                                takePhoto(
                                    context, 
                                    imageCapture, 
                                    executor
                                ) { uri ->
                                    selectedPerson?.let { person ->
                                        viewModel.saveReceipt(
                                            personId = person.id,
                                            categoryId = null,
                                            mediaPath = uri.toString(),
                                            mediaType = "photo",
                                            isPositive = false
                                        )
                                        onCaptureComplete()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!isVideoMode) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Capture")
                    }
                }

                TextButton(onClick = { isVideoMode = !isVideoMode }) {
                    Text(if (isVideoMode) "Video" else "Photo", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PersonSelector(
    persons: List<Person>,
    selected: Person?,
    onSelect: (Person) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(persons) { person ->
            FilterChip(
                selected = person == selected,
                onClick = { onSelect(person) },
                label = { Text(person.name) }
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onPhotoCaptured: (Uri) -> Unit
) {
    val file = File(context.filesDir, "receipt_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onPhotoCaptured(Uri.fromFile(file))
            }
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

private fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    executor: java.util.concurrent.Executor,
    onVideoCaptured: (Uri) -> Unit
): Recording {
    val file = File(context.filesDir, "receipt_${System.currentTimeMillis()}.mp4")
    val outputOptions = FileOutputOptions.Builder(file).build()
    
    return videoCapture.output
        .prepareRecording(context, outputOptions)
        .start(executor) { event ->
            if (event is VideoRecordEvent.Finalize) {
                if (!event.hasError()) {
                    onVideoCaptured(Uri.fromFile(file))
                }
            }
        }
}
