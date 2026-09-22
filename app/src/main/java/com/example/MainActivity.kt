package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.MainIdeScreen
import com.example.ui.components.LivePreviewView
import com.example.ui.viewmodel.IdeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: IdeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainIdeScreen(viewModel = viewModel)
        }
    }
}

/**
 * 1. Google Android Studio Preview - সরাসরি তৈরি করা অ্যাপের প্রিভিউ (Built App Live Preview)
 */
@Preview(name = "1. Built App Direct Preview (গুগল অ্যান্ড্রয়েড স্টুডিও প্রিভিউ)", showBackground = true, showSystemUi = true)
@Composable
fun BuiltAppAndroidStudioPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LivePreviewView(
                activeFileContent = IdeViewModel.DEFAULT_MAIN_ACTIVITY_CODE
            )
        }
    }
}

/**
 * 2. Google Android Studio Preview - সম্পূর্ণ NoTrack IDE ওয়ার্কস্পেস প্রিভিউ
 */
@Preview(name = "2. NoTrack IDE Workspace Preview", showBackground = true, showSystemUi = true)
@Composable
fun MainActivityAppPreview() {
    MainIdeScreen()
}
