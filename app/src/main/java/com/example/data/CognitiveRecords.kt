package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Helper extensions for vector serialization
fun FloatArray.toCsv(): String = joinToString(",") { it.toString() }
fun String.toFloatArray(): FloatArray {
    if (isEmpty()) return FloatArray(0)
    return split(",").map { it.toFloatOrNull() ?: 0f }.toFloatArray()
}

@Entity(tableName = "episodic_memory")
data class EpisodicMemory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // "perception", "system", "sync"
    val localLambda: Double, // local coherence score
    val nodeSource: String // Node ID that originated this
)

@Entity(tableName = "semantic_traces")
data class SemanticTrace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val concept: String,
    val meaning: String,
    val vectorCsv: String, // comma-separated vector representation
    val confidence: Double,
    val derivedFromIds: String, // CSV of episodic ids
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getVector(): FloatArray = vectorCsv.toFloatArray()
}

@Entity(tableName = "mythos_states")
data class MythosStateRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val narrative: String,
    val coherence: Double, // Global or local Lambda
    val supportingSemanticIds: String, // CSV of semantic link IDs
    val supportingEpisodicIds: String, // CSV of episodic link IDs
    val evolutionStage: Int, // 1: Fragmented, 2: Stable, 3: High Coherence / Integrated
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cognitive_nodes")
data class CognitiveNodeRecord(
    @PrimaryKey val nodeId: String, // e.g. "NODE-A", "NODE-B", "NODE-C"
    val labelName: String,
    val status: String, // "Active", "Syncing", "Fragmented", "Synthesizing"
    val localLambda: Double,
    val lastSyncTime: Long,
    val latestStateSummary: String
)

@Entity(tableName = "narrative_archetypes")
data class NarrativeArchetype(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val narrativeSnippet: String,
    val alignmentCoherence: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "rejection_logs")
data class RejectionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attemptedInput: String,
    val rejectionReason: String,
    val refusalNarrative: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "identity_invariants")
data class IdentityInvariant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val concept: String,
    val value: String,
    val category: String, // "principle", "target", "framework"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "coherence_history")
data class CoherenceHistoryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val globalLambda: Double,
    val episodicCoherence: Double,
    val semanticCoherence: Double,
    val mythosCoherence: Double,
    val updateTrigger: String
)


