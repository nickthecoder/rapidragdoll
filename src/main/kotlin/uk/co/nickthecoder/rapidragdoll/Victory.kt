package uk.co.nickthecoder.rapidragdoll

import org.jbox2d.dynamics.joints.MouseJoint
import org.jbox2d.dynamics.joints.MouseJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.MouseHandler
import uk.co.nickthecoder.tickle.physics.pixelsToWorld
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.stage.findRoleAt

class Victory : AbstractDirector(), MouseHandler {

    var dragPosition = Vector2d()

    var dragging: Draggable? = null

    var mouseJoint: MouseJoint? = null

    var hand: Hand? = null

    lateinit var mainView: StageView

    override fun activated() {
        mainView = Game.instance.scene.findStageView("main")!!
        hand = mainView.stage.findRole<Hand>()!!
    }

    override fun onMouseButton(event: MouseEvent) {

        if (event.button == 0 && event.state == ButtonState.PRESSED) {
            pickUpObject(event)
        } else if (event.state == ButtonState.RELEASED) {
            dropObject()
            event.release()
        }
    }

    override fun onMouseMove(event: MouseEvent) {
        dragging?.let {
            dragObject(event, it)
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

    fun dragObject(event: MouseEvent, obj: Draggable) {
        mainView.screenToView(event.screenPosition, event.viewPosition)
        mouseJoint?.target?.set(pixelsToWorld(event.viewPosition))
    }

}
