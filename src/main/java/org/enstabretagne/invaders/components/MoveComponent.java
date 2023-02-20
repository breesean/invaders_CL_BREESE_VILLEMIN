package org.enstabretagne.invaders.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;

import static org.enstabretagne.invaders.Constants.*;

/**
 * The component that handles the movement of the entity.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class MoveComponent extends Component {
    /**
     * Horizontal movement for a single frame.
     */
    private double dx = 0;
    /**
     * Vertical movement for a single frame.
     */
    private double dy = 0;

    /**
     * Horizontal speed of the entity.
     */
    private final double HORIZONTAL_SPEED;
    /**
     * Vertical speed of the entity.
     */
    private final double VERTICAL_SPEED;

    // TODO: set 'final' or provide way of changing values ?
    /**
     * Is the entity allowed to move left?
     */
    private boolean leftValid = false;
    /**
     * Is the entity allowed to move right?
     */
    private boolean rightValid = false;
    /**
     * Is the entity allowed to move up?
     */
    private boolean upValid = false;
    /**
     * Is the entity allowed to move down?
     */
    private boolean downValid = false;

    /**
     * The position of the entity.
     */
    private TransformComponent position;

    /**
     * @param horizontalSpeed
     *                  Horizontal speed of the entity.
     * @param verticalSpeed
     *                  Vertical speed of the entity.
     * @param validDirections
     *                  The directions allowed for movement (bit-mask).
     *                  0b1111 -> DOWN | UP | RIGHT | LEFT
     *                  0b1010 -> DOWN | RIGHT
     */
    public MoveComponent(double horizontalSpeed, double verticalSpeed, int validDirections) {
        super();

        this.HORIZONTAL_SPEED = horizontalSpeed;
        this.VERTICAL_SPEED = verticalSpeed;

        if (((validDirections >> Directions.LEFT.ordinal()) & 0b1) == 1) {
            this.leftValid = true;
        }
        if (((validDirections >> Directions.RIGHT.ordinal()) & 0b1) == 1) {
            this.rightValid = true;
        }
        if (((validDirections >> Directions.UP.ordinal()) & 0b1) == 1) {
            this.upValid = true;
        }
        if (((validDirections >> Directions.DOWN.ordinal()) & 0b1) == 1) {
            this.downValid = true;
        }
    }

    /**
     * Is called every frame.
     * @param tpf
     *          Time per frame. Is dependency-injected.
     */
    public void onUpdate(double tpf) {
        // tpf = time per frame (1/tpf = fps), used to keep a constant speed, in all framerate weathers
        this.dx = tpf * this.HORIZONTAL_SPEED;
        this.dy = tpf * this.VERTICAL_SPEED;
    }

    /*
     * Checks to stay in bounds are probably useless, because of the KeepInBoundsComponent
     */

    /**
     * Move the entity left.
     */
    public void moveLeft() {
        if (!this.leftValid) {
            return;
        }

        if (getEntity().getX() - dx >= 0) {
            this.position.translateX(-dx);
        }
    }

    /**
     * Move the entity right.
     */
    public void moveRight() {
        if (!this.rightValid) {
            return;
        }

        if (getEntity().getX() + dx + SPRITE_SIZE <= APP_WIDTH) {
            this.position.translateX(dx);
        }
    }

    /**
     * Move the entity down.
     */
    public void moveDown() {
        if (!this.downValid) {
            return;
        }

        if (getEntity().getY() + dy + SPRITE_SIZE <= APP_HEIGHT) {
            this.position.translateY(dy);
        }
    }

    /**
     * Move the entity up.
     */
    public void moveUp() {
        if (!this.upValid) {
            return;
        }

        if (getEntity().getY() - dy >= 0) {
            this.position.translateY(-dy);
        }
    }
}
