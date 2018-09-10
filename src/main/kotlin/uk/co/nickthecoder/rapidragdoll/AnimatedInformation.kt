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
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.AttributeType
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.MoveBy
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Attribute

open class AnimatedInformation : ActionRole() {

    @Attribute(AttributeType.RELATIVE_POSITION)
    val moveBy = Vector2d(0.0, -300.0)

    @Attribute
    var seconds = 2.0

    open fun go() {
        replaceAction(goAction())
    }

    open fun goAction(): Action {
        // Adjust the "moveBy" to account for window resizing. moveBy is suitable for the size defined in game info.
        val view = actor.stage?.firstView()!!
        val gi = Resources.instance.gameInfo
        moveBy.mul(view.rect.width.toDouble() / gi.width, view.rect.height.toDouble() / gi.height)
        // Now it is suitable for the current window size.

        return MoveBy(actor.position, moveBy, seconds, Eases.bounce3)
    }

}

class Information : AnimatedInformation() {
    override fun goAction(): Action {
        return super.goAction().then { actor.die() }
    }
}

class SceneComplete : AnimatedInformation()

class TimeIsUp : AnimatedInformation()

class YouLose : AnimatedInformation()
