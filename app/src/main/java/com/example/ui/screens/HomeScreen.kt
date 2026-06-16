package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.model.NewsArticle
import com.example.viewmodel.NewsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (String) -> Unit,
    viewModel: NewsViewModel
) {
    val articles by viewModel.filteredArticles.collectAsState()
    val breakingNews by viewModel.breakingNews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val isReadMode by viewModel.isReadMode.collectAsState()

    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    val selectedInterests by viewModel.selectedInterests.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()

    val savedArticles by viewModel.savedArticles.collectAsState()
    val selectedAuthor by viewModel.selectedAuthor.collectAsState()
    val authorArticles by viewModel.authorArticles.collectAsState()

    val isScreenSecurityEnabled by viewModel.isScreenSecurityEnabled.collectAsState()
    val isIncognitoMode by viewModel.isIncognitoMode.collectAsState()
    val isSecureInAppBrowser by viewModel.isSecureInAppBrowser.collectAsState()
    val isAutoUpdateEnabled by viewModel.isAutoUpdateEnabled.collectAsState()

    val categories = listOf("All", "Technology", "Business", "Environment", "Sports", "Arts", "Politics", "Defense", "War", "Viral Stories", "Science", "Health", "Entertainment", "Travel", "Education", "Lifestyle", "Fashion")
    val regions = listOf("All", "United States", "China", "Japan", "Germany", "India", "United Kingdom", "France", "Italy", "Brazil", "Canada", "Russia", "South Korea", "Australia", "Mexico", "Spain", "Indonesia", "Netherlands", "Saudi Arabia", "Turkey", "Switzerland", "Taiwan", "Poland", "Argentina", "Belgium", "Sweden", "Thailand", "Ireland", "Austria", "Nigeria", "Israel", "United Arab Emirates", "Malaysia", "South Africa", "Singapore", "Philippines", "Denmark", "Iran", "Pakistan", "Colombia", "Romania", "Chile", "Czech Republic", "Finland", "Vietnam", "Portugal", "Peru", "Greece", "New Zealand", "Egypt")
    
    val topNewspapersByCountry = mapOf(
        "United States" to listOf("All", "NY Times", "Washington Post", "Wall Street Journal", "USA Today", "Tech Times", "Associated Press"),
        "United Kingdom" to listOf("All", "The Guardian", "The Times", "Daily Telegraph", "BBC News", "Reuters"),
        "India" to listOf("All", "Times of India", "The Hindu", "Hindustan Times"),
        "Australia" to listOf("All", "The Australian", "Sydney Morning Herald", "Herald Sun"),
        "Canada" to listOf("All", "Toronto Star", "The Globe and Mail", "National Post"),
        "Japan" to listOf("All", "Yomiuri Shimbun", "Asahi Shimbun", "The Japan Times"),
        "Germany" to listOf("All", "Süddeutsche Zeitung", "Frankfurter Allgemeine", "Die Welt"),
        "France" to listOf("All", "Le Monde", "Le Figaro", "Libération"),
        "Brazil" to listOf("All", "O Globo", "Folha de S.Paulo", "Estadão"),
        "Pakistan" to listOf("All", "Dawn", "The News International", "The Nation", "Daily Times", "Business Recorder")
    )

    val isAdmin by viewModel.isAdmin.collectAsState()
    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showPasswordPromptDialog by remember { mutableStateOf(false) }

    var showAllBreaking by remember { mutableStateOf(false) }
    var showRegionSheet by remember { mutableStateOf(false) }
    var regionSearchQuery by remember { mutableStateOf("") }
    var selectedCountryForNewspapers by remember { mutableStateOf<String?>(null) }
    var currentTab by remember { mutableIntStateOf(0) }
    var selectedAuthorCountry by remember { mutableStateOf("All") }
    var showSecurityHubDialog by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val authorCountries = listOf(
        "All" to "All 🌎",
        "Pakistan" to "Pakistan 🇵🇰",
        "United States" to "United States 🇺🇸",
        "United Kingdom" to "United Kingdom 🇬🇧",
        "India" to "India 🇮🇳",
        "Germany" to "Germany 🇩🇪",
        "France" to "France 🇫🇷",
        "Brazil" to "Brazil 🇧🇷",
        "Canada" to "Canada 🇨🇦",
        "Australia" to "Australia 🇦🇺",
        "Japan" to "Japan 🇯🇵"
    )

    val listState = rememberLazyListState()

    LaunchedEffect(articles.size) {
        if (articles.isNotEmpty() && (listState.firstVisibleItemIndex <= 2)) {
            listState.animateScrollToItem(0)
        }
    }

    if (!onboardingCompleted) {
        OnboardingScreen(
            categories = categories.filter { it != "All" },
            selectedInterests = selectedInterests,
            onInterestToggle = { viewModel.toggleInterest(it) },
            onComplete = { viewModel.completeOnboarding(selectedInterests) }
        )
    } else {
        Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        if (isSearchExpanded) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search news, authors...", style = MaterialTheme.typography.bodyMedium) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        } else {
                            Text(
                                text = androidx.compose.ui.text.buildAnnotatedString {
                                    withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                        append("Global")
                                    }
                                    withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                        append("Press")
                                    }
                                },
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold 
                            ) 
                        }
                    },
                    actions = {
                        if (isSearchExpanded) {
                            IconButton(onClick = { 
                                viewModel.updateSearchQuery("")
                                isSearchExpanded = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Search"
                                )
                            }
                        } else {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Open Search"
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.toggleReadMode() }) {
                            Icon(
                                imageVector = if (isReadMode) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Read Mode"
                            )
                        }
                        var showSecurityMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { showSecurityMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Security Options"
                                )
                            }
                            DropdownMenu(
                                expanded = showSecurityMenu,
                                onDismissRequest = { showSecurityMenu = false }
                            ) {
                                Text(
                                    text = "PROACTIVE SHIELD",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("Screen Shield Protection", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.Security, 
                                            contentDescription = null,
                                            tint = if (isScreenSecurityEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        Switch(
                                            checked = isScreenSecurityEnabled,
                                            onCheckedChange = { viewModel.toggleScreenSecurity() },
                                            modifier = Modifier.scale(0.8f)
                                        )
                                    },
                                    onClick = { viewModel.toggleScreenSecurity() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Session Incognito Mode", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.VisibilityOff, 
                                            contentDescription = null,
                                            tint = if (isIncognitoMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        Switch(
                                            checked = isIncognitoMode,
                                            onCheckedChange = { viewModel.toggleIncognitoMode() },
                                            modifier = Modifier.scale(0.8f)
                                        )
                                    },
                                    onClick = { viewModel.toggleIncognitoMode() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Secure Reader Sandbox", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.LibraryBooks, 
                                            contentDescription = null,
                                            tint = if (isSecureInAppBrowser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        Switch(
                                            checked = isSecureInAppBrowser,
                                            onCheckedChange = { viewModel.toggleSecureInAppBrowser() },
                                            modifier = Modifier.scale(0.8f)
                                        )
                                    },
                                    onClick = { viewModel.toggleSecureInAppBrowser() }
                                )
                                DropdownMenuItem(
                                    text = { Text("Auto-OTA Update Mode", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { 
                                        Icon(
                                            imageVector = Icons.Default.Info, 
                                            contentDescription = null,
                                            tint = if (isAutoUpdateEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        ) 
                                    },
                                    trailingIcon = {
                                        Switch(
                                            checked = isAutoUpdateEnabled,
                                            onCheckedChange = { viewModel.toggleAutoUpdate() },
                                            modifier = Modifier.scale(0.8f)
                                        )
                                    },
                                    onClick = { viewModel.toggleAutoUpdate() }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                DropdownMenuItem(
                                    text = { Text("Forced Security Sync", fontWeight = FontWeight.Normal) },
                                    onClick = { 
                                        showSecurityMenu = false
                                        viewModel.triggerManualUpdateCheck()
                                        showSecurityHubDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Clear Active States", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Normal) },
                                    onClick = { 
                                        showSecurityMenu = false
                                        viewModel.clearSecureSessions()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Open Advanced Hub...", fontWeight = FontWeight.Normal) },
                                    onClick = { 
                                        showSecurityMenu = false
                                        showSecurityHubDialog = true
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                if (isAdmin) {
                                    DropdownMenuItem(
                                        text = { Text("🔧 Backend Dev Settings", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                                        onClick = { 
                                            showSecurityMenu = false
                                            showDeveloperDialog = true
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("🔒 Lock Developer Options", fontWeight = FontWeight.Normal) },
                                        onClick = { 
                                            showSecurityMenu = false
                                            viewModel.logoutAdmin()
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = { Text("🔓 Unlock Developer Options", fontWeight = FontWeight.Normal) },
                                        onClick = { 
                                            showSecurityMenu = false
                                            showPasswordPromptDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                // Scrollable Categories TabRow
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        val index = categories.indexOf(selectedCategory).takeIf { it >= 0 } ?: 0
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {}
                ) {
                    categories.forEachIndexed { index, cat ->
                        Tab(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.updateCategory(cat) },
                            text = { Text(cat, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "News") },
                    label = { Text("News", maxLines = 1) }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "Articles") },
                    label = { Text("Articles", maxLines = 1) }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { 
                        val count = savedArticles.size
                        if (count > 0) {
                            BadgedBox(
                                badge = { 
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) { 
                                        Text(count.toString()) 
                                    } 
                                }
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = "Saved")
                            }
                        } else {
                            Icon(Icons.Default.Favorite, contentDescription = "Saved")
                        }
                    },
                    label = { Text("Saved", maxLines = 1) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showRegionSheet = true; selectedCountryForNewspapers = null },
                    icon = { Icon(Icons.Default.LocationOn, contentDescription = "Country") },
                    label = { Text("Country", maxLines = 1) }
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3; viewModel.selectAuthor(null) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Authors") },
                    label = { Text("Authors", maxLines = 1) }
                )
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = listState
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (currentTab == 0 || currentTab == 1) {
                    val displayArticles = if (currentTab == 0) breakingNews else articles
                    
                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (currentTab == 0) "LATEST FEED" else "TOP ARTICLES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            if (currentTab == 0) {
                                Text(
                                    if (showAllBreaking) "Hide All" else "Show All",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .clickable { showAllBreaking = !showAllBreaking }
                                )
                            }
                        }
                    }

                    if (displayArticles.isEmpty()) {
                        item { Text("No articles found.", modifier = Modifier.padding(16.dp)) }
                    } else {
                        val limit = if (currentTab == 0 && !showAllBreaking) 5 else displayArticles.size
                        items(displayArticles.take(limit), key = { it.id }) { article ->
                            NewsCard(
                                article = article,
                                onClick = { onArticleClick(article.id) },
                                onUpvote = { viewModel.adjustCategoryWeight(article.category, true) },
                                onDownvote = { viewModel.adjustCategoryWeight(article.category, false) },
                                onBlockSource = { viewModel.toggleBlockSource(article.source) },
                                onFollowSource = { viewModel.toggleFollowSource(article.source) },
                                isFollowed = prefs.followedSources.contains(article.source),
                                isBlocked = prefs.blockedSources.contains(article.source)
                            )
                        }
                    }
                } else if (currentTab == 2) {
                    item {
                        Text(
                            "SAVED ARTICLES",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    if (savedArticles.isEmpty()) {
                        item { Text("No saved articles yet.", modifier = Modifier.padding(16.dp)) }
                    } else {
                        items(savedArticles, key = { it.id }) { article ->
                            NewsCard(
                                article = article,
                                onClick = { onArticleClick(article.id) },
                                onUpvote = { viewModel.adjustCategoryWeight(article.category, true) },
                                onDownvote = { viewModel.adjustCategoryWeight(article.category, false) },
                                onBlockSource = { viewModel.toggleBlockSource(article.source) },
                                onFollowSource = { viewModel.toggleFollowSource(article.source) },
                                isFollowed = prefs.followedSources.contains(article.source),
                                isBlocked = prefs.blockedSources.contains(article.source)
                            )
                        }
                    }
                } else if (currentTab == 3) {
                    if (selectedAuthor != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.selectAuthor(null) },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surface, androidx.compose.foundation.shape.CircleShape)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("JOURNALIST FOCUS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        Text(selectedAuthor ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                        if (authorArticles.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                                        Spacer(Modifier.height(8.dp))
                                        Text("No live articles fetched yet for this author.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Our live crawler is actively searching regional feeds...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        } else {
                            items(authorArticles, key = { it.id }) { article ->
                                NewsCard(
                                    article = article,
                                    onClick = { onArticleClick(article.id) },
                                    onUpvote = { viewModel.adjustCategoryWeight(article.category, true) },
                                    onDownvote = { viewModel.adjustCategoryWeight(article.category, false) },
                                    onBlockSource = { viewModel.toggleBlockSource(article.source) },
                                    onFollowSource = { viewModel.toggleFollowSource(article.source) },
                                    isFollowed = prefs.followedSources.contains(article.source),
                                    isBlocked = prefs.blockedSources.contains(article.source)
                                )
                            }
                        }
                    } else {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(
                                    "TOP PRESS JOURNALISTS",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Read elite columns from around the world",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Horizontal Scroll of Author Country Chips
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(authorCountries) { (countryCode, displayLabel) ->
                                    val isSelected = selectedAuthorCountry == countryCode
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedAuthorCountry = countryCode },
                                        label = { Text(displayLabel, style = MaterialTheme.typography.bodyMedium) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            labelColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = null
                                    )
                                }
                            }
                        }

                        // Filter our global authors
                        val filteredAuthors = if (selectedAuthorCountry == "All") {
                            com.example.viewmodel.globalAuthorsList
                        } else {
                            com.example.viewmodel.globalAuthorsList.filter { it.country == selectedAuthorCountry }
                        }

                        items(filteredAuthors) { author ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { viewModel.selectAuthor(author.name) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                border = CardDefaults.outlinedCardBorder(true).copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = author.avatarUrl,
                                            contentDescription = author.name,
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .background(MaterialTheme.colorScheme.surface),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    author.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    author.countryFlag,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            Text(
                                                "${author.source} · ${author.country}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Specialty badge
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                author.specialty.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    
                                    Spacer(Modifier.height(12.dp))
                                    
                                    Text(
                                        author.bio,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 3
                                    )
                                    
                                    Spacer(Modifier.height(12.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                author.reads,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            "View Written Articles →",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRegionSheet) {
        ModalBottomSheet(onDismissRequest = { 
            showRegionSheet = false 
            selectedCountryForNewspapers = null
        }) {
            if (selectedCountryForNewspapers == null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Region", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                    OutlinedTextField(
                        value = regionSearchQuery,
                        onValueChange = { regionSearchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        placeholder = { Text("Search country...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                    LazyColumn {
                        val filteredRegions = regions.filter { it.contains(regionSearchQuery, ignoreCase = true) }
                        if (filteredRegions.isEmpty()) {
                            item {
                                Text("No countries found", modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        items(filteredRegions) { reg ->
                            Text(
                                text = reg,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedRegion == reg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (reg == "All") {
                                            viewModel.updateRegion(reg, "All")
                                            showRegionSheet = false
                                        } else {
                                            selectedCountryForNewspapers = reg
                                        }
                                    }
                                    .padding(vertical = 12.dp)
                            )
                        }
                        item { Spacer(Modifier.height(32.dp)) }
                    }
                }
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Newspaper in ${selectedCountryForNewspapers}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                    LazyColumn {
                        val newspapers = topNewspapersByCountry[selectedCountryForNewspapers] ?: listOf("All", "${selectedCountryForNewspapers} Times", "The Daily ${selectedCountryForNewspapers}", "${selectedCountryForNewspapers} Post")
                        items(newspapers) { paper ->
                            Text(
                                text = paper,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateRegion(selectedCountryForNewspapers ?: "All", paper)
                                        showRegionSheet = false
                                        selectedCountryForNewspapers = null
                                    }
                                    .padding(vertical = 12.dp)
                            )
                        }
                        item { Spacer(Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }

    if (showSecurityHubDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityHubDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Security & Privacy Hub",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 480.dp)) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        SecurityPrivacyDashboard(viewModel = viewModel)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityHubDialog = false }) {
                    Text("DISMISS", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showPasswordPromptDialog) {
        PasswordPromptDialog(
            onDismiss = { showPasswordPromptDialog = false },
            onSubmit = { pwd ->
                val success = viewModel.loginAsAdmin(pwd)
                if (success) {
                    showPasswordPromptDialog = false
                    showDeveloperDialog = true
                }
            }
        )
    }

    if (showDeveloperDialog) {
        DeveloperSettingsDialog(
            viewModel = viewModel,
            categories = categories.filter { it != "All" },
            onDismiss = { showDeveloperDialog = false }
        )
    }
    }
}

@Composable
fun NewsCard(
    article: NewsArticle,
    onClick: () -> Unit,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit,
    onBlockSource: () -> Unit,
    onFollowSource: () -> Unit,
    isFollowed: Boolean,
    isBlocked: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder(true).copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline))
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    article.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Text("${article.source} • ${article.country}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(article.title, style = MaterialTheme.typography.titleLarge, maxLines = 3, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val timeString = DateUtils.getRelativeTimeSpanString(article.date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    Text(timeString.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Personalize category weighting feedback actions
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onUpvote,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "Upvote", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("More Like This", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onDownvote, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ThumbDown, contentDescription = "Downvote", modifier = Modifier.size(16.dp))
                    }
                }
                
                // Block/Follow source dynamics
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(
                        onClick = onFollowSource,
                        colors = if (isFollowed) {
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            ButtonDefaults.filledTonalButtonColors()
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(if (isFollowed) "Following" else "Follow Source", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onBlockSource, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = "Block Source",
                            modifier = Modifier.size(16.dp),
                            tint = if (isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    categories: List<String>,
    selectedInterests: Set<String>,
    onInterestToggle: (String) -> Unit,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardDefaults.outlinedCardBorder(true).copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Tailor Your Experience",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Select categories to train and personalize your global news feed. You can update these anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(Modifier.height(24.dp))
                
                // Categories grid / wrapped Row
                FlowRow(
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedInterests.contains(cat)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onInterestToggle(cat) },
                            label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Build My Press Feed", fontWeight = FontWeight.Bold)
                }
                
                Spacer(Modifier.height(12.dp))
                
                TextButton(onClick = onComplete) {
                    Text("Skip Interests (Guest Access)", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun SecurityPrivacyDashboard(viewModel: NewsViewModel) {
    val isScreenSecurityEnabled by viewModel.isScreenSecurityEnabled.collectAsState()
    val isIncognitoMode by viewModel.isIncognitoMode.collectAsState()
    val isSecureInAppBrowser by viewModel.isSecureInAppBrowser.collectAsState()
    val isAutoUpdateEnabled by viewModel.isAutoUpdateEnabled.collectAsState()
    val updateVersion by viewModel.updateVersion.collectAsState()
    val isUpdateChecking by viewModel.isUpdateChecking.collectAsState()
    val updateProgress by viewModel.updateProgress.collectAsState()
    val updateLogs by viewModel.updateLogs.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "SECURITY & PRIVACY HUB",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Android Proactive Shield",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Enforcing API 36 (Android 16) sandbox compliance targets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 1: Automated Over-The-Air Updates (Auto Downloads)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder(true)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "AUTOMATED LIVE OTA SERVICES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    "Keep news database definitions and system configurations updated automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                // Current version
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "CURRENT SECURE VERSION",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                updateVersion,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (isUpdateChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "SECURED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Auto Update Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Auto-Download Live Updates",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Periodically queries and updates app datasets securely under Android 16 background threads.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isAutoUpdateEnabled,
                        onCheckedChange = { viewModel.toggleAutoUpdate() }
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (isUpdateChecking) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Downloading & Applying Secure OTA Package...",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${(updateProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { updateProgress },
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Manual trigger button
                Button(
                    onClick = { viewModel.triggerManualUpdateCheck() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdateChecking,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (isUpdateChecking) "DOWNLOADING UPDATE..." else "FORCE SECURE UPDATE DIAGNOSTIC",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Terminal Logs
                if (updateLogs.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "OTA UPDATE TRANSMISSION LOGS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        val scrollState = rememberScrollState()
                        LaunchedEffect(updateLogs.size) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fillMaxWidth()
                        ) {
                            updateLogs.forEach { log ->
                                Text(
                                    text = log,
                                    color = Color(0xFF00FF66),
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    ),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Proactive Privacy Shields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder(true)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "PROACTIVE PRIVACY SHIELDS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Toggle 1: Screen Security (FLAG_SECURE)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Screen Security Protection",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Blocks native screenshots, video recordings, and masks the app's contents in the OS recent screen preview container.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isScreenSecurityEnabled,
                        onCheckedChange = { viewModel.toggleScreenSecurity() }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Toggle 2: Session Incognito
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Session Incognito Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Prevents logging search queries, wipes recent navigation targets from active state buffers, and disables indexing cached images.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isIncognitoMode,
                        onCheckedChange = { viewModel.toggleIncognitoMode() }
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Toggle 3: Encrypted In-App Reader
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Secure In-App Reader Sandbox",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Decrypts and reads original articles inside a safe, un-tracked in-app browser instead of sending external third-party intents.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isSecureInAppBrowser,
                        onCheckedChange = { viewModel.toggleSecureInAppBrowser() }
                    )
                }
            }
        }

        // Section 3: Android Target Architecture Details & System Control
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder(true)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "OS ENVIRONMENT & SYSTEM INTEGRITY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val systemDetails = listOf(
                    "Target SDK Level" to "API 36 / Android 16",
                    "Cleartext Protocols" to "Blocked (SSL/TLS enforced only)",
                    "Data Backups" to "AES-256 Cloud Locked & Decrypted on Device",
                    "Runtime Sandboxing" to "Target Virtual Architecture Sandbox",
                    "Notifications State" to if (hasNotificationPermission) "Active & Safe" else "Blocked"
                )

                systemDetails.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            key,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            value,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.clearSecureSessions() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        "SECURE DATA PURGE (ZEROIZE STATE)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordPromptDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Unlock Developer Portal", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Please enter the Admin Security Code to gain root privilege and edit active preferences/triggers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        isError = false
                    },
                    label = { Text("Developer Password") },
                    singleLine = true,
                    isError = isError,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isError) {
                    Text(
                        "Invalid code. Access denied.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.trim() == "admin" || password.trim() == "1234") {
                        onSubmit(password)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Verify Authorization")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DeveloperSettingsDialog(
    viewModel: NewsViewModel,
    categories: List<String>,
    onDismiss: () -> Unit
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val isServerOffline by viewModel.isServerOffline.collectAsState()
    val consoleLogs by viewModel.developerConsoleLogs.collectAsState()

    // State for Injector form
    var injectTitle by remember { mutableStateOf("") }
    var injectSource by remember { mutableStateOf("") }
    var injectSnippet by remember { mutableStateOf("") }
    var injectCategory by remember { mutableStateOf(categories.firstOrNull() ?: "Technology") }
    var injectIsBreaking by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // State for custom block/follow source addition
    var customSourceInput by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Title Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "🔧 Backend Developer Settings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Admin privileges verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Scrollable Panels
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    
                    // Card 1: Network & Server State Simulator
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "📡 NETWORK / INFRASTRUCTURE SIMULATION",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Simulated Server Offline",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Forces active network fetches and news databases to report empty/offline fallback states instantly.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = isServerOffline,
                                    onCheckedChange = { viewModel.toggleServerOffline() }
                                )
                            }
                        }
                    }

                    // Card 2: Custom Article Direct Injector Command
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "📝 DATABASE INJECTOR CONTROL",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            
                            Text(
                                "Inject mock articles into the global feed with custom configurations to test real-time update listeners.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = injectTitle,
                                onValueChange = { injectTitle = it },
                                label = { Text("Article Title") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = injectSource,
                                    onValueChange = { injectSource = it },
                                    label = { Text("News Source") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = injectCategory,
                                        onValueChange = { },
                                        label = { Text("Category") },
                                        readOnly = true,
                                        trailingIcon = {
                                            IconButton(onClick = { categoryDropdownExpanded = true }) {
                                                Icon(Icons.Default.List, contentDescription = "Select Category")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    DropdownMenu(
                                        expanded = categoryDropdownExpanded,
                                        onDismissRequest = { categoryDropdownExpanded = false }
                                    ) {
                                        categories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat) },
                                                onClick = {
                                                    injectCategory = cat
                                                    categoryDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            OutlinedTextField(
                                value = injectSnippet,
                                onValueChange = { injectSnippet = it },
                                label = { Text("News Snippet Summary") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = injectIsBreaking,
                                        onCheckedChange = { injectIsBreaking = it }
                                    )
                                    Text("Mark as BREAKING News article", style = MaterialTheme.typography.bodyMedium)
                                }
                                Button(
                                    onClick = {
                                        if (injectTitle.isNotBlank() && injectSource.isNotBlank()) {
                                            viewModel.injectCustomArticle(
                                                title = injectTitle,
                                                source = injectSource,
                                                category = injectCategory,
                                                snippet = if (injectSnippet.isBlank()) "No snippet provided." else injectSnippet,
                                                isBreaking = injectIsBreaking
                                            )
                                            injectTitle = ""
                                            injectSource = ""
                                            injectSnippet = ""
                                            injectIsBreaking = false
                                        }
                                    },
                                    enabled = injectTitle.isNotBlank() && injectSource.isNotBlank()
                                ) {
                                    Text("Inject to Feed")
                                }
                            }
                        }
                    }

                    // Card 3: Dynamic Category Weight Tuning
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🎛️ RECOMMENDATION WEIGHT TUNER",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                "Calibrate exact recommendation scoring models for each category. Positive scores promote relevant content; negative scores demote.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            categories.forEach { cat ->
                                val score = prefs.weights[cat] ?: 0
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        cat,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(110.dp)
                                    )
                                    Slider(
                                        value = score.toFloat(),
                                        onValueChange = { viewModel.setCategoryWeight(cat, it.toInt()) },
                                        valueRange = -15f..15f,
                                        steps = 30,
                                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(44.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (score > 0) MaterialTheme.colorScheme.primaryContainer 
                                                else if (score < 0) MaterialTheme.colorScheme.errorContainer
                                                else MaterialTheme.colorScheme.secondaryContainer
                                            )
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (if (score > 0) "+$score" else "$score"),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (score > 0) MaterialTheme.colorScheme.onPrimaryContainer
                                                   else if (score < 0) MaterialTheme.colorScheme.onErrorContainer
                                                   else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card 4: Blacklist & Whitelist Source Controls
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "🛡️ WHITELIST & BLACKLISTED SOURCE ACTIONS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customSourceInput,
                                    onValueChange = { customSourceInput = it },
                                    label = { Text("News Outlet Name") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (customSourceInput.isNotBlank()) {
                                            viewModel.addFollowedSource(customSourceInput.trim())
                                            customSourceInput = ""
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    enabled = customSourceInput.isNotBlank()
                                ) {
                                    Text("Follow")
                                }
                                Button(
                                    onClick = {
                                        if (customSourceInput.isNotBlank()) {
                                            viewModel.addBlockedSource(customSourceInput.trim())
                                            customSourceInput = ""
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    enabled = customSourceInput.isNotBlank()
                                ) {
                                    Text("Block")
                                }
                            }

                            // Followed lists
                            Text("Whitelisted Sources (Followed):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            if (prefs.followedSources.isEmpty()) {
                                Text("No custom followed sources.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            } else {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    prefs.followedSources.forEach { source ->
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                                .clickable { viewModel.toggleFollowSource(source) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                Icons.Default.Close, 
                                                contentDescription = "Remove", 
                                                modifier = Modifier.size(12.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            // Blocked lists
                            Text("Blacklisted Sources (Blocked from entire feed):", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            if (prefs.blockedSources.isEmpty()) {
                                Text("No blocked sources.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            } else {
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    prefs.blockedSources.forEach { source ->
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.errorContainer)
                                                .clickable { viewModel.toggleBlockSource(source) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                            Spacer(Modifier.width(6.dp))
                                            Icon(
                                                Icons.Default.Close, 
                                                contentDescription = "Remove", 
                                                modifier = Modifier.size(12.dp),
                                                tint = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Card 5: Sandbox Purge & Preferences Factory Defaults
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🚨 FACTORY DEFAULTS OVERRIDES",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Hard Purge & Reset Onboarding",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Clears all personal coefficients, interests, locks, blocklists, and restarts the onboard setup flow.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    onClick = { viewModel.resetAllPreferences() }
                                ) {
                                    Text("Zeroize Engine")
                                }
                            }
                        }
                    }

                    // Card 6: Developer Real-time Transaction Console
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "💬 REAL-TIME DIAGNOSTIC MONOSPACE CONSOLE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    "SYSTEM MONITORING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFEB3B)
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 120.dp, max = 220.dp)
                                    .background(Color(0xFF0F0F0F))
                                    .padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    consoleLogs.forEach { log ->
                                        Text(
                                            text = log,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = if (log.contains("WARNING") || log.contains("FAIL")) Color(0xFFFF5722) 
                                                   else if (log.contains("COMMAND")) Color(0xFFFFC107)
                                                   else Color(0xFF00FF66),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Close Button in footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        shape = RoundedCornerShape(12.dp),
                        onClick = onDismiss
                    ) {
                        Text("Close Panel")
                    }
                }
            }
        }
    }
}
