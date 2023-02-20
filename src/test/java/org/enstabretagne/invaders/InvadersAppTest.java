package org.enstabretagne.invaders;

import com.almasb.fxgl.dsl.components.KeepInBoundsComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.enstabretagne.invaders.components.Directions;
import org.enstabretagne.invaders.components.EntityType;
import org.enstabretagne.invaders.components.MoveComponent;
import org.enstabretagne.invaders.components.PlayerComponent;

import static org.enstabretagne.invaders.Constants.*;

import com.almasb.fxgl.dsl.FXGL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class InvadersAppTest {
    private PlayerComponent playerComponent;
    private MoveComponent playerMoveComponent;

    @BeforeEach
    public void setup(){
        playerComponent = new PlayerComponent(Directions.UP);

        int validDirections = 0;
        validDirections |= 1 << Directions.LEFT.ordinal();
        validDirections |= 1 << Directions.RIGHT.ordinal();

        playerMoveComponent = new MoveComponent(PLAYER_SPEED, PLAYER_SPEED, validDirections);

        FXGL.entityBuilder()
                .at((double) (APP_WIDTH - SPRITE_SIZE) / 2,Constants.APP_HEIGHT - SPRITE_SIZE)
                .type(EntityType.PLAYER)
                // .view(FXGL.texture("canon.png"))
                .bbox(new HitBox(new Point2D(CANNON_X, CANNON_Y), BoundingShape.box(CANNON_W, CANNON_H)))
                .with(playerComponent)
                .with(playerMoveComponent)
                .with(new KeepInBoundsComponent(new Rectangle2D(0, 0, APP_WIDTH, APP_HEIGHT)))
                .with(new CollidableComponent(true))
                .build();
    }

    @Test
    @DisplayName("Generate test Player entity")
    public void playerSpawnTest(){
        assertEquals((double) (APP_WIDTH - SPRITE_SIZE) / 2, playerComponent.getEntity().getX());
        assertEquals(APP_HEIGHT - SPRITE_SIZE, playerComponent.getEntity().getY());
    }

    /**
     * Test the MoveComponent associated to a PlayerComponent.
     * The player is supposed to move horizontally but not vertically.
     * The player is supposed to stay in bounds.
     */
    @Test
    @DisplayName("Move Player entity")
    public void playerMoveTest() {
        // Need to simulate game tick to update the player position, or dx = 0...
        double tpf = 1 / 60.0;
        double expectedDx = PLAYER_SPEED * tpf;
        playerMoveComponent.onUpdate(tpf);

        // Simple case: move to the left from the centre
        double formerX = playerComponent.getEntity().getX();
        double formerY = playerComponent.getEntity().getY();
        playerMoveComponent.moveLeft();
        assertEquals(formerX - expectedDx, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());

        // Simple case: move to the right from the centre
        formerX = playerComponent.getEntity().getX();
        formerY = playerComponent.getEntity().getY();
        playerMoveComponent.moveRight();
        assertEquals(formerX + expectedDx, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());

        // Complex cases: Try to move left/right while at the extremities of the scene

        // Place the player at the left of the scene
        formerX = 0;
        formerY = playerComponent.getEntity().getY();
        playerComponent.getEntity().setPosition(formerX, APP_HEIGHT - SPRITE_SIZE);
        playerMoveComponent.moveLeft();
        assertEquals(formerX, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());

        // Place the player at the right of the scene
        formerX = APP_WIDTH;
        formerY = playerComponent.getEntity().getY();
        playerComponent.getEntity().setPosition(formerX, APP_HEIGHT - SPRITE_SIZE);
        playerMoveComponent.moveRight();
        assertEquals(formerX, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());

        // Simple case: try to move up/down, should not move

        // Place at the center to be far from bounds and move up
        formerX = (double) APP_WIDTH / 2;
        formerY = (double) APP_HEIGHT / 2;
        playerComponent.getEntity().setPosition(formerX, formerY);
        playerMoveComponent.moveUp();
        assertEquals(formerX, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());

        // Place at the center to be far from bounds and move down
        playerComponent.getEntity().setPosition(formerX, formerY);
        playerMoveComponent.moveDown();
        assertEquals(formerX, playerComponent.getEntity().getX());
        assertEquals(formerY, playerComponent.getEntity().getY());
    }
}
