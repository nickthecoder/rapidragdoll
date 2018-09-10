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
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.util.Attribute

class Countdown : ActionRole() {

    @Attribute
    var seconds = 30

    override fun activated() {
        updateText()
    }

    fun go() {
        replaceAction(Delay(1.0).then { countdown() }.repeat(seconds).then { AbstractPlay.instance.timeIsUp() })
    }

    fun stop() {
        replaceAction(null)
    }

    fun countdown() {
        seconds--
        updateText()
    }

    fun updateText() {
        actor.textAppearance?.text = timeString(seconds)
    }
}
