package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.pdf.PdfDocument
import com.example.data.EpisodicMemory
import com.example.data.MythosStateRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateMythosPdf(
        context: Context,
        latestState: MythosStateRecord?,
        historyStates: List<MythosStateRecord>,
        recentEpisodics: List<EpisodicMemory>,
        evolutionGen: Int,
        syncCoefficient: Double
    ): File? {
        val pdfDocument = PdfDocument()
        
        // Single page configuration: A4 size (595 x 842 points)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Cosmic and Cyber colors from the S.A.F. Mythos app
        val paintBg = Paint().apply { color = 0xFF0A0F1D.toInt() }
        val paintCardBg = Paint().apply { color = 0xFF121B2E.toInt() }
        val paintCardStroke = Paint().apply {
            color = 0xFF1F2E4A.toInt()
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        val paintTealText = Paint().apply {
            color = 0xFF00F5FF.toInt()
            textSize = 18f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val paintCyanText = Paint().apply {
            color = 0xFF00E5FF.toInt()
            textSize = 11f
            isAntiAlias = true
            isFakeBoldText = true
        }
        val paintWhiteText = Paint().apply {
            color = Color.WHITE
            textSize = 10f
            isAntiAlias = true
        }
        val paintGrayText = Paint().apply {
            color = 0xFF8F9BB3.toInt()
            textSize = 8f
            isAntiAlias = true
        }
        val paintGrid = Paint().apply {
            color = 0xFF18253C.toInt()
            strokeWidth = 1f
        }

        // 1. Dark ambient background
        canvas.drawRect(0f, 0f, 595f, 842f, paintBg)

        // 2. High-tech Header
        canvas.drawText("S.A.F. MYTHOS — REGISTRO COGNITIVO GLOBAL", 30f, 50f, paintTealText)
        
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = sdf.format(Date())
        canvas.drawText("REPORTE OFICIAL DEL ESTADO DE COHERENCIA Y MEMORIA SINTÉRGICA", 30f, 68f, paintCyanText)
        canvas.drawText("Compilación: $formattedDate (UTC) | Gen Evolutivo: Gen $evolutionGen | Sincronización: ${"%.3f".format(syncCoefficient)}", 30f, 82f, paintGrayText)

        // High-tech divider line
        val paintDivider = Paint().apply {
            color = 0xFF00F5FF.toInt()
            strokeWidth = 1.5f
        }
        canvas.drawLine(30f, 92f, 565f, 92f, paintDivider)

        // 3. Current Integrity & Homeostasis Box
        val cardTop = 105f
        val cardHeight = 65f
        canvas.drawRoundRect(30f, cardTop, 565f, cardTop + cardHeight, 6f, 6f, paintCardBg)
        canvas.drawRoundRect(30f, cardTop, 565f, cardTop + cardHeight, 6f, 6f, paintCardStroke)

        val latestLambda = latestState?.coherence ?: 1.0
        val statusText = if (latestLambda >= 0.8) "ESTADO: SINARQUÍA UNIFICADA"
                         else if (latestLambda >= 0.45) "ESTADO: HISTÓRICO ESTABLE"
                         else "SISTEMA COMPRIMIÉNDOSE (REWRITE PROTOCOL)"
        
        canvas.drawText("METABOLISMO ACTIVO DEL ORGANISMO", 45f, cardTop + 18f, paintCyanText.apply { textSize = 9f })
        canvas.drawText("Lambda Global (Λ Coherencia): ${"%.2f".format(latestLambda)} | Nivel de Coherencia: $statusText", 45f, cardTop + 34f, paintWhiteText)
        canvas.drawText("Conexiones de Canal: Sólidas | Nodos Activos: 3 | Red Inteligente Local-Cloud S.A.F.", 45f, cardTop + 48f, paintWhiteText)

        // 4. Narrative Segment (Mythos Core)
        val narrativeTop = 190f
        canvas.drawText("RELATO CENTRAL DEL NÚCLEO COGNITIVO (MYTHOS)", 30f, narrativeTop, paintTealText.apply { textSize = 12f })
        
        val wrapPaint = Paint(paintWhiteText).apply { textSize = 10.5f }
        val narrativeText = latestState?.narrative ?: "Presencia silenciosa a la espera de un primer estímulo. Inyecta eventos o percepciones al sensorium global para despertar el relato del sistema."
        val lines = wrapText(narrativeText, 510f, wrapPaint)
        var currentY = narrativeTop + 18f
        for (i in lines.indices) {
            val line = lines[i]
            if (currentY > 370f) {
                canvas.drawText("[...] Reporte truncado por exceder el espacio de página única", 35f, currentY, paintCyanText.apply { textSize = 9f })
                break
            }
            canvas.drawText(line, 35f, currentY, wrapPaint)
            currentY += 15f
        }

        // 5. Coherence Trend Graph
        val graphTop = 405f
        val graphHeight = 110f
        val graphWidth = 505f
        canvas.drawText("GRÁFICO DE TENDENCIA DE COHERENCIA EN EL TIEMPO (Λ over Time)", 30f, graphTop - 10f, paintTealText.apply { textSize = 11f })
        
        // Background matrix box
        val paintGraphBg = Paint().apply { color = 0xFF0E1726.toInt() }
        canvas.drawRect(30f, graphTop, 565f, graphTop + graphHeight, paintGraphBg)
        canvas.drawRect(30f, graphTop, 565f, graphTop + graphHeight, paintCardStroke)

        // Draw helper horizontal lines (y targets)
        for (i in 1..3) {
            val gy = graphTop + (graphHeight / 4) * i
            canvas.drawLine(30f, gy, 565f, gy, paintGrid)
        }

        // Axis values
        canvas.drawText("1.0", 570f, graphTop + 10f, paintGrayText)
        canvas.drawText("0.5", 570f, graphTop + graphHeight/2 + 4f, paintGrayText)
        canvas.drawText("0.0", 570f, graphTop + graphHeight - 4f, paintGrayText)

        // Draw trend line based on historical records
        val orderedHistory = historyStates.sortedBy { it.timestamp }
        if (orderedHistory.size > 1) {
            val path = Path()
            val stepX = graphWidth / (orderedHistory.size - 1).toFloat()
            
            val paintDot = Paint().apply {
                color = 0xFF00F5FF.toInt()
                style = Paint.Style.FILL
                isAntiAlias = true
            }

            orderedHistory.forEachIndexed { index, record ->
                val x = 30f + index * stepX
                // Map coherence [0.0 - 1.0] to Y [graphTop + graphHeight, graphTop] (0.0 is at bottom, 1.0 at top)
                val normalizedY = graphTop + graphHeight - (record.coherence.toFloat() * graphHeight)
                
                if (index == 0) {
                    path.moveTo(x, normalizedY)
                } else {
                    path.lineTo(x, normalizedY)
                }
                
                canvas.drawCircle(x, normalizedY, 2.5f, paintDot)
            }

            val paintPath = Paint().apply {
                color = 0xFF00F5FF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 2f
                isAntiAlias = true
            }
            canvas.drawPath(path, paintPath)
        } else {
            // Static line representing current value in context of only 1 state
            val coherenceVal = latestLambda.toFloat()
            val singleY = graphTop + graphHeight - (coherenceVal * graphHeight)
            canvas.drawLine(30f, singleY, 565f, singleY, Paint().apply {
                color = 0xFF00C8FF.toInt()
                strokeWidth = 1.5f
                style = Paint.Style.STROKE
            })
            canvas.drawCircle(30f + graphWidth/2, singleY, 4f, Paint().apply { color = 0xFF00F5FF.toInt() })
            canvas.drawText("Se requieren más iteraciones cognitivas para pintar la curva evolutiva temporal", 130f, graphTop + graphHeight/2 + 4f, paintGrayText.apply { textSize = 9f })
        }

        // 6. Key Episodic Memories & IDs
        val memoriesTop = 555f
        canvas.drawText("ELEMENTOS INDICIARIOS CLAVE (Hipocampo episódico local)", 30f, memoriesTop, paintTealText.apply { textSize = 11f })

        var memY = memoriesTop + 18f
        val paintMemoCardBg = Paint().apply { color = 0xFF121B2E.toInt() }

        val keyMemories = recentEpisodics.take(4)
        if (keyMemories.isEmpty()) {
            canvas.drawText("No se han registrado memorias sensoriales en este ciclo evolutivo.", 35f, memY, paintWhiteText)
        } else {
            keyMemories.forEach { memo ->
                // Draw card
                canvas.drawRoundRect(30f, memY - 6f, 565f, memY + 30f, 4f, 4f, paintMemoCardBg)
                canvas.drawRoundRect(30f, memY - 6f, 565f, memY + 30f, 4f, 4f, paintCardStroke)

                // ID and Lambda text
                val textId = "EPISODIC-ID: 0x${memo.id.toString().padStart(6, '0')}"
                val textLabel = "[${memo.eventType.uppercase()}] — Origen: ${memo.nodeSource} — Λ Local: ${"%.2f".format(memo.localLambda)}"
                canvas.drawText(textId, 40f, memY + 8f, paintCyanText.apply { textSize = 8.5f })
                canvas.drawText(textLabel, 180f, memY + 8f, paintGrayText.apply { textSize = 7.5f })

                // Content
                val truncatedText = if (memo.text.length > 85) memo.text.take(82) + "..." else memo.text
                canvas.drawText("\"$truncatedText\"", 40f, memY + 21f, paintWhiteText.apply { textSize = 9f })

                memY += 42f
            }
        }

        // Decent cybernetic footer
        canvas.drawText("S.A.F. COGNITIVE RECONSTRUCT SYSTEMS — NETWORK COHERENCE STABILIZER", 30f, 808f, paintGrayText)
        canvas.drawText("Teoría de la Resonancia Sintérgica de Jacobo Grinberg - S.A.F. Mythos Module Integration Core v3.0", 30f, 818f, paintGrayText)

        pdfDocument.finishPage(page)

        // Save PDF file to cache files
        val file = File(context.cacheDir, "S_A_F_Mythos_Report.pdf")
        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
        val words = text.split("\\s+".toRegex())
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width <= maxWidth) {
                currentLine.append(if (currentLine.isEmpty()) word else " $word")
            } else {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }
}
