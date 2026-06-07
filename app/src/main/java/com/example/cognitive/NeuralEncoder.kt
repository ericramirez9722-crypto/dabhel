package com.example.cognitive

import kotlin.math.sqrt

object NeuralEncoder {
    private const val VECTOR_DIM = 32

    /**
     * Generates a deterministic normalized FloatArray of 32 dimensions representing
     * the "semantic signature" of a given input text.
     */
    fun encode(text: String): FloatArray {
        val cleaned = text.lowercase().trim()
        val vector = FloatArray(VECTOR_DIM) { 0f }
        if (cleaned.isEmpty()) return vector

        // We use letter-grams and hash subdivisions to populate features
        val words = cleaned.split("\\s+".toRegex())
        
        // 1. Feature extraction by character hashes
        for (i in 0 until cleaned.length) {
            val charCode = cleaned[i].code
            val index = (charCode xor (i * 17)) % VECTOR_DIM
            vector[index] += 1.0f
        }

        // 2. Word-based semantic weight simulation
        for (word in words) {
            val wordHash = word.hashCode()
            val primaryIndex = Math.abs(wordHash) % VECTOR_DIM
            val secondaryIndex = Math.abs(wordHash xor 0xf00baa) % VECTOR_DIM
            vector[primaryIndex] += 1.5f
            vector[secondaryIndex] += 0.5f
        }

        // 3. Normalization (unit length) so Cosine Similarity is equivalent to dot product
        var magnitudeSquared = 0f
        for (i in 0 until VECTOR_DIM) {
            magnitudeSquared += vector[i] * vector[i]
        }
        val magnitude = sqrt(magnitudeSquared)

        if (magnitude > 0f) {
            for (i in 0 until VECTOR_DIM) {
                vector[i] /= magnitude
            }
        }
        return vector
    }

    /**
     * Computes the cosine similarity of two normalized float arrays.
     */
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
        if (a.size != b.size || a.isEmpty()) return 0.0
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0.0) 0.0 else dotProduct / denominator
    }
}
