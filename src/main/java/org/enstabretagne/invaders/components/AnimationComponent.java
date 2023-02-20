package org.enstabretagne.invaders.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

/**
 * Component used to animate sprites, eg. for aliens.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class AnimationComponent extends Component {
    final AnimatedTexture texture;

    /**
     * @param spriteName
     *              The name of the sprite to use.
     * @param framesPerRow
     *              The number of textures per row.
     * @param frameWidth
     *              The width of a single texture.
     * @param frameHeight
     *              The height of a single texture.
     * @param duration
     *              The total duration of the animation.
     * @param startFrame
     *              The position of the first frame of the animation in the row.
     * @param endFrame
     *              The position of the last frame of the animation in the row.
     */
    public AnimationComponent(String spriteName, int framesPerRow, int frameWidth, int frameHeight, Duration duration, int startFrame, int endFrame) {
        AnimationChannel anim = new AnimationChannel(FXGL.image(spriteName), framesPerRow, frameWidth, frameHeight, duration, startFrame, endFrame);

        this.texture = new AnimatedTexture(anim);
        this.texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(this.texture);
    }
}
