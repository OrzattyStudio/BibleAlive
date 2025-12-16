package com.bible.alive.utils

import com.bible.alive.domain.model.AgeGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageAdapter @Inject constructor() {

    fun adaptGreeting(ageGroup: AgeGroup): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> "¡Hola!"
            AgeGroup.ADULT -> "Buenos días"
            AgeGroup.ELDERLY -> "Que Dios le bendiga"
        }
    }

    fun adaptEncouragement(ageGroup: AgeGroup, streakDays: Int): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> {
                when {
                    streakDays == 0 -> "¡Empieza hoy tu racha! 🔥"
                    streakDays < 7 -> "¡$streakDays días seguidos! ¡Vas súper bien! 💪"
                    streakDays < 30 -> "¡$streakDays días! ¡Eres un crack! 🌟"
                    else -> "¡$streakDays días! ¡Increíble compromiso! 🏆"
                }
            }
            AgeGroup.ADULT -> {
                when {
                    streakDays == 0 -> "Comienza tu jornada de lectura hoy"
                    streakDays < 7 -> "$streakDays días de lectura continua. ¡Buen trabajo!"
                    streakDays < 30 -> "$streakDays días de constancia. ¡Excelente dedicación!"
                    else -> "$streakDays días de fidelidad. ¡Un logro admirable!"
                }
            }
            AgeGroup.ELDERLY -> {
                when {
                    streakDays == 0 -> "Le invitamos a iniciar su lectura diaria"
                    streakDays < 7 -> "$streakDays días de bendita lectura. Dios le bendice"
                    streakDays < 30 -> "$streakDays días alimentándose de la Palabra. Qué bendición"
                    else -> "$streakDays días de fidelidad a la Palabra. Dios honra su dedicación"
                }
            }
        }
    }

    fun adaptStreakWarning(ageGroup: AgeGroup, streakDays: Int): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> "¡Ey! Tu racha de $streakDays días está en riesgo. ¡No la pierdas!"
            AgeGroup.ADULT -> "Tu racha de $streakDays días puede terminar hoy. Lee unos versículos para mantenerla."
            AgeGroup.ELDERLY -> "Estimado(a), su racha de $streakDays días necesita su lectura de hoy. Le esperamos."
        }
    }

    fun adaptDailyVerseIntro(ageGroup: AgeGroup): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> "Tu versículo de hoy 📖"
            AgeGroup.ADULT -> "Versículo del día"
            AgeGroup.ELDERLY -> "La Palabra para hoy"
        }
    }

    fun adaptDevotionalTitle(ageGroup: AgeGroup): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> "Reflexión del día 💭"
            AgeGroup.ADULT -> "Devocional diario"
            AgeGroup.ELDERLY -> "Meditación espiritual"
        }
    }

    fun adaptReadingComplete(ageGroup: AgeGroup, chaptersRead: Int): String {
        return when (ageGroup) {
            AgeGroup.YOUNG -> {
                if (chaptersRead == 1) "¡Capítulo completado! 🎉"
                else "¡$chaptersRead capítulos completados! 🎉"
            }
            AgeGroup.ADULT -> {
                if (chaptersRead == 1) "Capítulo completado"
                else "$chaptersRead capítulos completados"
            }
            AgeGroup.ELDERLY -> {
                if (chaptersRead == 1) "Ha completado un capítulo. Bendiciones"
                else "Ha completado $chaptersRead capítulos. Dios le bendiga"
            }
        }
    }

    fun adaptButtonText(ageGroup: AgeGroup, action: ButtonAction): String {
        return when (action) {
            ButtonAction.CONTINUE_READING -> when (ageGroup) {
                AgeGroup.YOUNG -> "¡Seguir leyendo!"
                AgeGroup.ADULT -> "Continuar lectura"
                AgeGroup.ELDERLY -> "Continuar leyendo"
            }
            ButtonAction.START_READING -> when (ageGroup) {
                AgeGroup.YOUNG -> "¡Empezar a leer!"
                AgeGroup.ADULT -> "Iniciar lectura"
                AgeGroup.ELDERLY -> "Comenzar lectura"
            }
            ButtonAction.SHARE -> when (ageGroup) {
                AgeGroup.YOUNG -> "Compartir"
                AgeGroup.ADULT -> "Compartir"
                AgeGroup.ELDERLY -> "Compartir"
            }
            ButtonAction.LISTEN -> when (ageGroup) {
                AgeGroup.YOUNG -> "Escuchar 🎧"
                AgeGroup.ADULT -> "Escuchar"
                AgeGroup.ELDERLY -> "Escuchar lectura"
            }
        }
    }

    fun adaptErrorMessage(ageGroup: AgeGroup, errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.NETWORK -> when (ageGroup) {
                AgeGroup.YOUNG -> "¡Ups! No hay conexión. Revisa tu internet 📶"
                AgeGroup.ADULT -> "Error de conexión. Verifique su conexión a internet"
                AgeGroup.ELDERLY -> "No se pudo conectar. Por favor, verifique que tiene conexión a internet"
            }
            ErrorType.LOADING -> when (ageGroup) {
                AgeGroup.YOUNG -> "Algo salió mal. ¿Intentamos de nuevo?"
                AgeGroup.ADULT -> "Error al cargar. Por favor, intente nuevamente"
                AgeGroup.ELDERLY -> "Hubo un problema al cargar. Toque el botón para intentar de nuevo"
            }
            ErrorType.NOT_FOUND -> when (ageGroup) {
                AgeGroup.YOUNG -> "No encontramos eso. ¿Buscamos otra cosa?"
                AgeGroup.ADULT -> "Contenido no encontrado"
                AgeGroup.ELDERLY -> "No se encontró el contenido solicitado"
            }
        }
    }

    enum class ButtonAction {
        CONTINUE_READING,
        START_READING,
        SHARE,
        LISTEN
    }

    enum class ErrorType {
        NETWORK,
        LOADING,
        NOT_FOUND
    }
}
