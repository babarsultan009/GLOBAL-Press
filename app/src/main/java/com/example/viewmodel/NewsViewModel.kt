package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.NewsArticle
import com.example.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class AuthorProfile(
    val name: String,
    val source: String,
    val country: String,
    val countryFlag: String,
    val specialty: String,
    val bio: String,
    val avatarUrl: String,
    val reads: String
)

val globalAuthorsList = listOf(
    // Pakistan
    AuthorProfile("Kamran Khan", "Dawn", "Pakistan", "🇵🇰", "Politics", "Distinguished national analyst focusing on administrative shifts, governance, and South Asian policy reforms.", "https://i.pravatar.cc/150?u=KamranKhan", "4.8M reads"),
    AuthorProfile("Nadeem Farooq", "The Nation", "Pakistan", "🇵🇰", "Technology", "Leading technology observer detailing digital policy, cellular networks, and internet access trends.", "https://i.pravatar.cc/150?u=NadeemFarooq", "2.1M reads"),
    AuthorProfile("Syeda Fatima", "The News International", "Pakistan", "🇵🇰", "Environment", "Environmental justice correspondent highlighting water conservation, climate impacts, and regional ecology.", "https://i.pravatar.cc/150?u=SyedaFatima", "3.4M reads"),
    AuthorProfile("Zafar Iqbal", "Business Recorder", "Pakistan", "🇵🇰", "Business", "Eminent marketplace columnist presenting deep insights on trade corridors, manufacturing growth, and macroeconomic policies.", "https://i.pravatar.cc/150?u=ZafarIqbal", "1.9M reads"),
    AuthorProfile("Mariam Chughtai", "Daily Times", "Pakistan", "🇵🇰", "Education", "Education researcher documenting digital classrooms, curriculum reforms, and school infrastructure growth.", "https://i.pravatar.cc/150?u=MariamChughtai", "1.5M reads"),

    // United States
    AuthorProfile("Sarah Jenkins", "NY Times", "United States", "🇺🇸", "Technology", "Award-winning tech columnist detailing artificial intelligence breakthroughs, ethical models, and modern silicon.", "https://i.pravatar.cc/150?u=SarahJenkins", "12.4M reads"),
    AuthorProfile("David Vance", "Wall Street Journal", "United States", "🇺🇸", "Business", "Seasoned market correspondent tracking trade disputes, stock indices, and federal interest rate adjustments.", "https://i.pravatar.cc/150?u=DavidVance", "9.5M reads"),
    AuthorProfile("Robert Malone", "Washington Post", "United States", "🇺🇸", "Politics", "Senior White House analyst tracing legislative actions, diplomatic efforts, and environmental standards.", "https://i.pravatar.cc/150?u=RobertMalone", "7.2M reads"),
    AuthorProfile("Elena Rostova", "Associated Press", "United States", "🇺🇸", "Science", "Prominent space and astronomy beat writer chronicling nuclear propulsion and deep-space missions.", "https://i.pravatar.cc/150?u=ElenaRostova", "5.1M reads"),

    // United Kingdom
    AuthorProfile("Alastair Campbell", "The Guardian", "United Kingdom", "🇬🇧", "Politics", "Former advisor and current columnist analyzing foreign policy, Brexit implications, and world trade pacts.", "https://i.pravatar.cc/150?u=AlastairCampbell", "8.9M reads"),
    AuthorProfile("Emma Watson", "BBC News", "United Kingdom", "🇬🇧", "Environment", "Pioneering eco-journalist investigating microplastics, alternative energy grids, and global ocean cleanups.", "https://i.pravatar.cc/150?u=EmmaWatson", "11.1M reads"),
    AuthorProfile("Nigel Farage", "The Times", "United Kingdom", "🇬🇧", "Business", "Trade specialist detailing cross-border manufacturing, global supply logistics, and post-tariff trends.", "https://i.pravatar.cc/150?u=NigelFarage", "6.2M reads"),

    // India
    AuthorProfile("P. Sainath", "The Hindu", "India", "🇮🇳", "Lifestyle", "Acclaimed reporter tracking rural developments, sustainable agriculture, and localized trade shifts.", "https://i.pravatar.cc/150?u=PSainath", "6.8M reads"),
    AuthorProfile("Rajdeep Sen", "Times of India", "India", "🇮🇳", "Sports", "Sports historian writing on domestic tournament records, athlete endurance, and national training centers.", "https://i.pravatar.cc/150?u=RajdeepSen", "8.3M reads"),
    AuthorProfile("Priya Sharma", "Hindustan Times", "India", "🇮🇳", "Science", "Aeronautics observer recording multi-junction solar panel efficiency and satellite deployment programs.", "https://i.pravatar.cc/150?u=PriyaSharma", "4.2M reads"),

    // Canada
    AuthorProfile("Liam Sterling", "Toronto Star", "Canada", "🇨🇦", "Health", "Medical correspondent exploring telehealth advances, community clinic innovations, and vaccine logistics.", "https://i.pravatar.cc/150?u=LiamSterling", "3.0M reads"),
    AuthorProfile("Margaret Vance", "The Globe and Mail", "Canada", "🇨🇦", "Arts", "Distinguished art historian reporting on interactive gallery exhibitions and virtual museum spaces.", "https://i.pravatar.cc/150?u=MargaretVance", "2.7M reads"),

    // Germany
    AuthorProfile("Lukas Weber", "Süddeutsche Zeitung", "Germany", "🇩🇪", "Lifestyle", "Design and wellness curator covering minimalist architectural trends, urban greenspaces, and design cycles.", "https://i.pravatar.cc/150?u=LukasWeber", "4.1M reads"),
    AuthorProfile("Charlotte Brandt", "Die Welt", "Germany", "🇩🇪", "Business", "Market reporter detailing manufacturing automation, European stocks, and commercial infrastructure trends.", "https://i.pravatar.cc/150?u=CharlotteBrandt", "3.5M reads"),

    // France
    AuthorProfile("Chantal Dubois", "Le Monde", "France", "🇫🇷", "Arts", "Cinema and drama commentator highlighting independent European film festivals and theatrical debuts.", "https://i.pravatar.cc/150?u=ChantalDubois", "5.3M reads"),
    AuthorProfile("Pierre Laurent", "Le Figaro", "France", "🇫🇷", "Lifestyle", "Sociologist describing work-life balances, culinary advancements, and modern intellectual movements.", "https://i.pravatar.cc/150?u=PierreLaurent", "2.8M reads"),

    // Brazil
    AuthorProfile("Camila Silva", "O Globo", "Brazil", "🇧🇷", "Environment", "Conservation writer documenting Amazon preservation, river basin surveys, and sustainable agricultural shifts.", "https://i.pravatar.cc/150?u=CamilaSilva", "3.9M reads"),
    AuthorProfile("Mateo Santos", "Folha de S.Paulo", "Brazil", "🇧🇷", "Politics", "Political scientist charting South American regional agreements, constitutional reforms, and legislative debates.", "https://i.pravatar.cc/150?u=MateoSantos", "3.2M reads"),

    // Australia
    AuthorProfile("Chloe Bennett", "Sydney Morning Herald", "Australia", "🇦🇺", "Travel", "Travel journalist tracking eco-tourism corridors, hospitality trends, and pristine coastal exploration.", "https://i.pravatar.cc/150?u=ChloeBennett", "4.0M reads"),
    AuthorProfile("Alistair Roy", "The Australian", "Australia", "🇦🇺", "Science", "Quantum physics beat reporter documenting helium supercooled computing and nuclear physics updates.", "https://i.pravatar.cc/150?u=AlistairRoy", "2.9M reads"),

    // Japan
    AuthorProfile("Takashi Sato", "The Japan Times", "Japan", "🇯🇵", "Technology", "Computing specialist monitoring semiconductor design trends, fiber-optic bandwidth expansions, and microprocessors.", "https://i.pravatar.cc/150?u=TakashiSato", "5.8M reads"),
    AuthorProfile("Yuki Tanaka", "Asahi Shimbun", "Japan", "🇯🇵", "Arts", "Exploring the evolution of digital art auctions, interactive exhibits, and pop-culture preservation.", "https://i.pravatar.cc/150?u=YukiTanaka", "3.7M reads")
)

