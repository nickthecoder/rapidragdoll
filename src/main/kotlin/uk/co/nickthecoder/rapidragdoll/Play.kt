package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.util.Attribute

open class Play : AbstractDirector(), MouseHandler {

    @Attribute
    var maxDolls = 20

    @Attribute
    var nextScene: String = ""

    var sceneComplete = false

    /**
     * The currently selected launcher.
     */
    var launcher: Launcher? = null
        set(v) {
            field = v
            launchers.forEach {
                if (v !== it) {
                    it.deselect()
                }
            }
        }

    /**
     * The number of remaining objectives till the scene is complete
     */
    var objectives = 0
        set(v) {
            field = v
            if (v == 0) {
                glassView.stage.findRole<SceneComplete>()?.complete = true
            }
        }

    /**
     * A list of all launchers in the scene. Populated when the scene begins by looking for all Actors with Roles
     * of type Launcher.
     */
    val launchers = mutableListOf<Launcher>()

    /**
     * The Aim role, which follow the mouse. This is use to aim the Dolls.
     * Set when the scene begins.
     */
    var aim: Aim? = null

    /**
     * A list of all Dolls in the scene. When the maximum number of dolls is exceeded, the earliest doll is killed
     * and removed from the list. A new Doll is added to the list when a Launcher creates a Doll.
     */
    val dolls = mutableListOf<Doll>()

    var escape: Input? = null

    lateinit var mainView: StageView

    lateinit var glassView: StageView

    init {
        instance = this
    }

    override fun begin() {

        mainView = Game.instance.scene.findView("main") as StageView
        glassView = Game.instance.scene.findView("glass") as StageView
        escape = Resources.instance.inputs.find("escape")

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

    override fun onKey(event: KeyEvent) {
        if (escape?.matches(event) == true) {
            if (sceneComplete) {
                nextScene()
            } else {
                Game.instance.startScene("menu")
            }
        }
    }

    /**
     * When the left mouse button is pressed, ask the current Launcher to create a Doll
     * (unless the scene is complete, in which case, go to the next scene).
     * When the middle or right button is pressed and dragged, pan the scene.
     */
    override fun onMouseButton(event: MouseEvent) {
        if (event.button == 0) {
            if (event.state == ButtonState.PRESSED) {
                if (sceneComplete) {
                    nextScene()
                } else {
                    aim?.let {
                        launcher?.launch(it.actor.position)
                    }
                }
            }
        } else {
            if (event.state == ButtonState.PRESSED) {
                panStart.set(mainView.screenToView(event.screenPosition))
                event.capture()
            } else if (event.state == ButtonState.RELEASED) {
                event.release() // End dragging
            }
        }
    }

    var panStart = Vector2d()

    override fun onMouseMove(event: MouseEvent) {
        mainView.screenToView(event.screenPosition, event.viewPosition)
        mainView.centerX += panStart.x - event.viewPosition.x
        mainView.centerY += panStart.y - event.viewPosition.y
        mainView.screenToView(event.screenPosition, panStart)
    }

    /**
     * Called from Launcher whenever a Doll is created.
     */
    fun launched(doll: Doll) {
        dolls.add(doll)
        if (dolls.size > maxDolls) {
            dolls.removeAt(0).ending = true
        }
    }

    fun nextScene() {
        Game.instance.startScene(nextScene)
    }

    companion object {
        /**
         * Allows easy access to this Director, set when a Play object is created.
         */
        lateinit var instance: Play
    }

}
