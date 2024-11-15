package com.android.streetworkapp.model.moderation

class TextModerationViewModel {

}


data class TagAnnotation(val tag: TEXT_MODERATION_TAGS, val probability: Double)

enum class TEXT_MODERATION_TAGS {
    TOXICITY, INSULT, THREAT
}