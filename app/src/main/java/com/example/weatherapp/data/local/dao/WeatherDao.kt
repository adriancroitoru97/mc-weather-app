package com.example.weatherapp.data.local.dao

import androidx.room.*
import com.example.weatherapp.data.local.entity.CityEntity
import com.example.weatherapp.data.local.entity.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM cities ORDER BY isFavorite DESC, cityName ASC")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE isFavorite = 1 ORDER BY cityName ASC")
    fun getFavoriteCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    suspend fun getCityById(cityId: Int): CityEntity?

    @Query("SELECT * FROM cities WHERE cityId = :cityId")
    fun getCityByIdFlow(cityId: Int): Flow<CityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Update
    suspend fun updateCity(city: CityEntity)

    @Query("UPDATE cities SET isFavorite = :isFavorite WHERE cityId = :cityId")
    suspend fun updateFavoriteStatus(cityId: Int, isFavorite: Boolean)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("DELETE FROM cities WHERE cityId = :cityId")
    suspend fun deleteCityById(cityId: Int)

    @Query("DELETE FROM cities WHERE isFavorite = 0")
    suspend fun deleteNonFavoriteCities()

    @Query("SELECT COUNT(*) FROM cities WHERE cityId = :cityId")
    suspend fun cityExists(cityId: Int): Int

    @Query("""
        SELECT * FROM cities 
        WHERE ABS(latitude - :latitude) < 0.01 
        AND ABS(longitude - :longitude) < 0.01 
        LIMIT 1
    """)
    suspend fun findCityByCoordinates(latitude: Double, longitude: Double): CityEntity?
}

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecasts WHERE cityId = :cityId ORDER BY timestamp ASC")
    fun getForecastsForCity(cityId: Int): Flow<List<ForecastEntity>>

    @Query("SELECT * FROM forecasts WHERE cityId = :cityId ORDER BY timestamp ASC")
    suspend fun getForecastsForCitySync(cityId: Int): List<ForecastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<ForecastEntity>)

    @Query("DELETE FROM forecasts WHERE cityId = :cityId")
    suspend fun deleteForecastsForCity(cityId: Int)

    @Query("DELETE FROM forecasts WHERE lastUpdated < :timestamp")
    suspend fun deleteOldForecasts(timestamp: Long)
}