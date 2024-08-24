package pl.lbiio.easyflashcards.db_access

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.lbiio.easyflashcards.tables.Achievement
import pl.lbiio.easyflashcards.tables.BackupMethod
import pl.lbiio.easyflashcards.tables.Flashcard
import pl.lbiio.easyflashcards.tables.FlashcardsPackage
import pl.lbiio.easyflashcards.tables.LearningCardProgress
import pl.lbiio.easyflashcards.tables.LearningPackageProgress
import pl.lbiio.easyflashcards.tables.MemoryCardProgress
import pl.lbiio.easyflashcards.tables.MemoryPackageProgress
import pl.lbiio.easyflashcards.tables.QuizCardProgress
import pl.lbiio.easyflashcards.tables.QuizPackageProgress
import pl.lbiio.easyflashcards.tables.SharedPackage

@Database(entities = [BackupMethod::class, Achievement::class, Flashcard::class, FlashcardsPackage::class, LearningCardProgress::class, LearningPackageProgress::class, MemoryPackageProgress::class, QuizPackageProgress::class, SharedPackage::class], version = 1, exportSchema = false)
abstract class EasyFlashcardsDatabase : RoomDatabase() {
    abstract fun easyFlashCardDao(): EasyFlashcardsDao
}