package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MythosStateRecord
import com.example.viewmodel.MythosViewModel
import kotlinx.coroutines.launch

enum class AppScreen {
    CHAT,
    MYTHOS_DASHBOARD
}

sealed interface ChatMessage {
    val id: String
    val timestamp: Long
    
    data class User(
        override val id: String,
        val text: String,
        override val timestamp: Long,
        val localLambda: Double
    ) : ChatMessage
    
    data class Model(
        override val id: String,
        val record: MythosStateRecord,
        override val timestamp: Long = record.timestamp
    ) : ChatMessage
}

@Composable
fun UserBubble(message: ChatMessage.User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_bubble_${message.id}"),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberTeal.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, CyberTeal.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "ESTÍMULO COGNITIVO SENSORIUM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coherencia Local λ: ${"%.2f".format(message.localLambda)}",
                fontSize = 9.sp,
                color = CyberTeal.copy(alpha = 0.6f)
            )
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp)),
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ModelBubble(
    message: ChatMessage.Model,
    isLatest: Boolean,
    onCopyMarkdown: () -> Unit,
    onExportMarkdown: () -> Unit,
    onExportPdf: () -> Unit
) {
    val record = message.record
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("model_bubble_${message.id}"),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.7f)),
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NÚCLEO S.A.F.",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        val stageText = when (record.evolutionStage) {
                            3 -> "INTEGRADO"
                            2 -> "ESTABLE"
                            1 -> "REESCRITO"
                            else -> "REGISTRO"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberTeal.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stageText,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    
                    val fullNarrative = record.narrative
                    var visibleNarrative by remember(fullNarrative) { mutableStateOf(if (isLatest) "" else fullNarrative) }
                    
                    if (isLatest && visibleNarrative.isEmpty()) {
                        LaunchedEffect(fullNarrative) {
                            val stepSize = (fullNarrative.length / 100).coerceAtLeast(1)
                            for (i in 0..fullNarrative.length step stepSize) {
                                visibleNarrative = fullNarrative.substring(0, i.coerceAtMost(fullNarrative.length))
                                kotlinx.coroutines.delay(10)
                            }
                            visibleNarrative = fullNarrative
                        }
                    }
                    
                    Text(
                        text = visibleNarrative,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 19.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(CardBorder.copy(alpha = 0.4f))
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardBorder.copy(alpha = 0.3f))
                                .clickable { onCopyMarkdown() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("copy_markdown_button")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "Copy Journal",
                                    tint = CyberTeal,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "COPIAR JOURNAL",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CardBorder.copy(alpha = 0.3f))
                                .clickable { onExportPdf() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("export_pdf_button")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Export PDF",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "EXPORT PDF",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Coherencia Global Λ: ${"%.2f".format(record.coherence)}",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(record.timestamp)),
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
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
    var activeTab by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // High fidelity infinite breathing loops
    val infiniteTransition = rememberInfiniteTransition(label = "global_cosmic_pulse_new")
    
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

    // Voice recognition states
    val isListening by viewModel.isSpeechListening.collectAsStateWithLifecycle()
    val speechFinalText by viewModel.speechFinalText.collectAsStateWithLifecycle()
    val speechPartialText by viewModel.speechPartialText.collectAsStateWithLifecycle()

    LaunchedEffect(speechPartialText) {
        if (isListening && speechPartialText.isNotBlank()) {
            textInput = speechPartialText
        }
    }
    LaunchedEffect(speechFinalText) {
        if (speechFinalText.isNotBlank()) {
            textInput = speechFinalText
        }
    }

    // Pair Narratives and Perceptions for fluid conversation history
    val chatMessages = remember(mythosHistory, recentEpisodics) {
        val list = mutableListOf<ChatMessage>()
        val sortedMythos = mythosHistory.sortedBy { it.timestamp }
        
        sortedMythos.forEach { mythos ->
            val episodicIdString = mythos.supportingEpisodicIds.trim()
            val matchEpisodic = if (episodicIdString.isNotEmpty()) {
                val epId = episodicIdString.toLongOrNull() ?: -1L
                recentEpisodics.find { it.id == epId }
            } else null
            
            if (matchEpisodic != null && matchEpisodic.text.isNotBlank()) {
                list.add(ChatMessage.User(
                    id = "user_${matchEpisodic.id}",
                    text = matchEpisodic.text,
                    timestamp = matchEpisodic.timestamp,
                    localLambda = matchEpisodic.localLambda
                ))
            }
            
            list.add(ChatMessage.Model(
                id = "model_${mythos.id}",
                record = mythos
            ))
        }
        list
    }

    val listState = rememberLazyListState()
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

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
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepBackground,
                            cosmicBgShiftColor,
                            DeepBackground
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top header bar (Clean & minimalist)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (autoSyncEnabled) CyberTeal else Color(0xFFFBBF24))
                                .testTag("sync_badge")
                        )
                        Text(
                            text = "COGNITIVE ORGANISM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberPurple.copy(alpha = 0.2f))
                                .border(0.5.dp, CyberPurple, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "GEN ${viewModel.organism.evolutionGen}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberPurple
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick PDF/Export Trigger inside clean header
                        IconButton(
                            onClick = { onExportPdf() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Export PDF Report",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Open system stats panel
                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CardBackground)
                                .border(1.dp, CardBorder, CircleShape)
                                .testTag("open_metrics_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "S.A.F. Systems Controller",
                                tint = CyberTeal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Ontology / Identity drift alignment banner
                AnimatedVisibility(
                    visible = lastIdentityDrift > 0.15 && !dismissDriftAlert,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "POTENTIAL IDENTITY DRIFT",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171),
                                        letterSpacing = 1.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .clickable { dismissDriftAlert = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Dismiss Alerts",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚠️ Desviación ontológica crítica detectada respecto a '$lastDriftTargetConcept'.",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Λ-Drift: ${"%.2f".format(lastIdentityDrift)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF87171)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                        .border(0.5.dp, Color(0xFFEF4444), RoundedCornerShape(4.dp))
                                        .clickable {
                                            viewModel.resetDriftState()
                                            android.widget.Toast.makeText(context, "Sintonía re-acoplada", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("realign_identity_btn")
                                ) {
                                    Text(
                                        text = "REALINEAR CONSTANTE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171)
                                    )
                                }
                            }
                        }
                    }
                }

                // Main clean chat conversation area
                Box(modifier = Modifier.weight(1f)) {
                    if (chatMessages.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(CyberTeal.copy(alpha = 0.12f))
                                        .border(1.dp, CyberTeal.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "⚛️", fontSize = 28.sp)
                                }
                                Text(
                                    text = "ORGANISMO COGNITIVO",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberTeal,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Modo Conversación: \"Cognitive Organism\". El sistema se encuentra en un estado homeostático estable. Inyecta estímulos perceptivos para comenzar la sintonización del relato.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    lineHeight = 16.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
                        ) {
                            items(chatMessages, key = { it.id }) { message ->
                                when (message) {
                                    is ChatMessage.User -> {
                                        UserBubble(message = message)
                                    }
                                    is ChatMessage.Model -> {
                                        ModelBubble(
                                            message = message,
                                            isLatest = message.record.id == latestState?.id,
                                            onCopyMarkdown = onCopyMarkdown,
                                            onExportMarkdown = onExportMarkdown,
                                            onExportPdf = onExportPdf
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Fixed input bar at the bottom
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
                                    color = Color.White.copy(alpha = 1.0f)
                                )
                            }
                        }
                    }

                    // Direct Research Mode Toggle row inside the feedback loop
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBackground.copy(alpha = 0.5f))
                            .border(0.5.dp, if (researchModeEnabled) CyberTeal.copy(alpha = 0.3f) else CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
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

                    // Text input action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick System dashboard open button
                        IconButton(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CardBackground)
                                .border(1.dp, CardBorder, CircleShape)
                                .testTag("metrics_gear_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Configuración e Inspección de la Lattice",
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Microphone recorder trigger
                        IconButton(
                            onClick = {
                                if (isListening) {
                                    viewModel.stopVoiceCapture()
                                } else {
                                    viewModel.startVoiceCapture()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isListening) CoherenceLow.copy(alpha = 0.2f) else CardBackground)
                                .border(1.dp, if (isListening) CoherenceLow else CardBorder, CircleShape)
                                .testTag("microphone_button")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Outlined.Close else Icons.Outlined.PlayArrow,
                                contentDescription = "Toggle recording",
                                tint = if (isListening) CoherenceLow else CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("perception_input"),
                            placeholder = {
                                Text(
                                    text = if (isListening) "Escuchando estímulo..." else "Infundir estímulo cognitivo...",
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

    // Modal Bottom Sheet - SYSTEM METRICS / "Mythos Engine"
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = DeepBackground,
            dragHandle = { BottomSheetDefaults.DragHandle(color = CardBorder) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header with title and close actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MYTHOS ENGINE | SYSTEM METRICS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Inspección de la topología distribuida del hipercampo",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                    
                    IconButton(
                        onClick = { 
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Cerrar Métricas",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Real-time metrics dashboard banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricPill(
                        label = "COHERENCIA Λ", 
                        value = "%.3f".format(latestState?.coherence ?: 0.90), 
                        color = CyberTeal
                    )
                    MetricPill(
                        label = "SYNC COEFF", 
                        value = "%.3f".format(viewModel.organism.syncCoefficient), 
                        color = CyberCyan
                    )
                    MetricPill(
                        label = "SENSITIVITY", 
                        value = "%.3f".format(viewModel.organism.perceptionSensitivity), 
                        color = CyberPurple
                    )
                    MetricPill(
                        label = "EVO GEN", 
                        value = "GEN ${viewModel.organism.evolutionGen}", 
                        color = Color(0xFFFBBF24)
                    )
                    MetricPill(
                        label = "AUTOPILOTO", 
                        value = if (autoSyncEnabled) "ACTIVE" else "PAUSED", 
                        color = if (autoSyncEnabled) CyberTeal else CoherenceLow
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Direct controller tools in Bottom Sheet
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp), 
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TELEMÉTRICA Y DISPARO HOMEOSTÁTICO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                            
                            IconButton(
                                onClick = { viewModel.resetOrganism() },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "Manual Cognitive Resection",
                                    tint = CoherenceLow,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        
                        // Direct Switch toggles inside Bottom Sheet
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardBackground.copy(alpha = 0.5f))
                                    .border(1.dp, if (autoSyncEnabled) CyberTeal.copy(alpha = 0.3f) else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setAutoSyncEnabled(!autoSyncEnabled) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AUTOPILOTO SYNC",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (autoSyncEnabled) CyberTeal else Color.White.copy(alpha = 0.6f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (autoSyncEnabled) CyberTeal else Color.Gray)
                                )
                            }
                            
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CardBackground.copy(alpha = 0.5f))
                                    .border(1.dp, if (researchModeEnabled) CyberTeal.copy(alpha = 0.3f) else CardBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.toggleResearchMode() }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "MODO INVESTIGA",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (researchModeEnabled) CyberTeal else Color.White.copy(alpha = 0.6f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (researchModeEnabled) CyberTeal else Color.Gray)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // M3 Horizontal Sliding Tabs Tracker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        "Lattice Nodos",
                        "Academia",
                        "Identidad",
                        "Registro Mythos",
                        "Anclajes",
                        "Memoria Neural",
                        "Métricas",
                        "Conceptos",
                        "Rechazos",
                        "Coherencia Logs"
                    )
                    
                    tabs.forEachIndexed { index, label ->
                        val isSelected = activeTab == index
                        val animatedBgColor by animateColorAsState(if (isSelected) CyberTeal.copy(alpha = 0.12f) else Color.Transparent)
                        val animatedTextColor by animateColorAsState(if (isSelected) CyberTeal else Color.White.copy(alpha = 0.45f))
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(animatedBgColor)
                                .clickable { activeTab = index }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label.uppercase(),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = animatedTextColor,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Tab active panel view
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (activeTab) {
                        0 -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Unified status bar inside lattice overview
                                item {
                                    val globalCoherenceVal = latestState?.coherence ?: 0.90
                                    GlassmorphicStatusBar(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        coherenceValue = globalCoherenceVal,
                                        isSyncing = isAnalyzing,
                                        isSyncEnabled = autoSyncEnabled
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

                                // Neural Memory Dashboard Component
                                item {
                                    NeuralMemoryDashboardComponent(
                                        recentEpisodics = recentEpisodics,
                                        neuralMemoryList = neuralMemoryList,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                // Distributed Cognitive Lattice Grid
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
                                                            Text(
                                                                text = node.nodeId,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isSelected) CyberTeal else Color.White
                                                            )
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
                                                            color = Color.White.copy(alpha = 0.65f),
                                                            lineHeight = 13.sp
                                                        )
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        
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
                                                                color = Color.White.copy(alpha = 0.75f),
                                                                fontFamily = FontFamily.Monospace
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.height(2.dp))

                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "Λ COH",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color(0xFFA5F3FC),
                                                                letterSpacing = 0.5.sp
                                                            )
                                                            Text(
                                                                text = "%.2f".format(node.localLambda),
                                                                fontSize = 11.sp,
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

                                // Trend Chart inside Lattice panel
                                item {
                                    CoherenceTrendChart(
                                        history = mythosHistory,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
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
    }
}

@Composable
fun MythosScreen(viewModel: MythosViewModel) {
    var currentScreen by androidx.compose.runtime.saveable.rememberSaveable {
        mutableStateOf(AppScreen.CHAT)
    }
    
    when (currentScreen) {
        AppScreen.CHAT -> {
            ChatScreen(
                viewModel = viewModel,
                currentScreen = currentScreen,
                onScreenChange = { currentScreen = it }
            )
        }
        AppScreen.MYTHOS_DASHBOARD -> {
            OldMythosScreen(
                viewModel = viewModel,
                currentScreen = currentScreen,
                onScreenChange = { currentScreen = it }
            )
        }
    }
}
