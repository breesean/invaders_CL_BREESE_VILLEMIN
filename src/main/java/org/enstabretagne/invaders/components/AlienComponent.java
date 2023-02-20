package org.enstabretagne.invaders.components;

import javafx.util.Duration;

/**
 * The component that ties an alien Entity to its behaviour.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class AlienComponent extends AnimationComponent {
    /**
     * The block the alien is a part of is needed in order to remove the alien from the world AND the block.
     */
    private final AlienBlock block;
    private final int id;
    private final Directions facing;

    /**
     * @param id
     *          An alien has a unique id in its block.
     * @param block
     *          The block the alien is a part of.
     * @param orientation
     *          The direction the alien is facing.
     */
    public AlienComponent(int id, AlienBlock block, Directions orientation) {
        super("invader.png", 2, 64, 64, Duration.seconds(1), 0, 1);

        this.block = block;
        this.id = id;

        if (orientation != Directions.UP && orientation != Directions.DOWN) {
            throw new RuntimeException("Invalid facing direction");
        }
        this.facing = orientation;
    }

    /**
     * @return the id of the alien (ids are unique in a block).
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return the block the alien is a part of.
     */
    public AlienBlock getBlock() {
        return this.block;
    }
}
