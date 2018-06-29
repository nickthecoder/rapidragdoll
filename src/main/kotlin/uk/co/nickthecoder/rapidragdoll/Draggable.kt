package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Role

interface Draggable : Role {

    fun mass() = actor.body?.mass ?: 0f

    fun scale(scale: Double) {
        scale(scale, scale)
    }

    fun scale(scaleX: Double, scaleY: Double) {
        actor.scale.mul(scaleX, scaleY)
    }
}
