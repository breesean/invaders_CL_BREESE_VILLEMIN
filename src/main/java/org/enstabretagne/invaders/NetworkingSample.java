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

import static com.almasb.fxgl.dsl.FXGL.*;


   
public class NetworkingSample extends GameApplication {

    private boolean isServer;

    private Server<Bundle> server;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        var cb = new CheckBox();
        cb.selectedProperty().addListener((o, old, isSelected) -> {
            var bundle = new Bundle("CheckBoxData");
            bundle.put("isSelected", isSelected);

            if (isServer)
                server.broadcast(bundle);
        });

        addUINode(cb, 100, 100);

        runOnce(() -> {
            getDialogService().showConfirmationBox("Is Server?", answer -> {
                isServer = answer;

                if (isServer) {
                    server = getNetService().newTCPServer(2000);
                    server.startAsync();
                } else {
                    var client = getNetService().newTCPClient("localhost", 2000);
                    client.setOnConnected(connection -> {
                        connection.addMessageHandlerFX((conn, message) -> {
                            boolean isSelected = message.get("isSelected");

                            cb.setSelected(isSelected);
                        });
                    });
                    client.connectAsync();
                }
            });
        }, Duration.seconds(0.2));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

