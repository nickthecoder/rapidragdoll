package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.Role

interface Draggable : Role {

    fun mass() = actor.body?.mass ?: 0f

}
