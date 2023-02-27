package org.enstabretagne.invaders.components;

import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import org.enstabretagne.invaders.IComportement;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static org.enstabretagne.invaders.Constants.*;

/**
 * The component for entities who can shoot.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class BlasterComponent extends Component {

    /**
     * A cooldown is applied between shots, or the entity would shoot 60 times per second.
     */
    public double lastShot = 0;
    /**
     * A cooldown is applied between audio uses, because they overlap and it becomes a mess.
     */
    private double lastSound = 0;
    /**
     * The audio file to play at each shot. (Ability to differentiate sound according to entity).
     */
    private final String soundFile;
    /**
     * Speed of the generated entities. (Ability to differentiate speed according to entity).
     */
    public final double BLAST_SPEED;
    /**
     * Sprite used for the generated entities. (Ability to differentiate sprite according to entity).
     */
    public final String sprite;
    /**
     * Direction of the entity shooting - changes the direction of the generated entities.
     */
    public final Directions facing;

    /**
     *
     * @param orientation
     *          The direction of the entity shooting.
     * @param blastSpeed
     *              The speed of the generated entities.
     * @param sprite
     *              The sprite of the generated entities.
     * @param sound
     *              The audio file to play at each shot.
     */
    public BlasterComponent(Directions orientation, double blastSpeed, String sprite, String sound) {
        super();

        this.BLAST_SPEED = blastSpeed;
        this.sprite = sprite;
        this.soundFile = sound;

        if (orientation != Directions.UP && orientation != Directions.DOWN) {
            throw new RuntimeException("Invalid facing direction");
        }
        this.facing = orientation;
    }

    /**
     * Shoots a blast (generates an entity) is the cooldown is over.
     */
    IComportement mastrategy;
    public void blast() {
        mastrategy.execute(this);
    }

    /**
     * Plays the audio file if the cooldown is over.
     */
    public void playSound() {
        if (getGameTimer().getNow() - this.lastSound >= BLAST_SOUND_COOLDOWN) {
            play(this.soundFile);
            this.lastSound = getGameTimer().getNow();
        }
    }
}
