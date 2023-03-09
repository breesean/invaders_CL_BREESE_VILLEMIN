package org.enstabretagne.invaders;

import com.almasb.fxgl.entity.SpawnData;
import org.enstabretagne.invaders.components.BlasterComponent;
import org.enstabretagne.invaders.components.Directions;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;
import static org.enstabretagne.invaders.Constants.BLAST_COOLDOWN;
import static org.enstabretagne.invaders.Constants.SPRITE_SIZE;

public class ComportementTirDouble implements IComportement {

    @Override
    public void execute(BlasterComponent BL) {
        BL.sprite= "Blue_small.png";
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
            data.put("speed", BL.BLAST_SPEED*2);
            getGameWorld().spawn("Blast", data);
            BL.lastShot = getGameTimer().getNow();

            BL.playSound();

            SpawnData data2;
            if (BL.facing == Directions.UP) {
                // +- SPRITE_SIZE / 2 to get it above/below, +-1 to prevent hitboxes from touching
                data2 = new SpawnData(BL.getEntity().getX(), BL.getEntity().getY() - SPRITE_SIZE / 2 - 1);
            } else if (BL.facing == Directions.DOWN) {
                data2 = new SpawnData(BL.getEntity().getRightX(), BL.getEntity().getBottomY() + SPRITE_SIZE / 2 + 1);
            } else {
                throw new RuntimeException("Invalid facing direction");
            }
            data2.put("direction", BL.facing);
            data2.put("sprite", BL.sprite);
            data2.put("speed", BL.BLAST_SPEED*0.5);
            System.out.println(BL.facing);
            getGameWorld().spawn("Blast", data2);
            BL.lastShot = getGameTimer().getNow();

            BL.playSound();
        }
    }
}
