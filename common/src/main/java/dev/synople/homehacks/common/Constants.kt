package dev.synople.homehacks.common

const val AUDIT_START_TIME = 8 // Earliest time an audit can take place. 08:00
const val AUDIT_END_TIME = 17 // Latest time an audit can take place. 17:00
const val AUDIT_TIME_LENGTH =
    30 // Takes 30min to do an audit. This is how long each schedule block is
const val AUDIT_LOOKAHEAD = 7 // Maximum number of days ahead someone can schedule an audit

const val SURVEY_VERSION = "v1"