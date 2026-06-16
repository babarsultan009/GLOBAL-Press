package com.example.repository

import com.example.BuildConfig
import com.example.model.Content
import com.example.model.GenerateContentRequest
import com.example.model.NewsArticle
import com.example.model.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class NewsRepository {
    suspend fun getNews(): List<NewsArticle> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fakeArticles
        }

        val prompt = """
            Generate exactly 8 realistic current affairs news articles from around the world.
            Include:
            1. At least one article on the US-Iran diplomatic pact/deal ensuring de-escalation of maritime patrols and maritime security in Geneva.
            2. At least one article on Pakistan's current affair situations, such as strategic technology investments or green grid developments.
            3. Detailed current affairs from various world contexts (e.g. European markets, sustainable energy in Asia, chip coalitions).

            Format: You must respond ONLY with a raw JSON array of objects representing the articles. Do not wrap in markdown blocks like ```json or similar.
            Each object must contain these fields:
            - id: a unique identifier string (e.g. "g1", "g2")
            - title: comprehensive headline
            - source: a reliable news outlet name (e.g. "Reuters", "Dawn", "Le Monde", "Bloomberg")
            - country: string (e.g. "Pakistan", "United States", "France")
            - category: one of [Technology, Business, Environment, Sports, Arts, Politics, Defense, War, Science, Health, Entertainment, Travel, Education, Lifestyle, Fashion]
            - snippet: brief summary sentence
            - fullText: extensive multiple-paragraph coverage (at least 150 words)
            - imageUrl: a high-quality relevant Unsplash photo URL
            - date: long timestamp (e.g., current time in millisecond epoch)
            - isBreaking: boolean (true for high impact breaking stories like US-Iran pact)
            - author: name with newspaper affiliation (e.g. "Arshad Chaudhry - Dawn")
            - authorImageUrl: "https://i.pravatar.cc/150?u=some_unique_string"
            - url: "https://example.com"
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt)
                    )
                )
            )
        )

        try {
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            var jsonString = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonString != null) {
                jsonString = jsonString.trim()
                if (jsonString.startsWith("```")) {
                    jsonString = jsonString.substringAfter("```json").substringAfter("```")
                    if (jsonString.endsWith("```")) {
                        jsonString = jsonString.substringBeforeLast("```")
                    }
                    jsonString = jsonString.trim()
                }

                val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, NewsArticle::class.java)
                val adapter = RetrofitClient.moshi.adapter<List<NewsArticle>>(type)
                val parsedList = adapter.fromJson(jsonString)
                if (!parsedList.isNullOrEmpty()) {
                    return@withContext parsedList
                }
            }
            fakeArticles
        } catch (e: Exception) {
            e.printStackTrace()
            fakeArticles
        }
    }

    suspend fun getSummary(articleText: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Summary unavailable: No valid Gemini API Key configured in Secrets panel."
        }
        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Summarize the following news article simply and concisely for an average reader. Article: $articleText")
                    )
                )
            )
        )
        try {
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Could not generate summary."
        } catch (e: Exception) {
            "Error generating summary: ${e.message}"
        }
    }

    private val fakeArticles = listOf(
        NewsArticle(
            id = "us_iran_pact",
            title = "Diplomatic History: US and Iran Formalize Landmark Sea-Lane Security Pact",
            source = "Reuters",
            country = "United States",
            category = "Politics",
            snippet = "A comprehensive bilateral agreement has been signed between Washington and Tehran, ensuring open maritime commerce and de-escalation of maritime patrols.",
            fullText = "In an unexpected and highly historic development, diplomatic representatives from the United States and Iran have signed a landmark maritime security pact in Geneva today. The bilateral agreement, titled the 'Sustained Maritime Corridor & Safety Framework,' aims to establish secure, cooperative pathways for commercial shipping across busy trade channels. It marks the first formal bilateral pact of this magnitude between the two nations in decades.\n\nAccording to joint communiqués, both parties have pledged to coordinate on safe passage guarantees, reduce localized defensive naval exercises, and establish a hot-line communications network to resolve potential high-frequency escalations instantaneously. Strategic corridors like the Strait of Hormuz will undergo standardized regulatory de-escalation check-ins to alleviate long-standing tensions that have burdened international supply logistics.\n\nInternational analysts have received the announcement with cautious optimism. 'This is a dramatic recalibration of Middle Eastern geopolitical realities,' explained a senior fellow in security studies. 'While deep-seated ideological differences persist, the shared interest of both Washington and Tehran in maintaining uninterrupted trade routes has established a resilient, pragmatic baseline for peaceful cooperation.' As part of the pact, several maritime sanctions will undergo a phased rollback, representing a major victory for global business hubs.",
            imageUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 30,
            isBreaking = true,
            author = "David Ignatius - Washington Post",
            authorImageUrl = "https://i.pravatar.cc/150?u=DavidIgnatius"
        ),
        NewsArticle(
            id = "pak_green_grid",
            title = "Pakistan's Tech Initiative: Green Solar Microgrids Launched Nationwide",
            source = "Dawn",
            country = "Pakistan",
            category = "Environment",
            snippet = "Islamabad unveils a massive green grid modernization project alongside foreign investors, bringing robust renewable energy to remote regions.",
            fullText = "In a sweeping effort to modernize the national power grid, the government of Pakistan has officially launched the 'National Green Microgrid Initiative' (NGMI). This ambitious infrastructure project aims to install over 500 solar-powered digital microgrids across municipal and rural districts within the fiscal year. Partnering with major international green technology coalitions, the project represents a major step toward resolving energy supply challenges.\n\nThe initialization ceremony in Islamabad was attended by diplomatic representatives and global green-tech investors. The program utilizes highly advanced lithium-iron-phosphate battery storage arrays paired with smart grid technologies to lower overall transmission losses by up to 40%. Prime Minister announced that this initiative will secure resilient power access for millions of households and small businesses.\n\nAnalysts emphasize that this project is a critical turning point for the domestic economy. By reducing reliance on imported fuels, Pakistan stands to stabilize its foreign exchange reserves while creating thousands of specialized technical jobs. Local manufacturing facilities are also being established to manufacture photovoltaic brackets and components, ensuring sustainable industrial growth in the long run.",
            imageUrl = "https://images.unsplash.com/photo-1509391366360-2e959784a276?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 45,
            isBreaking = true,
            author = "Parvez Hoodbhoy - Dawn",
            authorImageUrl = "https://i.pravatar.cc/150?u=ParvezHoodbhoy"
        ),
        NewsArticle(
            id = "pak_india_trade",
            title = "Bilateral Progress: Pakistan and India Agree to Facilitate Agricultural Trade Corridor",
            source = "The Hindu",
            country = "Pakistan",
            category = "Politics",
            snippet = "In a positive turn of regional diplomacy, Pakistan and India have agreed to reopen specific border checkposts for critical agricultural trade.",
            fullText = "In a positive turn of regional diplomacy, Pakistan and India have agreed to reopen specific border checkposts for critical agricultural trade. The milestone agreement, negotiated through quiet channels, aims to address seasonal agricultural shortages on both sides of the border and de-escalate cross-border trade friction.\n\nUnder the new accord, designated agricultural produce and livestock can pass through direct trade gates under streamlined biosecurity protocols. The trade de-escalation is expected to lower primary food price inflation indexes significantly in regional border markets.\n\nWhile larger political discussions remain slow, key corporate chambers on both sides celebrated the news, stressing that standard economic cooperation is a highly effective, resilient driver of long-term regional stability.",
            imageUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 4,
            isBreaking = false,
            author = "Rajdeep Sen - Times of India",
            authorImageUrl = "https://i.pravatar.cc/150?u=RajdeepSen"
        ),
        NewsArticle(
            id = "1",
            title = "Global Tech Summit Unveils Next Gen AI Solutions",
            source = "NY Times",
            country = "United States",
            category = "Technology",
            snippet = "An op-ed: The upcoming Global Tech Summit promises exciting new advancements in structural neural symbolic AI.",
            fullText = "The upcoming Global Tech Summit promises exciting new advancements in structural neural symbolic AI and quantum-grade computing. Leading international experts have announced a major, industry-wide shift towards models that consume less operational energy while providing a tenfold increase in computing capabilities.\n\nThis landmark event will also mark a clear turning point for standard machine learning architectures, as hardware companies transition to hardware-accelerated neural symbolic reasoning. Panel discussions focused extensively on ethical AI guidelines and the global imperative to standardize evaluation metrics across borders. The ubiquitous nature of these discussions highlighted the ephemeral lifespan of current-generation silicon architectures.\n\nIndustry leaders from major multinational corporations laid out a unified development roadmap for the next decade. The implications for everyday consumers are massive, ranging from highly personalized medical diagnostics to fully autonomous smart city grids. Furthermore, open-source communities are expected to play an increasingly critical role in democratizing these high-end tools, ensuring that equitable access is prioritized alongside technological growth. This paradigm shift will require sagacious decisions from international regulators to obfuscate potential systemic hazards.",
            imageUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
            isBreaking = false,
            author = "Sarah Jenkins - NY Times",
            authorImageUrl = "https://i.pravatar.cc/150?u=SarahJenkins"
        ),
        NewsArticle(
            id = "2",
            title = "Trending: Mysterious Monolith Discovered in the French Alps",
            source = "Viral Trends",
            country = "France",
            category = "Viral Stories",
            snippet = "A shiny metallic monolith has been spotted by hikers in the French Alps, triggering global curiosity.",
            fullText = "A shiny metallic monolith has been spotted by hikers in the French Alps, sparking massive viral speculation online. Similar to the public interest events of 2020, the structures appear to have been planted overnight without leaving any physical trace of heavy agricultural machinery or transport trucks.\n\nTourists and internet sleuths are already flocking to the region, creating a sudden and positive surge in the local economy. Authorities are currently investigating the physical origins of the monolith to ensure it does not disrupt local mountain wildlife or pose a hazard to hikers trek pathways. Meanwhile, social media platforms are completely flooded with memes and conspiracy theories ranging from avant-garde art installations to extraterrestrial visitors.\n\nThe global debate has transcended the internet, becoming a major talking point on daytime television across Europe. Local officials are urging extreme caution for those attempting the dangerous trek to view it in person. The loquacious presenters on television networks seem unable to obfuscate their fascination with this seemingly ubiquitous phenomenon. Whether it is an ephemeral promotional stunt or a resilient piece of modern art remains to be seen.",
            imageUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 15,
            isBreaking = true,
            author = "Chantal Dubois - Le Monde",
            authorImageUrl = "https://i.pravatar.cc/150?u=ChantalDubois"
        ),
        NewsArticle(
            id = "3",
            title = "European Markets Rally After Landmark Trade Deal",
            source = "The Times",
            country = "United Kingdom",
            category = "Business",
            snippet = "Editorial: Stocks across major European indices saw their highest jump this quarter as trade tensions eased.",
            fullText = "Stocks across major European indices saw their highest jump this quarter as trade tensions eased following a comprehensive new agreement. Investors are highly optimistic about cross-border commerce stabilizing and shipping delays easing. Financial analysts predict this positive trend will carry into the next fiscal year, providing much-needed relief to manufacturing and transport sectors.\n\nSeveral prominent trade unions have expressed their support, citing protective provisions that support domestic workers while still encouraging international collaboration. The ripple effects are already being felt in Asian markets, where tech and manufacturing stocks closed at record highs this afternoon. Economists warn, however, that sustaining this momentum requires immediate legislative action regarding upcoming tariff revisions. This new paradigm of cooperation seeks to alleviate long-standing maritime tensions, but only a truly resilient economy can overcome the ubiquitous trade challenges ahead.",
            imageUrl = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 5,
            isBreaking = false,
            author = "Nigel Farage - The Times",
            authorImageUrl = "https://i.pravatar.cc/150?u=NigelFarage"
        ),
        NewsArticle(
            id = "4",
            title = "Advances in Solar Cell Efficiency Break Global Records",
            source = "Renewable Daily",
            country = "Australia",
            category = "Environment",
            snippet = "A laboratory in Sydney has achieved a historic 35% efficiency rate for commercial solar panels.",
            fullText = "A research laboratory in Sydney has achieved a historic 35% efficiency rate for commercial solar panels, significantly outperforming current market standards. The scientific breakthrough utilizes a novel multi-junction tandem cell architecture to capture a broader spectrum of incoming sunlight. Deployment of these panels could drastically reduce urban reliance on fossil fuels within the decade.\n\nThe research is currently being expedited for mass production capabilities across regional markets. Projections show that integrating this advanced solar technology into existing municipal infrastructure could drop consumer electricity costs by up to 15% for residential users. This innovation represents a crucial step forward in fulfilling global emissions mandates outlined in recent climate accords. With such innovative methods, researchers hope to alleviate the worst of climate change and establish a resilient energy paradigm.",
            imageUrl = "https://images.unsplash.com/photo-1500485035595-cbe6f645feb1?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 12,
            author = "Chloe Bennett - Sydney Morning Herald",
            authorImageUrl = "https://i.pravatar.cc/150?u=ChloeBennett"
        ),
        NewsArticle(
            id = "5",
            title = "Championship Finals: Underdogs Take the Cricket Trophy",
            source = "Times of India",
            country = "India",
            category = "Sports",
            snippet = "In a stunning upset, the underdog team triumphed against the reigning champions in a nail-biter.",
            fullText = "In a stunning upset, the regional cricket team triumphed against the reigning champions in a match that will be remembered for generations. With their star bowler delivering an impeccable final over, the team secured victory by a mere two runs. Millions gathered in city squares to celebrate the historic win late into the night.\n\nThe underdogs entered the tournament with staggering odds against them, having faced multiple injuries throughout the qualifying rounds. Their coach praised the team's incredible resilience and tactical execution under immense pressure. Following the win, the team captain announced plans to launch a youth sports foundation to nurture upcoming talent from rural areas. The loquacious captain gave a sagacious speech that managed to alleviate any doubts about the team's ubiquitous popularity.",
            imageUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
            author = "Rajdeep Sen - Times of India",
            authorImageUrl = "https://i.pravatar.cc/150?u=RajdeepSen"
        ),
        NewsArticle(
            id = "6",
            title = "New Interactive Art Exhibition Opens at Gallery",
            source = "Cultural Hub",
            country = "Canada",
            category = "Arts",
            snippet = "A stunning new immersive exhibition opens, highlighting interactive post-modern light sculptures.",
            fullText = "A stunning new immersive exhibition opens, highlighting interactive post-modern sculptures that respond dynamically to audience movements. The artist hopes to provoke thoughts on physical human connectivity in a modern digital age. The exhibit is slated to run for three months before going on a global tour.\n\nVisitors describe the experience as deeply profound and visually mesmerizing. By blending traditional material sculpting with algorithmic light displays, the exhibition pushes the boundaries of conventional gallery viewing. Critics are already hailing it as one of the most culturally significant art installations of the decade. The ephemeral nature of the light displays contrasts sharply with the resilient physical structures, establishing a new paradigm that seeks to obfuscate the lines between real and virtual art.",
            imageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 48,
            author = "Margaret Vance - The Globe and Mail",
            authorImageUrl = "https://i.pravatar.cc/150?u=MargaretVance"
        ),
        NewsArticle(
            id = "7",
            title = "Federal Bureau Redoubles Commitment to Clean Energy",
            source = "Dawn",
            country = "Pakistan",
            category = "Environment",
            snippet = "The central environment bureau revealed its ambitious timeline to launch solar smart microgrids.",
            fullText = "The central environment bureau of Pakistan has revealed its ambitious timeline to launch solar smart microgrids across provincial capitals. Utilizing advanced photovoltaic architectures, the municipal transmission loss could be cut in half. Engineers are already beginning training to prepare for the monumental grid modernization.\n\nThe announcement was accompanied by detailed schematics for modular battery storage units that utilize local manufacturing capabilities for rapid deployment. Public-private partnerships will play an essential role, with key materials being sourced from collaborative clean-energy projects across Europe and Asia. The target is to establish a comprehensive green grid by the late 2030s. This sagacious investment in innovative technology will likely alleviate long-standing power shortages, proving resilient enough to establish stable regional development.",
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 72,
            isBreaking = false,
            author = "Kamran Khan - Dawn",
            authorImageUrl = "https://i.pravatar.cc/150?u=KamranKhan"
        ),
        NewsArticle(
            id = "8",
            title = "Next-Generation Hypersonic Air Shield Tests Successful",
            source = "Washington Post",
            country = "United States",
            category = "Defense",
            snippet = "The missile defense program has validated advanced hypersonic interceptors, achieving flawless target locking.",
            fullText = "National defense intelligence and contracting systems have announced a highly successful validation of next-generation defense interceptors over Pacific testing grounds. Designed to counter hypersonic thermal threats, the early-warning missile tracking system successfully locked and neutralized orbital simulated assets in record time.\n\nMilitary developers collaborating with technical institutes highlighted the strategic role of quantum-grade sensor arrays placed aboard low-orbit satellites. The system is designed to bypass geographic blindspots, establishing a secure defense shield. Strategic coordinators emphasized that maintaining reliable deterrence requires extremely resilient electronic countermeasures.\n\nThe strategic development is being analyzed by security experts across allied countries as a milestone for cooperative airspace safety frameworks.",
            imageUrl = "https://images.unsplash.com/photo-1509062522246-3755977927d7?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 80,
            isBreaking = false,
            author = "Sarah Jenkins - Washington Post",
            authorImageUrl = "https://i.pravatar.cc/150?u=SarahJenkins"
        ),
        NewsArticle(
            id = "9",
            title = "Security Alliances Formulate Drone Detection Safeguards",
            source = "BBC News",
            country = "United Kingdom",
            category = "War",
            snippet = "New tactical doctrine emphasizes active defense countermeasures and real-time localized jamming nets.",
            fullText = "A coalition of international conflict defense planners has released a comprehensive joint tactical doctrine addressing modern autonomous drone warfare. The strategic guidelines emphasize rapid implementation of localized signal-jamming networks and mobile electromagnetic kinetic defenses to secure civilian zones from active conflicts.\n\nDefense reporters note the strategy seeks to mitigate the high-frequency mobility of autonomous tactical units, which have restructured front-line engagements. The tactical changes prioritize preserving humanitarian transport channels and establishing corridors of absolute security.\n\nWhile positional maps shift dynamically, military leaders emphasize that cooperative intelligence sharing combined with robust civilian warning systems are vital pillars for resolving complex modern security issues.",
            imageUrl = "https://images.unsplash.com/photo-1590247813693-5541f1c609fd?auto=format&fit=crop&w=800&q=80",
            date = System.currentTimeMillis() - 1000 * 60 * 60 * 96,
            isBreaking = true,
            author = "Rupert Giles - BBC News",
            authorImageUrl = "https://i.pravatar.cc/150?u=RupertGiles"
        )
    )
}
