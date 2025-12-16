package com.bible.alive.domain.usecase

import com.bible.alive.data.local.dao.UserReadingDao
import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.domain.model.AgeGroup
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

class GetRecommendedVersesUseCase @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userReadingDao: UserReadingDao
) {

    private val youngVerses = listOf(
        VerseLocation(19, 23, 1, "Confianza"),         // Salmos 23:1
        VerseLocation(50, 4, 13, "Fortaleza"),         // Filipenses 4:13
        VerseLocation(20, 3, 5, "Sabiduría"),          // Proverbios 3:5
        VerseLocation(45, 8, 28, "Propósito"),         // Romanos 8:28
        VerseLocation(6, 1, 9, "Valentía"),            // Josué 1:9
        VerseLocation(40, 11, 28, "Descanso"),         // Mateo 11:28
        VerseLocation(24, 29, 11, "Esperanza"),        // Jeremías 29:11
        VerseLocation(58, 11, 1, "Fe"),                // Hebreos 11:1
        VerseLocation(46, 10, 13, "Tentación"),        // 1 Corintios 10:13
        VerseLocation(49, 6, 1, "Familia"),            // Efesios 6:1
    )

    private val adultVerses = listOf(
        VerseLocation(19, 37, 4, "Deseos"),            // Salmos 37:4
        VerseLocation(20, 16, 3, "Trabajo"),           // Proverbios 16:3
        VerseLocation(45, 12, 2, "Transformación"),    // Romanos 12:2
        VerseLocation(40, 6, 33, "Prioridades"),       // Mateo 6:33
        VerseLocation(59, 1, 5, "Sabiduría"),          // Santiago 1:5
        VerseLocation(23, 40, 31, "Renovación"),       // Isaías 40:31
        VerseLocation(47, 12, 9, "Gracia"),            // 2 Corintios 12:9
        VerseLocation(48, 5, 22, "Fruto"),             // Gálatas 5:22
        VerseLocation(51, 3, 23, "Servicio"),          // Colosenses 3:23
        VerseLocation(60, 5, 7, "Ansiedad"),           // 1 Pedro 5:7
    )

    private val elderlyVerses = listOf(
        VerseLocation(19, 103, 1, "Gratitud"),         // Salmos 103:1
        VerseLocation(19, 91, 1, "Protección"),        // Salmos 91:1
        VerseLocation(23, 46, 4, "Vejez"),             // Isaías 46:4
        VerseLocation(50, 4, 6, "Paz"),                // Filipenses 4:6
        VerseLocation(19, 71, 18, "Legado"),           // Salmos 71:18
        VerseLocation(43, 14, 27, "Paz interior"),     // Juan 14:27
        VerseLocation(47, 4, 16, "Renovación"),        // 2 Corintios 4:16
        VerseLocation(19, 92, 14, "Fructificación"),   // Salmos 92:14
        VerseLocation(55, 4, 7, "Carrera"),            // 2 Timoteo 4:7
        VerseLocation(66, 21, 4, "Esperanza"),         // Apocalipsis 21:4
    )

    private val generalVerses = listOf(
        VerseLocation(43, 3, 16, "Amor de Dios"),      // Juan 3:16
        VerseLocation(19, 119, 105, "Guía"),           // Salmos 119:105
        VerseLocation(62, 4, 8, "Amor"),               // 1 Juan 4:8
        VerseLocation(49, 2, 8, "Salvación"),          // Efesios 2:8
        VerseLocation(43, 14, 6, "Camino"),            // Juan 14:6
        VerseLocation(47, 5, 17, "Nueva vida"),        // 2 Corintios 5:17
        VerseLocation(19, 34, 8, "Bondad"),            // Salmos 34:8
        VerseLocation(46, 13, 4, "Amor verdadero"),    // 1 Corintios 13:4
        VerseLocation(40, 28, 20, "Presencia"),        // Mateo 28:20
        VerseLocation(66, 3, 20, "Comunión"),          // Apocalipsis 3:20
    )

    suspend operator fun invoke(count: Int = 5): Result<List<RecommendedVerse>> {
        return try {
            val translation = userPreferencesRepository.preferredTranslation.first()
            val userProfile = userPreferencesRepository.getUserProfileSync()
            val recentReadings = userReadingDao.getRecentReadingHistory(20).first()

            val ageBasedVerses = when (userProfile.ageGroup) {
                AgeGroup.YOUNG -> youngVerses
                AgeGroup.ADULT -> adultVerses
                AgeGroup.ELDERLY -> elderlyVerses
            }

            val combinedVerses = (ageBasedVerses + generalVerses).distinctBy { it.hashCode() }

            val recentBookNumbers = recentReadings.map { it.bookNumber }.toSet()
            val prioritizedVerses = combinedVerses.sortedBy { verse ->
                if (verse.book in recentBookNumbers) 1 else 0
            }

            val selectedLocations = prioritizedVerses
                .shuffled(Random(System.currentTimeMillis()))
                .take(count)

            val recommendations = mutableListOf<RecommendedVerse>()
            for (location in selectedLocations) {
                val verseResult = bibleRepository.getVerse(
                    translation = translation,
                    bookNumber = location.book,
                    chapter = location.chapter,
                    verse = location.verse
                )
                if (verseResult is Result.Success) {
                    recommendations.add(
                        RecommendedVerse(
                            verse = verseResult.data,
                            category = location.category,
                            reason = getReasonForCategory(location.category, userProfile.ageGroup)
                        )
                    )
                }
            }

            if (recommendations.isNotEmpty()) {
                Result.Success(recommendations)
            } else {
                Result.Error(Exception("Could not load recommended verses"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getVersesForCategory(category: String, count: Int = 3): Result<List<Verse>> {
        return try {
            val translation = userPreferencesRepository.preferredTranslation.first()
            val allVerses = youngVerses + adultVerses + elderlyVerses + generalVerses
            val categoryVerses = allVerses.filter { 
                it.category.equals(category, ignoreCase = true) 
            }

            val verses = mutableListOf<Verse>()
            for (location in categoryVerses.take(count)) {
                val result = bibleRepository.getVerse(
                    translation = translation,
                    bookNumber = location.book,
                    chapter = location.chapter,
                    verse = location.verse
                )
                if (result is Result.Success) {
                    verses.add(result.data)
                }
            }

            Result.Success(verses)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun getReasonForCategory(category: String, ageGroup: AgeGroup): String {
        val baseReason = when (category) {
            "Confianza" -> "Para fortalecer tu confianza en Dios"
            "Fortaleza" -> "Cuando necesitas fuerzas"
            "Sabiduría" -> "Para tomar decisiones sabias"
            "Propósito" -> "Para entender el plan de Dios"
            "Valentía" -> "Para enfrentar tus miedos"
            "Descanso" -> "Cuando te sientes cansado"
            "Esperanza" -> "Para renovar tu esperanza"
            "Fe" -> "Para fortalecer tu fe"
            "Paz" -> "Para encontrar paz interior"
            "Amor" -> "Para conocer el amor de Dios"
            "Familia" -> "Para bendecir tu familia"
            "Trabajo" -> "Para tu vida laboral"
            "Transformación" -> "Para crecer espiritualmente"
            "Prioridades" -> "Para ordenar tus prioridades"
            "Renovación" -> "Para renovar tus fuerzas"
            "Gracia" -> "Para entender la gracia de Dios"
            "Fruto" -> "Para desarrollar el fruto del Espíritu"
            "Servicio" -> "Para servir a otros"
            "Ansiedad" -> "Para superar la ansiedad"
            "Gratitud" -> "Para cultivar la gratitud"
            "Protección" -> "Para sentir la protección de Dios"
            "Legado" -> "Para dejar un legado de fe"
            else -> "Recomendado para ti"
        }

        return when (ageGroup) {
            AgeGroup.YOUNG -> "$baseReason en esta etapa de tu vida"
            AgeGroup.ADULT -> "$baseReason en tu día a día"
            AgeGroup.ELDERLY -> "$baseReason en esta temporada de sabiduría"
        }
    }

    data class RecommendedVerse(
        val verse: Verse,
        val category: String,
        val reason: String
    )

    private data class VerseLocation(
        val book: Int,
        val chapter: Int,
        val verse: Int,
        val category: String
    )
}
