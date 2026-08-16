package com.saee.combattimer.model

enum class SportType {
    MMA_AMATEUR,
    MMA_PRO_REGULAR,
    MMA_PRO_TITLE,
    BOXING_AMATEUR,
    BOXING_PROFESSIONAL,
    JUDO,
    INTERVAL,
    SHARK_TANK
}

/**
 * A fixed sport ruleset. Only [INTERVAL] allows configuring round/rest duration;
 * only [BOXING_PROFESSIONAL] allows configuring the round count; only
 * [SHARK_TANK] allows configuring round duration and round count with no rest
 * field at all (it's always 0 - back-to-back rounds, no break). Every other
 * sport starts exactly as defined here.
 */
data class SportPreset(
    val type: SportType,
    val displayName: String,
    val defaultRounds: Int,
    val roundSeconds: Int,
    val restSeconds: Int,
    val hasGoldenScore: Boolean = false,
    val roundsConfigurable: Boolean = false,
    val roundsRange: IntRange = 1..1,
    val fullyConfigurable: Boolean = false,
    val roundSecondsConfigurable: Boolean = false
) {
    val isConfigurable: Boolean get() = roundsConfigurable || fullyConfigurable || roundSecondsConfigurable
}

object SportPresets {
    val MMA_AMATEUR = SportPreset(
        type = SportType.MMA_AMATEUR,
        displayName = "MMA Amateur",
        defaultRounds = 3,
        roundSeconds = 180,
        restSeconds = 60
    )

    val MMA_PRO_REGULAR = SportPreset(
        type = SportType.MMA_PRO_REGULAR,
        displayName = "MMA Pro (Regular Fight)",
        defaultRounds = 3,
        roundSeconds = 300,
        restSeconds = 60
    )

    val MMA_PRO_TITLE = SportPreset(
        type = SportType.MMA_PRO_TITLE,
        displayName = "MMA Pro (Title Fight)",
        defaultRounds = 5,
        roundSeconds = 300,
        restSeconds = 60
    )

    val BOXING_AMATEUR = SportPreset(
        type = SportType.BOXING_AMATEUR,
        displayName = "Boxing Amateur",
        defaultRounds = 3,
        roundSeconds = 180,
        restSeconds = 60
    )

    val BOXING_PROFESSIONAL = SportPreset(
        type = SportType.BOXING_PROFESSIONAL,
        displayName = "Boxing Professional",
        defaultRounds = 4,
        roundSeconds = 180,
        restSeconds = 60,
        roundsConfigurable = true,
        roundsRange = 4..12
    )

    val JUDO = SportPreset(
        type = SportType.JUDO,
        displayName = "Judo",
        defaultRounds = 1,
        roundSeconds = 240,
        restSeconds = 0,
        hasGoldenScore = true
    )

    val INTERVAL = SportPreset(
        type = SportType.INTERVAL,
        displayName = "Interval Setting",
        defaultRounds = 3,
        roundSeconds = 180,
        restSeconds = 60,
        fullyConfigurable = true
    )

    /** Back-to-back rounds with no rest between them - just the 10-second clapper and the buzzer. */
    val SHARK_TANK = SportPreset(
        type = SportType.SHARK_TANK,
        displayName = "Shark Tank",
        defaultRounds = 3,
        roundSeconds = 180,
        restSeconds = 0,
        roundsConfigurable = true,
        roundsRange = 1..20,
        roundSecondsConfigurable = true
    )

    val ALL: List<SportPreset> = listOf(
        MMA_AMATEUR,
        MMA_PRO_REGULAR,
        MMA_PRO_TITLE,
        BOXING_AMATEUR,
        BOXING_PROFESSIONAL,
        JUDO,
        INTERVAL,
        SHARK_TANK
    )
}
