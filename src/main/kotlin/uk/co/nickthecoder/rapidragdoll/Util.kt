package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Game

fun timeString(seconds: Int) = "${seconds / 60}:${String.format("%02d", (seconds % 60))}"

fun scenePreferences(sceneName: String) = Game.instance.preferences.node("scenes").node(sceneName)
