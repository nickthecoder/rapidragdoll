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
package uk.co.nickthecoder.rapidragdoll.ui

import org.joml.Vector2d
import uk.co.nickthecoder.rapidragdoll.AbstractPlay
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.stage.findRolesAt
import uk.co.nickthecoder.tickle.util.Button

class Arrow : AbstractRole() {

    private val mouse = Vector2d()
    private val tempVector = Vector2d()

    lateinit var pointerActor: Actor

    lateinit var aimActor: Actor

    var hidden = false

    override fun activated() {
        super.activated()
        pointerActor = actor.createChild("pointer")
        pointerActor.zOrder = 998.0
        pointerActor.stage = Game.instance.scene.findStage("glass")
        pointerActor.zOrder = 999.0
        pointerActor.hide()

        aimActor = actor.createChild("aim")
        aimActor.hide()
    }

    override fun tick() {

        // If the Hand is over a Button, then hide this Actor, and show the Hand.
        if (pointerActor.stage?.findRolesAt<Button>(pointerActor.position)?.isEmpty() == true) {
            if (hidden) {
                pointerActor.hide()
                actor.event("default")
                hidden = false
            }
        } else {
            // Revert to normal when not over a Button.
            if (!hidden) {
                actor.hide()
                aimActor.hide()
                pointerActor.event("default")
                hidden = true
            }
        }

        if (!hidden) {
            actor.stage?.firstView()?.mousePosition(mouse)
            AbstractPlay.instance.launcher?.let {
                actor.position.set(it.actor.position)
                mouse.sub(actor.position, tempVector)
                val magnitude = tempVector.length()

                if (magnitude > it.speed) {
                    // Show the Aim actor, and draw the arrow from the launcher towards the mouse position
                    // With magnitude of the maximum speed of the launcher.
                    aimActor.event("default")
                    tempVector.normalize(it.speed).add(actor.position)
                    actor.ninePatchAppearance?.lineTo(tempVector)
                } else {
                    // Hide the Aim actor, and draw the arrow from the launcher to the mouse position
                    aimActor.hide()
                    actor.ninePatchAppearance?.lineTo(mouse)
                }
            }
        }
    }

}
