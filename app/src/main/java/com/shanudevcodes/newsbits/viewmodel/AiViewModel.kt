package com.shanudevcodes.newsbits.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shanudevcodes.newsbits.BuildConfig
import com.shanudevcodes.newsbits.data.NewsArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import kotlin.collections.forEachIndexed

class AiViewModel: ViewModel(){

    private val _geminiResponse = MutableStateFlow<List<String>>(emptyList())
    val geminiResponse = _geminiResponse

    private val _isResponseFetched = MutableStateFlow<Boolean>(false)
    val isResponseFetched = _isResponseFetched

    private var isFetching = false

    private val summaryType =/*"detailed"*/ "concise"

    fun getGeminiResponse(topNews: List<NewsArticle>){
        if (isFetching) return
        isFetching = true
        viewModelScope.launch {
            delay(1000)
            if (topNews.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    try {
                        Log.d("Gemini","run")
                        val apiKey = BuildConfig.Gemini_API_Key
                        val url =
                            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"


                        val client = OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .build()

                        val promptBuilder = StringBuilder()
                        promptBuilder.appendLine("You are a reliable summarization assistant.")
                        promptBuilder.appendLine()
                        when (summaryType) {
                            "concise" -> promptBuilder.appendLine("Provide a **concise summary** of each article as short as possible without missing key details.")
                            "detailed" -> promptBuilder.appendLine("Provide a **detailed summary** for each article — include key facts as well as relevant context and elaborate it in detail.")
                        }
                        promptBuilder.appendLine()
                        promptBuilder.appendLine("Summarize each of the following news articles individually in one bullet per article. Ensure:")
                        promptBuilder.appendLine("- Each bullet starts with a \"*\"")
                        promptBuilder.appendLine("- Retain all factual details like names, dates, numbers, and company names")
                        promptBuilder.appendLine("- The summary should reflect only the content from the article and not add external info")
                        promptBuilder.appendLine("- Output exactly 10 bullets — one per article")
                        promptBuilder.appendLine("- Keep the tone factual and news-like")
                        promptBuilder.appendLine()
                        promptBuilder.appendLine("Here are the articles:")
                        topNews.forEachIndexed { index, article ->
                            promptBuilder.appendLine("${index + 1}. ${article.title} - ${article.description}")
                        }

                        val prompt = promptBuilder.toString()

                        val jsonBody = """
                                {
                                  "contents": [
                                    {
                                      "parts": [
                                        {
                                          "text": ${JSONObject.quote(prompt)}
                                        }
                                      ]
                                    }
                                  ]
                                }
                                """.trimIndent()

                        val request = Request.Builder()
                            .url(url)
                            .post(jsonBody.toRequestBody("application/json".toMediaType()))
                            .build()

                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            val summaryText = JSONObject(responseBody)
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text")

                            val bulletPoints = summaryText
                                .split("\n")
                                .mapNotNull { line ->
                                    val trimmed = line.trim()
                                    if (trimmed.startsWith("*")) {
                                        trimmed.removePrefix("*").trim()
                                    } else null
                                }

                            _geminiResponse.value = bulletPoints
                            _isResponseFetched.value = true
                        } else {
                            Log.e(
                                "Gemini",
                                "Failed: ${response.code} - ${response.body?.string()}"
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("Gemini", "Exception: ${e.message}", e)
                    } finally {
                        isFetching = false
                    }
                }
            }
        }
    }
}