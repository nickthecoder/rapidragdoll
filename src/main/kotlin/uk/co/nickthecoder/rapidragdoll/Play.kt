package uk.co.nickthecoder.rapidragdoll

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.AbstractDirector
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.movement.PanTo
import uk.co.nickthecoder.tickle.events.*
import uk.co.nickthecoder.tickle.physics.RoleContactManager
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.stage.StageView
import uk.co.nickthecoder.tickle.stage.findRole
import uk.co.nickthecoder.tickle.stage.findRoles
import uk.co.nickthecoder.tickle.util.Attribute

open class Play : AbstractDirector(), MouseHandler {

    /**
     * Maximum number of dolls - The oldest dolls are killed when this number is exceeded.
     */
    @Attribute
    var maxDolls = 20

    /**
     * The next scene when this one has been successfully completed.
     * If the scene is the last in the set, then the next scene will be the menu.
     */
    @Attribute
    var nextScene: String = ""

    /**
     * The scene when the escape key is pressed to end the game.
     * The default is to show the main menu, but other scenes may show a difference menu.
     * For example, the turorial scene will return to the tutorial menu.
     */
    @Attribute
    var menuName = "menu"

    // Extent of the game area - Used to constrain panning
    @Attribute
    val bottomLeft = Vector2d(0.0, 0.0)

    // Extent of the game area - Used to constrain panning
    @Attribute
    val topRight = Vector2d(Resources.instance.gameInfo.width.toDouble(), Resources.instance.gameInfo.height.toDouble())

    /**
     * Set to true by SceneComplete role when it's animation is finished.
     * That animation is started from within [objectivesComplete].
     */
    var sceneComplete = false

    /**
     * Set to true when the time is up, or you have failed in other ways.
     */
    var lost = false

    /**
     * Pans the main view when a launcher is selected.
     */
    var panAction: Action? = null
        set(v) {
            field = v
            v?.begin()
        }

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
            if (v != null) {
                panAction = PanTo(mainView, v.panTo, 0.2, Eases.easeOut).then { panAction = null }
            }
        }

    /**
     * The number of remaining objectives till the scene is complete
     */
    var objectives = 0
        set(v) {
            field = v
            if (!lost && v == 0) {
                objectivesComplete()
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

    /**
     * The escape key returns to the menu.
     */
    var escape: Input? = null

    /**
     * The F5 key restarts the scene.
     */
    var restart: Input? = null

    lateinit var mainView: StageView

    lateinit var glassView: StageView

    var startTime = 0.0

    var left: Input? = null
    var right: Input? = null
    var up: Input? = null
    var down: Input? = null

    init {
        instance = this
    }

    override fun begin() {

        left = Resources.instance.inputs.find("left")
        right = Resources.instance.inputs.find("right")
        up = Resources.instance.inputs.find("up")
        down = Resources.instance.inputs.find("down")

        mainView = Game.instance.scene.findView("main") as StageView
        glassView = Game.instance.scene.findView("glass") as StageView
        escape = Resources.instance.inputs.find("escape")
        restart = Resources.instance.inputs.find("restart")

        var launcherCount = 0
        mainView.stage.actors.forEach { actor ->
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
        startTime = Game.instance.seconds
    }

    override fun activated() {
        super.activated()
        Game.instance.scene.world?.setContactListener(RoleContactManager())
    }

    override fun tick() {
        super.tick()
        panAction?.act()

        if (up?.isPressed() == true) {
            mainView.centerY += 5
            constrainView()
        }
        if (down?.isPressed() == true) {
            mainView.centerY -= 5
            constrainView()
        }
        if (left?.isPressed() == true) {
            mainView.centerX -= 5
            constrainView()
        }
        if (right?.isPressed() == true) {
            mainView.centerX += 5
            constrainView()
        }
    }

    override fun postTick() {
        constrainView()
    }

    var started = false

    override fun onKey(event: KeyEvent) {
        if (escape?.matches(event) == true) {
            Game.instance.startScene(menuName)
        }
        if (restart?.matches(event) == true) {
            Game.instance.startScene(Game.instance.sceneName)
        }
    }

    var panStart = Vector2d()

    /**
     * When the left mouse button is pressed, ask the current Launcher to create a Doll
     * (unless the scene is complete, in which case, go to the next scene).
     * When the middle or right button is pressed and dragged, pan the scene.
     */
    override fun onMouseButton(event: MouseEvent) {
        if (event.button == 0) {
            if (event.state == ButtonState.RELEASED) {
                if (sceneComplete) {
                    nextScene()
                } else {
                    aim?.let {
                        if (!started) {
                            glassView.stage.findRole<Countdown>()?.go()
                            mainView.stage.findRoles<Information>().forEach { it.go() }
                            started = true
                        }
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

    override fun onMouseMove(event: MouseEvent) {

        mainView.screenToView(event.screenPosition, event.viewPosition)
        mainView.centerX += panStart.x - event.viewPosition.x
        mainView.centerY += panStart.y - event.viewPosition.y
        mainView.screenToView(event.screenPosition, panStart)
        constrainView()
    }

    fun constrainView() {

        var x = mainView.centerX - Game.instance.window.width / 2
        var y = mainView.centerY - Game.instance.window.height / 2
        if (x < bottomLeft.x) {
            mainView.centerX += bottomLeft.x - x
        }
        if (y < bottomLeft.y) {
            mainView.centerY += bottomLeft.y - y
        }
        x = mainView.centerX + Game.instance.window.width / 2
        y = mainView.centerY + Game.instance.window.height / 2
        if (x > topRight.x) {
            mainView.centerX -= x - topRight.x
        }
        if (y > topRight.y) {
            mainView.centerY -= y - topRight.y
        }

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

    fun objectivesComplete() {
        glassView.stage.findRoles<SceneComplete>().forEach { it.go() }
        glassView.stage.findRoles<Countdown>().forEach { it.stop() }

        val preferences = scenePreferences(Game.instance.sceneName)
        preferences.set("completed", true)
        preferences.set("time", Game.instance.seconds - startTime)
    }

    /**
     * Called from Countdown, when the countdown reaches zero.
     */
    fun timeIsUp() {
        if (!sceneComplete) {
            lost = true
            glassView.stage.findRoles<TimeIsUp>().forEach {
                it.go()
            }
        }
    }

    /**
     * Called from Fragile when it is knocked over
     */
    open fun knockedFragile() {
        if (!sceneComplete) {
            lost = true
            glassView.stage.findRoles<YouLose>().forEach {
                it.go()
            }
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
