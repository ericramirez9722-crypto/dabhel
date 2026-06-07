package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlin.math.abs
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MythosScreen(viewModel: MythosViewModel) {
    val latestState by viewModel.latestMythosState.collectAsStateWithLifecycle()
    val mythosHistory by viewModel.mythosStateList.collectAsStateWithLifecycle()
    val nodes by viewModel.cognitiveNodes.collectAsStateWithLifecycle()
    val recentEpisodics by viewModel.recentEpisodics.collectAsStateWithLifecycle()
    val semanticTraces by viewModel.semanticTraces.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val selectedNodeId by viewModel.selectedNode.collectAsStateWithLifecycle()
    val rawEvolLog by viewModel.internalRuleLogs.collectAsStateWithLifecycle()
    val autoSyncEnabled by viewModel.autoSyncEnabled.collectAsStateWithLifecycle()
    val researchModeEnabled by viewModel.researchModeEnabled.collectAsStateWithLifecycle()

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
        contentWindowInsets = WindowInsets.safeDrawing
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

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .graphicsLayer(
                                                scaleX = nodeScale,
                                                scaleY = nodeScale
                                            )
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(animatedBgColor)
                                            .border(
                                                width = 1.5.dp,
                                                color = animatedBorderColor,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { viewModel.selectNode(node.nodeId) }
                                            .padding(10.dp)
                                            .testTag("node_${node.nodeId}")
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
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.5f),
                                                lineHeight = 12.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            // Coherence representation bar
                                            Text(
                                                text = "Λ: ${"%.2f".format(node.localLambda)}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    node.localLambda > 0.82 -> CoherenceHigh
                                                    node.localLambda > 0.45 -> CoherenceMid
                                                    else -> CoherenceLow
                                                }
                                            )
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
                                    val tabs = listOf("Mythos", "Academia", "Identidad", "Métricas", "Conceptos", "Rechazos")
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
                                    3 -> MetricsDashboardView(viewModel = viewModel)
                                    4 -> ConceptosView(traces = semanticTraces, episodics = recentEpisodics, viewModel = viewModel)
                                    5 -> RejectionsView(viewModel = viewModel)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Glow radial progress indicator
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .drawBehind {
                        val baseRadius = size.minDimension / 2.3f
                        
                        // Halo wave 1 (Expanding)
                        drawCircle(
                            color = if (globalCoherence > 0.45) CyberTeal.copy(alpha = 0.08f) else CoherenceLow.copy(alpha = 0.1f),
                            radius = baseRadius * scaleHalo1,
                            center = center
                        )
                        
                        // Halo wave 2 (Dynamic shimmer)
                        drawCircle(
                            color = if (globalCoherence > 0.45) CyberCyan.copy(alpha = 0.04f) else CoherenceLow.copy(alpha = 0.06f),
                            radius = baseRadius * scaleHalo2,
                            center = center
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { globalCoherence.toFloat() },
                    modifier = Modifier.size(68.dp),
                    color = if (globalCoherence > 0.45) CyberTeal else CoherenceLow,
                    trackColor = CardBorder,
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
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Integration state & adaptive logs view
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
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = logs,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp
                )
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
                OutlinedButton(
                    onClick = onCopyMarkdown,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberTeal
                    ),
                    border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_markdown_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Copiar",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copiar Journal de Consciencia",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Row 2: Exportar .MD and Exportar PDF side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Exportar .md Button (Outlined themed style)
                    OutlinedButton(
                        onClick = onExportMarkdown,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCyan
                        ),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_markdown_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Exportar Markdown",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Exportar .MD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Exportar PDF Button (Solid themed style in CyberTeal)
                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberTeal,
                            contentColor = DeepBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sync_optimization_card"),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, CardBorder)
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
                // High-fidelity sync status icon with custom glowing circles
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (autoSyncEnabled) CyberTeal.copy(alpha = 0.12f) else Color(0xFFFBBF24).copy(alpha = 0.12f))
                        .border(1.dp, if (autoSyncEnabled) CyberTeal.copy(alpha = 0.45f) else Color(0xFFFBBF24).copy(alpha = 0.45f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (autoSyncEnabled) Icons.Outlined.Refresh else Icons.Outlined.Warning,
                        contentDescription = "Estado de Sincronización",
                        tint = if (autoSyncEnabled) CyberTeal else Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
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
fun ArchetypesView(viewModel: MythosViewModel) {
    val archetypes by viewModel.narrativeArchetypes.collectAsStateWithLifecycle()
    val isSynthesizing by viewModel.isSynthesizingArchetype.collectAsStateWithLifecycle()
    val lastSynthesized by viewModel.lastSynthesizedArchetype.collectAsStateWithLifecycle()
    
    // Voice Portal States
    val isListening by viewModel.isSpeechListening.collectAsStateWithLifecycle()
    val partialText by viewModel.speechPartialText.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.speechRmsDb.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()
    
    var ideaInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // Live update input field with voice transcription
    LaunchedEffect(partialText, isListening) {
        if (isListening && partialText.isNotBlank()) {
            ideaInput = partialText
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
        // Form to enter idea
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                    text = "Inyecta una semilla conceptual hablando por el micrófono o escribiendo (ej. 'Eterno Retorno', 'Guardián del Vacío', 'Sombra Digital') para compilar un arquetipo poético a través del LLM Cortex.",
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
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val alphaScale by infiniteTransition.animateFloat(
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
                
                Button(
                    onClick = {
                        if (ideaInput.isNotBlank()) {
                            viewModel.synthesizeNewArchetype(ideaInput)
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
                    
                    // Button to clear all (cognitive resection of archetypes)
                    Text(
                        text = "PURGAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoherenceLow,
                        modifier = Modifier
                            .clickable { viewModel.clearAllArchetypes() }
                            .padding(4.dp)
                    )
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
    var conceptInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("principle") } // principle, framework, target

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("principle" to "PRINCIPIO", "framework" to "MARCO S.A.F.", "target" to "OBJETIVO").forEach { (cat, label) ->
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
                            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSel) CyberTeal else Color.White.copy(alpha = 0.5f))
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
                    Text(
                        text = "RESETEAR NÚCLEO DE VALORES",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoherenceLow,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { viewModel.clearIdentityInvariants() }
                            .padding(top = 4.dp)
                    )
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
                        Text(
                            text = "DEPURAR REGISTRO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoherenceLow,
                            modifier = Modifier
                                .clickable { viewModel.clearRejections() }
                                .padding(4.dp)
                                .testTag("clear_rejections_button")
                        )
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

