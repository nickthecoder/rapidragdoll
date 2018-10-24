package uk.co.nickthecoder.rapidragdoll.ui

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Scale
import uk.co.nickthecoder.tickle.action.animation.Turn
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Button
import uk.co.nickthecoder.tickle.util.ButtonEffects

class MenuButtonEffects : ButtonEffects {

    override fun enter(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY * 1.2, Eases.easeOut)
    }

    override fun exit(button: Button): Action? {
        return Scale(button.actor, 0.1, button.actor.scaleXY / 1.2, Eases.easeIn)
    }

    override fun clicked(button: Button): Action? {
        //return Scale(button.actor, 0.5, 6.0, Eases.easeInQuad)
        //        .and(Fade(button.actor.color, 0.5, 0f, Eases.easeInQuad))
        return Scale(button.actor, 0.7, button.actor.scaleXY / 1.2, Eases.easeOut)
                .and(
                        Turn(button.actor.direction, 0.65, button.actor.direction + Angle.degrees(2 * 360.0), Eases.easeOutBack)
                )
    }
}
