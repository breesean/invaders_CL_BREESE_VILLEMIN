package org.enstabretagne.invaders;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.KeepInBoundsComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;

import javafx.geometry.Rectangle2D;
import org.enstabretagne.invaders.components.*;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getSettings;
import static org.enstabretagne.invaders.Constants.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The entity factory of the game. Controls the way entities are spawned and binds components togethers.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class InvadersFactory implements EntityFactory {
    /**
     * Players entity, displayed as cannons.
     * @param data
     *          The data used to create the entity: X, Y, orientation.
     * @return the created entity
     */
    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        Texture texture;

        // Couldn't find way of rotating texture, so we use a different one - not enough time to search more
        Directions facing = data.get("facing");
        if (facing == Directions.UP) {
            texture = FXGL.texture("cannon_up.png");
        } else if (facing == Directions.DOWN) {
            texture = FXGL.texture("cannon_down.png");
        } else {
            throw new RuntimeException("Invalid facing direction");
        }

        int validDirections = 0;
        validDirections |= 1 << Directions.LEFT.ordinal();
        validDirections |= 1 << Directions.RIGHT.ordinal();

        // dev-mode only (useful to test hitboxes, win/lose conditions etc.)
        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
            validDirections |= 1 << Directions.UP.ordinal();
            validDirections |= 1 << Directions.DOWN.ordinal();
        }

        // TODO: Come back to remove checks for bounds in movement? (is done by keepInBoundsComponent)
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(EntityType.PLAYER)
                .view(texture)
                .bbox(new HitBox(new Point2D(CANNON_X, CANNON_Y), BoundingShape.box(CANNON_W, CANNON_H)))
                .with(new PlayerComponent(facing))
                .with(new MoveComponent(PLAYER_SPEED, PLAYER_SPEED, validDirections))
                .with(new BlasterComponent(facing, PLAYER_BLAST_SPEED, "tir.png", "blast_sound.wav"))
                .with(new KeepInBoundsComponent(new Rectangle2D(0, 0, APP_WIDTH, APP_HEIGHT)))
                .collidable()
                .build();
    }

    /**
     * Enemy entity, displayed as a aliens.
     * @param data
     *          The data used to create the entity: X, Y, orientation, parent block.
     * @return the created entity
     */
    @Spawns("Alien")
    public Entity newAlien(SpawnData data) {
        int id = data.get("id");
        AlienBlock block = data.get("block");

        Directions facing = data.get("facing");
        if (facing != Directions.UP && facing != Directions.DOWN) {
            throw new RuntimeException("Invalid facing direction");
        }

        int action = ThreadLocalRandom.current().nextInt(0, 100 + 1);
        if (action<20){ //20% de chance d'avoir un tir double
            new IComportement execute;
        }
        if (action>=20 && action<50){ //30% de chance de tir simple
            action=1;
        }
        if (action>=50){ //50% de chance d'infanterie simple
            action=2;
        }

        int validDirections = 0;
        validDirections |= 1 << Directions.LEFT.ordinal();
        validDirections |= 1 << Directions.RIGHT.ordinal();
        validDirections |= 1 << Directions.UP.ordinal();
        validDirections |= 1 << Directions.DOWN.ordinal();

        return FXGL.entityBuilder(data)
                .type(EntityType.ALIEN)
                .at(data.getX(), data.getY())
                .bbox(new HitBox(new Point2D(INVADER_X, INVADER_Y), BoundingShape.box(INVADER_W, INVADER_H)))
                .with(new AlienComponent(id, block, Directions.DOWN, strategie))
                .with(new MoveComponent(ALIEN_HORIZONTAL_SPEED, ALIEN_VERTICAL_SPEED, validDirections))
                .with(new BlasterComponent(facing, ALIEN_BLAST_SPEED, "zap.png", "blast_sound.wav"))
                .with(new KeepInBoundsComponent(new Rectangle2D(0, 0, APP_WIDTH, APP_HEIGHT)))
                .collidable()
                .build();
    }

    /**
     * Projectile entity.
     * @param data
     *          The data used to create the entity: X, Y, orientation, speed, sprite..
     * @return the created entity
     */
    @Spawns("Blast")
    public Entity newBlast(SpawnData data) {
        var texture = FXGL.texture((String) data.get("sprite"));
        int direction = data.get("direction") == Directions.UP ? -1 : 1;
        double speed = data.get("speed");

        return FXGL.entityBuilder(data)
                .type(EntityType.BLAST)
                .at(data.getX(), data.getY())
                .bbox(new HitBox(new Point2D(BLAST_X, BLAST_Y), BoundingShape.box(BLAST_W, BLAST_H)))
                .view(texture)
                .with(new ProjectileComponent(new Point2D(0, direction), speed))
                .collidable()
                .with(new OffscreenCleanComponent())
                .build();
    }
}
