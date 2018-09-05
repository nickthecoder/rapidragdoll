/*
Rapid Rag Doll
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.AutoFlushPreferences
import uk.co.nickthecoder.tickle.util.SceneButton

class LevelStatus : SceneButton() {

    @Attribute
    var text = ""

    @Attribute
    var requiredScene = ""

    @Attribute
    var unlocked = false

    @Attribute(AttributeType.RELATIVE_POSITION)
    val textPosition = Vector2d()

    @Attribute(AttributeType.RELATIVE_POSITION)
    val timePosition = Vector2d()

    @Attribute
    var showTime = true

    var preferences: AutoFlushPreferences? = null

    override fun activated() {
        super.activated()

        if (!unlocked && requiredScene.isNotBlank() && !Game.instance.preferences.node("scenes").node(requiredScene).getBoolean("completed", false)) {
            actor.hide()
            return
        }

        val buttonA = actor.createChild("text")
        buttonA.textAppearance?.text = text
        buttonA.position.add(textPosition)
        buttonA.zOrder = actor.zOrder + 1
        buttonA.viewAlignmentX = actor.viewAlignmentX
        buttonA.viewAlignmentY = actor.viewAlignmentY

        preferences = Game.instance.preferences.node("scenes").node(scene)
        unlocked = unlocked || preferences?.getBoolean("unlocked", false) ?: false

        val role = buttonA.role
        if (role is LevelButton) {
            role.sceneName = scene
        }

        if (showTime) {
            val seconds = preferences?.getInt("seconds", -1) ?: -1
            if (seconds >= 0) {
                actor.event("completed")
                val timeA = actor.createChild("time")
                timeA.textAppearance?.text = text
                timeA.position.add(textPosition)
            }
        }

    }

}
