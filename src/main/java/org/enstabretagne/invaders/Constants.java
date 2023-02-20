package org.enstabretagne.invaders;

/**
 * Constants used throughout the game.
 * @author Antoine Breesé
 * @author Henri lardy
 */
public final class Constants {
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 900;

    /**
     * The size of an individual sprite image. Sprites are square-shaped. If the sprite is used for an animation, then
     * its height and width must always be multiples of this size.
     */
    public static final int SPRITE_SIZE = 64;

    /**
     * Cooldown time between two shots.
     * This cooldown is necessary because the player would otherwise shoot 60 times per second.
     * @see org.enstabretagne.invaders.components.BlasterComponent
     */
    public static final double BLAST_COOLDOWN = 0.200;

    /**
     * The sound cooldown is necessary because the addition of all the blast sound is unbearable.
     * @see org.enstabretagne.invaders.components.BlasterComponent
     */
    public static final double BLAST_SOUND_COOLDOWN = 0.200;

    /**
     * Probability for an alone alien to shoot. This probability is divided by the number of alien that can effectively shoot.
     * @see org.enstabretagne.invaders.components.BlasterComponent
     */
    public static final double ALIEN_BLAST_CHANCE = 0.5;

    /**
     * Speed at which the players' blasts move.
     * @see org.enstabretagne.invaders.InvadersFactory
     * @see org.enstabretagne.invaders.components.BlasterComponent
     */
    public static final double PLAYER_BLAST_SPEED = 300;

    /**
     * Speed at which aliens' blasts move.
     * @see org.enstabretagne.invaders.InvadersFactory
     * @see org.enstabretagne.invaders.components.BlasterComponent
     */
    public static final double ALIEN_BLAST_SPEED = 100;

    /**
     * Speed at which the player moves.
     * @see org.enstabretagne.invaders.InvadersFactory
     * @see org.enstabretagne.invaders.components.MoveComponent
     */
    public static final double PLAYER_SPEED = 300; // At 60fps, it does +- 5px/frame

    /**
     * Speed at which aliens move horizontally.
     * @see org.enstabretagne.invaders.InvadersFactory
     * @see org.enstabretagne.invaders.components.MoveComponent
     */
    public static final double ALIEN_HORIZONTAL_SPEED = 1200; // 600;

    /**
     * Speed at which aliens move vertically.
     * @see org.enstabretagne.invaders.InvadersFactory
     * @see org.enstabretagne.invaders.components.MoveComponent
     */
    public static final double ALIEN_VERTICAL_SPEED = 1200; // 600;


    /**
     * Blast hit-box width.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int BLAST_W = SPRITE_SIZE / 8;
    /**
     * Blast hit-box height.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int BLAST_H = SPRITE_SIZE / 4;
    /**
     * Blast hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int BLAST_X = SPRITE_SIZE / 2 - BLAST_W / 2;
    /**
     * Blast hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int BLAST_Y = SPRITE_SIZE / 2 - BLAST_H / 2;


    /**
     * Cannon hit-box width.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int CANNON_W = SPRITE_SIZE - SPRITE_SIZE / 4;
    /**
     * Cannon hit-box height.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int CANNON_H = CANNON_W;
    /**
     * Cannon hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int CANNON_X = SPRITE_SIZE / 2 - CANNON_W / 2;
    /**
     * Cannon hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int CANNON_Y = SPRITE_SIZE / 2 - CANNON_H / 2;


    /**
     * Alien hit-box width.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int INVADER_W = SPRITE_SIZE;
    /**
     * Alien hit-box height.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int INVADER_H = SPRITE_SIZE - SPRITE_SIZE / 8;
    /**
     * Alien hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int INVADER_X = SPRITE_SIZE / 2 - INVADER_W / 2;
    /**
     * Alien hit-box offset from top-left corner of sprite.
     * @see org.enstabretagne.invaders.InvadersFactory
     */
    public static final int INVADER_Y = SPRITE_SIZE / 2 - INVADER_H / 2;
}

