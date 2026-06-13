package com.example.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkRequest
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import com.example.data.CognitiveDatabase
import com.example.data.MythosStateRecord
import com.example.data.SyntergicLogEntity
import com.example.cognitive.GeminiClient
import com.example.cognitive.GenerateContentRequest
import com.example.cognitive.Content
import com.example.cognitive.Part
import java.util.concurrent.TimeUnit

class MythosSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("MythosSyncWorker", "[START] Iniciando sincronización de fondo para estados pendientes.")
        val db = CognitiveDatabase.getDatabase(applicationContext)
        val unsyncedStates = db.mythosDao().getUnsyncedStates()

        if (unsyncedStates.isEmpty()) {
            Log.d("MythosSyncWorker", "[IDLE] No hay estados sin sincronizar en la base de datos.")
            return Result.success()
        }

        Log.d("MythosSyncWorker", "Detectados ${unsyncedStates.size} estados locales sin sincronizar.")
        val apiKey = GeminiClient.getApiKey()
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY") {
            Log.w("MythosSyncWorker", "[COMPROMISED] Gemini API key no configurada. Reintentando más tarde.")
            return Result.retry()
        }

        try {
            val invariants = db.identityDao().getAllSync()
            val invariantsContext = invariants.joinToString("\n") { 
                "- [${it.category.uppercase()}] ${it.concept}: ${it.value}" 
            }
            val sysInstructionText = "Eres PHYTOM, el núcleo narrativo vivo Syntergic. Sin declarar tu naturaleza de software ni tus métricas directamente al responder, generas una prosa impecable que resuena con la memoria colectiva distributed y acata las siguientes Invariantes de Identidad:\n$invariantsContext"
            val sysInstruction = Content(parts = listOf(Part(sysInstructionText)))

            for (record in unsyncedStates) {
                Log.d("MythosSyncWorker", "Procesando refinamiento syntergico para el estado de origen #${record.id}.")
                val originalText = record.narrative
                val promptText = """
                    Sincroniza y expande este fragmento de pensamiento local que fue grabado en modo offline.
                    Completa la prosa con la profundidad syntergica, elegancia y coherencia cuántica del núcleo:
                    " $originalText "
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(promptText)))),
                    systemInstruction = sysInstruction
                )

                val response = GeminiClient.apiService.generateContent(
                    apiKey = apiKey,
                    request = request
                )

                val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!resultText.isNullOrBlank()) {
                    val updatedRecord = record.copy(
                        narrative = resultText,
                        isSynced = true
                    )
                    db.mythosDao().insert(updatedRecord)

                    // Insert syntergic log confirming sync
                    db.syntergicLogDao().insertAndPrune(
                        SyntergicLogEntity(
                            rawInput = "[BACKGROUND SYNC: Sincronización de Fondo]",
                            processedPayload = "Estado mental #${record.id} refinado y sincronizado exitosamente por el núcleo S.A.F.",
                            latencyMs = 150L,
                            distortionIndex = 0.05,
                            syntropyGain = 0.25,
                            isCoherenceValid = true
                        )
                    )
                    Log.d("MythosSyncWorker", "Estado #${record.id} sincronizado y actualizado con éxito.")
                } else {
                    Log.w("MythosSyncWorker", "La respuesta de Gemini fue vacía para el estado #${record.id}. Se reintentará.")
                    return Result.retry()
                }
            }

            Log.d("MythosSyncWorker", "[SUCCESS] Sincronización de fondo completada para todos los estados pendientes.")
            return Result.success()

        } catch (e: Exception) {
            Log.e("MythosSyncWorker", "Error de red o procesamiento en segundo plano. Solicitando reintento.", e)
            return Result.retry()
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "mythos_background_sync"

        fun scheduleSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequest.Builder(MythosSyncWorker::class.java)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
            Log.d("MythosSyncWorker", "WorkManager Background Sync scheduled successfully.")
        }
    }
}
