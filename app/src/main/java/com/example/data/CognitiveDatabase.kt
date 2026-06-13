package com.example.data

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        EpisodicMemory::class,
        SemanticTrace::class,
        MythosStateRecord::class,
        CognitiveNodeRecord::class,
        NarrativeArchetype::class,
        RejectionRecord::class,
        IdentityInvariant::class,
        CoherenceHistoryRecord::class,
        NeuralMemoryEntry::class,
        NarrativeAnchor::class,
        SyntergicLogEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class CognitiveDatabase : RoomDatabase() {
    abstract fun episodicDao(): EpisodicDao
    abstract fun semanticDao(): SemanticDao
    abstract fun mythosDao(): MythosDao
    abstract fun nodeDao(): NodeDao
    abstract fun archetypeDao(): ArchetypeDao
    abstract fun rejectionDao(): RejectionDao
    abstract fun identityDao(): IdentityDao
    abstract fun coherenceHistoryDao(): CoherenceHistoryDao
    abstract fun neuralMemoryDao(): NeuralMemoryDao
    abstract fun narrativeAnchorDao(): NarrativeAnchorDao
    abstract fun syntergicLogDao(): SyntergicLogDao

    companion object {
        @Volatile
        private var INSTANCE: CognitiveDatabase? = null

        fun getDatabase(context: Context): CognitiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CognitiveDatabase::class.java,
                    "cognitive_organism_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
