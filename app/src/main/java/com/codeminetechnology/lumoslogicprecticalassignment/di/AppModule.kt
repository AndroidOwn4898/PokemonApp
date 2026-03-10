package com.codeminetechnology.lumoslogicprecticalassignment.di


import android.content.Context
import androidx.room.Room
import com.codeminetechnology.lumoslogicprecticalassignment.data.local.PokemonDatabase
import com.codeminetechnology.lumoslogicprecticalassignment.data.remote.PokemonApiService
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.PokemonRepository
import com.codeminetechnology.lumoslogicprecticalassignment.domain.repository.PokemonRepositoryImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Hilt dependency injection module
 * Provides singleton instances for API, Database, and Repository
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    /**
     * Provide Gson instance with builder configuration
     */
    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    /**
     * Provide OkHttpClient with logging interceptor
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provide Retrofit API service
     */
    @Singleton
    @Provides
    fun providePokemonApiService(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): PokemonApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PokemonApiService::class.java)
    }

    /**
     * Provide Room database
     */
    @Singleton
    @Provides
    fun providePokemonDatabase(
        @ApplicationContext context: Context,
    ): PokemonDatabase {
        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            "pokemon_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provide Pokemon DAO
     */
    @Singleton
    @Provides
    fun providePokemonDao(database: PokemonDatabase) = database.pokemonDao()

    /**
     * Provide Pokemon Repository implementation
     */
    @Singleton
    @Provides
    fun providePokemonRepository(
        apiService: PokemonApiService,
        pokemonDao: com.codeminetechnology.lumoslogicprecticalassignment.data.local.dao.PokemonDao,
        gson: Gson,
    ): PokemonRepository {
        return PokemonRepositoryImpl(apiService, pokemonDao, gson)
    }
}