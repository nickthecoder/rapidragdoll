package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.AutoFlushPreferences
import uk.co.nickthecoder.tickle.util.Button
import uk.co.nickthecoder.tickle.util.CostumeAttribute

class LevelStatus : Button() {

    @Attribute
    var text = ""

    @Attribute
    var sceneName = ""

    @Attribute
    var unlocked = false

    @CostumeAttribute
    val textPosition = Vector2d()

    @CostumeAttribute
    val timePosition = Vector2d()

    @CostumeAttribute
    var showTime = true

    var preferences: AutoFlushPreferences? = null

    override fun activated() {
        super.activated()

        val textA = actor.createChild("text")
        textA.textAppearance?.text = text
        textA.position.add(textPosition)

        preferences = Game.instance.preferences.node("scenes").node(sceneName)
        unlocked = unlocked || preferences?.getBoolean("unlocked", false) ?: false

        if (unlocked) {
            actor.event("unlocked")

            if (showTime) {
                val seconds = preferences?.getInt("seconds", -1) ?: -1
                if (seconds >= 0) {
                    actor.event("completed")
                    val timeA = actor.createChild("time")
                    timeA.textAppearance?.text = text
                    timeA.position.add(textPosition)
                }
            }

        } else {
            enabled = false
        }
    }

    override fun onClicked(event: MouseEvent) {
        if (unlocked) {
            Game.instance.startScene(sceneName)
        }
    }

}
