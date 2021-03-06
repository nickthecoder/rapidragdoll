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

import uk.co.nickthecoder.tickle.Role

interface Draggable : Role {

    fun mass() = actor.body?.mass ?: 0.0

    fun scale(scale: Double) {
        scale(scale, scale)
    }

    fun scale(scaleX: Double, scaleY: Double) {
        actor.scale.mul(scaleX, scaleY)
    }
}
