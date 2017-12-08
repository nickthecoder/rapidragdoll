package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.MouseJoint
import org.jbox2d.dynamics.joints.MouseJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseHandler
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.stage.findRoleAt
import uk.co.nickthecoder.tickle.stage.findRoles

class Victory : Play(), MouseHandler {

    var dragPosition = Vector2d()

    var dragging: Draggable? = null

    var mouseJoint: MouseJoint? = null

    var hand: Hand? = null

    val grow = Resources.instance.inputs.find("grow")
    val shrink = Resources.instance.inputs.find("shrink")

    override fun sceneLoaded() {
        super.sceneLoaded()
        mainView = Game.instance.scene.findStageView("main")!!

        var dollZOrder = 1.0
        hand = mainView.stage.findRole<Hand>()!!
        mainView.stage.findRoles<Doll>().forEach { doll ->
            doll.actor.zOrder = dollZOrder++

        }
    }

    override fun onKey(event: KeyEvent) {
        if (grow?.matches(event) == true) {
            dragging?.scale(1.2)
        } else if (shrink?.matches(event) == true) {
            dragging?.scale(1 / 1.2)
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

    override fun onMouseMove(event: MouseEvent) {
        if (event.button == 0) {
            dragObject(event)
        } else {
            super.onMouseMove(event)
        }
    }

    fun dropObject() {
        if (dragging != null) {
            dragging = null
            mouseJoint?.let { Game.instance.scene.world?.destroyJoint(it) }
        }
        hand?.actor?.event("default")
    }

    fun pickUpObject(event: MouseEvent) {
        mainView.screenToView(event.screenPosition, dragPosition)

        val role = mainView.findRoleAt<Draggable>(dragPosition)
        if (role != null) {

            dragging = role
            event.capture()

            val world = Game.instance.scene.world!!
            dragging!!.actor.body!!.isAwake = true

            val jointDef = MouseJointDef()
            jointDef.maxForce = role.mass() * (Math.abs(world.gravity.y) + Math.abs(world.gravity.x)) * 2f
            world.pixelsToWorld(jointDef.target, dragPosition)
            jointDef.bodyA = hand!!.actor.body
            jointDef.bodyB = dragging!!.actor.body

            val joint = world.createJoint(jointDef)
            mouseJoint = joint as MouseJoint
            return

        }
    }

    fun dragObject(event: MouseEvent) {
        mainView.screenToView(event.screenPosition, event.viewPosition)
        mouseJoint?.target?.set(pixelsToWorld(event.viewPosition))
    }

    override fun knockedFragile() {
        // Do nothing
    }

}
