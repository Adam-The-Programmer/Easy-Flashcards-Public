package pl.lbiio.easyflashcards.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.lbiio.easyflashcards.db_access.EasyFlashcardsDao
import pl.lbiio.easyflashcards.db_access.EasyFlashcardsDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFlashCardDao(easyFlashCardsDatabase: EasyFlashcardsDatabase): EasyFlashcardsDao =
        easyFlashCardsDatabase.easyFlashCardDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): EasyFlashcardsDatabase =
        Room.databaseBuilder(
            context,
            EasyFlashcardsDatabase::class.java,
            "easy_flashcards_db"
        )
            .fallbackToDestructiveMigration()
            .build()
}