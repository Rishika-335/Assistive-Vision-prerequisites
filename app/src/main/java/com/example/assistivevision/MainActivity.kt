package com.example.assistivevision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.assistivevision.ui.theme.AssistiveVisionTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val bitmapImage = remember { mutableStateOf<Bitmap?>(null) }

            val viewModel: ObjectDetectionViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            val context = LocalContext.current
            val scrollState = rememberScrollState()


            val cameraLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.TakePicturePreview()
                ) { bitmap ->
                    bitmapImage.value = bitmap
                    imageUri = null
                    bitmap?.let { viewModel.detectObjects(it) }
                }

            val camPermission =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) cameraLauncher.launch(null)
                    else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT)
                        .show()
                }

            val galleryLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri ->
                    imageUri = uri
                    bitmapImage.value = null
                    uri?.let {
                        val bitmap = uriToBitmap(context, it)
                        viewModel.detectObjects(bitmap)
                    }
                }

            val storagePermission =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    if (granted) galleryLauncher.launch("image/*")
                    else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT)
                        .show()
                }


            AssistiveVisionTheme {
                val scrollBehavior =
                    TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(
                                    "Assistive Vision",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    },
                ) { innerPadding ->

                    Column(modifier = Modifier.fillMaxSize()) {

                        // TOP SECTION
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color.Blue)
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Identify the object around you",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Take a photo, we'll describe it for you",
                                color = Color.White,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(Modifier.height(12.dp))

                            Row {
                                ActionButton(
                                    "Capture Photo",
                                    painterResource(R.drawable.camera),
                                    Color(0xFF1E5DBA)
                                ) {
                                    camPermission.launch(android.Manifest.permission.CAMERA)
                                }
                                Spacer(Modifier.width(12.dp))
                                ActionButton(
                                    "Choose from Gallery",
                                    painterResource(R.drawable.camera),
                                    Color(0xFF4CAF50)
                                ) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        storagePermission.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(innerPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (bitmapImage.value != null || imageUri != null) {

                                if (imageUri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageUri)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxHeight(0.45f),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                if (bitmapImage.value != null) {
                                    Image(
                                        bitmap = bitmapImage.value!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxHeight(0.45f),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Spacer(Modifier.height(12.dp))

                                when {
                                    uiState.isLoading -> {
                                        CircularProgressIndicator()
                                        Text("Analyzing image…")
                                    }

                                    uiState.error != null -> {
                                        Text(
                                            uiState.error ?: "An unknown error occurred",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    uiState.detectedObjects != null -> {
                                        Text(
                                            "Detected Objects",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                        Spacer(Modifier.height(8.dp))

                                        uiState.detectedObjects?.let { label ->
                                            Text(
                                                text = "${label.text} ",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }


                                    }
                                }

                                if (uiState.detectedObjects != null || uiState.error != null) {
                                    Spacer(Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            viewModel.reset()
                                            bitmapImage.value = null
                                            imageUri = null
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Red
                                        )
                                    ) {
                                        Text("Reset")
                                    }
                                }
                            } else {
                                Text(
                                    "Nothing to display.",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                        }
                    }
                }
            }
        }
    }
    @Composable
    fun ActionButton(text: String,
                     icon: Painter,
                     backgroundColor: Color,
                     onClick: () -> Unit
    ) {
        Surface( onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            color = backgroundColor, shadowElevation = 8.dp,
            modifier = Modifier .height(80.dp) .width(160.dp) )
        {
            Row( modifier = Modifier .fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center ) {
                Image( painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp) )
                Spacer(modifier = Modifier.width(12.dp))
                Text( text = text, color = Color.White, fontWeight = FontWeight.SemiBold ) } } }


    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

}