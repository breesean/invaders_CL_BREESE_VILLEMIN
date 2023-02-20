package org.enstabretagne.invaders.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.getWorldProperties;

/**
 * The component representing a playable entity. Represented by a cannon.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class PlayerComponent extends Component {
    private Directions facing;

    /**
     * @param orientation
     *                  The direction the cannon should shoot.
     */
    public PlayerComponent(Directions orientation) {
        if (orientation != Directions.UP && orientation != Directions.DOWN) {
            throw new RuntimeException("Invalid facing direction");
        }
        this.facing = orientation;
    }

    /**
     * Remove the player from the world and end the game.
     * @param player
     *          The entity this component is associated with.
     */
    public void die(Entity player) {
        player.removeFromWorld();
        getWorldProperties().setValue("endGame", true);
    }
}