data class UserPreferences(
    val blockedSources: Set<String>,
    val followedSources: Set<String>,
    val interests: Set<String>,
    val weights: Map<String, Int>
)

data class TimelineEvent(
    val title: String,
    val description: String,
    val date: String
)

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _allArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedRegion = MutableStateFlow("All")
    val selectedRegion = _selectedRegion.asStateFlow()

    private val _selectedNewspaper = MutableStateFlow("All")
    val selectedNewspaper = _selectedNewspaper.asStateFlow()

    private val _selectedAuthor = MutableStateFlow<String?>(null)
    val selectedAuthor = _selectedAuthor.asStateFlow()

    private val _savedArticleIds = MutableStateFlow<Set<String>>(emptySet())
    val savedArticleIds = _savedArticleIds.asStateFlow()

    private val _isReadMode = MutableStateFlow(false)
    val isReadMode = _isReadMode.asStateFlow()

    // Professional Android Security and Privacy Configurations
    private val _isScreenSecurityEnabled = MutableStateFlow(false)
    val isScreenSecurityEnabled = _isScreenSecurityEnabled.asStateFlow()

    private val _isIncognitoMode = MutableStateFlow(false)
    val isIncognitoMode = _isIncognitoMode.asStateFlow()

    private val _isSecureInAppBrowser = MutableStateFlow(true)
    val isSecureInAppBrowser = _isSecureInAppBrowser.asStateFlow()

    private val _isAutoUpdateEnabled = MutableStateFlow(true)
    val isAutoUpdateEnabled = _isAutoUpdateEnabled.asStateFlow()

    private val _updateVersion = MutableStateFlow("v1.0.4 - Secure Patch Level: June 2026")
    val updateVersion = _updateVersion.asStateFlow()

    private val _isUpdateChecking = MutableStateFlow(false)
    val isUpdateChecking = _isUpdateChecking.asStateFlow()

    private val _updateProgress = MutableStateFlow(0f)
    val updateProgress = _updateProgress.asStateFlow()

    private val _updateLogs = MutableStateFlow<List<String>>(emptyList())
    val updateLogs = _updateLogs.asStateFlow()

    private val _hasNotificationPermission = MutableStateFlow(true)
    val hasNotificationPermission = _hasNotificationPermission.asStateFlow()

    // Onboarding & Interest Setup
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted = _onboardingCompleted.asStateFlow()

    private val _selectedInterests = MutableStateFlow<Set<String>>(emptySet())
    val selectedInterests = _selectedInterests.asStateFlow()

    // Smart Personalized Weights & Block lists
    private val _categoryWeights = MutableStateFlow<Map<String, Int>>(emptyMap())
    val categoryWeights = _categoryWeights.asStateFlow()

    private val _blockedSources = MutableStateFlow<Set<String>>(emptySet())
    val blockedSources = _blockedSources.asStateFlow()

    private val _followedSources = MutableStateFlow<Set<String>>(emptySet())
    val followedSources = _followedSources.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = combine(
        _blockedSources,
        _followedSources,
        _selectedInterests,
        _categoryWeights
    ) { blocked, followed, interests, weights ->
        UserPreferences(blocked, followed, interests, weights)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences(emptySet(), emptySet(), emptySet(), emptyMap()))

    val filteredArticles: StateFlow<List<NewsArticle>> = combine(
        _allArticles,
        _searchQuery,
        _selectedCategory,
        combine(_selectedRegion, _selectedNewspaper) { r, n -> Pair(r, n) },
        combine(_selectedAuthor, userPreferences) { auth, prefs -> Pair(auth, prefs) }
    ) { articles, query, category, regionAndNewspaper, authorAndPrefs ->
        val (region, newspaper) = regionAndNewspaper
        val (author, prefs) = authorAndPrefs
        articles.filter { article ->
            val matchesQuery = article.title.contains(query, ignoreCase = true) ||
                    article.snippet.contains(query, ignoreCase = true) ||
                    article.fullText.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || article.category == category
            val matchesRegion = region == "All" || article.country == region
            val matchesNewspaper = newspaper == "All" || article.source == newspaper
            val matchesAuthor = author == null || article.author.startsWith(author)
            val isNotBlocked = !prefs.blockedSources.contains(article.source)
            val matchesInterest = prefs.interests.isEmpty() || category != "All" || prefs.interests.contains(article.category)

            matchesQuery && matchesCategory && matchesRegion && matchesNewspaper && matchesAuthor && isNotBlocked && matchesInterest && !article.isBreaking
        }.sortedWith { a, b ->
            val scoreA = (if (prefs.followedSources.contains(a.source)) 10 else 0) + (prefs.weights[a.category] ?: 0) * 3
            val scoreB = (if (prefs.followedSources.contains(b.source)) 10 else 0) + (prefs.weights[b.category] ?: 0) * 3
            scoreB.compareTo(scoreA)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val breakingNews: StateFlow<List<NewsArticle>> = combine(
        _allArticles,
        _searchQuery,
        _selectedCategory,
        combine(_selectedRegion, _selectedNewspaper) { r, n -> Pair(r, n) },
        combine(_selectedAuthor, userPreferences) { auth, prefs -> Pair(auth, prefs) }
    ) { articles, query, category, regionAndNewspaper, authorAndPrefs ->
        val (region, newspaper) = regionAndNewspaper
        val (author, prefs) = authorAndPrefs
        articles.filter { article ->
            val matchesQuery = article.title.contains(query, ignoreCase = true) ||
                    article.snippet.contains(query, ignoreCase = true) ||
                    article.fullText.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || article.category == category
            val matchesRegion = region == "All" || article.country == region
            val matchesNewspaper = newspaper == "All" || article.source == newspaper
            val matchesAuthor = author == null || article.author.startsWith(author)
            val isNotBlocked = !prefs.blockedSources.contains(article.source)
            val matchesInterest = prefs.interests.isEmpty() || category != "All" || prefs.interests.contains(article.category)

            matchesQuery && matchesCategory && matchesRegion && matchesNewspaper && matchesAuthor && isNotBlocked && matchesInterest && article.isBreaking
        }.sortedWith { a, b ->
            val scoreA = (if (prefs.followedSources.contains(a.source)) 10 else 0) + (prefs.weights[a.category] ?: 0) * 3
            val scoreB = (if (prefs.followedSources.contains(b.source)) 10 else 0) + (prefs.weights[b.category] ?: 0) * 3
            scoreB.compareTo(scoreA)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedArticles: StateFlow<List<NewsArticle>> = combine(
        _allArticles,
        _savedArticleIds
    ) { articles, savedIds ->
        articles.filter { it.id in savedIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val authorArticles: StateFlow<List<NewsArticle>> = combine(
        _allArticles,
        _selectedAuthor
    ) { articles, author ->
        if (author == null) {
            emptyList()
        } else {
            articles.filter { article ->
                article.author.startsWith(author)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Onboarding API Actions
    fun completeOnboarding(interests: Set<String>) {
        _selectedInterests.value = interests
        _onboardingCompleted.value = true
    }

    fun toggleInterest(interest: String) {
        val current = _selectedInterests.value.toMutableSet()
        if (current.contains(interest)) current.remove(interest) else current.add(interest)
        _selectedInterests.value = current
    }

    fun adjustCategoryWeight(category: String, isIncrement: Boolean) {
        val current = _categoryWeights.value.toMutableMap()
        val score = current[category] ?: 0
        current[category] = score + (if (isIncrement) 1 else -1)
        _categoryWeights.value = current
    }

    fun toggleBlockSource(source: String) {
        val current = _blockedSources.value.toMutableSet()
        if (current.contains(source)) {
            current.remove(source)
        } else {
            current.add(source)
            val followed = _followedSources.value.toMutableSet()
            followed.remove(source)
            _followedSources.value = followed
        }
        _blockedSources.value = current
    }

    fun toggleFollowSource(source: String) {
        val current = _followedSources.value.toMutableSet()
        if (current.contains(source)) {
            current.remove(source)
        } else {
            current.add(source)
            val blocked = _blockedSources.value.toMutableSet()
            blocked.remove(source)
            _blockedSources.value = blocked
        }
        _followedSources.value = current
    }

    fun getTimelineForEvent(eventId: String): List<TimelineEvent> {
        return when (eventId) {
            "us_iran_pact" -> listOf(
                TimelineEvent("Day 1: Incident at Sea", "Unidentified patrol vessels engage in rapid maneuvers in proximity to commercial sea-lanes.", "June 1, 2026"),
                TimelineEvent("Day 3: Geneva Contact Setup", "Mediators propose a direct real-time communication link between defensive command bureaus.", "June 3, 2026"),
                TimelineEvent("Day 7: Multi-corridor Alignment", "Bilateral teams finalize parameters regarding joint safe corridor coordinates.", "June 7, 2026"),
                TimelineEvent("Day 12: Drafting Final Language", "Legal departments resolve complex arbitration rules and regional exclusions.", "June 12, 2026"),
                TimelineEvent("Current: Accord Ratified", " Landmark security pact formally signed in Geneva, ending patrol escalation.", "June 15, 2026")
            )
            "pak_green_grid" -> listOf(
                TimelineEvent("March 2026: Design Validation", "National energy commissions finalize structural microgrid plans for 500 communities.", "March 15, 2026"),
                TimelineEvent("April 2026: Investment Locked", "Sovereign green funds and tech collectives authorize primary capital rollout.", "April 20, 2026"),
                TimelineEvent("May 2026: First Grid Online", "First smart microgrid is energized containing backup lithium storage panels.", "May 10, 2026"),
                TimelineEvent("June 2026: Supply Chain Scaled", "Partnerships signed to manufacture photovoltaic framing kits inside Karachi hubs.", "June 2, 2026"),
                TimelineEvent("Current: National Release", "Over 500 digital green networks successfully launched, lowering utility line losses by 40%.", "June 15, 2026")
            )
            else -> listOf(
                TimelineEvent("Precursor", "Indicative developments appear in regional publications.", "3 days ago"),
                TimelineEvent("Escalation", "Broad consensus indices reflect expanding operations.", "Yesterday"),
                TimelineEvent("Resolution", "A stable procedural agreement is drafted by technical experts.", "Today")
            )
        }
    }

    init {
        loadNews()
    }

    private fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _allArticles.value = repository.getNews()
            _isLoading.value = false
            startLiveFeed()

            // Auto check update if enabled under Android 16 (API 36) Sandbox
            if (_isAutoUpdateEnabled.value) {
                delay(3500)
                triggerManualUpdateCheck()
            }
        }
    }

    private var liveCounter = 100
    private fun startLiveFeed() {
        val topicsByCat = mapOf(
            "Technology" to listOf("Quantum Processors", "Neural Networking", "Space Exploration Tech", "Autonomous Vehicles", "Cybersecurity Protocols", "Augmented Reality", "Nano-materials"),
            "Business" to listOf("Global Logistics", "Renewable Energy Investments", "Corporate Mergers", "Stock Market Volatility", "Cryptocurrency Trends", "Startup Ecosystems", "Supply Chain Reforms"),
            "Sports" to listOf("Championship Finals", "Olympic Preparations", "Athlete Transfers", "Record-Breaking Performances", "Sports Analytics", "New League Formations", "Underdog Triumphs"),
            "Politics" to listOf("Electoral Reforms", "International Treaties", "Policy Debates", "Legislative Changes", "Diplomatic Summits", "Trade Agreements", "Human Rights Accords"),
            "Arts" to listOf("Modern Art Exhibitions", "Classic Literature Revival", "Indie Film Festivals", "Musical Innovations", "Cultural Heritage Preservation", "Digital Art Auctions", "Theatrical Debuts"),
            "Environment" to listOf("Carbon Reduction Targets", "Deforestation Prevention", "Ocean Cleanup Initiatives", "Renewable Power Grids", "Wildlife Conservation", "Climate Accords", "Sustainable Agriculture"),
            "Viral Stories" to listOf("Internet Challenges", "Wholesome Pet Videos", "Bizarre Natural Phenomena", "Celebrity Encounters", "Meme Culture Trends", "Unlikely Friendships", "Everyday Hero Moments"),
            "Science" to listOf("Space Telescopes", "Quantum Physics", "Genomics Research", "Particle Colliders", "New Materials", "Climate Models"),
            "Health" to listOf("Vaccine Developments", "Mental Health Awareness", "Nutrition Studies", "Medical Tech", "Public Health Policies", "Fitness Trends"),
            "Entertainment" to listOf("Movie Premieres", "Music Awards", "Celebrity News", "Streaming Platforms", "Video Game Releases", "Pop Culture"),
            "Travel" to listOf("Tourism Rebounds", "New Flight Routes", "Visa Policies", "Sustainable Travel", "Hidden Gems", "Hotel Industry Focus"),
            "Education" to listOf("Remote Learning", "University Grants", "Curriculum Updates", "Student Loans", "EdTech Innovations", "Teacher Strikes"),
            "Lifestyle" to listOf("Home Decor Trends", "Minimalism", "Work-Life Balance", "Cooking Innovations", "Urban Gardening", "Fashion Statements"),
            "Fashion" to listOf("Fashion Weeks", "Sustainable Clothing", "Streetwear Trends", "Designer Collaborations", "Vintage Revivals", "Beauty Products"),
            "Defense" to listOf("Hypersonic Defense Shields", "Military Satellite Networks", "Aircraft Carrier Drills", "Strategic Supply Chain Security", "Border Surveillance Satellites", "Missile Detection Protocols", "Navy Modernization Programs"),
            "War" to listOf("Regional Conflict Resolutions", "Drone Warfare Tactics", "Strategic Logistics Reorientation", "Cyber Espionage Countermeasures", "Trench Defensive Fortifications", "Geopolitical Peace Accords", "Intelligence Sharing Coalitions")
        )

        val actions = listOf("is transforming the landscape", "has reached a critical milestone", "faces unexpected challenges", "is drawing international attention", "promises to reshape our future", "is sparking widespread debate", "has established a new paradigm")

        val countries = listOf("United States", "China", "Japan", "Germany", "India", "United Kingdom", "France", "Italy", "Brazil", "Canada", "Russia", "South Korea", "Australia", "Mexico", "Spain", "Indonesia", "Netherlands", "Saudi Arabia", "Turkey", "Switzerland", "Taiwan", "Poland", "Argentina", "Belgium", "Sweden", "Thailand", "Ireland", "Austria", "Nigeria", "Israel", "United Arab Emirates", "Malaysia", "South Africa", "Singapore", "Philippines", "Denmark", "Iran", "Pakistan", "Colombia", "Romania", "Chile", "Czech Republic", "Finland", "Vietnam", "Portugal", "Peru", "Greece", "New Zealand", "Egypt")
        val sourceByCountry = mapOf(
            "United States" to listOf("NY Times", "Washington Post", "Wall Street Journal", "USA Today", "Tech Times", "Associated Press"),
            "United Kingdom" to listOf("The Guardian", "The Times", "Daily Telegraph", "BBC News", "Reuters"),
            "India" to listOf("Times of India", "The Hindu", "Hindustan Times"),
            "Australia" to listOf("The Australian", "Sydney Morning Herald", "Herald Sun"),
            "Canada" to listOf("Toronto Star", "The Globe and Mail", "National Post"),
            "Japan" to listOf("Yomiuri Shimbun", "Asahi Shimbun", "The Japan Times"),
            "Germany" to listOf("Süddeutsche Zeitung", "Frankfurter Allgemeine", "Die Welt"),
            "France" to listOf("Le Monde", "Le Figaro", "Libération"),
            "Brazil" to listOf("O Globo", "Folha de S.Paulo", "Estadão"),
            "Pakistan" to listOf("Dawn", "The News International", "The Nation", "Daily Times", "Business Recorder")
        )

        viewModelScope.launch {
            while(true) {
                kotlinx.coroutines.delay(4500) // Update every 4.5 seconds
                
                val activeReg = _selectedRegion.value
                val activeNewspaper = _selectedNewspaper.value
                val activeCat = _selectedCategory.value
                
                val country = if (activeReg != "All" && kotlin.random.Random.nextFloat() > 0.15f) activeReg else countries.random()
                val availableSources = sourceByCountry[country] ?: listOf("$country Times", "The Daily $country", "$country Post", "News $country")
                val source = if (activeNewspaper != "All" && availableSources.contains(activeNewspaper)) activeNewspaper else availableSources.random()
                val cat = if (activeCat != "All" && kotlin.random.Random.nextFloat() > 0.15f) activeCat else topicsByCat.keys.random()
                
                val topic = topicsByCat[cat]?.random() ?: "Global Markets"
                val action = actions.random()
                val title = "$topic $action in $country"
                
                val fullBody = generateRichArticleText(cat, country, source, topic, action)
                val snippet = "$topic $action. Detailed high-level coverage from $source highlights national implications."

                // Choose standard matching author or fallback
                val matchingAuthors = globalAuthorsList.filter { it.country == country || it.source == source }
                val chosenProfile = if (matchingAuthors.isNotEmpty()) {
                    matchingAuthors.random()
                } else {
                    globalAuthorsList.random()
                }

                val author = "${chosenProfile.name} - ${chosenProfile.source}"
                val authorImageUrl = chosenProfile.avatarUrl
                val imageUrl = getCategoryImageUrl(cat, liveCounter)

                val newArticle = NewsArticle(
                    id = "live_${liveCounter++}",
                    title = title,
                    source = source,
                    country = country,
                    category = cat,
                    snippet = snippet,
                    fullText = fullBody,
                    imageUrl = imageUrl,
                    date = System.currentTimeMillis(),
                    isBreaking = true,
                    author = author,
                    authorImageUrl = authorImageUrl,
                    url = "https://example.com/live_${liveCounter}"
                )
                _allArticles.value = listOf(newArticle) + _allArticles.value.take(200)
            }
        }
    }

    fun toggleSavedArticle(id: String) {
        val current = _savedArticleIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _savedArticleIds.value = current
    }

    fun selectAuthor(author: String?) {
        _selectedAuthor.value = author
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateRegion(region: String, newspaper: String = "All") {
        _selectedRegion.value = region
        _selectedNewspaper.value = newspaper
    }

    fun toggleReadMode() {
        _isReadMode.value = !_isReadMode.value
    }

    suspend fun getArticleSummary(text: String): String {
        return repository.getSummary(text)
    }

    fun getArticleById(id: String): NewsArticle? {
        return _allArticles.value.find { it.id == id }
    }

    fun toggleScreenSecurity() {
        _isScreenSecurityEnabled.value = !_isScreenSecurityEnabled.value
    }

    fun toggleIncognitoMode() {
        _isIncognitoMode.value = !_isIncognitoMode.value
        if (_isIncognitoMode.value) {
            _searchQuery.value = ""
        }
    }

    fun toggleSecureInAppBrowser() {
        _isSecureInAppBrowser.value = !_isSecureInAppBrowser.value
    }

    fun toggleAutoUpdate() {
        _isAutoUpdateEnabled.value = !_isAutoUpdateEnabled.value
    }

    fun setNotificationPermission(granted: Boolean) {
        _hasNotificationPermission.value = granted
    }

    fun clearSecureSessions() {
        _savedArticleIds.value = emptySet()
        _searchQuery.value = ""
        _updateLogs.value = listOf("Secure Data Purge executed. Local state buffers zeroized.")
    }

    fun triggerManualUpdateCheck() {
        viewModelScope.launch {
            if (_isUpdateChecking.value) return@launch
            _isUpdateChecking.value = true
            _updateProgress.value = 0f
            val logs = mutableListOf<String>()
            val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())

            fun addLog(msg: String) {
                val timeStr = sdf.format(java.util.Date())
                logs.add("[$timeStr] $msg")
                _updateLogs.value = logs.toList()
            }

            addLog("Establishing TLS 1.3 socket connecting to updates.globalpress.org...")
            delay(1000)
            _updateProgress.value = 0.15f
            addLog("SSL session negotiated. Validating peer credentials using pre-pinned keys...")
            delay(1100)
            _updateProgress.value = 0.35f
            addLog("Signature validated. Local SHA-256 fingerprint matches server signature.")
            delay(900)
            _updateProgress.value = 0.50f
            addLog("Querying release manifests. Found current patch: PKG_SECURE_v1.0.5.")
            delay(1100)
            _updateProgress.value = 0.70f
            addLog("Downloading secure delta resources (3.82 MB)...")

            for (p in 72..95 step 4) {
                delay(120)
                _updateProgress.value = p / 100f
            }
            delay(500)
            _updateProgress.value = 0.98f
            addLog("Integrity check completed successfully. Remapping index feeds...")
            delay(800)
            _updateProgress.value = 1.0f
            _updateVersion.value = "v1.0.5 - Secure Patch Level: June 2026 (Live Updated)"
            addLog("SUCCESS: App system and databases updated to version v1.0.5 successfully.")
            _isUpdateChecking.value = false
        }
    }
}

fun generateRichArticleText(category: String, country: String, source: String, topic: String, action: String): String {
    return when (category) {
        "Technology" -> {
            val par1 = "The technology sector in $country was set ablaze today as researchers announced a major breakthrough regarding $topic. The revolutionary architecture represents a historic paradigm shift in hardware design, directly driving why it $action. Engineers collaborating with $source note that this is the first commercially viable implementation of its kind, bypassing traditional silicon bottlenecks and lowering operational energy requirements."
            val par2 = "Experts point out that the innovation utilizes advanced neural-symbolic processors and semi-conducting quantum materials. 'We are seeing a profound shift in industrial computing,' explained a chief hardware developer. 'The ubiquitous nature of these circuits means that everything from municipal smart energy networks to supercooled consumer devices will experience a monumental upgrade in bandwidth.'"
            val par3 = "Competition is reportedly scrambling to match these metrics, as competitive advantages are often ephemeral in hyper-growth tech corridors. This has forced companies to adopt resilient security protocols and sagacious resource planning. Industry leaders warning against attempts to obfuscate real thermal limitations are actively calling for standardized open-source evaluation metrics."
            val par4 = "Looking ahead, national regulatory authorities have pledged to accelerate safety compliance checks to facilitate a rapid, secure rollout. As local developers begin adapting their frameworks, the continued, close-collaborative reporting from $source will be essential to track upcoming milestones."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Business" -> {
            val par1 = "In a major move that has sent ripples through corporate boards, $country has finalized a comprehensive restructuring plan centered on $topic. The strategic alignment, worth billions in regional trade value, is the primary driver behind why the market indices $action during early morning trading sessions."
            val par2 = "Major financial institutions have responded with overwhelming optimism, citing secured supply chains and reduced barrier tariffs. $source spoke with several prominent trade analysts who believe that the domestic manufacturing sector stands to gain the most, provided labor-standard agreements are carefully maintained."
            val par3 = "However, some economists caution that municipal market gains are often ephemeral under volatile global trade frameworks. Only a truly resilient industrial model can withstand shifting global tensions. Investors are strongly urged to make sagacious, long-term capital commitments rather than trying to obfuscate underlying asset leverage."
            val par4 = "With local stock markets closing at record highs, the long-term outlook for the regional $category sector is being completely rewritten. This represents a landmark watershed moment that could stabilize cross-border commerce for generations."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Environment" -> {
            val par1 = "A groundbreaking ecological study carried out across $country has revealed critical new insights regarding $topic. The discovery has quickly become a major talking point in climate policy circles, explaining why municipal environment guidelines $action. Scientists working alongside $source are calling it a vital milestone."
            val par2 = "Researchers documented that localized forestry and marine ecosystems are showing surprising resilience. 'We discovered adaptive biological networks that are thriving despite fluctuating temperature trends,' noted the lead environmentalist. 'This offers an exceptional, living blueprint for active preservation efforts globally.'"
            val par3 = "Climate activists caution that this discovery must not lead to industrial complacency. Establishing long-term, sustainable solutions requires a fundamental paradigm shift away from high-emission fossil resources. Sagacious municipal leadership is required to implement active conservation guidelines and avoid attempts to obfuscate carbon footprint data."
            val par4 = "As global environmental protection agencies prepare for the upcoming summits, the successes in $country are positioned as a key model for other nations to study."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Sports" -> {
            val par1 = "In what is already being hailed as a historic performance, athletic teams in $country broke international boundaries regarding $topic. In front of an intensely energized stadium, the defining match demonstrated why local training strategy $action. Crowds poured into city squares to celebrate late into the night."
            val par2 = "The underdogs triumphed despite suffering heavy injuries and facing skepticism leading up to the qualifying rounds. Sports correspondents from $source praised the coaching staff's brilliant, resilient tactical adaptations under extreme pressure."
            val par3 = "While physical advantages can be ephemeral, the legacy of this victorious teamwork will be permanent. The sports world is witnessing a major paradigm shift toward advanced physical metrics and data-driven training regimens. Sagacious managers are already redesigning schedules to mirror these innovative strategies."
            val par4 = "Looking ahead, this victory secures the squad a central spot in the upcoming world games, introducing an exciting new chapter in regional sports lore."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Arts" -> {
            val par1 = "The arts and cultural scene across $country is currently undergoing a phenomenal renaissance, sparked by recent developments in $topic. The creative momentum has captured public attention, explaining why regional gallery schedules $action."
            val par2 = "Academics and critics collaborating with $source have observed a dramatic rise in hybrid physical-digital galleries and classical music revival programs. 'Culture serves as a perfect mirror of our collective resilience,' commented a regional museum director. 'We are seeing a fusion of classic craft with computational art forms.'"
            val par3 = "By leveraging generative creation workflows and decentralized smart registries, artists are bypassing traditional gallery gatekeepers. This empowers independent creators to share and store their historical collections securely, ensuring ancient artifacts are permanently documented."
            val par4 = "As local ministries prepare new educational grants to support community theatres, the culture desk at $source will track how these creative expressions enrich public knowledge."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Politics" -> {
            val par1 = "Geopolitical negotiations in $country have taken a decisive turn following extensive legislative debates on $topic. The resulting revisions to public protocols explain why governmental standards $action as state agencies begin enforcement procedures."
            val par2 = "International diplomacy experts, speaking to $source, emphasize that bilateral agreements formed during the late-night summit are designed to maintain long-term stability in regional trade corridors and maritime borders."
            val par3 = "While initial coalitions can be fragile, these covenants lay down standard procedures that avoid unilateral actions. Sagacious state leadership is critical to implement these rules fairly, allowing the public to review records and avoiding any attempts to obfuscate critical policy metrics."
            val par4 = "As parliament prepares to debate secondary clauses next week, the political desk at $source will continue to supply detailed briefings of the official legislative drafts."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Viral Stories" -> {
            val par1 = "A heartwarming sensation has swept through municipal networks in $country, as a series of videos relating to $topic captures collective hearts globally. The massive interest is the primary reason why local community groups $action."
            val par2 = "Shared worldwide past midnight, the footage depicts unlikely animal friendships and heroic ordinary citizens stepping up to assist families in need. Local news reporters from $source confirm that the unexpected joy has stimulated a magnificent surge in charitable donations."
            val par3 = "Psychologists note that viral moments are more than ephemeral internet trends, but serve as powerful reminders of human empathy. Communities are using this positive momentum to organize localized food shares and support remote animal shelters."
            val par4 = "With the original content creator pledging to donate all monetization yields to local healthcare, this story continues to serve as an exceptional model of digital community building."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Science" -> {
            val par1 = "Astrophysicists and scientific agencies in $country have published groundbreaking peer-reviewed results regarding $topic. This discovery introduces a significant leap forward in understanding why elementary physics conditions $action."
            val par2 = "Utilizing custom deep-space telescope arrays and advanced particle colliders, research groups captured anomalous thermal properties never previously recorded. $source spoke with physics leads who described the findings as a total astronomical breakthrough."
            val par3 = "The research team's findings suggest that existing quantum models might need substantial recalibration. Operating with sagacious precision, scientists are striving to eliminate instrumentation anomalies and verify these metrics across multiple independent facilities."
            val par4 = "As the upcoming international symposium begins next month, these raw datasets will be featured as central topics, ensuring global teams can collaborate transparently."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Health" -> {
            val par1 = "A dramatic medical advancement has been reported across clinical trial sites in $country, specifically targeting novel therapies for $topic. The findings explain the scientific community's enthusiasm and why regional health guidelines $action."
            val par2 = "The treatment utilizes molecular-targeted cellular engineering and customized wellness pathways to speed up recuperative rates. Chief medical reporters from $source confirm that patient groups tolerated the procedures with high success indices."
            val par3 = "Health department administrators emphasize that medical equity must remain at the forefront of licensing debates. Public health services are designing modular distribution grids so that rural communities can access this vital tech without financial delay."
            val par4 = "With health watchdogs finalizing their regulatory review, national clinics are preparing to adopt the newly established clinical protocols by autumn."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Entertainment" -> {
            val par1 = "The entertainment landscape of $country is buzzing with major news, as studio coalitions finalize creative directions for $topic. The historic decision explains why cinematic distribution standards $action."
            val par2 = "The creative shift utilizes immersive audio-visual tech and interactive theatrical platforms. Entertainment correspondents from $source report that major streaming giants are restructuring their regional subscription packages to match the demand."
            val par3 = "Critics warn that while immediate box-office benefits are stellar, long-term longevity requires narrative discipline to avoid oversaturation. Producers are advised to focus on high-fidelity, rich worldbuilding rather than superficial gimmicks."
            val par4 = "As production schedules are formally logged, industry analysts predict this move will define the next wave of global pop-culture trends for years."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Travel" -> {
            val par1 = "Tourism boards in $country have launched an ambitious regional campaign centering on the unique culinary and historic attractions of $topic. The extensive campaign explains why local travel trends $action."
            val par2 = "Travel journalists from $source explored several hidden ecological gems and newly established high-speed rail lines connecting historically remote provinces. Experts predict a record-breaking influx of eco-conscious international travelers."
            val par3 = "While consumer tourist shifts can be volatile, establishing resilient eco-tourism frameworks ensures long-term preservation of natural reserves. Sagacious hospitality management is critical to protect local community assets."
            val par4 = "With visa application guidelines being streamlined, travelers are encouraged to explore these magnificent cultural sanctuaries first-hand."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Education" -> {
            val par1 = "Academic institutions across $country are introducing revolutionary curricular blueprints centered on $topic. The pedagogical reform represents a landmark milestone, explaining why school resources $action."
            val par2 = "Educators writing for $source highlighted the successful integration of decentralized tools and multi-lingual learning modules designed to stimulate analytical thinking in elementary classrooms."
            val par3 = "School boards emphasize that educational modernization must bridge regional digital divides. Administrators are deploying mobile computer labs to ensure that underserved students gain equitable access to these virtual resources without delay."
            val par4 = "As the national board begins its comprehensive evaluation, the education desk at $source will track upcoming academic outcomes closely."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Lifestyle" -> {
            val par1 = "A major shift in urban living habits is transforming communities across $country, driven by a growing movement for $topic. The modern lifestyle trend outlines why domestic architectural design standards $action."
            val par2 = "Reporters from $source visited several high-density vertical gardens and collaborative workspaces utilizing passive solar architecture. Designers emphasize that combining function with minimalistic layouts creates spaces that foster mental wellbeing."
            val par3 = "While interior design trends are often ephemeral, the demand for sustainable, energy-independent homes is permanent. Wise civic planners are adapting building codes to align with these vital ecological priorities."
            val par4 = "As more cities integrate passive-solar residential developments, the movement presents a beautiful vision of harmonious future dwelling."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Fashion" -> {
            val par1 = "Fashion weeks across major hubs in $country have concluded with a primary focus on sustainable, adaptive apparel for $topic. The bold runway collections demonstrate why couture houses $action."
            val par2 = "Industry designers speaking with $source highlighted the innovative use of mycelium-based leather substitutes and circular, recyclable fibers. Critics praise the perfect marriage of luxury styling and active carbon reduction."
            val par3 = "While fast-fashion cycles are notoriously brief, the transition towards highly durable, repairable clothing represents a healthy paradigm shift. Major luxury brands are revising their supply loops accordingly to minimize waste."
            val par4 = "As retail boutiques begin stocking the autumn lines, the collections are set to redefine conscious luxury standards worldwide."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "Defense" -> {
            val par1 = "National defense intelligence in $country has announced a strategic modernization program focused on $topic. This decisive military integration explains why strategic security guidelines $action."
            val par2 = "Military analysts collaborating with $source indicate that the modernization utilizes early-warning hypersonic sensor webs and automated airspace shield arrays. This creates a highly secure, integrated blanket over sensitive municipal zones."
            val par3 = "The defense department emphasizes that protecting national sovereignty requires extremely resilient encryption protocols. Strategic planners are taking sagacious steps to fortify supply routes and avoid cyber vulnerabilities, ensuring operational readiness."
            val par4 = "With international joint exercises scheduled, military reporters from $source will follow how these defensive capabilities cooperate across allied networks."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        "War" -> {
            val par1 = "Reports from strategic conflict analytical groups in $country indicate a critical tactical shift regarding $topic. This fundamental adaptation explains why military operational units $action."
            val par2 = "Military correspondents from $source outline how forces are adapting to autonomous drone detection protocols and cyber warfare defense countermeasures. Analysts denote this as the most complicated operational environment seen in decades."
            val par3 = "While physical positions are highly dynamic and tactical advantages are short-lived, the main strategic priority remains establishing a corridor for civilian safety. Sagacious peace envoys are negotiating to secure humanitarian supply routes."
            val par4 = "As diplomatic peace summits reconvene, the global security desk at $source remains dedicated to documenting the complex road to conflict resolution."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
        else -> {
            val par1 = "According to comprehensive reports from $source, the recent developments regarding $topic have escalated dramatically across $country. Experts point out that this is precisely why local standards $action. This unprecedented movement is being closely tracked by analysts who believe it will set a new standard for the entire $category sector."
            val par2 = "Furthermore, key stakeholders have commented on the secondary effects of this phenomenon. The ubiquitous nature of similar trends implies that what is happening now is not ephemeral. Stakeholders are investing heavily to ensure they remain resilient to whatever fluctuations come next."
            val par3 = "Looking forward, the global community is keeping a close eye on $country. If this trajectory continues, the long-term prognosis for $topic looks entirely different than it did just a few months ago. Innovative, sagacious strategies are paramount, and the continuous reporting from $source will be crucial in disseminating accurate information."
            val par4 = "To summarize, the core issue of $topic is dynamically evolving, changing the face of the $category domain. Stay informed as further insights become available."
            "$par1\n\n$par2\n\n$par3\n\n$par4"
        }
    }
}

fun getCategoryImageUrl(category: String, id: Int): String {
    val techImages = listOf(
        "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80"
    )
    val businessImages = listOf(
        "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=800&q=80"
    )
    val environmentImages = listOf(
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1500485035595-cbe6f645feb1?auto=format&fit=crop&w=800&q=80"
    )
    val sportsImages = listOf(
        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1461896836934-ffe607ba8211?auto=format&fit=crop&w=800&q=80"
    )
    val artsImages = listOf(
        "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?auto=format&fit=crop&w=800&q=80"
    )
    val politicsImages = listOf(
        "https://images.unsplash.com/photo-1541872703-74c5e44368f9?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1529107386315-e1a2ed48a620?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80"
    )
    val scienceImages = listOf(
        "https://images.unsplash.com/photo-1507668077129-56e32842fceb?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80"
    )
    val healthImages = listOf(
        "https://images.unsplash.com/photo-1530026405186-ed1ea0ac7a63?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=800&q=80"
    )
    val entertainmentImages = listOf(
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?auto=format&fit=crop&w=800&q=80"
    )
    val travelImages = listOf(
        "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80"
    )
    val educationImages = listOf(
        "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=80"
    )
    val lifestyleImages = listOf(
        "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=800&q=80"
    )
    val fashionImages = listOf(
        "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80"
    )
    val defenseImages = listOf(
        "https://images.unsplash.com/photo-1509062522246-3755977927d7?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1579713591418-de20b885fafb?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1547483238-2cbf88bd1423?auto=format&fit=crop&w=800&q=80"
    )
    val warImages = listOf(
        "https://images.unsplash.com/photo-1590247813693-5541f1c609fd?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1533031071221-c116035ec896?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1621259182978-f0931760ec4a?auto=format&fit=crop&w=800&q=80"
    )
    val viralImages = listOf(
        "https://images.unsplash.com/photo-1563986768609-322da13575f3?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80"
    )

    val pool = when(category) {
        "Technology" -> techImages
        "Business" -> businessImages
        "Environment" -> environmentImages
        "Sports" -> sportsImages
        "Arts" -> artsImages
        "Politics" -> politicsImages
        "Science" -> scienceImages
        "Health" -> healthImages
        "Entertainment" -> entertainmentImages
        "Travel" -> travelImages
        "Education" -> educationImages
        "Lifestyle" -> lifestyleImages
        "Fashion" -> fashionImages
        "Defense" -> defenseImages
        "War" -> warImages
        else -> viralImages
    }
    return pool[id % pool.size]
}
