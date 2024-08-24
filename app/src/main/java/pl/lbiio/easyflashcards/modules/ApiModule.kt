package pl.lbiio.easyflashcards.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.lbiio.easyflashcards.services.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://easyflashcards-413816.ew.r.appspot.com/api/"

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val customRetrofit = ApiService.createRetrofit(BASE_URL)
        return customRetrofit.create(ApiService::class.java)
    }
}