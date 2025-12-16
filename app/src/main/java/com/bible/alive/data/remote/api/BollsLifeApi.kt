package com.bible.alive.data.remote.api

import com.bible.alive.data.remote.dto.BookDto
import com.bible.alive.data.remote.dto.ChapterDto
import com.bible.alive.data.remote.dto.TranslationDto
import com.bible.alive.data.remote.dto.VerseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BollsLifeApi {

    @GET("get-translations/")
    suspend fun getTranslations(): Response<List<TranslationDto>>

    @GET("get-books/{translation}/")
    suspend fun getBooks(
        @Path("translation") translation: String
    ): Response<List<BookDto>>

    @GET("get-text/{translation}/{book}/{chapter}/")
    suspend fun getChapter(
        @Path("translation") translation: String,
        @Path("book") bookNumber: Int,
        @Path("chapter") chapter: Int
    ): Response<List<ChapterDto>>

    @GET("get-text/{translation}/{book}/{chapter}/{verse}/")
    suspend fun getVerse(
        @Path("translation") translation: String,
        @Path("book") bookNumber: Int,
        @Path("chapter") chapter: Int,
        @Path("verse") verse: Int
    ): Response<VerseDto>

    @GET("get-random-verse/{translation}/")
    suspend fun getRandomVerse(
        @Path("translation") translation: String
    ): Response<VerseDto>

    companion object {
        const val BASE_URL = "https://bolls.life/api/"

        object Translations {
            const val RV1960 = "RVR1960"
            const val NTV = "NTV"
            const val NVI = "NVI"
        }
    }
}
