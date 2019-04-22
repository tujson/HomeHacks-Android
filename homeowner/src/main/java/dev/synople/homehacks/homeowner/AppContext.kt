package dev.synople.homehacks.homeowner

import android.app.Application
import dev.synople.homehacks.common.models.Homeowner

class AppContext : Application() {
    companion object {
        var user: Homeowner = Homeowner()
    }
}