package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ArticleScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: NewsViewModel = viewModel()
      val isReadMode by viewModel.isReadMode.collectAsState()
      val isScreenSecurityEnabled by viewModel.isScreenSecurityEnabled.collectAsState()

      androidx.compose.runtime.LaunchedEffect(isScreenSecurityEnabled) {
          if (isScreenSecurityEnabled) {
              window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
          } else {
              window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
          }
      }

      MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            GlobalNewsApp(viewModel)
            
            // Eye Comfort / Read Mode overlay (Allows interactions underneath)
            if (isReadMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFFB000).copy(alpha = 0.15f))
                )
            }
        }
      }
    }
  }
}

@Composable
fun GlobalNewsApp(viewModel: NewsViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onArticleClick = { articleId ->
                    navController.navigate("article/$articleId")
                },
                viewModel = viewModel
            )
        }
        composable("article/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            ArticleScreen(
                articleId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

