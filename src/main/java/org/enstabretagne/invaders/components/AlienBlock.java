package org.enstabretagne.invaders.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;

import java.util.ArrayList;
import java.util.Hashtable;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getWorldProperties;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static org.enstabretagne.invaders.Constants.*;

/**
 * A rectangular block of aliens, bound together for coherent movement.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class AlienBlock {
    public Hashtable<Integer, Entity> alienDict;
    public final int numberRow;
    public final int alienPerRow;

    /**
     * Direction of movement. Must be Directions.UP or Directions.DOWN.
     * @see Directions
     */
    private final Directions facing;

    /**
     * Bounds of rectangle block (to move block according to extreme aliens)
     */
    private double leftBound, rightBound, topBound, bottomBound;
    /**
     * The id of extreme aliens are necessary to calculate bounds at each move
     * (if alien has been killed, new bounds must be calculated.)
     */
    // id of extreme aliens are necessary to calculate bounds at each move
    private int idTopMostAlien, idBottomMostAlien, idLeftMostAlien, idRightMostAlien;

    /**
     * The block moves left to right, and at a border, goes towards player, then moves right to left, etc.
     */
    private boolean isMovingRight = true;

    /**
     * Builds a rectangular block of aliens, from left to right then in the direction of facing.
     * @param numberRow
     *              Number of rows in the block.
     * @param alienPerRow
     *              Number of aliens per rows.
     * @param orientation
     *              Direction of movement of the block. Must be Directions.UP or Directions.DOWN.
     */
    public AlienBlock(int numberRow, int alienPerRow, Directions orientation) {
        this.numberRow = numberRow;
        this.alienPerRow = alienPerRow;

        if (orientation != Directions.UP && orientation != Directions.DOWN) {
            throw new RuntimeException("Invalid facing direction");
        }
        this.facing = orientation;
        int d = (this.facing == Directions.DOWN) ? 1 : -1;
        // offset if needed, or both blocks overlap at the center.
        int offset = (this.facing == Directions.DOWN) ? 0 : SPRITE_SIZE;
        double center = (double) APP_HEIGHT / 2;

        alienDict = new Hashtable<>();

        for (int i = 0; i < numberRow; i++) {
            for (int j = 0; j < alienPerRow; j++) {
                /*
                 * Some margin is needed between aliens. It needs to be proportional to SPRITE_SIZE.
                 * Here, we use, 5px = SPRITE_SIZE / 12 et 10px = SPRITE_SIZE / 6
                 * So with x = 5px + j * (SPRITE_SIZE + 10px), we get:
                 */
                SpawnData data;
                data = new SpawnData(
                        (SPRITE_SIZE / 12 + j * (SPRITE_SIZE + SPRITE_SIZE / 6)),
                        center - offset + d * (double) (i * SPRITE_SIZE)
                );
                data.put("facing", this.facing);
                data.put("id", i*alienPerRow+j);
                data.put("block", this);

                addAlien(i*alienPerRow+j, spawn("Alien", data));
            }
        }

        if (getWorldProperties().getInt("alienLeft") == -1) {
            getWorldProperties().setValue("alienLeft", numberRow * alienPerRow);
        } else {
            getWorldProperties().increment("alienLeft", alienPerRow * numberRow);
        }

        // No need to search for them, we've just built them.
        this.idTopMostAlien = 0;
        this.idLeftMostAlien = 0;
        this.idBottomMostAlien = numberRow*alienPerRow - 1;
        this.idRightMostAlien = numberRow*alienPerRow - 1;

        this.updateBounds();
    }

    /**
     * Add an alien entity to the block. Aliens are identified by a unique id in a block, to allow differentiation
     * between them.
     * @param id
     *          The id of the alien.
     * @param alien
     *         The alien entity.
     */
    public void addAlien(int id, Entity alien) {
        this.alienDict.put(id, alien);
    }

    /**
     * Remove alien from the block, and remove it from the game world.
     * @param alien
     *          The alien entity to remove.
     */
    public void removeAlien(Entity alien) {
        alien.removeFromWorld();
        this.alienDict.remove(alien.getComponent(AlienComponent.class).getId());

        getWorldProperties().increment("alienLeft", -1);
    }

    /**
     * Update the bounds of the block, using aliens at its extremities.
     */
    private void updateBounds() {
        if (this.alienDict.isEmpty()) {
            return;  // Early escape, to allow game to end
        }

        if (!this.alienDict.containsKey(idLeftMostAlien)) {
            this.findLeftMostAlien();
        }
        if (!this.alienDict.containsKey(idRightMostAlien)) {
            this.findRightMostAlien();
        }
        if (!this.alienDict.containsKey(idTopMostAlien)) {
            this.findTopMostAlien();
        }
        if (!this.alienDict.containsKey(idBottomMostAlien)) {
           this.findBottomMostAlien();
        }

        // Precautions have been taken to avoid null.getX() errors when dict is empty
        // Are useless now, but left for clarity
        if (this.idLeftMostAlien != -1) {
            this.leftBound = this.alienDict.get(this.idLeftMostAlien).getX();
        }
        if (idRightMostAlien != -1) {
            this.rightBound = this.alienDict.get(this.idRightMostAlien).getRightX();
        }
        if (this.idTopMostAlien != -1) {
            this.topBound = this.alienDict.get(this.idTopMostAlien).getY();
        }
        if (this.idBottomMostAlien != -1) {
            this.bottomBound = this.alienDict.get(this.idBottomMostAlien).getBottomY();
        }
    }

    /**
     * Find the left most alien in the block.
     * Search pattern applied:
     * 1 3
     * 2 4
     */
    private void findLeftMostAlien() {
        for (int x = 0; x < this.alienPerRow; x++) {
            for (int y = 0; y < this.numberRow; y++) {
                if (this.alienDict.containsKey(y*this.alienPerRow + x)) {
                    this.idLeftMostAlien = y*this.alienPerRow + x;
                    return;
                }
            }
        }

        // Useless, because we check if alienDict is empty before calling this method
        this.idLeftMostAlien = -1;
    }

    /**
     * Find the right most alien in the block.
     * Search pattern applied:
     * 3 1
     * 4 2
     */
    private void findRightMostAlien() {
        for (int x = this.alienPerRow - 1; x >= 0; x--) {
            for (int y = 0; y < this.numberRow; y++) {
                if (this.alienDict.containsKey(y*this.alienPerRow + x)) {
                    this.idRightMostAlien = y*this.alienPerRow + x;
                    return;
                }
            }
        }

        // Useless, because we check if alienDict is empty before calling this method
        this.idRightMostAlien = -1;
    }

    /**
     * Find the top most alien in the block.
     * Search pattern applied:
     * 1 2
     * 3 4
     */
    private void findTopMostAlien() {
        for (int y = 0; y < this.numberRow; y++) {
            for (int x = 0; x < this.alienPerRow; x++) {
                if (this.alienDict.containsKey(y*this.alienPerRow + x)) {
                    this.idTopMostAlien = y*this.alienPerRow + x;
                    return;
                }
            }
        }

        // Useless, because we check if alienDict is empty before calling this method
        this.idTopMostAlien = -1;
    }

    /**
     * Find the bottom most alien in the block.
     * Search pattern applied:
     * 3 4
     * 1 2
     */
    private void findBottomMostAlien() {
        for (int y = this.numberRow - 1; y >= 0; y--) {
            for (int x = 0; x < this.alienPerRow; x++) {
                if (this.alienDict.containsKey(y*this.alienPerRow + x)) {
                    this.idBottomMostAlien = y*this.alienPerRow + x;
                    return;
                }
            }
        }

        // Useless, because we check if alienDict is empty before calling this method
        this.idBottomMostAlien = -1;
    }

    /**
     * Move the block.
     * If at the left or right bound, the block will move up/down according to AlienBock#facing and change direction.
     * If the bottom or top bounds are reached, game-over.
     * Update bounds after moving.
     */
    public void move() {
        if (this.bottomBound - SPRITE_SIZE < 0 || this.topBound + SPRITE_SIZE > APP_HEIGHT) {
            getWorldProperties().setValue("endGame", true);
            return;
        }

        if (this.isMovingRight && this.rightBound + SPRITE_SIZE / 2 >= APP_WIDTH) {
            this.isMovingRight = false;
            if (this.facing == Directions.UP) {
                this.moveUp();
            } else {
                this.moveDown();
            }
        } else if (!this.isMovingRight && this.leftBound - SPRITE_SIZE / 2 <= 0) {
            this.isMovingRight = true;
            if (this.facing == Directions.UP) {
                this.moveUp();
            } else {
                this.moveDown();
            }
        } else {
            if (this.isMovingRight) {
                this.moveRight();
            } else {
                this.moveLeft();
            }
        }

        this.updateBounds();
    }

    /**
     * Move the block left by individually moving each alien.
     */
    public void moveLeft() {
        for (Entity alien : this.alienDict.values()) {
            alien.getComponent(MoveComponent.class).moveLeft();
        }
    }

    /**
     * Move the block right by individually moving each alien.
     */
    public void moveRight() {
        for (Entity alien : this.alienDict.values()) {
            alien.getComponent(MoveComponent.class).moveRight();
        }
    }

    /**
     * Move the block down by individually moving each alien.
     */
    public void moveDown() {
        for (Entity alien : this.alienDict.values()) {
            alien.getComponent(MoveComponent.class).moveDown();
        }
    }

    /**
     * Move the block up by individually moving each alien.
     */
    public void moveUp() {
        for (Entity alien : this.alienDict.values()) {
            alien.getComponent(MoveComponent.class).moveUp();
        }
    }

    public void action()  {

    }

    /**
     * Find aliens who can shoot. Then randomly make them shoot.
     * Only aliens in an extreme line (if facing DOWN, then only the bottom aliens can shoot).
     *
     *
     * ex of search pattern for block facing down :
     * 2 4  and if only 2 4  remain, then 1 and 4 might shoot.
     * 1 3              1 .
     */
    public void maybeAct() {
        ArrayList<Entity> mightAct = new ArrayList<>();
        for (int x = 0; x < this.alienPerRow; x++) {
            // Given the way we fill the hashmap, no need to differentiate facing in this loop!
            for (int y = numberRow - 1; y >= 0; y--) {
                // Keep the first alien that can act per column.
                if (this.alienDict.containsKey(y*this.alienPerRow + x)) {
                    mightAct.add(alienDict.get(y*this.alienPerRow + x));
                    break;
                }
            }
        }

        for (Entity alien : mightAct) {
            if (FXGLMath.randomBoolean(ALIEN_ACTION_CHANCE / mightAct.size())) {
                alien.getComponent(BlasterComponent.class).blast(); //Ici on veut "execute strategie numero alien.action"
            }
        }
    }
}
