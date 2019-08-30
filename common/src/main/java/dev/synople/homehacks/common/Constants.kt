package dev.synople.homehacks.common

const val auditStartTime = 8 // Earliest time an audit can take place. 08:00
const val auditEndTime = 17 // Latest time an audit can take place. 17:00
const val auditTimeLength = 30 // Takes 30min to do an audit. This is how long each schedule block is
const val auditLookahead = 7 // Maximum number of days ahead someone can schedule an audit