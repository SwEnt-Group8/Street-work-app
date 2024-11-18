package com.android.streetworkapp.model.moderation

interface TextModerationRepository {
    /**
     * Analyses the content, returns true if the content is under the threshold, false otherwise
     * @param content The text to be analyzed
     * @param thresholds A map that holds the threshold values for each tags. Each threshold is the probability of the text being included in this tag, ranges from 0-1.
     * @return True if the text is under the thresholds for all tags, false otherwise
     */
    fun evaluateText(content: String, thresholds: Map<TextModerationTags, Double>): Boolean

    /**
     * Gets the tags and their probabilities for param content
     * @param content Text to be analyzed
     * @return TextEvaluationResult.Success if the API could process the content, TextEvaluationResult.Error if an error was encountered
     */
    fun getTextAnnotations(content: String): TextEvaluationResult
}