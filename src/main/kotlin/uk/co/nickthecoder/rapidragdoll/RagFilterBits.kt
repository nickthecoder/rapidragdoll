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
