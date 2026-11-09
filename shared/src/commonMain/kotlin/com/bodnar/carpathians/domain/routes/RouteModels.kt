package com.bodnar.carpathians.domain.routes

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** Текст двома мовами застосунку (контент маршрутів). */
data class LocalizedText(val uk: String, val en: String) {
    fun get(isUkrainian: Boolean): String = if (isUkrainian) uk else en
}

enum class Difficulty { Easy, Medium, Hard }

/** Ключова точка маршруту. */
data class RoutePoint(
    val title: LocalizedText,
    /** Висота в метрах; null — для точок без висоти (повернення, водоспад). */
    val altitude: Int?,
    val description: LocalizedText,
    val image: DrawableResource,
)

/** Туристичний маршрут з дизайну (секція "Оберіть свій маршрут"). */
data class HikingRoute(
    val id: String,
    val name: StringResource,
    val difficulty: Difficulty,
    val lengthKm: Int,
    /** Тривалість у днях: 1 або 2 (для "1–2 дні"). */
    val maxDays: Int,
    val cover: DrawableResource,
    val mapUrl: String,
    val points: List<RoutePoint>,
) {
    /** Найвища точка маршруту. */
    val maxAltitude: Int get() = points.mapNotNull { it.altitude }.maxOrNull() ?: 0
}
