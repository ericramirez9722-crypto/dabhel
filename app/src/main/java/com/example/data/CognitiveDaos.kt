package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EpisodicMemory)

    @Query("SELECT * FROM episodic_memory ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<EpisodicMemory>>

    @Query("SELECT * FROM episodic_memory ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<EpisodicMemory>

    @Query("DELETE FROM episodic_memory")
    suspend fun clear()
}

@Dao
interface SemanticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trace: SemanticTrace)

    @Query("SELECT * FROM semantic_traces ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SemanticTrace>>

    @Query("SELECT * FROM semantic_traces ORDER BY timestamp DESC")
    suspend fun getAllSync(): List<SemanticTrace>

    @Query("DELETE FROM semantic_traces")
    suspend fun clear()
}

@Dao
interface MythosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: MythosStateRecord)

    @Query("SELECT * FROM mythos_states ORDER BY timestamp DESC LIMIT 1")
    fun observeLatest(): Flow<MythosStateRecord?>

    @Query("SELECT * FROM mythos_states ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestSync(): MythosStateRecord?

    @Query("SELECT * FROM mythos_states ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<MythosStateRecord>>

    @Query("DELETE FROM mythos_states")
    suspend fun clear()
}

@Dao
interface NodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(node: CognitiveNodeRecord)

    @Query("SELECT * FROM cognitive_nodes ORDER BY nodeId ASC")
    fun observeNodes(): Flow<List<CognitiveNodeRecord>>

    @Query("SELECT * FROM cognitive_nodes")
    suspend fun getAllNodes(): List<CognitiveNodeRecord>

    @Query("SELECT * FROM cognitive_nodes WHERE nodeId = :id")
    suspend fun getNodeById(id: String): CognitiveNodeRecord?

    @Query("DELETE FROM cognitive_nodes")
    suspend fun clear()
}

@Dao
interface ArchetypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(archetype: NarrativeArchetype)

    @Query("SELECT * FROM narrative_archetypes ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<NarrativeArchetype>>

    @Query("DELETE FROM narrative_archetypes")
    suspend fun clear()
}

@Dao
interface RejectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RejectionRecord)

    @Query("SELECT * FROM rejection_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<RejectionRecord>>

    @Query("DELETE FROM rejection_logs")
    suspend fun clear()
}

@Dao
interface IdentityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invariant: IdentityInvariant)

    @Query("SELECT * FROM identity_invariants ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<IdentityInvariant>>

    @Query("SELECT * FROM identity_invariants ORDER BY timestamp DESC")
    suspend fun getAllSync(): List<IdentityInvariant>

    @Delete
    suspend fun delete(invariant: IdentityInvariant)

    @Query("DELETE FROM identity_invariants")
    suspend fun clear()
}

@Dao
interface CoherenceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CoherenceHistoryRecord)

    @Query("SELECT * FROM coherence_history ORDER BY timestamp ASC")
    fun observeAll(): Flow<List<CoherenceHistoryRecord>>

    @Query("SELECT * FROM coherence_history ORDER BY timestamp ASC LIMIT 100")
    suspend fun getRecentLimit(): List<CoherenceHistoryRecord>

    @Query("DELETE FROM coherence_history")
    suspend fun clear()
}


