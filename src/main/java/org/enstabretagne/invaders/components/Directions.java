package org.enstabretagne.invaders.components;

public enum Directions {
    /**
     * NEVER CHANGE ORDER IN THIS ENUM, because their ordinal() is used in the code.
     * This is important because they are used to pass around authorised direction movements:
     * We flip bits: LEFT is bit 0, RIGHT is bit 1, etc.
     */

    LEFT,
    RIGHT,
    UP,
    DOWN
}
