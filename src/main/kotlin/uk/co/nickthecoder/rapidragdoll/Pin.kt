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

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.Until
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade

/**
 * When the pin is knocked over, then one of the scene's objectives have been met.
 */
class Pin : ActionRole() {

    override fun createAction(): Action {

        AbstractPlay.instance.objectives++

        return Until { (actor.direction.degrees < -70 || actor.direction.degrees > 70) }
                .then {
                    actor.event("score")
                    AbstractPlay.instance.objectives--
                }
                .then(Fade(actor.color, 1.0, 0f, Eases.easeIn))
                .then(Kill(actor))
    }

}
