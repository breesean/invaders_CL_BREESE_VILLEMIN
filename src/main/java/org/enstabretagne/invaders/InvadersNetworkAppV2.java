package org.enstabretagne.invaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.multiplayer.ReplicationEvent;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.enstabretagne.invaders.components.*;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;
import static org.enstabretagne.invaders.Constants.*;
import static org.enstabretagne.invaders.Constants.SPRITE_SIZE;


public class InvadersNetworkAppV2 extends GameApplication {

    private final Logger log = Logger.get(GameApplication.class);

    private boolean isServer = false;

    private Server<Bundle> server;
    private Connection<Bundle> connection;

    private MoveComponent moveComponentBottom;

    private MoveComponent moveComponentTop;

    private BlasterComponent blasterComponentBottom;

    private BlasterComponent blasterComponentTop;

    private Input clientInput;
    private EventBus clientBus;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initInput() {

        //Player at the bottom
        onKey(KeyCode.Q, () -> moveComponentBottom.moveLeft());
        onKey(KeyCode.D, () -> moveComponentBottom.moveRight());
        onKey(KeyCode.SPACE, () -> blasterComponentBottom.blast());


        clientInput = new Input();

        onKeyBuilder(clientInput, KeyCode.LEFT)
                .onAction(() -> moveComponentTop.moveLeft());
        onKeyBuilder(clientInput, KeyCode.RIGHT)
                .onAction(() -> moveComponentTop.moveRight());
        onKeyBuilder(clientInput, KeyCode.ENTER)
                .onAction(() -> blasterComponentTop.blast());

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("alienLeft", -1);
        vars.put("endGame", false);
        vars.put("timerBegin", getGameTimer().getNow());
    }

