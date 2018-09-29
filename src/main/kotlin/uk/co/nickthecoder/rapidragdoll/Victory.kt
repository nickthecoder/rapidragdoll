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
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseListener
import uk.co.nickthecoder.tickle.physics.TickleMouseJoint
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.stage.findRoleAt
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute

class Victory : AbstractPlay(), MouseListener {

    @Attribute
    var showAll = false

    var dragPosition = Vector2d()

    var dragging: Draggable? = null

    var mouseJoint: TickleMouseJoint? = null

    var hand: Hand? = null

    var elastic: Elastic? = null

    // Inputs to grow/shrink items while they are being dragged
    val grow = Resources.instance.inputs.find("grow")
    val shrink = Resources.instance.inputs.find("shrink")
    val stretch = Resources.instance.inputs.find("stretch") // Grow in Y direction only
    val squash = Resources.instance.inputs.find("squash") // Shrink in Y direction only

    override fun sceneLoaded() {
        super.sceneLoaded()
        mainView = Game.instance.scene.findStageView("main")!!

        var dollZOrder = 1.0

        // All Victory scenes must have one and only one Hand object
        hand = mainView.stage.findRole<Hand>()!!
        // All Victory scenes must have one and only one Elastic object.
        elastic = mainView.stage.findRole<Elastic>()!!

        // Ensure that the dolls' parts overlap in a sensible fashion by giving each Doll a unique
        // z-order. Each DollPart will use the Doll's z-order, plus a fractional amount, so that
        // every z-order is unique.
        mainView.stage.findRoles<Doll>().forEach { doll ->
            doll.actor.zOrder = dollZOrder++
        }

        if (!showAll) {
            // Only keep the objects that have been "earned".
            // Some objects will only appear if the player has successfully completed a scene.
            // If the scene hasn't been completed, then kill the actor before the scene starts.
            mainView.stage.findRoles<Reward>().forEach { reward ->
                if (reward.rewardForScene.isNotBlank()) {
                    if (!scenePreferences(reward.rewardForScene).getBoolean("completed", false)) {
                        reward.actor.die()
                    }
                }
            }
        }
    }

    override fun onKey(event: KeyEvent) {
        if (grow?.matches(event) == true) {
            dragging?.scale(1.2)
        } else if (shrink?.matches(event) == true) {
            dragging?.scale(1 / 1.2)
        } else if (stretch?.matches(event) == true) {
            dragging?.scale(1.0, 1.2)
        } else if (squash?.matches(event) == true) {
            dragging?.scale(1.0, 1 / 1.2)
        } else {
            super.onKey(event)
        }
    }

    override fun onMouseButton(event: MouseEvent) {

        if (event.button == 0) {
            if (event.state == ButtonState.PRESSED) {
                hand?.actor?.event("down")
                pickUpObject(event)
            } else if (event.state == ButtonState.RELEASED) {
                dropObject()
                event.release()
                hand?.actor?.event("default")
            }
        } else {
            super.onMouseButton(event)
        }
    }

    fun dropObject() {
        if (dragging != null) {
            dragging = null
            mouseJoint?.destroy()
        }
        hand?.actor?.event("default")
        elastic?.mouseJoint = null
    }

    fun pickUpObject(event: MouseEvent) {
        mainView.screenToView(event.screenPosition, dragPosition)

        val role = mainView.findRoleAt<Draggable>(dragPosition)
        if (role != null) {

            dragging = role
            event.capture()

            val world = mainView.stage.world!!
            dragging!!.actor.body!!.jBox2DBody.isAwake = true

            val maxForce = role.mass() * (Math.abs(world.gravity.y) + Math.abs(world.gravity.x)) / 50.0
            mouseJoint = TickleMouseJoint(dragging!!.actor, dragPosition, maxForce)
            elastic?.mouseJoint = mouseJoint

            return

        }
    }

    override fun knockedFragile() {
        // Do nothing
    }

}
