package com.bodnar.carpathians.domain.routes

interface RoutesRepository {
    suspend fun getRoutes(): List<HikingRoute>
    suspend fun getRoute(id: String): HikingRoute?
}

/** Список маршрутів з необов'язковим фільтром за складністю. */
class GetRoutesUseCase(private val repository: RoutesRepository) {
    suspend operator fun invoke(difficulty: Difficulty? = null): List<HikingRoute> =
        repository.getRoutes().filter { difficulty == null || it.difficulty == difficulty }
}

class GetRouteDetailsUseCase(private val repository: RoutesRepository) {
    suspend operator fun invoke(id: String): HikingRoute? = repository.getRoute(id)
}