    @Override
    protected void initGame() {
        clientBus = new EventBus();

        getGameWorld().addEntityFactory(new InvadersFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        runOnce(() -> {

            getDialogService().showConfirmationBox("Êtes vous le serveur ?", answer -> {
                isServer = answer;

                if (isServer) {
                    // TODO: have only server init and only client init code to override

                    initPhysics();

                    server = getNetService().newTCPServer(2000);
                    server.setOnConnected(conn -> {
                        connection = conn;

                        getExecutor().startAsyncFX(() -> {
                            SpawnData dataBottom = new SpawnData(
                                    100,
                                    100);

                            Entity playerBottom = spawn("Player", dataBottom);
                            moveComponentBottom = playerBottom.getComponent(MoveComponent.class);
                            blasterComponentBottom = playerBottom.getComponent(BlasterComponent.class);
                            getMPService().spawn(conn, playerBottom,"Player");

                            SpawnData dataTop = new SpawnData(
                                    0,
                                    0);
                            Entity playerTop = spawn("Player", dataTop);
                            moveComponentTop = playerTop.getComponent(MoveComponent.class);
                            blasterComponentTop = playerTop.getComponent(BlasterComponent.class);
                            getMPService().spawn(conn, playerTop, "Player");

                            getMPService().addInputReplicationReceiver(conn, clientInput);
                            getMPService().addPropertyReplicationSender(conn, getWorldProperties());

                            getMPService().addEventReplicationSender(conn, clientBus);
                        });
                    });

                    server.startAsync();

                } else {

                    var client = getNetService().newTCPClient("localhost", 2000);
                    client.setOnConnected(conn -> {
                        getMPService().addEntityReplicationReceiver(conn, getGameWorld());
                        getMPService().addInputReplicationSender(conn, getInput());
                        getMPService().addPropertyReplicationReceiver(conn, getWorldProperties());

                        clientBus.addEventHandler(CustomReplicationEvent.CUSTOM_EVENT, event -> {
                            getNotificationService().pushNotification(event.data + ": " + event.value);
                        });

                        getMPService().addEventReplicationReceiver(conn, clientBus);
                    });

                    client.connectAsync();

                    getInput().setProcessInput(false);
                }

                doInit();
            });

        }, Duration.seconds(0.5));
    }

    private MultiplayerService getMPService() {
        return getService(MultiplayerService.class);
    }

    private void doInit() {
        run(() -> {
            if (isServer && connection != null) {
                //var point = FXGLMath.randomPoint(new Rectangle2D(600, 0, 100, 500));

                //var e = spawn("enemy", point);

                //getMPService().spawn(connection, e, "enemy");
                System.out.println("Serveur : Initialisation faîte avec succès");
            }
        }, Duration.seconds(1));
    }

    @Override
    protected void initUI() {
        Text textScore1 = getUIFactoryService().newText("", Color.WHITE, 22);

        textScore1.setTranslateX(APP_WIDTH - 30);
        textScore1.setTranslateY(20);

        // This UI element is bound to the global variable and updated automatically
        textScore1.textProperty().bind(getWorldProperties().intProperty("alienLeft").asString());

        getGameScene().addUINodes(textScore1);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ALIEN, EntityType.BLAST) {
            @Override
            protected void onCollisionBegin(Entity alien, Entity blast) {
                blast.removeFromWorld();
                alien.getComponent(AlienComponent.class).getBlock().removeAlien(alien);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BLAST, EntityType.PLAYER) {
            @Override
            protected void onCollisionBegin(Entity blast, Entity player) {
                blast.removeFromWorld();
                player.getComponent(PlayerComponent.class).die(player);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ALIEN, EntityType.PLAYER) {
            @Override
            protected void onCollisionBegin(Entity alien, Entity player) {
                player.getComponent(PlayerComponent.class).die(player);
            }
        });
    }


    @Override
    protected void onUpdate(double tpf) {
        if (clientInput != null) {
            clientInput.update(tpf);
        }
        if (getWorldProperties().getInt("alienLeft") == 0) {
            getWorldProperties().setValue("endGame", true);
        }

        if (getWorldProperties().getBoolean("endGame")) {
            endGame();
        }


    //        if (isServer && connection != null) {
    //            var bundle = new Bundle("pos");
    //            bundle.put("x", player1.getX());
    //            bundle.put("y", player1.getY());
    //
    //            server.broadcast(bundle);
    //        }
    }

    private void endGame() {
        double timerBegin = getWorldProperties().getDouble("timerBegin");
        double timerEnd = getGameTimer().getNow();
        double gameDuration = Math.round((timerEnd - timerBegin) * 100.0) / 100.0;
        String logLabel = "Game ended - ";

        int alienLeft = getWorldProperties().getInt("alienLeft");

        if (alienLeft > 0) {
            play("game_over_sound.wav");
            logLabel = logLabel.concat("defeat - " + gameDuration + "s - " + alienLeft + " alien(s) left.");
            getDialogService().showMessageBox("Game Over! " + alienLeft + " alien(s) left.", () -> {
                getDialogService().showConfirmationBox("Play again?", this::resetGame);
            });
        } else {
            play("victory_sound.wav");
            logLabel = logLabel.concat("victory - " + gameDuration +"s.");
            getDialogService().showMessageBox("Victory in " + gameDuration + "s!", () -> {
                getDialogService().showConfirmationBox("Play again?", this::resetGame);
            });
        }

        log.info(logLabel);
    }

    /**
     * Resets the game.
     * @param playAgain
     *          Boolean controlling if the player wants to play again.
     */
    private void resetGame(Boolean playAgain) {
        if (playAgain) {
            log.info("Restarted game.");
            getGameController().startNewGame();
        } else {
            getGameController().gotoMainMenu();
        }
    }

    public static class CustomReplicationEvent extends ReplicationEvent {

        public static final EventType<CustomReplicationEvent> CUSTOM_EVENT = new EventType<>(ReplicationEvent.ANY, "CUSTOM_EVENT");

        public String data;
        public double value;

        public CustomReplicationEvent(String data, double value) {
            super(CUSTOM_EVENT);

            this.data = data;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}