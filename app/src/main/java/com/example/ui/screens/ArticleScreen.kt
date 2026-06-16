package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.viewmodel.NewsViewModel
import com.example.viewmodel.TimelineEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    articleId: String,
    viewModel: NewsViewModel,
    onBack: () -> Unit
) {
    val article = viewModel.getArticleById(articleId)
    var aiSummary by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var isScraping by remember { mutableStateOf(true) }
    var scrapedText by remember { mutableStateOf("") }
    
    val savedArticles by viewModel.savedArticleIds.collectAsState()
    val isSaved = savedArticles.contains(articleId)

    if (article == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Article not found.")
        }
        return
    }

    LaunchedEffect(article.id) {
        isGenerating = true
        aiSummary = viewModel.getArticleSummary(article.fullText)
        isGenerating = false
        
        // Simulate deep scrape
        isScraping = true
        delay(1200)
        
        val expandedContent = buildString {
            append("This detailed report provides extensive coverage regarding the latest movements in the ${article.category} sector.\n\n")
            append(article.fullText).append("\n\n")
            append("In light of these continuous shifts, industry veterans suggest this might be a watershed moment. As ${article.source} extensively analyzed, ")
            append("when global systems interlock so completely, predicting localized fallout becomes exceptionally difficult. Local authorities are already preparing extensive countermeasures.\n\n")
            append("Furthermore, sociological implications are immense. A paradigm shift often begins with seemingly ephemeral adjustments but cascades into ubiquitous cultural change. ")
            append("It highlights the sagacious decisions required of leaders. Obfuscating data will no longer provide safe harbor for institutions averse to transparency.")
        }
        scrapedText = expandedContent
        isScraping = false
    }

    val scrollState = rememberScrollState()
    val progress by remember {
        derivedStateOf {
            if (scrollState.maxValue > 0) {
                scrollState.value.toFloat() / scrollState.maxValue.toFloat()
            } else {
                0f
            }
        }
    }

    // Interactive Text-To-Speech Setup
    val context = LocalContext.current
    var tts: android.speech.tts.TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val initializedTts = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                // Fully set up
            }
        }
        tts = initializedTts
        onDispose {
            initializedTts.stop()
            initializedTts.shutdown()
        }
    }

    // Auto-Scroll States
    var isAutoScrolling by remember { mutableStateOf(false) }
    var autoScrollSpeed by remember { mutableFloatStateOf(10f) } // Pixels per check

    LaunchedEffect(isAutoScrolling, autoScrollSpeed) {
        if (isAutoScrolling) {
            while (true) {
                delay(150)
                scrollState.scrollBy(autoScrollSpeed)
            }
        }
    }

    // Custom Reading Comfort Layout Themes (OLED Black, Sepia, Charcoal, Default)
    var selectedTheme by remember { mutableStateOf("Default") }
    val (themeBg, themeText, themePrimaryAccent) = when (selectedTheme) {
        "Sepia" -> Triple(Color(0xFFFBF4E6), Color(0xFF5C4033), Color(0xFF8B5A2B))
        "Charcoal" -> Triple(Color(0xFF2E2E2E), Color(0xFFE5E5E5), Color(0xFF82B1FF))
        "OLED Black" -> Triple(Color(0xFF000000), Color(0xFFFFFFFF), Color(0xFFFF4081))
        else -> Triple(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.primary)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(article.source, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleSavedArticle(articleId) }) {
                            Icon(if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, contentDescription = "Bookmark")
                        }
                        IconButton(onClick = { /* Share stub */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .offset(y = (-32).dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(themeBg)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "TOP STORY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        "${article.category} • ${DateUtils.getRelativeTimeSpanString(article.date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = themeText.copy(alpha = 0.7f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(article.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = themeText)
                
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (article.authorImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = article.authorImageUrl,
                            contentDescription = article.author,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Column {
                        Text(article.author, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = themeText)
                        Text("Source: ${article.source}", style = MaterialTheme.typography.labelMedium, color = themePrimaryAccent)
                    }
                }
                Spacer(Modifier.height(24.dp))
                
                var textSizeMultiplier by remember { mutableStateOf(1.0f) }
                
                // Advanced Reader Toolbar: Theme selectors, Audio Playback, and Auto-Scroll Engine
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Comfort Reading Hub", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            
                            // Font Sizers
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { if (textSizeMultiplier > 0.82f) textSizeMultiplier -= 0.15f }) {
                                    Text("A-", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                                Text("${(textSizeMultiplier * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                                TextButton(onClick = { if (textSizeMultiplier < 1.78f) textSizeMultiplier += 0.15f }) {
                                    Text("A+", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Theme cycle
                            IconButton(onClick = {
                                selectedTheme = when (selectedTheme) {
                                    "Default" -> "Sepia"
                                    "Sepia" -> "Charcoal"
                                    "Charcoal" -> "OLED Black"
                                    else -> "Default"
                                }
                            }) {
                                Icon(Icons.Default.Palette, contentDescription = "Switch Theme")
                            }
                            Text(selectedTheme, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            
                            // Audio Player TTS button
                            Button(
                                onClick = {
                                    if (isSpeaking) {
                                        tts?.stop()
                                        isSpeaking = false
                                    } else {
                                        val runText = scrapedText.ifEmpty { article.fullText }
                                        tts?.speak(runText, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "news_stream_id")
                                        isSpeaking = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(if (isSpeaking) "Mute Audio" else "Listen", style = MaterialTheme.typography.labelSmall)
                            }

                            // Dynamic Auto Scroll
                            IconButton(onClick = { isAutoScrolling = !isAutoScrolling }) {
                                Icon(
                                    imageVector = if (isAutoScrolling) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    tint = if (isAutoScrolling) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    contentDescription = "Auto Scroll"
                                )
                            }
                        }
                    }
                }
                
                if (isScraping) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = themePrimaryAccent)
                            Spacer(Modifier.height(16.dp))
                            Text("Deep scraping article content...", style = MaterialTheme.typography.labelMedium, color = themeText.copy(alpha = 0.7f))
                        }
                    }
                } else {
                    val primaryColor = themePrimaryAccent
                    val annotatedText = remember(scrapedText) {
                        buildAnnotatedString {
                            append(scrapedText)
                            val wordsRegex = """\b[a-zA-Z]{8,}\b""".toRegex()
                            wordsRegex.findAll(scrapedText).forEach { matchResult ->
                                addStyle(
                                    style = SpanStyle(
                                        color = primaryColor, 
                                        textDecoration = TextDecoration.Underline, 
                                        fontWeight = FontWeight.Bold
                                    ), 
                                    start = matchResult.range.first, 
                                    end = matchResult.range.last + 1
                                )
                            }
                        }
                    }

                    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                    var popupWord by remember { mutableStateOf<Pair<String, String>?>(null) }
                    val coroutineScope = rememberCoroutineScope()
                    var isFetchingDefinition by remember { mutableStateOf(false) }
                    
                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * textSizeMultiplier,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.55f * textSizeMultiplier
                        ),
                        color = themeText,
                        onTextLayout = { textLayoutResult = it },
                        modifier = Modifier.pointerInput(annotatedText) {
                            detectTapGestures(
                                onDoubleTap = { pos ->
                                    textLayoutResult?.let { layoutResult ->
                                        val offset = layoutResult.getOffsetForPosition(pos)
                                        var start = offset
                                        var end = offset
                                        val text = scrapedText
                                        while (start > 0 && text[start - 1].isLetterOrDigit()) start--
                                        while (end < text.length && text[end].isLetterOrDigit()) end++
                                        if (start < end) {
                                            val word = text.substring(start, end).lowercase()
                                            coroutineScope.launch {
                                                isFetchingDefinition = true
                                                val definition = withContext(Dispatchers.IO) {
                                                    try {
                                                        val url = java.net.URL("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
                                                        val connection = url.openConnection() as java.net.HttpURLConnection
                                                        connection.requestMethod = "GET"
                                                        if (connection.responseCode == 200) {
                                                            val response = connection.inputStream.bufferedReader().use { it.readText() }
                                                            val regex = """"definition"\s*:\s*"([^"]+)"""".toRegex()
                                                            val match = regex.find(response)
                                                            match?.groupValues?.get(1) ?: "No specific definition found."
                                                        } else {
                                                            "Word not found in dictionary."
                                                        }
                                                    } catch (e: Exception) {
                                                        "Offline or error fetching from dictionary."
                                                    }
                                                }
                                                isFetchingDefinition = false
                                                popupWord = word to definition
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    )

                    if (isFetchingDefinition) {
                        AlertDialog(
                            onDismissRequest = { },
                            title = { Text("Fetching meaning...", style = MaterialTheme.typography.titleLarge) },
                            text = { CircularProgressIndicator() },
                            confirmButton = { }
                        )
                    }

                    if (popupWord != null) {
                        AlertDialog(
                            onDismissRequest = { popupWord = null },
                            title = { Text(popupWord!!.first.replaceFirstChar { it.uppercaseChar() }, style = MaterialTheme.typography.titleLarge) },
                            text = { Text(popupWord!!.second, style = MaterialTheme.typography.bodyLarge) },
                            confirmButton = {
                                TextButton(onClick = { popupWord = null }) {
                                    Text("Got it")
                                }
                            }
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // AI Summary Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(true).copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("AI INSIGHT (GEMINI SUMMARY)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        if (isGenerating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text("Generating simple summary...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Text(aiSummary ?: "No summary available.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Ongoing Developing Event Timeline View Section (US-Iran / Pakistan solar launcher)
                val timelineEvents = viewModel.getTimelineForEvent(article.id)
                if (timelineEvents.isNotEmpty()) {
                    Text(
                        "DEVELOPMENT TIMELINE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = themePrimaryAccent,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    timelineEvents.forEachIndexed { idx, ev ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Timeline dot & line
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(28.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(if (idx == timelineEvents.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                                )
                                if (idx < timelineEvents.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(56.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(ev.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = themeText)
                                Text(ev.description, style = MaterialTheme.typography.bodySmall, color = themeText.copy(alpha = 0.8f))
                                Text(ev.date, style = MaterialTheme.typography.labelSmall, color = themePrimaryAccent, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
