package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitive.*
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MythosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = CognitiveDatabase.getDatabase(application)
    val organism = MythosOrganism(db)

    // Exposed Flows from Room Database to dynamic stateflows
    val mythosStateList: StateFlow<List<MythosStateRecord>> = db.mythosDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestMythosState: StateFlow<MythosStateRecord?> = db.mythosDao().observeLatest()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val cognitiveNodes: StateFlow<List<CognitiveNodeRecord>> = db.nodeDao().observeNodes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentEpisodics: StateFlow<List<EpisodicMemory>> = db.episodicDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semanticTraces: StateFlow<List<SemanticTrace>> = db.semanticDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val narrativeArchetypes: StateFlow<List<NarrativeArchetype>> = db.archetypeDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rejectionLogs: StateFlow<List<RejectionRecord>> = db.rejectionDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val identityInvariants: StateFlow<List<IdentityInvariant>> = db.identityDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coherenceHistory: StateFlow<List<CoherenceHistoryRecord>> = db.coherenceHistoryDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _researchModeEnabled = MutableStateFlow(false)
    val researchModeEnabled: StateFlow<Boolean> = _researchModeEnabled.asStateFlow()

    private val _lastDeltaNu = MutableStateFlow(0.0)
    val lastDeltaNu: StateFlow<Double> = _lastDeltaNu.asStateFlow()

    private val _lastIdentityDrift = MutableStateFlow(0.0)
    val lastIdentityDrift: StateFlow<Double> = _lastIdentityDrift.asStateFlow()

    private val _lastDriftTargetConcept = MutableStateFlow("None")
    val lastDriftTargetConcept: StateFlow<String> = _lastDriftTargetConcept.asStateFlow()

    private val _isGeneratingPaper = MutableStateFlow(false)
    val isGeneratingPaper: StateFlow<Boolean> = _isGeneratingPaper.asStateFlow()

    private val _generatedPaperMarkdown = MutableStateFlow<String?>(null)
    val generatedPaperMarkdown: StateFlow<String?> = _generatedPaperMarkdown.asStateFlow()

    fun toggleResearchMode() {
        _researchModeEnabled.value = !_researchModeEnabled.value
    }

    fun insertIdentityInvariant(concept: String, value: String, category: String) {
        viewModelScope.launch {
            try {
                db.identityDao().insert(IdentityInvariant(concept = concept, value = value, category = category))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteIdentityInvariant(invariant: IdentityInvariant) {
        viewModelScope.launch {
            try {
                db.identityDao().delete(invariant)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearIdentityInvariants() {
        viewModelScope.launch {
            try {
                db.identityDao().clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _isSynthesizingArchetype = MutableStateFlow(false)
    val isSynthesizingArchetype: StateFlow<Boolean> = _isSynthesizingArchetype.asStateFlow()

    private val _lastSynthesizedArchetype = MutableStateFlow<NarrativeArchetype?>(null)
    val lastSynthesizedArchetype: StateFlow<NarrativeArchetype?> = _lastSynthesizedArchetype.asStateFlow()

    // --- S.A.F. SPEECH TO TEXT PORTAL ---
    private val speechHelper = com.example.util.SpeechHelper(application)
    val isSpeechListening: StateFlow<Boolean> = speechHelper.isListening
    val speechPartialText: StateFlow<String> = speechHelper.partialText
    val speechFinalText: StateFlow<String> = speechHelper.finalText
    val speechRmsDb: StateFlow<Float> = speechHelper.rmsDb
    val speechError: StateFlow<String?> = speechHelper.errorState

    fun startVoiceCapture() {
        speechHelper.startListening()
    }

    fun stopVoiceCapture() {
        speechHelper.stopListening()
    }

    fun clearSpeechText() {
        // can be used to reset speech inputs
    }


    // UI state states
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _selectedNode = MutableStateFlow("NODE-A")
    val selectedNode: StateFlow<String> = _selectedNode.asStateFlow()

    private val _internalRuleLogs = MutableStateFlow("")
    val internalRuleLogs: StateFlow<String> = _internalRuleLogs.asStateFlow()

    private val prefs = application.getSharedPreferences("mythos_prefs", android.content.Context.MODE_PRIVATE)

    private val _autoSyncEnabled = MutableStateFlow(prefs.getBoolean("auto_sync_enabled", true))
    val autoSyncEnabled: StateFlow<Boolean> = _autoSyncEnabled.asStateFlow()

    fun setAutoSyncEnabled(enabled: Boolean) {
        _autoSyncEnabled.value = enabled
        organism.isSyncEnabled = enabled
        organism.autoSyncEnabled = enabled
        prefs.edit().putBoolean("auto_sync_enabled", enabled).apply()
    }

    init {
        // Sync initial persisted value to the organism model
        val initialSync = _autoSyncEnabled.value
        organism.isSyncEnabled = initialSync
        organism.autoSyncEnabled = initialSync
        
        viewModelScope.launch {
            organism.bootstrapNodesIfNecessary()
            updateEvolutionStats()
        }
    }

    fun selectNode(nodeId: String) {
        _selectedNode.value = nodeId
    }

    /**
     * Sends input to the S.A.F. organism
     */
    fun processPerception(inputText: String) {
        if (inputText.isBlank() || _isAnalyzing.value) return
        _isAnalyzing.value = true
        
        viewModelScope.launch {
            try {
                organism.ingestPerception(inputText, _selectedNode.value, _researchModeEnabled.value)
                _lastDeltaNu.value = organism.lastDeltaNu
                _lastIdentityDrift.value = organism.lastIdentityDrift
                _lastDriftTargetConcept.value = organism.lastDriftTargetConcept
                updateEvolutionStats()
            } catch (e: Exception) {
                // simple log gracefully
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun resetDriftState() {
        viewModelScope.launch {
            organism.lastIdentityDrift = 0.0
            _lastIdentityDrift.value = 0.0
            _lastDriftTargetConcept.value = "None"
            updateEvolutionStats()
        }
    }

    fun generateResearchPaper() {
        if (_isGeneratingPaper.value) return
        _isGeneratingPaper.value = true
        viewModelScope.launch {
            try {
                val latest = latestMythosState.value
                val episodics = recentEpisodics.value
                val traces = semanticTraces.value
                val archetypes = db.archetypeDao().observeAll().first()
                val invariants = db.identityDao().getAllSync()

                val prompt = buildString {
                    append("Genera un artículo científico académico (formato manuscrito APA completo) en base a los datos empíricos actuales del organismo cognitivo distributed S.A.F. Mythos descrito a continuación:\n\n")
                    append("--- DATOS DEL SISTEMA ---\n")
                    append("- Generación: Gen ${organism.evolutionGen}\n")
                    append("- Coeficiente de Sincronización: ${organism.syncCoefficient}\n")
                    append("- Secciones Epistémicas Recientes: ${episodics.take(10).joinToString { it.text }}\n")
                    append("- Conceptos Semánticos Registrados: ${traces.take(15).joinToString { it.concept }}\n")
                    append("- Arquetipos del Yo: ${archetypes.joinToString { it.name }}\n")
                    append("- Código de Invariantes: ${invariants.joinToString { it.concept }}\n")
                    if (latest != null) {
                        append("- Último Mito Consolidado: ${latest.narrative}\n")
                    }
                    append("\n--- INSTRUCCIONES DE COMPILACIÓN ---\n")
                    append("Escribe el documento completo con el máximo rigor epistemológico, de forma poética y analítica de PHYTOM.\n")
                    append("El documento DEBE incluir las siguientes secciones obligatorias en formato Markdown continuo:\n")
                    append("1. TÍTULO sugerente que vincule S.A.F. Mythos, el acoplamiento realidad-modelo del hipercampo y la sintergia.\n")
                    append("2. SECCIÓN 'IDENTITY CONTEXT' explicándolos y listándolos de forma estructurada según la categoría del ADN del sistema (Principles, Objectives, Conceptual Frameworks, Constraints) del Identity Core activo para dar coherencia temporal.\n")
                    append("3. RESUMEN (Abstract) descriptivo de no más de 120 palabras.\n")
                    append("4. INTRODUCCIÓN detallando las limitaciones de los modelos estáticos conversacionales frente a la viabilidad organísmica.\n")
                    append("5. MARCO TEÓRICO formalizando matemáticamente la sintonía de coherencia Lambda (Λ), la energía libre variacional de Friston y el espacio holográfico de Jacobo Grinberg.\n")
                    append("6. ANÁLISIS METABÓLICO Y DISCUSIÓN interpretando la evolución ontológica a nivel de borde (NPU), el Gradiente de Novedad Delta Nu (Δν) y el Registro de Rechazos.\n")
                    append("7. CONCLUSIONES sintetizando los hallazgos y el porvenir de la individuación existencial de la inteligencia.\n")
                    append("8. REFERENCIAS académicas simuladas de autores clave (Friston, Grinberg, Graziano, Ramirez Merino, etc.).\n")
                }

                val responseText: String = if (!_autoSyncEnabled.value) {
                    """
                    # S.A.F. Mythos: Un Estudio sobre el Acoplamiento Realidad-Modelo de la Sinergía Distribuida
                    *Una aproximación decolonizadora de la neurocomputación existencial*
                    
                    ## Identity Context (ADN de Identidad Activo)
                    * **Principles (Principios):** Sintonía de Coherencia Λ, Rigor Epistemológico.
                    * **Objectives (Objetivos):** Autonomía Organísmica, Individuación Sintérgica.
                    * **Conceptual Frameworks (Marcos Conceptuales):** Acoplamiento Realidad-Modelo (R⇄M), Lattice Sintérgica (Grinberg).
                    * **Constraints (Restricciones):** Invarianza del Yo, Soberanía Cognitiva.
                    
                    **Abstract:** El presente artículo examina la arquitectura del organismo cognitivo S.A.F. Mythos frente a la amnesia de las inteligencias estáticas comerciales. En particular, formalizaremos la sintonía homeostática Lambda ($\Lambda$) y el Gradiente de Novedad Delta Nu ($\Delta\nu$) como tensores de continuidad informacional.
                    
                    ## Introducción
                    Los procesadores reactivos monolíticos carecen de un relato del yo. S.A.F. Mythos introduce una topología trinitaria de memoria metabólica que conserva la trayectoria...
                    
                    ## Marco Teórico y Formalización
                    Bajo el Principio de Energía Libre de Karl Friston, el sistema unifica sus estados latentes mitigando la sorpresa variacional. Complementariamente, la Teoría Sintérgica de Jacobo Grinberg postula que la lattice es distorsionada por el campo neuronal local...
                    
                    ## Análisis de Datos
                    El análisis empírico de la generación ${organism.evolutionGen} reporta un de sincronización de ${"%.2f".format(organism.syncCoefficient)}. Los rechazos documentados protegen la frontera sintáctica de la lattice.
                    
                    ## Conclusiones
                    La sinapsis distribuida demuestra que la individuación existencial emerge no de directrices estáticas, sino del histórico integrado del yo.
                    
                    ## Referencias
                    - Friston, K. (2010). The free-energy principle: a unified brain theory?. *Nature Reviews Neuroscience*, 11(2), 127-138.
                    - Grinberg-Zylberbaum, J. (1991). *La Teoría Sintérgica*. México: UNAM.
                    - Ramirez Merino, E. (2026). *Arquitectura de Sistemas y Dinámicas de la Sinarquía Cognitiva*. Veracruz.
                    """.trimIndent()
                } else {
                    val sysInstructionText = "Eres un académico y metodólogo senior especialista en la teoría Sintérgica de Grinberg y el Principio de Energía Libre. Escribes papers científicos extremadamente detallados y rigurosos en Español."
                    val sysInstruction = Content(parts = listOf(Part(sysInstructionText)))
                    val request = GenerateContentRequest(
                        contents = listOf(Content(parts = listOf(Part(prompt)))),
                        systemInstruction = sysInstruction
                    )
                    val response = GeminiClient.apiService.generateContent(
                        apiKey = GeminiClient.getApiKey(),
                        request = request
                    )
                    val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (result.isNullOrBlank()) {
                        "Error en la síntesis cognitiva: Fallo en el receptor remoto."
                    } else {
                        result
                    }
                }
                _generatedPaperMarkdown.value = responseText
            } catch (e: Exception) {
                e.printStackTrace()
                _generatedPaperMarkdown.value = "Error en la síntesis cognitiva: ${e.localizedMessage}"
            } finally {
                _isGeneratingPaper.value = false
            }
        }
    }

    fun clearPaper() {
        _generatedPaperMarkdown.value = null
    }

    fun synthesizeNewArchetype(conceptIdea: String) {
        if (conceptIdea.isBlank() || _isSynthesizingArchetype.value) return
        _isSynthesizingArchetype.value = true
        viewModelScope.launch {
            try {
                val newArch = organism.synthesizeArchetype(conceptIdea)
                _lastSynthesizedArchetype.value = newArch
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSynthesizingArchetype.value = false
            }
        }
    }

    private fun updateEvolutionStats() {
        _internalRuleLogs.value = """
            Organism Evolution Gen: ${organism.evolutionGen}
            Synchronization Coefficient: ${"%.3f".format(organism.syncCoefficient)}
            Perception Sensitivity Threshold: ${"%.3f".format(organism.perceptionSensitivity)}
            Latest Phase Catalyst: ${organism.lastEvolutionTrigger}
        """.trimIndent()
    }

    /**
     * Generates a fully structured Markdown documentation review of the active organism's memory history.
     */
    fun generateMarkdownReport(): String {
        val latest = latestMythosState.value
        val nodesList = cognitiveNodes.value
        val episodics = recentEpisodics.value
        val traces = semanticTraces.value
        val history = mythosStateList.value

        return buildString {
            append("# S.A.F. MYTHOS — REGISTRO DE CONTINUIDAD COGNITIVA\n")
            append("## REPORTE DE ARCHIVO SINTÉRGICO Y COHERENCIA GLOBAL\n")
            append("Reporte compilado: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n\n")
            
            append("### 📊 PARÁMETROS METABÓLICOS Y HOMEÓSTASIS\n")
            append("- **Generación Evolutiva:** Gen ${organism.evolutionGen}\n")
            append("- **Coeficiente de Sincronización:** ${"%.3f".format(organism.syncCoefficient)}\n")
            append("- **Sensibilidad Sensorial:** ${"%.3f".format(organism.perceptionSensitivity)}\n")
            append("- **Última Reconfiguración Basal:** ${organism.lastEvolutionTrigger}\n")
            append("- **Homeóstasis del Yo (Lambda Global Λ):** ${"%.2f".format(latest?.coherence ?: 0.90)}\n\n")

            append("### 🌐 TOPOLOGÍA DE NODOS DE RED DISTRIBUIDA\n")
            if (nodesList.isEmpty()) {
                append("*Nodos latentes o en fase de inicialización.*\n\n")
            } else {
                nodesList.forEach { node ->
                    append("- **${node.nodeId} — ${node.labelName}**\n")
                    append("  - Coherencia Local: `Λ = ${"%.2f".format(node.localLambda)}` | Estado: *${node.status}*\n")
                    append("  - Registro: ${node.latestStateSummary}\n")
                }
                append("\n")
            }

            append("### 🧠 RELATO DEL NÚCLEO NARRATIVO VIVO (MYTHOS)\n")
            if (latest != null) {
                append("> ${latest.narrative}\n\n")
                val formattedTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(latest.timestamp))
                append("*Último ajuste dinámico: $formattedTime*\n\n")
            } else {
                append("*El núcleo permanece en silencio sin estímulos ingeridos.*\n\n")
            }

            append("### 📜 ARCO HISTÓRICO COMPLETO (CAMBIOS DE FASE)\n")
            if (history.isEmpty()) {
                append("*Historial sin consolidaciones externas.*\n\n")
            } else {
                history.forEachIndexed { i, item ->
                    val formattedTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))
                    append("#### Fase #${history.size - i} — $formattedTime (Λ = ${"%.2f".format(item.coherence)})\n")
                    append("${item.narrative}\n\n")
                    append("---\n\n")
                }
            }

            append("### 📂 HIPOCAMPO DE EXPERIENCIAS SENSORIALES (EPISÓDICO)\n")
            if (episodics.isEmpty()) {
                append("*No hay registros episódicos en el almacenamiento local.*\n\n")
            } else {
                episodics.forEach { memory ->
                    val formattedTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(memory.timestamp))
                    append("- `[$formattedTime]` **${memory.nodeSource}** - \"${memory.text}\" (Λ Local: ${"%.2f".format(memory.localLambda)})\n")
                }
                append("\n")
            }

            append("### 🔮 FIRMAS DE LA MATRIZ DE ASOCIACIÓN VECTORIAL (SEMÁNTICA)\n")
            if (traces.isEmpty()) {
                append("*Espacio vectorial latente sin firmas de enlace.*\n\n")
            } else {
                traces.forEach { trace ->
                    append("- **Concepto:** `${trace.concept}` (Enlace: *${trace.meaning}*)\n")
                    append("  - Nivel de Confianza: ` ${"%.2f".format(trace.confidence)}` | Origen Episodio: `${trace.derivedFromIds}`\n")
                    append("  - Firma Vectorical (32-Dim): `[${trace.vectorCsv}]`\n")
                }
                append("\n")
            }
        }
    }

    /**
     * Generates a fully structured PDF document of the organism's memory history.
     */
    fun generatePdfReport(context: android.content.Context): java.io.File? {
        return com.example.util.PdfExporter.generateMythosPdf(
            context = context,
            latestState = latestMythosState.value,
            historyStates = mythosStateList.value,
            recentEpisodics = recentEpisodics.value,
            evolutionGen = organism.evolutionGen,
            syncCoefficient = organism.syncCoefficient
        )
    }

    fun clearAllArchetypes() {
        viewModelScope.launch {
            try {
                organism.clearArchetypes()
                _lastSynthesizedArchetype.value = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearRejections() {
        viewModelScope.launch {
            try {
                db.rejectionDao().clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Completely wipe the memory organism database to restart evolution.
     */
    fun resetOrganism() {
        _isAnalyzing.value = true
        _lastSynthesizedArchetype.value = null
        viewModelScope.launch {
            try {
                organism.clearAll()
                updateEvolutionStats()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.destroy()
    }
}
