package org.enstabretagne.invaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;
import com.almasb.fxgl.core.serialization.Bundle;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private KeyCode keyCode;
    public ServerMessage(KeyCode keyCode) {
        this.keyCode = keyCode;
    }
    public KeyCode getKeyCode() {
        return keyCode;
    }
}