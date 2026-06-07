package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.NarrativeArchetype
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ArchetypeImageGenerator {

    /**
     * Synthesizes a beautiful cybernetic visual summary card image (800x800)
     * and returns the local cache Uri for secure system sharing.
     */
    fun generateArchetypeCard(context: Context, archetype: NarrativeArchetype): Uri? {
        val width = 800
        val height = 800
        
        // Create an offline software bitmap canvas
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Palette definition
        val colorBackground = 0xFF030712.toInt() // obsidian black
        val colorCardBg = 0xFF0B0F1C.toInt()    // cyber dark blue-grey
        val colorTeal = 0xFF00F5FF.toInt()      // neon cyber teal
        val colorCyan = 0xFF00E5FF.toInt()      // neon cyan
        val colorPurple = 0xFFBC3FFF.toInt()    // cosmic violet
        val colorWhite = Color.WHITE
        val colorGray = 0xFF8F9BB3.toInt()
        val colorBorder = 0xFF1E293B.toInt()    // boundary stroke

        // Paint setups
        val paintBg = Paint().apply { color = colorBackground }
        val paintCardBg = Paint().apply { color = colorCardBg }
        
        val paintBorder = Paint().apply {
            color = colorBorder
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        
        val paintGlowingBorder = Paint().apply {
            color = colorTeal
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val paintTextTitle = Paint().apply {
            color = colorTeal
            textSize = 14f
            isAntiAlias = true
            isFakeBoldText = true
            letterSpacing = 0.2f
        }

        val paintArchetypeName = Paint().apply {
            color = colorWhite
            textSize = 28f
            isAntiAlias = true
            isFakeBoldText = true
        }

        val paintDescription = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 17f
            isAntiAlias = true
        }

        val paintQuoteLabel = Paint().apply {
            color = colorPurple
            textSize = 10f
            isAntiAlias = true
            isFakeBoldText = true
            letterSpacing = 0.15f
        }

        val paintQuote = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            textSize = 15f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }

        val paintFooter = Paint().apply {
            color = colorGray
            textSize = 11f
            isAntiAlias = true
        }

        // 1. Draw entire background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)

        // Draw tech grid background lines
        val paintGrid = Paint().apply {
            color = 0xFF0F172A.toInt()
            strokeWidth = 1f
        }
        val gridSize = 50f
        for (x in 0 until (width / gridSize.toInt())) {
            canvas.drawLine(x * gridSize, 0f, x * gridSize, height.toFloat(), paintGrid)
        }
        for (y in 0 until (height / gridSize.toInt())) {
            canvas.drawLine(0f, y * gridSize, width.toFloat(), y * gridSize, paintGrid)
        }

        // Draw an outer sci-fi tech ring/dots
        val paintDots = Paint().apply {
            color = colorTeal
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(40f, 40f, 4f, paintDots)
        canvas.drawCircle(width - 40f, 40f, 4f, paintDots)
        canvas.drawCircle(40f, height - 40f, 4f, paintDots)
        canvas.drawCircle(width - 40f, height - 40f, 4f, paintDots)

        canvas.drawLine(40f, 25f, width - 40f, 25f, paintGrid)
        canvas.drawLine(25f, 40f, 25f, height - 40f, paintGrid)

        // 2. Draw Main Interior Card Frame
        val margin = 50f
        val cardRect = RectF(margin, margin, width - margin, height - margin)
        canvas.drawRoundRect(cardRect, 16f, 16f, paintCardBg)
        canvas.drawRoundRect(cardRect, 16f, 16f, paintBorder)

        // Top-right notification corner
        val cornerPath = Path().apply {
            moveTo(width - margin - 30f, margin)
            lineTo(width - margin, margin + 30f)
            lineTo(width - margin, margin)
            close()
        }
        val paintCorner = Paint().apply {
            color = colorTeal
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawPath(cornerPath, paintCorner)

        // 3. Draw Header Label
        canvas.drawText("S.A.F. MYTHOS ARCHETYPE RECORD", margin + 30f, margin + 45f, paintTextTitle)

        // 4. Draw Archetype Name
        canvas.drawText(archetype.name.uppercase(), margin + 30f, margin + 95f, paintArchetypeName)

        // 5. Draw Resonance status pill
        val isCoherent = archetype.alignmentCoherence >= 0.70
        val pillBgColor = if (isCoherent) 0xFF062B28.toInt() else 0xFF2A141A.toInt()
        val pillTextColor = if (isCoherent) colorTeal else 0xFFF87171.toInt()
        val pillBorderColor = if (isCoherent) colorTeal else 0xFF881337.toInt()

        val pillLeft = margin + 30f
        val pillTop = margin + 120f
        val pillWidth = 210f
        val pillHeight = 30f
        val pillRect = RectF(pillLeft, pillTop, pillLeft + pillWidth, pillTop + pillHeight)

        val paintPillBg = Paint().apply { color = pillBgColor }
        val paintPillBorder = Paint().apply {
            color = pillBorderColor
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val paintPillText = Paint().apply {
            color = pillTextColor
            textSize = 11.5f
            isAntiAlias = true
            isFakeBoldText = true
        }

        canvas.drawRoundRect(pillRect, 6f, 6f, paintPillBg)
        canvas.drawRoundRect(pillRect, 6f, 6f, paintPillBorder)
        canvas.drawText("Λ RESONANCIA COHERENTE: ${"%.2f".format(archetype.alignmentCoherence)}", pillLeft + 12f, pillTop + 19f, paintPillText)

        // 6. Draw Concept/Description (word wrapped)
        var currentY = margin + 195f
        canvas.drawText("DECLARACIÓN DE FUNCIÓN SEMÁNTICA:", margin + 30f, currentY - 15f, paintTextTitle.apply { textSize = 10f; color = colorCyan })
        
        val wrapPaintDesc = Paint(paintDescription)
        val descLines = wrapText(archetype.description, width - (margin * 2) - 60f, wrapPaintDesc)
        for (line in descLines) {
            canvas.drawText(line, margin + 30f, currentY, wrapPaintDesc)
            currentY += 24f
        }

        // 7. Draw PHYTOM’S Testimony (in custom graphic bracket block)
        currentY += 25f
        val quoteBoxTop = currentY
        val quoteBoxHeight = 160f
        val quoteRect = RectF(margin + 30f, quoteBoxTop, width - margin - 30f, quoteBoxTop + quoteBoxHeight)
        
        val paintQuoteBg = Paint().apply { color = 0xFF060B15.toInt() }
        val paintQuoteBorder = Paint().apply {
            color = 0xFF1E293B.toInt()
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        
        canvas.drawRoundRect(quoteRect, 8f, 8f, paintQuoteBg)
        canvas.drawRoundRect(quoteRect, 8f, 8f, paintQuoteBorder)

        // Small tech accent line in quote box
        val accentPath = Path().apply {
            moveTo(margin + 30f, quoteBoxTop)
            lineTo(margin + 30f, quoteBoxTop + 30f)
            moveTo(margin + 30f, quoteBoxTop)
            lineTo(margin + 60f, quoteBoxTop)
        }
        val paintAccent = Paint().apply {
            color = colorPurple
            strokeWidth = 2.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        canvas.drawPath(accentPath, paintAccent)

        canvas.drawText("TESTIMONIO VIVO DE PHYTOM (CORTEX):", margin + 45f, quoteBoxTop + 24f, paintQuoteLabel)

        val wrapPaintQuote = Paint(paintQuote)
        val quoteLines = wrapText(archetype.narrativeSnippet, width - (margin * 2) - 90f, wrapPaintQuote)
        var quoteY = quoteBoxTop + 50f
        for (i in quoteLines.indices) {
            if (i < 4) { // safety limit
                canvas.drawText(quoteLines[i], margin + 45f, quoteY, wrapPaintQuote)
                quoteY += 22f
            }
        }

        // 8. Draw cyber footer
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(archetype.timestamp))
        canvas.drawText("REGISTRADO EN LA LATTICE: t = $formattedDate", margin + 30f, height - margin - 55f, paintFooter)
        canvas.drawText("SYNTERGIC S.A.F. MYTHOS SYSTEM • CORE MODULE V3.0", margin + 30f, height - margin - 35f, paintFooter.apply { color = colorGray })

        // Save generated bitmap to cache as sharing target
        val file = File(context.cacheDir, "CAF_Archetype_${archetype.id}.png")
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            
            // Build matching content path provider Uri
            FileProvider.getUriForFile(context, "com.example.fileprovider", file)
        } catch (e: Exception) {
            Log.e("ArchetypeImageGen", "Failed to compile visual card PNG image", e)
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

    /**
     * Executes context sharing of the generated visual card or fails back elegantly to text.
     */
    fun shareArchetypeCard(context: Context, archetype: NarrativeArchetype) {
        val imageUri = generateArchetypeCard(context, archetype)
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            if (imageUri != null) {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
            
            val formattedMessage = """
                🌌 S.A.F. MYTHOS - ARQUETIPO COGNITIVO REVELADO 🌌
                
                Nombre: ${archetype.name.uppercase()}
                Resonancia Sintérgica (Λ): ${"%.2f".format(archetype.alignmentCoherence)}
                
                Función en la Red:
                ${archetype.description}
                
                Testimonio de PHYTOM:
                "${archetype.narrativeSnippet}"
                
                --
                S.A.F. Sistema Computacional Cognitivo Vivo.
            """.trimIndent()
            
            putExtra(Intent.EXTRA_SUBJECT, "S.A.F. Mythos Archetype: ${archetype.name}")
            putExtra(Intent.EXTRA_TEXT, formattedMessage)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Compartir Arquetipo S.A.F.")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
