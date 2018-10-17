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
package uk.co.nickthecoder.rapidragdoll.roles

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Rand

class SnowMachine : ActionRole() {

    @Attribute
    var toX = 1280.0

    @Attribute
    var period = 1.0

    @Attribute
    var count = 1000

    override fun createAction(): Action {
        return Delay(period)
                .then {
                    val snowA = actor.createChild("snow")
                    snowA.x = Rand.between(actor.x, toX)
                    snowA.y = actor.y
                }.repeat(count)
    }

}
