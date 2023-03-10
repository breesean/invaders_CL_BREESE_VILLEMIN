package org.enstabretagne.invaders;

import com.almasb.fxgl.entity.SpawnData;
import org.enstabretagne.invaders.components.BlasterComponent;
import org.enstabretagne.invaders.components.Directions;
import static org.enstabretagne.invaders.Constants.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static org.enstabretagne.invaders.Constants.SPRITE_SIZE;

/**
 * Stratégie de tir simple, comme implémentée auparavant
 */
public class ComportementTirSimple implements IComportement {
    /**
     * Méthode de tir classique, l'alien tire si sa période de cooldown est écoulée
     * @param BL blaster de l'alien considéré
     */
    @Override
    public void execute(BlasterComponent BL) {
        if (getGameTimer().getNow() - BL.lastShot >= BLAST_COOLDOWN) {
            SpawnData data;
            if (BL.facing == Directions.UP) {
                // +- SPRITE_SIZE / 2 to get it above/below, +-1 to prevent hitboxes from touching
                data = new SpawnData(BL.getEntity().getX(), BL.getEntity().getY() - SPRITE_SIZE / 2 - 1);
            } else if (BL.facing == Directions.DOWN) {
                data = new SpawnData(BL.getEntity().getRightX(), BL.getEntity().getBottomY() + SPRITE_SIZE / 2 + 1);
            } else {
                throw new RuntimeException("Invalid facing direction");
            }
            data.put("direction", BL.facing);
            data.put("sprite", BL.sprite);
            data.put("speed", BL.BLAST_SPEED);

            getGameWorld().spawn("Blast", data);
            BL.lastShot = getGameTimer().getNow();

            BL.playSound();
        }
    }
}
