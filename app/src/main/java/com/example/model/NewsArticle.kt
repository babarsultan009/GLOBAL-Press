package com.example.model

data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val country: String,
    val category: String,
    val snippet: String,
    val fullText: String,
    val imageUrl: String,
    val date: Long,
    val isBreaking: Boolean = false,
    val author: String = "Editorial Board",
    val authorImageUrl: String = "",
    val url: String = "https://example.com"
)
