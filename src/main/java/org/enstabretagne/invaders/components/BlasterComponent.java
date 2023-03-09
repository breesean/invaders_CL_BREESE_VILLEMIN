package org.enstabretagne.invaders.components;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import org.enstabretagne.invaders.InvadersNetworkAppV2;

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
    private double lastShot = 0;
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
    private final double BLAST_SPEED;
    /**
     * Sprite used for the generated entities. (Ability to differentiate sprite according to entity).
     */
    private final String sprite;
    /**
     * Direction of the entity shooting - changes the direction of the generated entities.
     */
    private final Directions facing;

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
    public void blast() {
        if (getGameTimer().getNow() - this.lastShot >= BLAST_COOLDOWN) {
            SpawnData data;
            if (this.facing == Directions.UP) {
                // +- SPRITE_SIZE / 2 to get it above/below, +-1 to prevent hitboxes from touching
                data = new SpawnData(getEntity().getX(), getEntity().getY() - SPRITE_SIZE / 2 - 1);
            } else if (this.facing == Directions.DOWN) {
                data = new SpawnData(getEntity().getRightX(), getEntity().getBottomY() + SPRITE_SIZE / 2 + 1);
            } else {
                throw new RuntimeException("Invalid facing direction");
            }
            data.put("direction", this.facing);
            data.put("sprite", this.sprite);
            data.put("speed", this.BLAST_SPEED);

            getGameWorld().spawn("Blast", data);
            lastShot = getGameTimer().getNow();

            playSound();
        }
    }

    /**
     * Plays the audio file if the cooldown is over.
     */
    private void playSound() {
        if (getGameTimer().getNow() - this.lastSound >= BLAST_SOUND_COOLDOWN) {
            play(this.soundFile);
            this.lastSound = getGameTimer().getNow();
        }
    }
}
