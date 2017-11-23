package uk.co.nickthecoder.rapidragdoll

import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.ButtonState
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.util.Attribute

class Play : AbstractDirector() {

    @Attribute
    var maxDolls = 20

    var launcher: Launcher? = null
        set(v) {
            field = v
            launchers.forEach {
                if (v !== it) {
                    it.deselect()
                }
            }
        }

    var aim: Aim? = null

    val launchers = mutableListOf<Launcher>()

    val dolls = mutableListOf<Doll>()

    init {
        instance = this
    }

    override fun begin() {
        var launcherCount = 0
        Game.instance.scene.findStage("main")?.actors?.forEach { actor ->
            val role = actor.role
            if (role is Launcher) {
                launcherCount++
                role.number = launcherCount
                launchers.add(role)
                if (launcher == null) {
                    launcher = role
                }
            } else if (role is Aim) {
                aim = role
            }
        }
    }

    override fun onMouseButton(event: MouseEvent) {
        if (event.state == ButtonState.PRESSED) {
            aim?.let {
                launcher?.launch(it)
            }
        }
    }

    fun launched(doll: Doll) {
        dolls.add(doll)
        println("Dolls ${dolls.size} vs $maxDolls")
        if (dolls.size > maxDolls) {
            val remove = dolls.removeAt(0)
            remove.actor.die()
        }
    }

    companion object {
        lateinit var instance: Play
    }

}
