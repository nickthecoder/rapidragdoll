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

import uk.co.nickthecoder.tickle.physics.FilterBits


class RagFilterBits : FilterBits {
    override fun values(): Map<String, Int> {
        return RagFilterBit.values().associateBy({ it.name }, { it.bit })
    }
}

enum class RagFilterBit(val bit: Int) {
    Body(1), Limb(2), Target(4), Solid(8);
}
