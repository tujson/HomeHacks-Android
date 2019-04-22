package dev.synople.homehacks.auditor

import android.app.Application
import dev.synople.homehacks.common.models.Auditor

class AppContext : Application() {
    companion object {
        var user: Auditor = Auditor()
    }
}