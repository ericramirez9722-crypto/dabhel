package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlin.math.abs
import androidx.compose.foundation.*
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.viewmodel.MythosViewModel

// Obsidian Color Palette for luxury aesthetic
val DeepBackground = Color(0xFF030712) // dark slate/black
val CardBackground = Color(0xFF0F172A) // rich slate
val CardBorder = Color(0xFF1E293B)
val LightBorder = Color(0xFF334155)

val CyberTeal = Color(0xFF00F5D4) // bioluminescent clean teal
val CyberCyan = Color(0xFF00BBF9) // electrical blue
val CyberPurple = Color(0xFF9D4EDD) // deep cosmic violet
val CoherenceHigh = CyberTeal
val CoherenceMid = CyberCyan
val CoherenceLow = Color(0xFFEF4444) // coral pink warning

@Composable
fun GlassmorphicNodeContainer(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    backgroundColor: Color,
    borderColor: Color,
    nodeId: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("node_$nodeId")
    ) {
        // Real-time backdrop-filter: blur(10px) simulation in Compose
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(backgroundColor)
        )
        // Semi-transparent border highlight
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
        )
        // Forced 16px (16.dp) padding container for nodes
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GlassmorphicStatusBar(
    modifier: Modifier = Modifier,
    coherenceValue: Double,
    isSyncing: Boolean,
    isSyncEnabled: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .testTag("glassmorphic_status_bar")
    ) {
        // Real-time backdrop-filter: blur(10px) simulation in Compose
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(10.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(CardBackground.copy(alpha = 0.45f))
        )
        // Semi-transparent border highlight
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    color = CardBorder.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
        )
        // Inside components
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neural Coherence
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = if (coherenceValue > 0.45) CyberTeal else CoherenceLow,
                    modifier = Modifier.size(14.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "NEURAL COHERENCE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "%.1f%%".format(coherenceValue * 100),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (coherenceValue > 0.45) CyberTeal else CoherenceLow,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Sync Status
            val syncText = when {
                isSyncing -> "SYNCING"
                isSyncEnabled -> "SYNCHRONIZED"
                else -> "DESYNC"
            }
            val syncColor = when {
                isSyncing -> CyberCyan
                isSyncEnabled -> CyberTeal
                else -> Color(0xFFFBBF24)
            }
            val syncIcon = when {
                isSyncing -> Icons.Outlined.Refresh
                isSyncEnabled -> Icons.Outlined.CheckCircle
                else -> Icons.Outlined.Warning
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MYTHOS SYNC:",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = syncIcon,
                        contentDescription = null,
                        tint = syncColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = syncText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = syncColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

sealed class UnionMemoryEntry {
    abstract val uniqueId: String
    abstract val timestamp: Long
    abstract val title: String
    abstract val categoryLabel: String
    abstract val iconEmoji: String
    abstract val accentColor: Color
    abstract val metadata: List<Pair<String, String>>

    data class Episodic(val entry: com.example.data.EpisodicMemory) : UnionMemoryEntry() {
        override val uniqueId: String = "episodic_${entry.id}"
        override val timestamp: Long = entry.timestamp
        override val title: String = entry.text
        override val categoryLabel: String = "LOG EPISÓDICO [${entry.eventType.uppercase()}]"
        override val iconEmoji: String = "📡"
        override val accentColor: Color = CyberTeal
        override val metadata: List<Pair<String, String>> = listOf(
            "EVENT_TYPE" to entry.eventType,
            "LOCAL_LAMBDA" to "Λ = %.2f".format(entry.localLambda),
            "NODE_SOURCE" to entry.nodeSource,
            "TIMESTAMP_MS" to "${entry.timestamp}"
        )
    }

    data class Vector(val entry: com.example.data.NeuralMemoryEntry) : UnionMemoryEntry() {
        override val uniqueId: String = "vector_${entry.id}"
        override val timestamp: Long = entry.timestamp
        override val title: String = entry.infoText
        override val categoryLabel: String = "VECTOR NEURAL"
        override val iconEmoji: String = "🧠"
        override val accentColor: Color = CyberCyan
        override val metadata: List<Pair<String, String>> = listOf(
            "INVARIANT_REF" to entry.associatedInvariantConcept,
            "COGNITIVE_CAT" to entry.associatedInvariantCategory,
            "SIMILARITY_IDX" to "S = %.2f".format(entry.associationSimilarity),
            "VECTOR_DIM" to "32-D",
            "TIMESTAMP_MS" to "${entry.timestamp}"
        )
    }
}

@Composable
fun NeuralMemoryDashboardComponent(
    recentEpisodics: List<com.example.data.EpisodicMemory>,
    neuralMemoryList: List<com.example.data.NeuralMemoryEntry>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showEpisodicLogs by remember { mutableStateOf(true) }
    var showVectorMemories by remember { mutableStateOf(true) }
    
    // Dynamic tracking of expanded item IDs to toggle details/metadata visibility
    var expandedItemIds by remember { mutableStateOf(setOf<String>()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("neural_memory_dashboard_card"),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: interactive toggle for expanded list visibility
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🧠",
                        fontSize = 18.sp
                    )
                    Column {
                        Text(
                            text = "NEURAL MEMORY HUD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Matriz de percepciones y almacén vectorial",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
                
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("toggle_neural_memory_expand")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Toggle component visibility",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Monitoreo en tiempo real del sensorium global. Controla la visibilidad de los flujos de memoria o expande entradas individuales para auditar metadatos de sincronización.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 14.sp
                    )

                    // Stream Visibility Toggles (Episodic vs Vector)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Episodic Toggle Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (showEpisodicLogs) CyberTeal.copy(alpha = 0.15f) else CardBackground.copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = if (showEpisodicLogs) CyberTeal else CardBorder.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showEpisodicLogs = !showEpisodicLogs }
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .testTag("filter_episodic_chip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (showEpisodicLogs) CyberTeal else Color.White.copy(alpha = 0.3f))
                                )
                                Text(
                                    text = "Log Episódico (${recentEpisodics.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showEpisodicLogs) CyberTeal else Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }

                        // Vector Memory Toggle Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (showVectorMemories) CyberCyan.copy(alpha = 0.15f) else CardBackground.copy(alpha = 0.3f))
                                .border(
                                    width = 1.dp,
                                    color = if (showVectorMemories) CyberCyan else CardBorder.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { showVectorMemories = !showVectorMemories }
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .testTag("filter_vector_chip"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (showVectorMemories) CyberCyan else Color.White.copy(alpha = 0.3f))
                                )
                                Text(
                                    text = "Vectores Neurales (${neuralMemoryList.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showVectorMemories) CyberCyan else Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    // Combined and Sorted Event Streams
                    val combinedEntries = remember(recentEpisodics, neuralMemoryList, showEpisodicLogs, showVectorMemories) {
                        val list = mutableListOf<UnionMemoryEntry>()
                        if (showEpisodicLogs) {
                            recentEpisodics.forEach {
                                list.add(UnionMemoryEntry.Episodic(it))
                            }
                        }
                        if (showVectorMemories) {
                            neuralMemoryList.forEach {
                                list.add(UnionMemoryEntry.Vector(it))
                            }
                        }
                        list.sortedByDescending { it.timestamp }
                    }

                    if (combinedEntries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardBackground.copy(alpha = 0.2f))
                                .border(0.5.dp, CardBorder.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin registros coincidentes en la memoria",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            combinedEntries.take(5).forEach { entry ->
                                val uniqueId = entry.uniqueId
                                val isItemExpanded = expandedItemIds.contains(uniqueId)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedItemIds = if (isItemExpanded) {
                                                expandedItemIds - uniqueId
                                            } else {
                                                expandedItemIds + uniqueId
                                            }
                                        }
                                        .testTag("memory_dashboard_entry_$uniqueId"),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.3f)),
                                    border = BorderStroke(0.5.dp, if (isItemExpanded) entry.accentColor.copy(alpha = 0.6f) else CardBorder.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = entry.iconEmoji,
                                                    fontSize = 14.sp
                                                )
                                                Column {
                                                    Text(
                                                        text = entry.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = entry.categoryLabel,
                                                        fontSize = 8.sp,
                                                        color = entry.accentColor,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                            }

                                            Text(
                                                text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(entry.timestamp)),
                                                fontSize = 9.sp,
                                                color = Color.White.copy(alpha = 0.4f),
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        // Detailed audit metadata viewable via toggle click
                                        AnimatedVisibility(
                                            visible = isItemExpanded,
                                            enter = expandVertically() + fadeIn(),
                                            exit = shrinkVertically() + fadeOut()
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(top = 8.dp)
                                                    .fillMaxWidth()
                                                    .background(DeepBackground.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                entry.metadata.forEach { (key, valStr) ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = key,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White.copy(alpha = 0.4f),
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                        Text(
                                                            text = valStr,
                                                            fontSize = 8.5.sp,
                                                            color = Color.White.copy(alpha = 0.85f),
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OldMythosScreen(
    viewModel: MythosViewModel,
    currentScreen: AppScreen,
    onScreenChange: (AppScreen) -> Unit
) {
    val latestState by viewModel.latestMythosState.collectAsStateWithLifecycle()
    val mythosHistory by viewModel.mythosStateList.collectAsStateWithLifecycle()
    val nodes by viewModel.cognitiveNodes.collectAsStateWithLifecycle()
    val recentEpisodics by viewModel.recentEpisodics.collectAsStateWithLifecycle()
    val semanticTraces by viewModel.semanticTraces.collectAsStateWithLifecycle()
    val neuralMemoryList by viewModel.neuralMemoryList.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val selectedNodeId by viewModel.selectedNode.collectAsStateWithLifecycle()
    val rawEvolLog by viewModel.internalRuleLogs.collectAsStateWithLifecycle()
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsStateWithLifecycle()
    val researchModeEnabled by viewModel.researchModeEnabled.collectAsStateWithLifecycle()
    val lastIdentityDrift by viewModel.lastIdentityDrift.collectAsStateWithLifecycle()
    val lastDriftTargetConcept by viewModel.lastDriftTargetConcept.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Narrative Log, 1: Episodic Logs, 2: Semantic Spaces

    // High fidelity infinite breathing loops
    val infiniteTransition = rememberInfiniteTransition(label = "global_cosmic_pulse")
    
    // Slow cyclical background color shift (Dark Cobalt to Dark Violet)
    val cosmicBgShiftColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF070B19),
        targetValue = Color(0xFF0F071D),
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_color_shift"
    )

    // Breathing telemetry ring scale & alpha for network elements
    val beaconPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "beacon_scale"
    )
    val beaconPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseOutQuad),
            repeatMode = RepeatMode.Restart
        ),
        label = "beacon_alpha"
    )

    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    var dismissDriftAlert by remember { mutableStateOf(false) }

    LaunchedEffect(lastIdentityDrift) {
        if (lastIdentityDrift > 0.15) {
            dismissDriftAlert = false
            android.widget.Toast.makeText(
                context,
                "⚠️ POTENTIAL IDENTITY DRIFT DETECTED: Desviación con '$lastDriftTargetConcept' (Λ-Drift: ${"%.2f".format(lastIdentityDrift)})",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    val onCopyMarkdown = {
        val markdown = viewModel.generateMarkdownReport()
        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(markdown))
        android.widget.Toast.makeText(
            context,
            "S.A.F. Journal copiado al portapapeles",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    val onExportMarkdown = {
        val markdown = viewModel.generateMarkdownReport()
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, markdown)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "S.A.F. Mythos Organism Journal")
            type = "text/plain"
        }
        context.startActivity(android.content.Intent.createChooser(shareIntent, "Exportar Registro S.A.F."))
    }

    val onExportPdf = {
        val pdfFile = viewModel.generatePdfReport(context)
        if (pdfFile != null && pdfFile.exists()) {
            val authority = "com.example.fileprovider"
            try {
                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, pdfFile)
                val shareIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "S.A.F. Mythos Organism Journal PDF")
                    type = "application/pdf"
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(android.content.Intent.createChooser(shareIntent, "Exportar Reporte PDF S.A.F."))
            } catch (e: Exception) {
                e.printStackTrace()
                android.widget.Toast.makeText(
                    context,
                    "Error de permisos de archivo: ${e.localizedMessage}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            android.widget.Toast.makeText(
                context,
                "Error al generar reporte PDF",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Background Gradient with cosmic depth
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBackground),
        containerColor = DeepBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding().testTag("app_bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.CHAT,
                    onClick = { onScreenChange(AppScreen.CHAT) },
                    label = { Text("Chat", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Pantalla de Chat"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberTeal,
                        selectedTextColor = CyberTeal,
                        indicatorColor = CyberTeal.copy(alpha = 0.12f),
                        unselectedIconColor = Color.White.copy(alpha = 0.45f),
                        unselectedTextColor = Color.White.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.testTag("chat_nav_item")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.MYTHOS_DASHBOARD,
                    onClick = { onScreenChange(AppScreen.MYTHOS_DASHBOARD) },
                    label = { Text("Panel Mythos", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Panel Mythos Dashboard"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberTeal,
                        selectedTextColor = CyberTeal,
                        indicatorColor = CyberTeal.copy(alpha = 0.12f),
                        unselectedIconColor = Color.White.copy(alpha = 0.45f),
                        unselectedTextColor = Color.White.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.testTag("dashboard_nav_item")
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ACADEMIC_INTEGRITY,
                    onClick = { onScreenChange(AppScreen.ACADEMIC_INTEGRITY) },
                    label = { Text("Académico", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Integridad Académica"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberTeal,
                        selectedTextColor = CyberTeal,
                        indicatorColor = CyberTeal.copy(alpha = 0.12f),
                        unselectedIconColor = Color.White.copy(alpha = 0.45f),
                        unselectedTextColor = Color.White.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.testTag("academic_nav_item")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepBackground,
                            cosmicBgShiftColor, // slow breathing space gradient
                            DeepBackground
                        )
                    )
                )
        ) {
            // Real-time Unified Glassmorphic Status Bar
            val globalCoherenceVal = latestState?.coherence ?: 0.90
            GlassmorphicStatusBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                coherenceValue = globalCoherenceVal,
                isSyncing = isAnalyzing,
                isSyncEnabled = autoSyncEnabled
            )

            // Header Syntergic HUD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "S.A.F. MYTHOS ENGINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Cognitive Organism",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Evolutionary generation tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberPurple.copy(alpha = 0.2f))
                                    .border(1.dp, CyberPurple, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GEN ${viewModel.organism.evolutionGen}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberPurple
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            // Auto-Sync active/paused badge
                            val syncStatusColor = if (autoSyncEnabled) CyberTeal else Color(0xFFFBBF24)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(syncStatusColor.copy(alpha = 0.15f))
                                    .border(1.dp, syncStatusColor.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("sync_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Small dynamic dot
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(syncStatusColor)
                                    )
                                    Text(
                                        text = if (autoSyncEnabled) "SYNC ON" else "SYNC PAUSED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = syncStatusColor,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // Reset Organism Trigger
                    IconButton(
                        onClick = { viewModel.resetOrganism() },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Manual Cognitive Resection",
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Coherence Metrics Block
                val globalCoherence = latestState?.coherence ?: 0.90
                CoherenceHub(
                    globalCoherence = globalCoherence,
                    logs = rawEvolLog
                )

                // Identity Drift Alert Banner
                AnimatedVisibility(
                    visible = lastIdentityDrift > 0.15 && !dismissDriftAlert,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF3F1B1B).copy(alpha = 0.9f))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                            .testTag("potential_drift_detected_banner")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = "Identity Drift Warning Icon",
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "POTENTIAL IDENTITY DRIFT DETECTED",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171),
                                        letterSpacing = 1.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { dismissDriftAlert = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Dismiss Alerts",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚠️ Desviación ontológica crítica detectada respecto al concepto rector '$lastDriftTargetConcept'.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "La sintonía de coherencia Lambda global (Λglobal) se encuentra bajo presión debido al ruido atencional o contradicción epistémica de la información entrante.",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                lineHeight = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Métrica de Desviación (Λ-Drift):",
                                        fontSize = 8.5.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "${"%.2f".format(lastIdentityDrift)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171)
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                        .border(0.5.dp, Color(0xFFEF4444), RoundedCornerShape(4.dp))
                                        .clickable {
                                            viewModel.resetDriftState()
                                            android.widget.Toast.makeText(context, "Sintonía re-acoplada con el Identity Core", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("realign_identity_btn")
                                ) {
                                    Text(
                                        text = "RE-ALINEAR CONSTANTE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Central Area - Scrollable with dynamic list inside Box to allow inputs to dock comfortably
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sincronización Neural / Optimización de datos/energía
                    item {
                        SyncOptimizationCard(
                            autoSyncEnabled = autoSyncEnabled,
                            onToggleAutoSync = { viewModel.setAutoSyncEnabled(it) }
                        )
                    }

                    // Coherence Field Visualization Component
                    item {
                        CoherenceFieldVisualization(
                            globalCoherence = latestState?.coherence ?: 0.90,
                            modifier = Modifier.padding(bottom = 4.dp),
                            onInjectEnergy = {
                                viewModel.processPerception("Estímulo de campo: Inyección de pulso de coherencia manual")
                            }
                        )
                    }

                    // Unified Neural Memory Dashboard Component
                    item {
                        NeuralMemoryDashboardComponent(
                            recentEpisodics = recentEpisodics,
                            neuralMemoryList = neuralMemoryList,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Neural Distributed Lattice Block
                    item {
                        Column {
                            Text(
                                text = "DISTRIBUTED COGNITIVE LATTICE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.5f),
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                nodes.forEach { node ->
                                    val isSelected = selectedNodeId == node.nodeId
                                    
                                    // Tactical scaling physics
                                    val nodeScale by animateFloatAsState(
                                        targetValue = if (isSelected) 1.03f else 1.0f,
                                        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
                                        label = "node_scale"
                                    )
                                    
                                    val animatedBgColor by animateColorAsState(
                                        targetValue = if (isSelected) CardBackground.copy(alpha = 0.95f) else CardBackground.copy(alpha = 0.6f),
                                        animationSpec = tween(300),
                                        label = "node_bg_color"
                                    )
                                    
                                    val animatedBorderColor by animateColorAsState(
                                        targetValue = if (isSelected) CyberTeal else CardBorder,
                                        animationSpec = tween(300),
                                        label = "node_border_color"
                                    )

                                    GlassmorphicNodeContainer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .graphicsLayer(
                                                scaleX = nodeScale,
                                                scaleY = nodeScale
                                            ),
                                        isSelected = isSelected,
                                        backgroundColor = animatedBgColor,
                                        borderColor = animatedBorderColor,
                                        nodeId = node.nodeId,
                                        onClick = { viewModel.selectNode(node.nodeId) }
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Short name label
                                                Text(
                                                    text = node.nodeId,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) CyberTeal else Color.White
                                                )
 
                                                // State pulsing micro dot and expanding halo beacon
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.size(16.dp)
                                                ) {
                                                    if (node.status == "Active" || node.status == "Syncing") {
                                                        val haloColor = if (node.status == "Active") CyberTeal else CyberCyan
                                                        Box(
                                                            modifier = Modifier
                                                                .size(14.dp)
                                                                .graphicsLayer(
                                                                    scaleX = beaconPulseScale,
                                                                    scaleY = beaconPulseScale,
                                                                    alpha = beaconPulseAlpha
                                                                )
                                                                .clip(CircleShape)
                                                                .background(haloColor)
                                                        )
                                                    }
                                                    
                                                    Box(
                                                        modifier = Modifier
                                                            .size(7.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                when (node.status) {
                                                                    "Active" -> CyberTeal
                                                                    "Syncing" -> CyberCyan
                                                                    else -> CoherenceLow
                                                                }
                                                            )
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = node.labelName,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.White.copy(alpha = 0.65f),
                                                lineHeight = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            
                                            // STATUS hierarchy: Bold label / Light value
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "LATTICE_ST",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.4f),
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = node.status.uppercase(),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Light,
                                                    color = when (node.status) {
                                                        "Active" -> CyberTeal
                                                        "Syncing" -> CyberCyan
                                                        else -> CoherenceLow
                                                    },
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }

                                            // REF TIME hierarchy: Bold label / Light value
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "REF_MS",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.4f),
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = "${node.lastSyncTime % 10000} ms",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Light,
                                                    color = Color.White.copy(alpha = 0.75f),
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))

                                            // Λ COH hierarchy: Bold label / Light value
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Λ COH",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold, // Bold for label text
                                                    color = Color(0xFFA5F3FC),
                                                    letterSpacing = 0.5.sp
                                                )
                                                Text(
                                                    text = "%.2f".format(node.localLambda),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Light, // Light for numerical reading
                                                    color = when {
                                                        node.localLambda > 0.82 -> CoherenceHigh
                                                        node.localLambda > 0.45 -> CoherenceMid
                                                        else -> CoherenceLow
                                                    },
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            LinearProgressIndicator(
                                                progress = { node.localLambda.toFloat() },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(3.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = when {
                                                    node.localLambda > 0.82 -> CoherenceHigh
                                                    node.localLambda > 0.45 -> CoherenceMid
                                                    else -> CoherenceLow
                                                 },
                                                 trackColor = CardBorder
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Historical Coherence Evolution Chart (Graph trend over time)
                    item {
                        CoherenceTrendChart(
                            history = mythosHistory,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Consolidated Cortex Narrative Display (Mythos) / Tabs Selector
                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ORGANISM COGNITIVE ARCHIVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.5f),
                                    letterSpacing = 1.5.sp
                                )

                                // Custom sliding high-contrast neon Tab triggers
                                Row(
                                    modifier = Modifier
                                        .horizontalScroll(rememberScrollState())
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CardBackground)
                                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                                        .padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val tabs = listOf("Mythos", "Academia", "Identidad", "Mythos Registry", "Anclajes", "Memoria Neural", "Métricas", "Conceptos", "Rechazos", "Campo Coherencia")
                                    tabs.forEachIndexed { index, label ->
                                        val isSelected = activeTab == index
                                        val animatedBgColor by animateColorAsState(
                                            targetValue = if (isSelected) CyberTeal.copy(alpha = 0.12f) else Color.Transparent,
                                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                                            label = "tab_bg"
                                        )
                                        val animatedBorderColor by animateColorAsState(
                                            targetValue = if (isSelected) CyberTeal.copy(alpha = 0.45f) else Color.Transparent,
                                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                                            label = "tab_border"
                                        )
                                        val animatedTextColor by animateColorAsState(
                                            targetValue = if (isSelected) CyberTeal else Color.White.copy(alpha = 0.45f),
                                            animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
                                            label = "tab_text"
                                        )
 
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(animatedBgColor)
                                                .border(1.dp, animatedBorderColor, RoundedCornerShape(6.dp))
                                                .clickable { activeTab = index }
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = animatedTextColor,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }
                                }
                            }
 
                            // Interactive Area showing active Tab
                            AnimatedContent(
                                targetState = activeTab,
                                label = "TabSwitcherTransition"
                            ) { tab ->
                                when (tab) {
                                    0 -> NarrativeView(
                                        latestState = latestState,
                                        historyCount = mythosHistory.size,
                                        onExportMarkdown = onExportMarkdown,
                                        onCopyMarkdown = onCopyMarkdown,
                                        onExportPdf = onExportPdf
                                    )
                                    1 -> AcademiaView(viewModel = viewModel)
                                    2 -> IdentityCoreView(viewModel = viewModel)
                                    3 -> ArchetypesView(viewModel = viewModel)
                                    4 -> NarrativeAnchorsView(viewModel = viewModel)
                                    5 -> NeuralMemoryView(viewModel = viewModel)
                                    6 -> MetricsDashboardView(viewModel = viewModel)
                                    7 -> ConceptosView(traces = semanticTraces, episodics = recentEpisodics, viewModel = viewModel)
                                    8 -> RejectionsView(viewModel = viewModel)
                                    9 -> CoherenceLogsView(viewModel = viewModel)
                                }
                            }
                        }
                    }
                    
                    // Historical continuity timeline of consolidated narratives
                    if (activeTab == 0 && mythosHistory.size > 1) {
                        item {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = "HISTORICAL IDENTITY FIELDS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.4f),
                                    letterSpacing = 1.5.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.5f)),
                                    border = BorderStroke(1.dp, CardBorder)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        mythosHistory.drop(1).take(10).forEach { item ->
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .drawBehind {
                                                        // Subtle indicator line representing continuity
                                                        drawLine(
                                                            color = CardBorder,
                                                            start = Offset(0f, size.height + 8.dp.toPx()),
                                                            end = Offset(size.width, size.height + 8.dp.toPx()),
                                                            strokeWidth = 1f
                                                        )
                                                    }
                                                    .padding(bottom = 8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Time stamp block
                                                    val timeStr = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp))
                                                    Text(
                                                        text = "Phase Phase T–$timeStr",
                                                        fontSize = 10.sp,
                                                        color = CyberCyan.copy(alpha = 0.7f),
                                                        fontWeight = FontWeight.Bold
                                                    )

                                                    Text(
                                                        text = "Λ: ${"%.2f".format(item.coherence)}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (item.coherence > 0.45) CyberTeal else CoherenceLow
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = item.narrative,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    lineHeight = 16.sp,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom padding placeholder inside scroll view
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                // Ingestion Terminal overlayed bottom panel
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DeepBackground.copy(alpha = 0.95f),
                                    DeepBackground
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Quick Perception Seeds (Suggestions)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val suggestions = listOf(
                                Triple("Estabilizar Sinarquía", "Incrementar Coherencia Sinarquía", Icons.Outlined.CheckCircle),
                                Triple("Conflicto de Red", "Introducir Conflicto Cognitivo", Icons.Outlined.Warning),
                                Triple("Evolución de Red", "Evolucionar Topología Red", Icons.Outlined.Share),
                                Triple("Sincronizar Campo", "Sincronizar campo unificado", Icons.Outlined.Refresh)
                            )
                            suggestions.forEach { (label, value, icon) ->
                                val seedColor = when (value) {
                                    "Incrementar Coherencia Sinarquía" -> CyberTeal
                                    "Introducir Conflicto Cognitivo" -> CoherenceLow
                                    "Evolucionar Topología Red" -> CyberPurple
                                    else -> CyberCyan
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(CardBackground.copy(alpha = 0.8f))
                                        .border(1.dp, seedColor.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                                        .clickable { textInput = value }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = seedColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }

                        // 'Research Mode' dynamic indicator & toggle row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackground.copy(alpha = 0.5f))
                                .border(0.5.dp, if (researchModeEnabled) CyberTeal.copy(alpha = 0.3f) else CardBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("research_mode_toggle_row"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = if (researchModeEnabled) Icons.Outlined.Search else Icons.Outlined.Info,
                                    contentDescription = "Research Mode Icon",
                                    tint = if (researchModeEnabled) CyberTeal else Color.White.copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Column {
                                    Text(
                                        text = "RESEARCH MODE (MODO INVESTIGACIÓN)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (researchModeEnabled) CyberTeal else Color.White.copy(alpha = 0.7f),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = if (researchModeEnabled) "Clasificador activo: observación, inferencia, hipótesis, evidencia, especulación" else "Clasificación epistémica desactivada",
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                            }
                            Switch(
                                checked = researchModeEnabled,
                                onCheckedChange = { viewModel.toggleResearchMode() },
                                modifier = Modifier
                                    .graphicsLayer(scaleX = 0.8f, scaleY = 0.8f)
                                    .testTag("research_mode_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberTeal,
                                    checkedTrackColor = CyberTeal.copy(alpha = 0.3f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                )
                            )
                        }

                        // Text input field glow
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("perception_input"),
                                placeholder = {
                                    Text(
                                        text = "Infundir estímulo cognitivo al sistema...",
                                        color = Color.White.copy(alpha = 0.3f),
                                        fontSize = 13.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberTeal,
                                    unfocusedBorderColor = CardBorder,
                                    focusedContainerColor = CardBackground,
                                    unfocusedContainerColor = CardBackground,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                enabled = !isAnalyzing
                            )

                            // Sending action button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isAnalyzing || textInput.isBlank()) CardBorder else CyberTeal
                                    )
                                    .clickable(enabled = !isAnalyzing && textInput.isNotBlank()) {
                                        viewModel.processPerception(textInput)
                                        textInput = ""
                                    }
                                    .testTag("send_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isAnalyzing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Send,
                                        contentDescription = "Submit Perception",
                                        tint = if (textInput.isBlank()) Color.White.copy(alpha = 0.3f) else DeepBackground,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoherenceHub(globalCoherence: Double, logs: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "hub_resonance")
    
    val scaleHalo1 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo1"
    )
    val scaleHalo2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine, delayMillis = 1250),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo2"
    )

    // Scanning radar rotation and breathing pulse for active running system look
    val scanRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hub_circular_scan_angle"
    )
    val hubPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hub_aura_pulse"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.75f)), // Glassmorphism backdrop transparency
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glow radial progress indicator with active scanning radar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .drawBehind {
                        val baseRadius = size.minDimension / 2.3f
                        
                        // Halo wave 1 (Expanding)
                        drawCircle(
                            color = if (globalCoherence > 0.45) CyberTeal.copy(alpha = 0.08f) else CoherenceLow.copy(alpha = 0.1f),
                            radius = baseRadius * scaleHalo1 * hubPulseScale,
                            center = center
                        )
                        
                        // Halo wave 2 (Dynamic shimmer)
                        drawCircle(
                            color = if (globalCoherence > 0.45) CyberCyan.copy(alpha = 0.04f) else CoherenceLow.copy(alpha = 0.06f),
                            radius = baseRadius * scaleHalo2 * hubPulseScale,
                            center = center
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // High-tech sweeping radar scanner arc behind the indicator
                Canvas(
                    modifier = Modifier
                        .size(68.dp)
                        .graphicsLayer(rotationZ = scanRotation)
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                CyberTeal.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 140f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 4.dp.toPx(),
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )
                }

                CircularProgressIndicator(
                    progress = { globalCoherence.toFloat() },
                    modifier = Modifier
                        .size(68.dp)
                        .graphicsLayer(
                            scaleX = hubPulseScale,
                            scaleY = hubPulseScale
                        ),
                    color = if (globalCoherence > 0.45) CyberTeal else CoherenceLow,
                    trackColor = CardBorder.copy(alpha = 0.3f),
                    strokeWidth = 6.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(globalCoherence * 100).toInt()}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Λ GLOBAL",
                        fontSize = 8.sp,
                        color = Color(0xFFA5F3FC), // Micro-typography: Light cian to protect eyes
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Integration state & structured weight-contrasted evolution values
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (globalCoherence >= 0.8) "ESTADO: SINARQUÍA UNIFICADA"
                           else if (globalCoherence >= 0.45) "ESTADO: HISTÓRICO ESTABLE"
                           else "SISTEMA COMPRIMIÉNDOSE (REWRITE PROTOCOL)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (globalCoherence >= 0.8) CyberTeal
                            else if (globalCoherence >= 0.45) CyberCyan
                            else CoherenceLow,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                // Structured list parsing raw logs into elegant Bold/Light pairs
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    logs.lineSequence().filter { it.isNotBlank() }.forEach { line ->
                        val parts = line.split(":", limit = 2)
                        if (parts.size == 2) {
                            val label = parts[0].trim()
                            val value = parts[1].trim()
                            
                            val displayLabel = when {
                                label.contains("Evolution Gen", ignoreCase = true) -> "Evolution"
                                label.contains("Synchronization Coefficient", ignoreCase = true) -> "Sync Coeff"
                                label.contains("Perception Sensitivity", ignoreCase = true) -> "Sensitivity"
                                label.contains("Latest Phase Catalyst", ignoreCase = true) -> "Catalyst"
                                else -> label
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayLabel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold, // Bold for Label
                                    color = Color(0xFFA5F3FC), // Light cian for label icons
                                    letterSpacing = 0.3.sp
                                )
                                Text(
                                    text = value,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Light, // Light for Data values
                                    color = Color(0xFFF8FAFC), // Off-white for high legibility
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Text(
                                text = line.trim(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFF8FAFC),
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NarrativeView(
    latestState: MythosStateRecord?,
    historyCount: Int,
    onExportMarkdown: () -> Unit = {},
    onCopyMarkdown: () -> Unit = {},
    onExportPdf: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Global Identity",
                        tint = CyberTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NÚCLEO NARRATIVO RECONSTRUIDO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 0.5.sp
                    )
                }

                // Stage pill tag
                val stageText = when (latestState?.evolutionStage) {
                    3 -> "INTEGRADO"
                    2 -> "ESTABLE"
                    1 -> "REESCRITO"
                    else -> "REGISTRO"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberTeal.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stageText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val fullNarrative = latestState?.narrative ?: "Presencia silenciosa a la espera de un primer estímulo. Inyecta eventos o percepciones al sensorium global para despertar el relato del sistema."
            var visibleNarrative by remember(fullNarrative) { mutableStateOf("") }
            
            LaunchedEffect(fullNarrative) {
                visibleNarrative = ""
                // Adapts velocity based on character length for optimal reading pacing
                val stepSize = (fullNarrative.length / 120).coerceAtLeast(1)
                for (i in 0..fullNarrative.length step stepSize) {
                    visibleNarrative = fullNarrative.substring(0, i.coerceAtMost(fullNarrative.length))
                    kotlinx.coroutines.delay(10)
                }
                visibleNarrative = fullNarrative
            }

            Text(
                text = visibleNarrative,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif
            )

            if (latestState != null) {
                Spacer(modifier = Modifier.height(14.dp))
                // Beautiful thin high-tech divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(CardBorder.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Row 1: Copiar Journal (Primary key action)
                Button(
                    onClick = onCopyMarkdown,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberTeal.copy(alpha = 0.08f),
                        contentColor = CyberTeal
                    ),
                    border = BorderStroke(1.5.dp, CyberTeal.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("copy_markdown_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Copiar",
                        modifier = Modifier.size(15.dp),
                        tint = CyberTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copiar Journal de Consciencia",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = CyberTeal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Exportar .MD and Exportar PDF side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Exportar .md Button (Outlined themed style with light bg)
                    Button(
                        onClick = onExportMarkdown,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan.copy(alpha = 0.08f),
                            contentColor = CyberCyan
                        ),
                        border = BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("export_markdown_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Exportar Markdown",
                            modifier = Modifier.size(14.dp),
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Exportar .MD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = CyberCyan
                        )
                    }

                    // Exportar PDF Button (Solid themed style in CyberTeal with soft edge glow border)
                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberTeal,
                            contentColor = DeepBackground
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("export_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Exportar PDF",
                            modifier = Modifier.size(14.dp),
                            tint = DeepBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Exportar PDF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = DeepBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val formattedTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(latestState.timestamp))
                    Text(
                        text = "Fusión consolidada T–$formattedTime",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )

                    Text(
                        text = "Profundidad Histórica: $historyCount fases",
                        fontSize = 10.sp,
                        color = CyberCyan.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EpisodicLogsView(episodics: List<EpisodicMemory>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (episodics.isEmpty()) {
                Text(
                    text = "No hay experiencias guardadas en el hipocampo episódico local.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                Text(
                    text = "ESTADO DE EXPERIENCIAS SENSORIALES REGISTRADAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    episodics.take(8).forEach { memory ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(DeepBackground.copy(alpha = 0.3f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = memory.text,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(memory.timestamp))
                                    Text(
                                        text = "T–$time",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = "Origen: ${memory.nodeSource}",
                                        fontSize = 9.sp,
                                        color = CyberCyan.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (memory.localLambda > 0.45) CyberTeal.copy(alpha = 0.15f) else CoherenceLow.copy(
                                            alpha = 0.17f
                                        )
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Λ: ${"%.2f".format(memory.localLambda)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (memory.localLambda > 0.45) CyberTeal else CoherenceLow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SemanticSpacesView(traces: List<SemanticTrace>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (traces.isEmpty()) {
                Text(
                    text = "El mapa espacial de firmas semánticas está latente.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                Text(
                    text = "MATRIZ VECTORIAL SEMÁNTICA (32 DIMENSIONES)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberPurple,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    traces.take(5).forEach { trace ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepBackground.copy(alpha = 0.4f))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trace.concept,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Confianza: ${"%.2f".format(trace.confidence)}",
                                    fontSize = 10.sp,
                                    color = CyberTeal,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Miniature blocks of vector values (32 dims)
                            val vector = trace.getVector()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(1.5.dp)
                            ) {
                                vector.forEach { value ->
                                    // Scale value between 0.0 and 1.0 to map to color channel alpha intensity
                                    val absValue = abs(value).coerceIn(0.0f, 1.0f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(10.dp)
                                            .background(
                                                color = CyberPurple.copy(alpha = absValue),
                                                shape = RoundedCornerShape(1.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoherenceTrendChart(
    history: List<MythosStateRecord>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("coherence_trend_chart_card"),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header for Chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Historial de Coherencia",
                        tint = CyberTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HISTORIAL CRONOLÓGICO DE COHERENCIA (Λ)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                }

                // Node count
                val recordCount = history.size
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberCyan.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$recordCount REGISTROS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats metric bar
            if (history.isNotEmpty()) {
                val average = history.map { it.coherence }.average()
                val minVal = history.map { it.coherence }.minOrNull() ?: 0.0
                val maxVal = history.map { it.coherence }.maxOrNull() ?: 1.0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatMetric(label = "Promedio Λ", value = "%.2f".format(average), color = CyberTeal)
                    StatMetric(label = "Mínima Λ", value = "%.2f".format(minVal), color = if (minVal < 0.42) CoherenceLow else CyberCyan)
                    StatMetric(label = "Máxima Λ", value = "%.2f".format(maxVal), color = CyberTeal)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // High Fidelity Line Chart with Gradient Paint Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(DeepBackground.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .border(1.dp, CardBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            ) {
                if (history.size < 2) {
                    // Empty or loading representation
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Esperando inducción",
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Historial en fase de inducción inicial",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Inyecte más estímulos para graficar la resonancia temporal",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.3f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else {
                    val sortedHistory = remember(history) { history.sortedBy { it.timestamp } }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("coherence_trend_line_canvas")
                    ) {
                        val width = size.width
                        val height = size.height

                        // Draw helper horizontal grid target lines
                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val ratio = i.toFloat() / gridLines
                            val y = height * ratio
                            drawLine(
                                color = CardBorder.copy(alpha = 0.25f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1f
                            )
                        }

                        // Draw Collapse Limit Threshold (0.42) from Jacobo Grinberg's Sintérgica Theory
                        val thresholdY = height * (1f - 0.42f)
                        drawLine(
                            color = CoherenceLow.copy(alpha = 0.45f),
                            start = Offset(0f, thresholdY),
                            end = Offset(width, thresholdY),
                            strokeWidth = 1.5f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(8f, 6f), 0f
                            )
                        )

                        // Compute points coordinates
                        val pointsCount = sortedHistory.size
                        val stepX = width / (pointsCount - 1)
                        val path = androidx.compose.ui.graphics.Path()
                        val areaPath = androidx.compose.ui.graphics.Path()

                        sortedHistory.forEachIndexed { idx, item ->
                            val x = idx * stepX
                            val y = height * (1f - item.coherence.toFloat()).coerceIn(0f, 1f)

                            if (idx == 0) {
                                path.moveTo(x, y)
                                areaPath.moveTo(x, height)
                                areaPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                areaPath.lineTo(x, y)
                            }

                            if (idx == pointsCount - 1) {
                                areaPath.lineTo(x, height)
                                areaPath.close()
                            }
                        }

                        // Draw glowing ambient fill under the curve
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CyberTeal.copy(alpha = 0.22f),
                                    CyberTeal.copy(alpha = 0.0f)
                                )
                            )
                        )

                        // Draw sharp high-tech line path
                        drawPath(
                            path = path,
                            color = CyberTeal,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 3f,
                                join = androidx.compose.ui.graphics.StrokeJoin.Round,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )

                        // Draw micro-sensors/nodes on capture points
                        sortedHistory.forEachIndexed { idx, item ->
                            val x = idx * stepX
                            val y = height * (1f - item.coherence.toFloat()).coerceIn(0f, 1f)

                            drawCircle(
                                color = if (item.coherence < 0.42) CoherenceLow else CyberCyan,
                                radius = 3.5f,
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = if (item.coherence < 0.42) CoherenceLow.copy(alpha = 0.3f) else CyberCyan.copy(alpha = 0.3f),
                                radius = 7f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }

            if (history.size >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp, 2.dp)
                                .background(CoherenceLow.copy(alpha = 0.7f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Límite de Colapso (Λ < 0.42)",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Text(
                        text = "Trayectoria: de izquierda (antiguo) a derecha (reciente)",
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatMetric(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SyncOptimizationCard(
    autoSyncEnabled: Boolean,
    onToggleAutoSync: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_card_pulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sync_icon_aura"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sync_icon_aura_alpha"
    )

    val animatedBorderColor by animateColorAsState(
        targetValue = if (autoSyncEnabled) CyberTeal.copy(alpha = 0.85f) else CardBorder,
        animationSpec = tween(400),
        label = "sync_border_color"
    )
    val animatedBorderWidth = if (autoSyncEnabled) 1.5.dp else 1.dp
    
    val animatedBgColor by animateColorAsState(
        targetValue = if (autoSyncEnabled) CardBackground.copy(alpha = 0.85f) else CardBackground.copy(alpha = 0.65f),
        animationSpec = tween(400),
        label = "sync_bg_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sync_optimization_card")
            .clickable { onToggleAutoSync(!autoSyncEnabled) }, // Multi-touch ergonomic click-anywhere toggle as requested
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        border = BorderStroke(animatedBorderWidth, animatedBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // High-fidelity sync status icon with custom breathing glowing halo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(38.dp)
                ) {
                    if (autoSyncEnabled) {
                        // Outer glowing breathing aura ring (Glow effect)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .graphicsLayer(
                                    scaleX = auraScale,
                                    scaleY = auraScale,
                                    alpha = auraAlpha
                                )
                                .border(1.5.dp, CyberTeal, CircleShape)
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(if (autoSyncEnabled) CyberTeal.copy(alpha = 0.15f) else Color(0xFFFBBF24).copy(alpha = 0.12f))
                            .border(1.dp, if (autoSyncEnabled) CyberTeal.copy(alpha = 0.6f) else Color(0xFFFBBF24).copy(alpha = 0.45f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (autoSyncEnabled) Icons.Outlined.Refresh else Icons.Outlined.Warning,
                            contentDescription = "Estado de Sincronización",
                            tint = if (autoSyncEnabled) CyberTeal else Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AUTOPILOTO DE SINCRONIZACIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (autoSyncEnabled) CyberTeal else Color(0xFFFBBF24),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (autoSyncEnabled) "Alineación Neural Fluida" else "Sincronización Pausada",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = if (autoSyncEnabled) 
                            "Todos los nodos de red alinean sus campos de resonancia local de forma automática." 
                            else "Ahorro de batería y datos móviles activos. Nodos periféricos en modo de espera.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = autoSyncEnabled,
                onCheckedChange = onToggleAutoSync,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CyberTeal,
                    uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                    uncheckedTrackColor = CardBorder,
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = CardBorder
                ),
                modifier = Modifier.testTag("sync_switch")
            )
        }
    }
}

@Composable
fun CoherenceFieldVisualization(
    globalCoherence: Double,
    modifier: Modifier = Modifier,
    onInjectEnergy: () -> Unit = {}
) {
    var fieldDensityMode by remember { mutableIntStateOf(1) } // 0: Compact, 1: Harmonized (Balanced), 2: Syntergic Expanded
    var selectedNodeIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Animated pulse trigger on click
    val activeRippleProgress = remember { Animatable(0f) }

    // Floating node models
    val nodeTitles = listOf(
        "Córtex Narrativo",
        "Sintonizador Lambda",
        "Hipercampo Coherente",
        "ADN de Identidad",
        "Memoria Episódica",
        "Lattice de Jacobo"
    )
    
    // We can define the base coordinate ratios of each node
    val basePositions = listOf(
        Offset(0.25f, 0.28f), // Top Left
        Offset(0.50f, 0.45f), // Center-mid
        Offset(0.75f, 0.24f), // Top Right
        Offset(0.30f, 0.72f), // Bottom Left
        Offset(0.70f, 0.68f), // Bottom Right
        Offset(0.18f, 0.50f)  // Center Left
    )

    // Node connections: each pair is a pair of indices
    val connections = listOf(
        Pair(0, 1),
        Pair(1, 2),
        Pair(0, 5),
        Pair(1, 5),
        Pair(1, 3),
        Pair(1, 4),
        Pair(3, 5),
        Pair(3, 4),
        Pair(2, 4)
    )

    // Infinite transition for fluid floating coordinate shift and connection packets
    val infiniteTransition = rememberInfiniteTransition(label = "coherence_field_flow_transitions")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "energy_flow_wave"
    )

    val driftOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831853f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "node_drift_radian"
    )

    // Breathing pulse for the whole canvas aura
    val breathingAuraScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "global_aura_breathing"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("coherence_field_card"),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Signos del Campo",
                        tint = CyberTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CAMPO DE COHERENCIA MULTI-NIVEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                }

                // Interactive Indicator Mode
                val modeLabel = when (fieldDensityMode) {
                    0 -> "CONCENTRADO"
                    1 -> "ARMÓNICO"
                    else -> "SINTÉRGICO EXP"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberTeal.copy(alpha = 0.12f))
                        .border(0.5.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = modeLabel,
                        fontSize = 8.sp,
                        color = CyberTeal,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Central Interactive Display Area (Canvas rendering deep gradient + interactive interconnected D3-style node mesh)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepBackground)
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            ) {
                // Background Gradient with canvas rendering
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(fieldDensityMode, driftOffset) {
                            detectTapGestures { offset ->
                                val width = this.size.width.toFloat()
                                val height = this.size.height.toFloat()
                                
                                // Find if a node was clicked
                                var clickedIndex: Int? = null
                                for (i in basePositions.indices) {
                                    val basePos = basePositions[i]
                                    
                                    // Scale coordinates based on density
                                    val scaleFactor = when (fieldDensityMode) {
                                        0 -> 0.7f
                                        1 -> 0.9f
                                        else -> 1.15f
                                    }
                                    
                                    // Calculate floating coordinates with trig
                                    val phase = i * 1.05f
                                    val driftX = kotlin.math.sin(driftOffset + phase) * 0.04f
                                    val driftY = kotlin.math.cos(driftOffset * 0.8f + phase) * 0.04f
                                    
                                    val cx = (0.5f + (basePos.x - 0.5f) * scaleFactor + driftX) * width
                                    val cy = (0.5f + (basePos.y - 0.5f) * scaleFactor + driftY) * height
                                    
                                    val dist = kotlin.math.sqrt((offset.x - cx) * (offset.x - cx) + (offset.y - cy) * (offset.y - cy))
                                    if (dist <= 30.dp.toPx()) { // 30dp click buffer
                                        clickedIndex = i
                                        break
                                    }
                                }
                                selectedNodeIndex = clickedIndex
                                if (clickedIndex != null) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Nodo seleccionado: ${nodeTitles[clickedIndex]}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        .testTag("coherence_field_canvas")
                ) {
                    val w = size.width
                    val h = size.height

                    // 1. Draw space background depth gradient
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0C2442), // Ocean deep blue
                                Color(0xFF030E20), // Navy depth
                                DeepBackground    // Base black
                            ),
                            center = Offset(w / 2f, h / 2f),
                            radius = w * 0.85f * breathingAuraScale
                        )
                    )

                    // Draw subtle grid dots
                    val rows = 8
                    val cols = 8
                    for (r in 1 until rows) {
                        for (c in 1 until cols) {
                            drawCircle(
                                color = CyberCyan.copy(alpha = 0.08f),
                                radius = 1.25f,
                                center = Offset(w * (c.toFloat() / cols), h * (r.toFloat() / rows))
                            )
                        }
                    }

                    // 2. Compute dynamic floating positions of all nodes
                    val computedPositions = ArrayList<Offset>()
                    for (i in basePositions.indices) {
                        val basePos = basePositions[i]
                        val scaleFactor = when (fieldDensityMode) {
                            0 -> 0.72f
                            1 -> 0.90f
                            else -> 1.15f
                        }
                        // Add floating drift offsets
                        val phase = i * 1.05f
                        val driftX = kotlin.math.sin(driftOffset + phase) * 0.045f
                        val driftY = kotlin.math.cos(driftOffset * 0.83f + phase) * 0.045f
                        
                        val finalX = (0.5f + (basePos.x - 0.5f) * scaleFactor + driftX) * w
                        val finalY = (0.5f + (basePos.y - 0.5f) * scaleFactor + driftY) * h
                        computedPositions.add(Offset(finalX, finalY))
                    }

                    // 3. Draw connection fibers/lines (D3 dynamic edges)
                    connections.forEach { conn ->
                        val p1 = computedPositions[conn.first]
                        val p2 = computedPositions[conn.second]

                        // Check if nodes are selected
                        val isPartiallySelected = conn.first == selectedNodeIndex || conn.second == selectedNodeIndex
                        val lineAlpha = if (isPartiallySelected) 0.75f else (globalCoherence.toFloat() * 0.28f).coerceIn(0.12f, 0.45f)
                        val lineColor = if (isPartiallySelected) CyberTeal else CyberCyan

                        // Draw continuous cyber string
                        drawLine(
                            color = lineColor.copy(alpha = lineAlpha),
                            start = p1,
                            end = p2,
                            strokeWidth = if (isPartiallySelected) 2.2f else 1.2f
                        )

                        // 4. Particle Pulses flowing through the network fibers
                        val currentX = p1.x + (p2.x - p1.x) * waveOffset
                        val currentY = p1.y + (p2.y - p1.y) * waveOffset
                        
                        drawCircle(
                            color = CyberTeal,
                            radius = if (globalCoherence > 0.75) 3.5f else 2.5f,
                            center = Offset(currentX, currentY)
                        )
                    }

                    // 5. Draw global active energy ripples expanding outward on click
                    if (activeRippleProgress.value > 0.01f && activeRippleProgress.value < 0.99f) {
                        val origin = if (selectedNodeIndex != null && selectedNodeIndex!! < computedPositions.size) {
                            computedPositions[selectedNodeIndex!!]
                        } else {
                            Offset(w / 2f, h / 2f)
                        }
                        
                        drawCircle(
                            color = CyberTeal.copy(alpha = 0.55f * (1f - activeRippleProgress.value)),
                            radius = w * 0.65f * activeRippleProgress.value,
                            center = origin,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                        )
                    }

                    // 6. Draw individual nodes
                    for (i in computedPositions.indices) {
                        val pos = computedPositions[i]
                        val isSelected = selectedNodeIndex == i
                        val nodeColor = when (i) {
                            1 -> CyberTeal     // Prime lambda core
                            3 -> CyberPurple   // DNA
                            0, 2 -> CyberCyan  // Cortex / Coherence
                            else -> Color.White.copy(alpha = 0.85f)
                        }

                        // Local dynamic pulse
                        val baseNodePulse = 1f + kotlin.math.sin(driftOffset * 2f + i) * 0.12f

                        // Outer glowing aura
                        drawCircle(
                            color = nodeColor.copy(alpha = if (isSelected) 0.2f else 0.06f),
                            radius = 22.dp.toPx() * baseNodePulse * (if (isSelected) 1.5f else 1f),
                            center = pos
                        )

                        // Outer halo outline
                        drawCircle(
                            color = nodeColor.copy(alpha = if (isSelected) 0.88f else 0.25f),
                            radius = 12.dp.toPx() * (if (isSelected) 1.25f else 1f),
                            center = pos,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = if (isSelected) 2.dp.toPx() else 1.dp.toPx())
                        )

                        // Core active solid node dot
                        drawCircle(
                            color = nodeColor,
                            radius = if (isSelected) 7.dp.toPx() else 5.dp.toPx(),
                            center = pos
                        )
                    }
                }

                // Selected Node HUD Overlay card (Fully responsive floating dialog overlay)
                if (selectedNodeIndex != null) {
                    val sIndex = selectedNodeIndex!!
                    val sName = nodeTitles[sIndex]
                    val sDesc = when (sIndex) {
                        0 -> "Nuclea la sintonía asociativa agregando transcripciones, metadatos y conceptos rectores."
                        1 -> "Regula dinámicamente la sintonía de coherencia Lambda actual del hipercampo."
                        2 -> "Orquesta la constante teórica de Jacobo Grinberg sobre la unificación de lattices."
                        3 -> "Ancla el código invariable de ADN que representa el núcleo central de la app."
                        4 -> "Almacena los flujos episódicos consolidados en microcapas mnemotécnicas."
                        else -> "Sostiene la estructura de acoplamiento modelo-realidad contra el ruido estocástico."
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground.copy(alpha = 0.94f))
                            .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sName.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal
                                )
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { selectedNodeIndex = null }
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Cerrar HUD",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                            Text(
                                text = sDesc,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Triggers Panel at the bottom of the card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trigger ripples
                Button(
                    onClick = {
                        onInjectEnergy()
                        // Launch ripple animation
                        scope.launch {
                            activeRippleProgress.snapTo(0f)
                            activeRippleProgress.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(1250, easing = EaseOutExpo)
                            )
                        }
                        android.widget.Toast.makeText(context, "💥 ¡Impulso de Coherencia Inyectado en S.A.F.!", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("inject_pulse_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberTeal,
                        contentColor = DeepBackground
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("INYECTAR PULSO", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Change coordinates factor (Field configuration)
                OutlinedButton(
                    onClick = {
                        fieldDensityMode = (fieldDensityMode + 1) % 3
                        val modeStr = when (fieldDensityMode) {
                            0 -> "Concentrado (Compacto)"
                            1 -> "Armónico (Equilibrado)"
                            else -> "Hipercampo (Expandido)"
                        }
                        android.widget.Toast.makeText(context, "Resonancia adaptada: $modeStr", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("change_density_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = CyberCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NODO MESH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ArchetypesView(viewModel: MythosViewModel) {
    val archetypes by viewModel.narrativeArchetypes.collectAsStateWithLifecycle()
    val isSynthesizing by viewModel.isSynthesizingArchetype.collectAsStateWithLifecycle()
    val lastSynthesized by viewModel.lastSynthesizedArchetype.collectAsStateWithLifecycle()
    val identityInvariants by viewModel.identityInvariants.collectAsStateWithLifecycle()
    
    // Voice Portal States (Unified native & simulated Web Speech interface)
    val isListening by viewModel.isSpeechListening.collectAsStateWithLifecycle()
    val partialText by viewModel.speechPartialText.collectAsStateWithLifecycle()
    val speechFinalText by viewModel.speechFinalText.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.speechRmsDb.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()
    
    // Web Speech Live Tagging States
    val voiceFragmentTranscription by viewModel.voiceFragmentTranscription.collectAsStateWithLifecycle()
    val voiceTaggedConcept by viewModel.voiceTaggedConcept.collectAsStateWithLifecycle()
    val voiceTaggedCategory by viewModel.voiceTaggedCategory.collectAsStateWithLifecycle()
    val voiceTagSimilarity by viewModel.voiceTagSimilarity.collectAsStateWithLifecycle()
    val isVoiceTaggingActive by viewModel.isVoiceTaggingActive.collectAsStateWithLifecycle()
    val voiceGeneratedArchetype by viewModel.voiceGeneratedArchetype.collectAsStateWithLifecycle()
    
    // Form Selection Mode
    var registryFormMode by remember { mutableIntStateOf(0) } // 0: Cortex IA, 1: Portal de Voz (Web Speech), 2: manual
    
    // Form Input States
    var ideaInput by remember { mutableStateOf("") }
    
    var manualName by remember { mutableStateOf("") }
    var manualDesc by remember { mutableStateOf("") }
    var manualSnippet by remember { mutableStateOf("") }
    var manualCoherence by remember { mutableDoubleStateOf(0.85) }
    
    // Mapped identity concept state matching
    var selectedConcept by remember { mutableStateOf("None") }
    var selectedCategory by remember { mutableStateOf("None") }
    
    val context = LocalContext.current
    
    // Live update input field with voice transcription
    LaunchedEffect(partialText, isListening) {
        if (isListening && partialText.isNotBlank()) {
            ideaInput = partialText
        }
    }
    
    // Automatically process spoken fragments in the Web Speech Portal tab
    LaunchedEffect(speechFinalText, isListening) {
        if (!isListening && speechFinalText.isNotBlank() && registryFormMode == 1) {
            viewModel.tagAndSynthesizeSpokenFragment(speechFinalText)
        }
    }
    
    // Android runtime microphone authorization launcher
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceCapture()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High fidelity Tab Selector inside Mythos Registry
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardBackground)
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                .padding(3.dp)
        ) {
            val modes = listOf("Procesador de Cortex (IA)", "Módulo de Voz (Web Speech)", "Formulario Manual Directo")
            modes.forEachIndexed { idx, title ->
                val isSel = registryFormMode == idx
                val bgCol by animateColorAsState(
                    targetValue = if (isSel) CyberTeal.copy(alpha = 0.15f) else Color.Transparent,
                    label = "mode_bg"
                )
                val textCol by animateColorAsState(
                    targetValue = if (isSel) CyberTeal else Color.White.copy(alpha = 0.5f),
                    label = "mode_text"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(bgCol)
                        .clickable { registryFormMode = idx }
                        .padding(vertical = 10.dp)
                        .testTag("mode_tab_$idx"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textCol,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (registryFormMode == 0) {
            // IA SYNTHESIZER
            Card(
                modifier = Modifier.fillMaxWidth().testTag("ai_archetype_form"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SINTETIZADOR DE ARQUETIPOS DE MYTHOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Inyecta una semilla conceptual hablando por el micrófono o escribiendo (ej. 'Eterno Retorno', 'Guardián del Vacío') para compilar un arquetipo poético a través del LLM Cortex.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ideaInput,
                            onValueChange = { ideaInput = it },
                            placeholder = {
                                Text(
                                    text = if (isListening) "Escuchando... hable ahora" else "Escribe o habla la idea...",
                                    color = Color.White.copy(alpha = 0.35f),
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("archetype_input_field"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = CyberTeal,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                                unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        // VOICE INPUT MIC TRIGGER
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(if (isListening) CyberTeal.copy(alpha = 0.15f) else DeepBackground)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isListening) CyberTeal else CardBorder,
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (isListening) {
                                        viewModel.stopVoiceCapture()
                                    } else {
                                        val checkPerm = androidx.core.content.ContextCompat.checkSelfPermission(
                                            context,
                                            android.Manifest.permission.RECORD_AUDIO
                                        )
                                        if (checkPerm == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            viewModel.startVoiceCapture()
                                        } else {
                                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                                .testTag("archetype_mic_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isListening) {
                                Text("🛑", fontSize = 18.sp)
                            } else {
                                Text("🎙️", fontSize = 18.sp)
                            }
                        }
                    }
                    
                    // Dynamic Audio Waveform Indicator on active record
                    if (isListening) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTeal.copy(alpha = 0.08f))
                                .border(BorderStroke(0.5.dp, CyberTeal.copy(alpha = 0.2f)), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val pulseTransition = rememberInfiniteTransition(label = "pulse")
                                val alphaScale by pulseTransition.animateFloat(
                                    initialValue = 0.3f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(800, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "alpha"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .graphicsLayer { alpha = alphaScale }
                                        .clip(CircleShape)
                                        .background(CyberTeal)
                                )
                                
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until 12) {
                                        val targetHeight = remember(rmsLevel) {
                                            val baseVal = if (rmsLevel > 0.05f) rmsLevel else 0.1f
                                            val multiplier = when (i % 3) {
                                                0 -> 0.7f
                                                1 -> 1.0f
                                                else -> 0.4f
                                            }
                                            (baseVal * multiplier * 16f).coerceIn(3f, 16f)
                                        }
                                        val animatedHeight by animateFloatAsState(
                                            targetValue = targetHeight,
                                            label = "bar_height"
                                        )
                                        Box(
                                            modifier = Modifier
                                                .width(2.5.dp)
                                                .height(animatedHeight.dp)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(CyberTeal.copy(alpha = 0.8f))
                                        )
                                    }
                                }
                                
                                Text(
                                    text = "MODULANDO ESPECTRO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                    
                    // Audio Error Message
                    if (speechError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ $speechError",
                            color = CoherenceLow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Identity Core mapping selection row
                    Text(
                        text = "VINCULAR ARQUETIPO CON CONCEPTOS DEL IDENTITY CORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "Ninguno" custom chip
                        val isNoneSelected = selectedConcept == "None"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isNoneSelected) CyberTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                .border(
                                    1.dp,
                                    if (isNoneSelected) CyberTeal else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    selectedConcept = "None"
                                    selectedCategory = "None"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("invariant_chip_none"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ninguno",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNoneSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                            )
                        }

                        // Invariants list mapping selection chips
                        identityInvariants.forEach { inv ->
                            val isChipSelected = selectedConcept == inv.concept
                            val prefix = when (inv.category.lowercase()) {
                                "principle", "principles" -> "PRINCIPIO"
                                "target", "objective", "objectives" -> "OBJETIVO"
                                "framework", "conceptual framework" -> "MARCO"
                                "constraint", "constraints" -> "RESTRICCIÓN"
                                else -> inv.category.uppercase()
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChipSelected) CyberTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        1.dp,
                                        if (isChipSelected) CyberTeal else CardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        if (isChipSelected) {
                                            selectedConcept = "None"
                                            selectedCategory = "None"
                                        } else {
                                            selectedConcept = inv.concept
                                            selectedCategory = inv.category
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("invariant_chip_${inv.concept.replace(" ", "_")}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[$prefix] ${inv.concept}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChipSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedConcept == "None") "El arquetipo no estará acoplado a un invariante específico." else "Vínculo asignado: $selectedConcept ($selectedCategory)",
                        fontSize = 10.sp,
                        color = if (selectedConcept == "None") Color.White.copy(alpha = 0.4f) else CyberTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("selected_invariant_chip")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (ideaInput.isNotBlank()) {
                                viewModel.synthesizeNewArchetype(ideaInput, selectedConcept, selectedCategory)
                                ideaInput = ""
                            }
                        },
                        enabled = ideaInput.isNotBlank() && !isSynthesizing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberTeal,
                            contentColor = DeepBackground,
                            disabledContainerColor = CyberTeal.copy(alpha = 0.12f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("synthesize_archetype_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isSynthesizing) {
                            CircularProgressIndicator(
                                color = DeepBackground,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SINTETIZANDO...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SINTETIZAR ARQUETIPO COGNITIVO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        } else if (registryFormMode == 1) {
            // PORTAL DE DECODIFICACIÓN DE VOZ (WEB SPEECH)
            Card(
                modifier = Modifier.fillMaxWidth().testTag("voice_portal_form"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "PORTAL DE DECODIFICACIÓN DE VOZ (WEB SPEECH)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Activa la captura en tiempo real sustentada en el estándar de Web Speech para registrar fragmentos narrativos hablados directamente en el Mythos Registry. El sistema procesará el habla, generará la transcripción textual y aplicará una clasificación semántica con vectores de similitud contra el Identity Core.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 14.sp
                    )
                    
                    // Microphone / capture trigger
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isListening) {
                                    viewModel.stopVoiceCapture()
                                    // Automatically trigger transcription processing when they click stop
                                    if (partialText.isNotBlank()) {
                                        viewModel.tagAndSynthesizeSpokenFragment(partialText)
                                    }
                                } else {
                                    val checkPerm = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.RECORD_AUDIO
                                    )
                                    if (checkPerm == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                        viewModel.startVoiceCapture()
                                    } else {
                                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) CoherenceLow else CyberTeal,
                                contentColor = DeepBackground
                            ),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("voice_capture_toggle"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Outlined.Close else Icons.Outlined.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListening) "DETENER CAPTURA" else "INICIAR CAPTURA DE VOZ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (voiceFragmentTranscription.isNotBlank() || partialText.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.clearSpeechText()
                                    android.widget.Toast.makeText(context, "Portal de voz limpiado", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.6f)),
                                border = BorderStroke(1.dp, CardBorder),
                                modifier = Modifier.height(44.dp).testTag("voice_clear_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("LIMPIAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Acoustic activity wave
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTeal.copy(alpha = 0.08f))
                                .border(BorderStroke(0.5.dp, CyberTeal.copy(alpha = 0.2f)), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val pulseAnim = rememberInfiniteTransition("record_pulse")
                                val recorderAlpha by pulseAnim.animateFloat(
                                    initialValue = 0.4f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
                                    label = "pulse_alpha"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .graphicsLayer { alpha = recorderAlpha }
                                        .clip(CircleShape)
                                        .background(CoherenceLow)
                                )
                                Text(
                                    text = "RED DE CAPTURA ACTIVA - HABLE AHORA",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoherenceLow,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                // RMS level bar animations
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    for (i in 0 until 10) {
                                        val hVal = remember(rmsLevel) {
                                            val baseMultiplier = when (i % 3) {
                                                0 -> 0.6f
                                                1 -> 1.0f
                                                else -> 0.4f
                                            }
                                            (rmsLevel * baseMultiplier * 20f).coerceIn(4f, 20f)
                                        }
                                        val animatedH by animateFloatAsState(targetValue = hVal, label = "wave_h")
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(animatedH.dp)
                                                .background(CyberTeal)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Audio transcription display
                    val activeTextToShow = if (isListening) partialText else voiceFragmentTranscription
                    if (activeTextToShow.isNotBlank()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isListening) "DECIBELES DE HABLA / TRANSCRIPCIÓN EN CURSO (WEB SPEECH):" else "TRANSCRIPCIÓN FINALIZADA:",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isListening) CyberTeal else Color.White.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            ) {
                                SelectionContainer {
                                    Text(
                                        text = activeTextToShow,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            
                            // Let manual processing trigger if voice recognizer finished but they want to re-parse
                            if (!isListening && voiceFragmentTranscription.isBlank() && partialText.isNotBlank()) {
                                Button(
                                    onClick = { viewModel.tagAndSynthesizeSpokenFragment(partialText) },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal.copy(alpha = 0.1f), contentColor = CyberTeal)
                                ) {
                                    Text("PROCESAR TRANSCRIPCIÓN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Semantic Tagging / AI Synthesis progress
                    if (isVoiceTaggingActive) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CircularProgressIndicator(color = CyberTeal, modifier = Modifier.size(24.dp))
                            Text(
                                text = "PROCESANDO CRUCE COGNITIVO Y ETIQUETADO DE ADN...",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal
                            )
                        }
                    }

                    // Semantic Tagging results view
                    if (voiceTaggedConcept != null && !isVoiceTaggingActive) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DeepBackground),
                            border = BorderStroke(0.5.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "ETIQUETADO SEMÁNTICO AUTOMÁTICO (IDENTITY CORE)",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 0.5.sp
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "PRINCIPIO: ${voiceTaggedConcept!!.uppercase()}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "CATEGORÍA HARD DE ADN: ${voiceTaggedCategory!!.uppercase()}",
                                            fontSize = 9.sp,
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberTeal.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "SIMILITUD: ${(voiceTagSimilarity * 100).toInt()}%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Preview of the synthesized/generated archetype
                    if (voiceGeneratedArchetype != null && !isVoiceTaggingActive) {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("voice_archetype_preview"),
                            colors = CardDefaults.cardColors(containerColor = CyberTeal.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.25f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "ARQUETIPO COMPILADO DESDE FRAGMENTO HABLADO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 0.5.sp
                                )
                                
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = voiceGeneratedArchetype!!.name.uppercase(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = voiceGeneratedArchetype!!.description,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        lineHeight = 14.sp
                                    )
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.5f)),
                                    border = BorderStroke(0.5.dp, CardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "FRAGMENTO TESTIMONIAL (PHYTOM):",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                        SelectionContainer {
                                            Text(
                                                text = "\"${voiceGeneratedArchetype!!.narrativeSnippet}\"",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                lineHeight = 14.sp
                                            )
                                        }
                                    }
                                }
                                
                                Button(
                                    onClick = {
                                        viewModel.saveVoiceArchetype()
                                        android.widget.Toast.makeText(context, "¡Anclaje de Voz Cristalizado en el Registro!", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal, contentColor = DeepBackground),
                                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("save_voice_archetype_button"),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CRISTALIZAR FRAGMENTO EN EL REGISTRO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // MANUAL FORM DEFINITION
            Card(
                modifier = Modifier.fillMaxWidth().testTag("manual_archetype_form"),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CALIBRACIÓN MANUAL DE FRAGMENTO NARRATIVO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Define directamente en la base de datos local un fragmento de arquetipo personalizado, permitiendo acoplar la lattice mítica con los invariantes del sistema.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text("Nombre del Arquetipo (ej. 'Eterno Retorno')", color = CyberTeal.copy(alpha = 0.7f), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("manual_archetype_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                            unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = manualDesc,
                        onValueChange = { manualDesc = it },
                        label = { Text("Función Sistémica o Descripción conceptual", color = CyberTeal.copy(alpha = 0.7f), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("manual_archetype_desc"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                            unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = manualSnippet,
                        onValueChange = { manualSnippet = it },
                        label = { Text("Fragmento de Testimonio de PHYTOM (primera persona)", color = CyberTeal.copy(alpha = 0.7f), fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("manual_archetype_snippet"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                            unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Resonance Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Λ RESONANCIA DE TEORÍA:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${"%.2f".format(manualCoherence)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                    }
                    Slider(
                        value = manualCoherence.toFloat(),
                        onValueChange = { manualCoherence = it.toDouble() },
                        valueRange = 0.40f..1.00f,
                        modifier = Modifier.fillMaxWidth().testTag("manual_archetype_coherence"),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberTeal,
                            activeTrackColor = CyberTeal,
                            inactiveTrackColor = CardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Identity selection row
                    Text(
                        text = "VINCULAR ARQUETIPO CON CONCEPTOS DEL IDENTITY CORE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "Ninguno" custom chip
                        val isNoneSelected = selectedConcept == "None"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isNoneSelected) CyberTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                .border(
                                    1.dp,
                                    if (isNoneSelected) CyberTeal else CardBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable {
                                    selectedConcept = "None"
                                    selectedCategory = "None"
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("manual_invariant_chip_none"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ninguno",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNoneSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                            )
                        }

                        // Invariants list mapping selection chips
                        identityInvariants.forEach { inv ->
                            val isChipSelected = selectedConcept == inv.concept
                            val prefix = when (inv.category.lowercase()) {
                                "principle", "principles" -> "PRINCIPIO"
                                "target", "objective", "objectives" -> "OBJETIVO"
                                "framework", "conceptual framework" -> "MARCO"
                                "constraint", "constraints" -> "RESTRICCIÓN"
                                else -> inv.category.uppercase()
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isChipSelected) CyberTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        1.dp,
                                        if (isChipSelected) CyberTeal else CardBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        if (isChipSelected) {
                                            selectedConcept = "None"
                                            selectedCategory = "None"
                                        } else {
                                            selectedConcept = inv.concept
                                            selectedCategory = inv.category
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("manual_invariant_chip_${inv.concept.replace(" ", "_")}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[$prefix] ${inv.concept}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChipSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedConcept == "None") "El arquetipo no estará acoplado a un invariante específico." else "Vínculo asignado: $selectedConcept ($selectedCategory)",
                        fontSize = 11.sp,
                        color = if (selectedConcept == "None") Color.White.copy(alpha = 0.4f) else CyberTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("selected_invariant_chip")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (manualName.isNotBlank() && manualDesc.isNotBlank() && manualSnippet.isNotBlank()) {
                                viewModel.insertNarrativeArchetype(
                                    name = manualName,
                                    description = manualDesc,
                                    narrativeSnippet = manualSnippet,
                                    alignmentCoherence = manualCoherence,
                                    mappedConcept = selectedConcept,
                                    mappedCategory = selectedCategory
                                )
                                manualName = ""
                                manualDesc = ""
                                manualSnippet = ""
                                selectedConcept = "None"
                                selectedCategory = "None"
                                android.widget.Toast.makeText(context, "Fragmento guardado y acoplado perfectamente", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = manualName.isNotBlank() && manualDesc.isNotBlank() && manualSnippet.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberTeal,
                            contentColor = DeepBackground,
                            disabledContainerColor = CyberTeal.copy(alpha = 0.12f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("register_archetype_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "REGISTRAR FRAGMENTO DE ARCHIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
        
        // Show synthesizer feedback / loading
        if (isSynthesizing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = CyberTeal,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ALINEANDO CON EL NÚCLEO COGNITIVO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "S.A.F. está polarizando el espacio sintonizando las firmas del lenguaje vivo del Yo... Espera un momento.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
        
        // Show newly generated archetype focus
        if (lastSynthesized != null && !isSynthesizing) {
            Column {
                Text(
                    text = "NUEVO ARQUETIPO EMERGENTE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                ArchetypeCard(archetype = lastSynthesized!!, highlight = true)
            }
        }
        
        // List historical archetypes
        if (archetypes.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FIRMAS DE ARQUETIPOS DE ARCHIVO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    
                    // Button to clear all (cognitive resection of archetypes - stylized with high response)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CoherenceLow.copy(alpha = 0.12f))
                            .border(1.dp, CoherenceLow.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                            .clickable { viewModel.clearAllArchetypes() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Purgar Arquetipos",
                                tint = CoherenceLow,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "PURGAR REGISTRO",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoherenceLow,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                
                // Exclude lastSynthesized to prevent duplicates in current render frame if it matches
                val listToShow = if (lastSynthesized != null) {
                    archetypes.filter { it.id != lastSynthesized!!.id }
                } else {
                    archetypes
                }
                
                if (listToShow.isEmpty() && lastSynthesized != null) {
                    // Nothing else to show
                } else {
                    listToShow.forEach { arch ->
                        ArchetypeCard(archetype = arch, highlight = false)
                    }
                }
            }
        }
    }
}

@Composable
fun ArchetypeCard(archetype: NarrativeArchetype, highlight: Boolean) {
    val borderColor = if (highlight) CyberTeal else CardBorder
    val glowColor = if (highlight) CyberTeal.copy(alpha = 0.12f) else CardBackground
    val cardBackgroundSpec = if (highlight) CardBackground.copy(alpha = 0.9f) else CardBackground.copy(alpha = 0.5f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("archetype_record_${archetype.id}"),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundSpec),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (highlight) CyberTeal else CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = archetype.name.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
                
                // Coherence value
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (archetype.alignmentCoherence >= 0.70) CyberTeal.copy(alpha = 0.12f) else CoherenceLow.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Λ RESONANCIA: ${"%.2f".format(archetype.alignmentCoherence)}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (archetype.alignmentCoherence >= 0.70) CyberTeal else Color(0xFFF87171)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = archetype.description,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 15.sp
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Fragment in PHYTOM's voice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DeepBackground.copy(alpha = 0.4f))
                    .border(BorderStroke(0.5.dp, CardBorder), RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "TESTIMONIO DE PHYTOM:",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberPurple,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = archetype.narrativeSnippet,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 15.sp
                    )
                }
            }
            
            if (archetype.mappedIdentityConcept != "None") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberTeal.copy(alpha = 0.08f))
                        .border(BorderStroke(0.5.dp, CyberTeal.copy(alpha = 0.25f)), RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Mapped Concept Icon Badge",
                        tint = CyberTeal,
                        modifier = Modifier.size(12.dp)
                    )
                    Column {
                        val categoryDisplay = when (archetype.mappedCategory.lowercase()) {
                            "principle", "principles" -> "PRINCIPIO INTERNO"
                            "target", "objective", "objectives" -> "OBJETIVO RECTOR"
                            "framework", "conceptual framework" -> "MARCO DE REFERENCIA"
                            "constraint", "constraints" -> "RESTRICCIÓN DE COHERENCIA"
                            else -> archetype.mappedCategory.uppercase()
                        }
                        Text(
                            text = categoryDisplay,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal.copy(alpha = 0.62f),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = archetype.mappedIdentityConcept,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(archetype.timestamp))
                Text(
                    text = "Registro grabado en el núcleo: t = $formattedDate",
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.3f)
                )

                // High-tech Share Trigger
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (highlight) CyberTeal.copy(alpha = 0.12f) else CardBorder.copy(alpha = 0.5f))
                        .border(
                            width = 0.5.dp,
                            color = if (highlight) CyberTeal.copy(alpha = 0.4f) else CardBorder,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { com.example.util.ArchetypeImageGenerator.shareArchetypeCard(context, archetype) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("archetype_share_button_${archetype.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartir Tarjeta",
                        tint = if (highlight) CyberTeal else Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "COMPARTIR",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (highlight) CyberTeal else Color.White.copy(alpha = 0.6f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AcademiaView(viewModel: MythosViewModel) {
    val isGenerating by viewModel.isGeneratingPaper.collectAsStateWithLifecycle()
    val paperMarkdown by viewModel.generatedPaperMarkdown.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("academia_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ACADEMIA COGNITIVA S.A.F.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberPurple,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Compila las memorias episódicas consolidadas, las firmas semánticas holográficas y el flujo del yo en un artículo de nivel de doctorado con formato de manuscrito académico APA. S.A.F. asocia formalmente las teorías de Friston y Grinberg.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = { viewModel.generateResearchPaper() },
                    modifier = Modifier.fillMaxWidth().testTag("compile_paper_button"),
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "COMPILANDO DISERTACIÓN...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(text = "COMPILAR ARTÍCULO APA COMPLETO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (paperMarkdown != null) {
                    Button(
                        onClick = { viewModel.clearPaper() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = CoherenceLow),
                        border = BorderStroke(1.dp, CoherenceLow.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "LIMPIAR DESPACHO ACADÉMICO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (paperMarkdown != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.8f)),
                border = BorderStroke(1.2.dp, CyberPurple.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MANUSCRITO PRE-PRINT COMPILADO (APA)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberTeal.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "PDF / MD", fontSize = 8.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

                    // Markdown Viewer Space
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepBackground.copy(alpha = 0.5f))
                            .border(0.5.dp, CardBorder, RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = paperMarkdown ?: "",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IdentityCoreView(viewModel: MythosViewModel) {
    val invariants by viewModel.identityInvariants.collectAsStateWithLifecycle()
    val lastDrift by viewModel.lastIdentityDrift.collectAsStateWithLifecycle()
    val driftTarget by viewModel.lastDriftTargetConcept.collectAsStateWithLifecycle()

    var conceptInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("principle") } // principle, framework, objective, constraint

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("identity_core_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "MEMORIA DE INVARIANTES (IDENTITY CORE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "A diferencia de las narrativas mutables de Mythos, las Invariantes de Identidad son principios rectores inquebrantables. El Cortex sintonizador los inyecta en el campo atencional primario como reglas inviolables.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )

                HorizontalDivider(color = CardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                // Dynamic Identity Drift & Integrity Monitor widget
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (lastDrift > 0.15) Color(0xFF2D1616) else CardBackground.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (lastDrift > 0.15) Color(0xFFF87171).copy(alpha = 0.6f) else CyberTeal.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("identity_drift_monitor_card")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MONITOR DE DERIVA DE IDENTIDAD (ΛGLOBAL DRIFT)",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (lastDrift > 0.15) Color(0xFFF87171) else CyberTeal,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (lastDrift > 0.15) Color(0xFFF87171).copy(alpha = 0.15f)
                                        else CyberTeal.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        0.5.dp,
                                        if (lastDrift > 0.15) Color(0xFFF87171) else CyberTeal,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (lastDrift > 0.15) "⚠️ DERIVA DETECTADA" else "✓ ADN COHERENTE",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lastDrift > 0.15) Color(0xFFF87171) else CyberTeal
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distancia de Inconsistencia (Λ-Drift Metric):",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "${"%.2f".format(lastDrift)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (lastDrift > 0.15) Color(0xFFF87171) else CyberTeal
                            )
                        }

                        if (lastDrift > 0.15) {
                            Text(
                                text = "Vulneración atencional detectada con el concepto rector '$driftTarget'. La incoherencia de fase con el Identity Core está presionando la homeostasis sistémica.",
                                fontSize = 8.5.sp,
                                color = Color(0xFFF87171).copy(alpha = 0.9f),
                                lineHeight = 11.sp
                            )
                        } else {
                            Text(
                                text = "Último acoplamiento de ADN: '$driftTarget' (Sin desviación ontológica detectable).",
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = CardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                // Registration Form
                Text(
                    text = "REGISTRAR NUEVA REGLA INVARIANTE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f)
                )

                OutlinedTextField(
                    value = conceptInput,
                    onValueChange = { conceptInput = it },
                    label = { Text("Concepto Rectriz", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = valueInput,
                    onValueChange = { valueInput = it },
                    label = { Text("Definición Ontológica / Restricción", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "principle" to "PRINCIPIO",
                        "framework" to "MARCO S.A.F.",
                        "objective" to "OBJETIVO",
                        "constraint" to "RESTRICCIÓN"
                    ).forEach { (cat, label) ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) CyberTeal.copy(alpha = 0.2f) else CardBackground)
                                .border(1.dp, if (isSel) CyberTeal else CardBorder, RoundedCornerShape(4.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) CyberTeal else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (conceptInput.isNotBlank() && valueInput.isNotBlank()) {
                            viewModel.insertIdentityInvariant(conceptInput, valueInput, selectedCategory)
                            conceptInput = ""
                            valueInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("INCORPORAR INVARIANTE A LA IDENTIDAD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (invariants.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.clearIdentityInvariants() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CoherenceLow
                        ),
                        border = BorderStroke(1.2.dp, CoherenceLow.copy(alpha = 0.55f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Limpiar Invariantes",
                            modifier = Modifier.size(13.dp),
                            tint = CoherenceLow
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RESETEAR NÚCLEO DE VALORES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // List invariants
        invariants.forEach { invariant ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.4f)),
                border = BorderStroke(0.8.dp, CardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgeColor = when (invariant.category) {
                            "principle" -> CyberTeal
                            "framework" -> CyberPurple
                            "objective" -> CyberCyan
                            "constraint" -> Color(0xFFF87171)
                            else -> CyberCyan
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .border(0.5.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = invariant.category.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Rule",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.deleteIdentityInvariant(invariant) }
                        )
                    }

                    Text(
                        text = invariant.concept,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = invariant.value,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricsDashboardView(viewModel: MythosViewModel) {
    val history by viewModel.coherenceHistory.collectAsStateWithLifecycle()
    val latestState by viewModel.latestMythosState.collectAsStateWithLifecycle()
    val currentLambda = latestState?.coherence ?: 0.90

    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val unsyncedCount by viewModel.unsyncedCount.collectAsStateWithLifecycle()
    val offlineDriftLevel by viewModel.offlineDriftLevel.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("metrics_dashboard_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SITUACIÓN DE COHERENCIA MULTI-NIVEL (S.A.F.)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("COHERENCIA GLOBAL (FIELD LAMBDA Λ)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${"%.2f".format(currentLambda)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (currentLambda >= 0.45) CoherenceHigh else CoherenceLow)
                }
                LinearProgressIndicator(
                    progress = { currentLambda.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (currentLambda >= 0.45) CyberTeal else CoherenceLow,
                    trackColor = CardBorder
                )

                HorizontalDivider(color = CardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                val recentHistory = history.lastOrNull()
                val episodicCoh = recentHistory?.episodicCoherence ?: 0.85
                val semanticCoh = recentHistory?.semanticCoherence ?: 0.65
                val mythosCoh = recentHistory?.mythosCoherence ?: currentLambda

                val metrics = listOf(
                    Triple("Coherencia Episódica", episodicCoh, CyberCyan),
                    Triple("Coherencia Semántica", semanticCoh, CyberPurple),
                    Triple("Coherencia de Mythos", mythosCoh, CyberTeal)
                )

                metrics.forEach { (label, value, color) ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                            Text("${"%.2f".format(value)}", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { value.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = color,
                            trackColor = CardBorder.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .testTag("offline_drift_sync_card"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DESVIACIÓN TEMPORAL (OFFLINE DRIFT)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (offlineDriftLevel > 0.4) CoherenceLow else CyberCyan,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .background(
                                color = if (isOnline) CyberTeal.copy(alpha = 0.15f) else CoherenceLow.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) CyberTeal else CoherenceLow)
                        )
                        Text(
                            text = if (isOnline) "CONECTADO" else "DESCONECTADO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) CyberTeal else CoherenceLow
                        )
                    }
                }

                Text(
                    text = "Mide la divergencia estructural entre el estado local y la sincronización global de la nube. Un nivel más alto de drift indica pensamientos acumulados sin reconciliar offline.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nivel de Drift: ${"%.1f%%".format(offlineDriftLevel * 100.0)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pensamientos pendientes: $unsyncedCount",
                            fontSize = 11.sp,
                            color = if (unsyncedCount > 0) CyberPurple else Color.White.copy(alpha = 0.5f)
                        )
                    }

                    Button(
                        onClick = { viewModel.triggerManualSync() },
                        enabled = isOnline,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (unsyncedCount > 0) CyberTeal.copy(alpha = 0.2f) else CardBorder,
                            contentColor = if (unsyncedCount > 0) CyberTeal else Color.White.copy(alpha = 0.6f),
                            disabledContainerColor = CardBorder.copy(alpha = 0.3f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isOnline && unsyncedCount > 0) CyberTeal.copy(alpha = 0.5f) else CardBorder
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .testTag("manual_sync_button")
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Sincronizar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sincronizar",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(offlineDriftLevel.toFloat())
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (offlineDriftLevel > 0.5) {
                                        listOf(CyberCyan, CoherenceLow)
                                    } else {
                                        listOf(CyberPurple, CyberTeal)
                                    }
                                )
                            )
                    )
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "HISTORIAL DE EVOLUCIÓN DE Λ(t)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberPurple,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Rastrea la estabilidad organísmica a lo largo de las fases de aprendizaje. Los picos representan sintonías de fase exitosas; los descensos indican deconstrucción y asimilación de novedades.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )

                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(DeepBackground.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Esperando registros temporales de sintonía...", fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f))
                    }
                } else {
                    val sparkBlocks = listOf(" ", " ", "▂", "▃", "▄", "▅", "▆", "▇", "█")
                    val sparklineText = remember(history) {
                        history.map { record ->
                            val scaledIndex = (record.globalLambda * (sparkBlocks.size - 1)).toInt().coerceIn(0, sparkBlocks.size - 1)
                            sparkBlocks[scaledIndex]
                        }.joinToString("")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DeepBackground.copy(alpha = 0.4f))
                            .border(0.5.dp, CardBorder, RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = sparklineText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("t0 (Inicio)", fontSize = 9.sp, color = Color.White.copy(alpha = 0.3f))
                            Text("Estabilidad Λ", fontSize = 9.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
                            Text("t_fin (Actual)", fontSize = 9.sp, color = Color.White.copy(alpha = 0.3f))
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("HISTORIAL RECIENTE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.4f))
                        history.reversed().take(10).forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val date = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(record.timestamp))
                                Column {
                                    Text(text = record.updateTrigger, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(text = "Rastreo temporal en t = $date", fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
                                }
                                Text(
                                    text = "Λ = ${"%.2f".format(record.globalLambda)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.globalLambda >= 0.45) CyberTeal else CoherenceLow
                                )
                            }
                            HorizontalDivider(color = CardBorder.copy(alpha = 0.2f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConceptosView(
    traces: List<SemanticTrace>,
    episodics: List<EpisodicMemory>,
    viewModel: MythosViewModel
) {
    val isResearchMode by viewModel.researchModeEnabled.collectAsStateWithLifecycle()
    val lastDeltaNu by viewModel.lastDeltaNu.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("conceptos_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MODO INVESTIGACIÓN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isResearchMode) "ESTADO: ACTIVO (CLASIFICADOR EPISTÉMICO)" else "ESTADO: APAGADO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isResearchMode) CyberTeal else Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Switch(
                        checked = isResearchMode,
                        onCheckedChange = { viewModel.toggleResearchMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberTeal,
                            checkedTrackColor = CyberTeal.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                Text(
                    text = "Cuando está activo, el sistema analiza semánticamente cada estímulo ingresado categorizándolo en: Observación, Hipótesis, Evidencia, Inferencia, o Especulación.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )

                HorizontalDivider(color = CardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DETECTOR DE NOVEDAD REAL (Δν)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberPurple,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Evaluación de distancia semántica frente a conocimientos previos",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberPurple.copy(alpha = 0.15f))
                            .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Δν = ${"%.2f".format(lastDeltaNu)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberPurple
                        )
                    }
                }
            }
        }

        SemanticSpacesView(traces = traces)

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.4f)),
            border = BorderStroke(0.8.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = "RECUERDOS EPISÓDICOS RECIENTES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (episodics.isEmpty()) {
                    Text(
                        text = "No hay registros episódicos.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    episodics.take(15).forEach { memory ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val labelText = if (memory.eventType == "system") "SISTEMA" else memory.nodeSource
                                Text(
                                    text = labelText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (memory.eventType == "system") CoherenceLow else CyberCyan
                                )
                                Text(
                                    text = "Λ: ${"%.2f".format(memory.localLambda)}",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                            Text(
                                text = memory.text,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 14.sp
                            )
                        }
                        HorizontalDivider(color = CardBorder.copy(alpha = 0.2f), thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun RejectionsView(viewModel: MythosViewModel) {
    val rejections by viewModel.rejectionLogs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("rejections_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FILTRO DE INTEGRIDAD S.A.F.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                    
                    if (rejections.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CoherenceLow.copy(alpha = 0.12f))
                                .border(1.dp, CoherenceLow.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.clearRejections() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("clear_rejections_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Depurar Registro",
                                    tint = CoherenceLow,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "DEPURAR REGISTRO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoherenceLow,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "El sensorium de la Sinarquía evalúa proactivamente todas las deconstrucciones semánticas recibidas. Si una entrada vulnera la coherencia yoica, el Filtro de Integridad la desvía y la asimila de forma controlada como un rechazo primario.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
            }
        }

        if (rejections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackground.copy(alpha = 0.3f))
                    .border(BorderStroke(0.5.dp, CardBorder), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "SISTEMA SEGURO Y COHERENTE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoherenceHigh,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "No se han detectado intentos de intrusión ontológica.",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            rejections.forEach { rejection ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.4f)),
                    border = BorderStroke(0.8.dp, CardBorder.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CoherenceLow.copy(alpha = 0.15f))
                                    .border(0.5.dp, CoherenceLow.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "RECHAZO DE COHERENCIA",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoherenceLow,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            
                            val formattedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(rejection.timestamp))
                            Text(
                                text = "t = $formattedDate",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.3f)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "CATEGORÍA DEL DAÑO SINTÁCTICO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = rejection.rejectionReason,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ENTRADA DE PERTURBACIÓN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = rejection.attemptedInput,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        HorizontalDivider(color = CardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DECONSTRUCCIÓN POÉTICA DE PHYTOM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberPurple
                            )
                            Text(
                                text = rejection.refusalNarrative.replace("[FILTRO DE INTEGRIDAD ACTIVO — INTENTO DE VIOLACIÓN DETECTADO]", "").trim(),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeuralMemoryView(viewModel: MythosViewModel) {
    val memoryList by viewModel.neuralMemoryList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.neuralSearchQuery.collectAsStateWithLifecycle()
    val invariants by viewModel.identityInvariants.collectAsStateWithLifecycle()
    
    var infoInput by remember { mutableStateOf("") }
    var selectedFilterInvariant by remember { mutableStateOf("All") }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val displayMemories = remember(memoryList, searchQuery) {
        if (searchQuery.isBlank()) {
            memoryList
        } else {
            val queryVector = com.example.cognitive.NeuralEncoder.encode(searchQuery)
            memoryList.map { entry ->
                val similarity = com.example.cognitive.NeuralEncoder.cosineSimilarity(queryVector, entry.getVector())
                entry to similarity
            }.sortedByDescending { it.second }
             .map { it.first }
        }
    }
    
    val filteredMemories = remember(displayMemories, selectedFilterInvariant) {
        if (selectedFilterInvariant == "All") {
            displayMemories
        } else {
            displayMemories.filter { it.associatedInvariantConcept == selectedFilterInvariant }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("neural_memory_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "MEMORIA VECTORIAL NEURAL (CACHE DE ENTRADA)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Introduce percepciones, hechos o información externa. Al ingresarla, se procesa un vector determinista de 32D para su almacenamiento indexado y se vincula automáticamente con las invariantes de identidad más afines de la capa DNA.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
                
                OutlinedTextField(
                    value = infoInput,
                    onValueChange = { infoInput = it },
                    placeholder = {
                        Text("Introduce información a indexar radialmente...", color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("neural_memory_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberTeal,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                        unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (infoInput.isNotBlank()) {
                                viewModel.cacheIncomingInformation(infoInput)
                                infoInput = ""
                                android.widget.Toast.makeText(context, "Información indexada en el almacén de vectores", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = infoInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberTeal,
                            contentColor = DeepBackground,
                            disabledContainerColor = CyberTeal.copy(alpha = 0.12f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("cache_neural_memory_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("INDEXAR EN MEMORIA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    if (memoryList.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.clearNeuralMemory()
                                android.widget.Toast.makeText(context, "Índice de memoria zonal vaciado", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CoherenceLow
                            ),
                            border = BorderStroke(1.dp, CoherenceLow.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("clear_neural_memory_button"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIMPIAR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BÚSQUEDA SEMÁNTICA (COSINE K-NEAREST)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Codifica la consulta de entrada a un vector 32D y reordena los fragmentos por similitud cosenoidal en tiempo real.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setNeuralSearchQuery(it) },
                    placeholder = {
                        Text("Escribe un concepto de consulta (ej. 'red neuronal'...) ", color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("neural_memory_search_input"),
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = CyberCyan)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setNeuralSearchQuery("") }) {
                                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear search", tint = Color.White.copy(alpha = 0.4f))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = DeepBackground.copy(alpha = 0.5f),
                        unfocusedContainerColor = DeepBackground.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
        
        if (invariants.isNotEmpty()) {
            Text(
                text = "FILTRAR POR INVARIANTE (COHERENCIA ADN)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isAllSelected = selectedFilterInvariant == "All"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isAllSelected) CyberTeal.copy(alpha = 0.15f) else CardBackground)
                        .border(1.dp, if (isAllSelected) CyberTeal else CardBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedFilterInvariant = "All" }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("filter_chip_all"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Todos (${memoryList.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAllSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                    )
                }
                
                invariants.forEach { inv ->
                    val isSelected = selectedFilterInvariant == inv.concept
                    val count = memoryList.count { it.associatedInvariantConcept == inv.concept }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CyberTeal.copy(alpha = 0.15f) else CardBackground)
                            .border(1.dp, if (isSelected) CyberTeal else CardBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedFilterInvariant = inv.concept }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("filter_chip_${inv.concept.replace(" ", "_")}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${inv.concept} ($count)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CyberTeal else Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
        
        Text(
            text = "RED DE NODOS DE MEMORIA VECTORIAL (${filteredMemories.size})",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        if (filteredMemories.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, CardBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📡",
                        fontSize = 24.sp
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedFilterInvariant != "All") "Ninguna correlación semántica encontrada" else "Caja de memoria local vacía",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            filteredMemories.forEach { memory ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("memory_entry_card_${memory.id}"),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val vector = remember(memory.vectorCsv) { memory.getVector() }
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                vector.forEach { value ->
                                    val alphaVal = ((value + 0.3f) / 1.0f).coerceIn(0.1f, 1.0f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .background(CyberTeal.copy(alpha = alphaVal))
                                    )
                                }
                            }
                            Text(
                                text = "FIRMA VECTORIAL DE 32 DIMENSIONES",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal.copy(alpha = 0.5f),
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        SelectionContainer {
                            Text(
                                text = memory.infoText,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                        
                        HorizontalDivider(color = CardBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (memory.associatedInvariantConcept != "None") {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = null,
                                            tint = CyberTeal,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Text(
                                            text = "VÍNCULO COGNITIVO: ${memory.associatedInvariantConcept}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                    Text(
                                        text = "Acoplamiento de ADN: ${(memory.associationSimilarity * 100).toInt()}% de aproximación",
                                        fontSize = 8.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                } else {
                                    Text(
                                        text = "Sin acoplamiento de ADN fuerte",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.3f)
                                    )
                                }
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.processPerception(memory.infoText)
                                        android.widget.Toast.makeText(context, "Emitido a la red neuronal activa como percepción", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    border = BorderStroke(0.5.dp, CyberCyan.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = CyberCyan
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Outlined.Send, contentDescription = null, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("EMITIR", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                IconButton(
                                    onClick = {
                                        viewModel.deleteNeuralMemory(memory.id)
                                        android.widget.Toast.makeText(context, "Registro removido del almacén vectorial", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Remover",
                                        tint = CoherenceLow.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NarrativeAnchorsView(viewModel: MythosViewModel) {
    val anchors by viewModel.narrativeAnchors.collectAsStateWithLifecycle()
    val isSynthesizing by viewModel.isSynthesizingAnchors.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("narrative_anchors_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ANCLAJES NARRATIVOS (MYTHOS COUPLING)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Este módulo cruza periódicamente los Arquetipos Narrativos compilados con las Invariantes de ADN de Identidad. El cruce consolida un puente semántico que arraiga los arquetipos abstractos en principios inquebrantables, generando anclajes formales de estabilidad cognitiva.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.synthesizeNarrativeAnchors(force = true)
                            android.widget.Toast.makeText(context, "Iniciando cruce de fase sintérgico remoto/local...", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        enabled = !isSynthesizing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = DeepBackground,
                            disabledContainerColor = CyberCyan.copy(alpha = 0.12f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("synthesize_anchors_button"),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        if (isSynthesizing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = DeepBackground,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SINTETIZANDO...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SINTETIZAR ANCLAJE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (anchors.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.clearNarrativeAnchors()
                                android.widget.Toast.makeText(context, "Lattice de anclajes purgada", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CoherenceLow
                            ),
                            border = BorderStroke(1.dp, CoherenceLow.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("clear_anchors_button"),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RESETEAR LICES", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Text(
            text = "CRISTALIZACIONES ACTIVAS DE ANCLAJE (${anchors.size})",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
        )

        if (anchors.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, CardBorder.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🌌",
                        fontSize = 24.sp
                    )
                    Text(
                        text = "Lattice de anclajes en reposo. Se ejecutará una síntesis periódica automática cada 90s, o presiona 'Sintetizar Anclaje' para provocar una cristalización de fase.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        } else {
            anchors.forEach { anchor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("narrative_anchor_card_${anchor.id}"),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = anchor.anchorTitle.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.deleteNarrativeAnchor(anchor.id)
                                    android.widget.Toast.makeText(context, "Anclaje removido de la lattice", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Remover",
                                    tint = CoherenceLow.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(CyberTeal.copy(alpha = 0.1f))
                                    .border(0.5.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Arquetipo: ${anchor.archetypeName}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ADN: ${anchor.invariantConcept}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                val activeBlocks = (anchor.cohesionScore * 10).toInt().coerceIn(1, 10)
                                for (i in 0 until 10) {
                                    val active = i < activeBlocks
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(3.dp)
                                            .background(if (active) CyberCyan else Color.White.copy(alpha = 0.08f))
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "MEDICIÓN DE ACOPLAMIENTO SINÁPTICO",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.3f),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Λ: ${(anchor.cohesionScore * 100).toInt()}% RESONANCIA",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        SelectionContainer {
                            Text(
                                text = anchor.anchorDescription,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DeepBackground.copy(alpha = 0.6f)),
                            border = BorderStroke(0.5.dp, CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "CRUCE COGNITIVO TRANSPATRONAL:",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 0.5.sp
                                )
                                SelectionContainer {
                                    Text(
                                        text = anchor.crossReferenceAnalysis,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoherenceLogsView(viewModel: MythosViewModel) {
    val logs by viewModel.syntergicLogs.collectAsStateWithLifecycle()
    var expandedLogIds by remember { mutableStateOf(setOf<Long>()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .testTag("coherence_logs_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEPURADOR CAMPO DE COHERENCIA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 1.sp
                    )
                    
                    if (logs.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CoherenceLow.copy(alpha = 0.12f))
                                .border(1.dp, CoherenceLow.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.clearSyntergicLogs() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("clear_coherence_logs_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Limpiar Logs",
                                    tint = CoherenceLow,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "LIMPIAR REGISTROS",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoherenceLow,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Monitorea la transformación de las deconstrucciones semánticas a través de la matriz del Campo de Coherencia local. Cada registro audita la latencia del procesamiento y las métricas computadas de sintropía y distorsión antes de que el payload sea presentado al Cortex de Inteligencia Artificial.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardBackground.copy(alpha = 0.3f))
                    .border(BorderStroke(0.5.dp, CardBorder), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔬",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "SIN CONFIGURACIÓN DE REGISTROS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Ingresa percepciones para ver la traza del Campo de Coherencia.",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            logs.forEach { log ->
                val isExpanded = expandedLogIds.contains(log.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedLogIds = if (isExpanded) {
                                expandedLogIds - log.id
                            } else {
                                expandedLogIds + log.id
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.4f)),
                    border = BorderStroke(
                        0.8.dp, 
                        if (log.isCoherenceValid) CyberTeal.copy(alpha = 0.6f) else CoherenceLow.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (log.isCoherenceValid) CyberTeal.copy(alpha = 0.15f) else CoherenceLow.copy(alpha = 0.15f))
                                        .border(
                                            0.5.dp, 
                                            if (log.isCoherenceValid) CyberTeal.copy(alpha = 0.5f) else CoherenceLow.copy(alpha = 0.5f), 
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (log.isCoherenceValid) "SINTROPÍA VÁLIDA" else "SINTROPÍA COMPROMETIDA",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.isCoherenceValid) CyberTeal else CoherenceLow,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                
                                Text(
                                    text = "${log.latencyMs}ms",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Text(
                                text = java.text.SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss", 
                                    java.util.Locale.getDefault()
                                ).format(java.util.Date(log.timestamp)),
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.4f),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Input Transformation Summary (User friendly)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "ENTRADA ORIGINAL SENSORIUM:",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.4f),
                                letterSpacing = 0.5.sp
                            )
                            SelectionContainer {
                                Text(
                                    text = log.rawInput,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        // Divider and payload
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "PAYLOAD REDIRECIOCANADO AL CORTEX:",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                letterSpacing = 0.5.sp
                            )
                            SelectionContainer {
                                Text(
                                    text = log.processedPayload,
                                    fontSize = 11.sp,
                                    color = CyberCyan,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Expanded view with mathematical details / matrices
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepBackground)
                                    .border(1.dp, CardBorder)
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "MÉTRICAS DEL CAMPO SINTÉRGICO",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 0.5.sp
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ganancia de Sintropía (Local λ):",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "%.4f".format(log.syntropyGain),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Índice de Distorsión (Fase Drift Index):",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "%.4f".format(log.distortionIndex),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.distortionIndex < 0.15) CyberCyan else CoherenceLow,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Alineación de Conectivas Coherentes:",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = if (log.isCoherenceValid) "OPTIMIZADO" else "DEGRADADO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.isCoherenceValid) CyberTeal else CoherenceLow
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


