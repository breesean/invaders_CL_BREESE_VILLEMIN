package org.enstabretagne.invaders;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


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
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.multiplayer.ReplicationEvent;
import com.almasb.fxgl.net.Connection;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

import org.enstabretagne.invaders.components.*;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static org.enstabretagne.invaders.Constants.*;

/**
 * The launcher of the game.
 * @author Antoine Breesé
 * @author Henri Lardy
 */
public class InvadersNetworkApp extends GameApplication {

    private boolean isServer = false;

    private Server<Bundle> server;
    private Connection<Bundle> connection;

    private Input clientInput;
    private EventBus clientBus;

    private final Logger log = Logger.get(GameApplication.class);

    /**
     * Initialises the game settings.
     * @param settings
     *               Is dependency-injected.
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.setTitle("Invaders");
        settings.setVersion("1.0");

        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CHANGE BEFORE SUBMISSION
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // settings.setApplicationMode(ApplicationMode.DEVELOPER);
        settings.setApplicationMode(ApplicationMode.RELEASE);
        settings.setDeveloperMenuEnabled(true);  // auto-off when above is RELEASE
        // settings.setProfilingEnabled(true);
    }

    private MoveComponent moveComponentBottom; //player1
    private MoveComponent moveComponentTop; //player 2
    private BlasterComponent blasterComponentBottom;
    private BlasterComponent blasterComponentTop;
    private AlienBlock alienBlockDown;
    private AlienBlock alienBlockUp;

    /**
     * Initialises the game's input. These are used in-game and not in menus.
     */
    @Override
    protected void initInput() {
        // initInput called before initGame, so we can't use playerTop.getComponent... It would've been a nice factorisation
        // And we have to use lambdas instead of method references, because they do not yet exist... raaah

        // Player at the bottom
        onKey(KeyCode.Q, () -> moveComponentBottom.moveLeft());
        onKey(KeyCode.D, () -> moveComponentBottom.moveRight());
        // dev-mode only (useful to test hitboxes, win/lose conditions etc.)
        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
            onKey(KeyCode.Z, () -> moveComponentBottom.moveUp());
            onKey(KeyCode.S, () -> moveComponentBottom.moveDown());
        }
        onKey(KeyCode.SPACE, () -> blasterComponentBottom.blast());

        //Handling player2 inputs (top)
        clientInput = new Input();

        onKeyBuilder(clientInput, LEFT)
                .onAction(() -> moveComponentTop.moveLeft());
        onKeyBuilder(clientInput, RIGHT)
                .onAction(() -> moveComponentTop.moveRight());
        onKeyBuilder(clientInput, ENTER)
                .onAction(() -> blasterComponentTop.blast());

    }

    /**
     * Initialises the game's entities. Is called before each game starts.
     */
    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new InvadersFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        //Gestion du serveur

        runOnce(() -> {

            getDialogService().showConfirmationBox("Êtes vous le serveur ?", answer -> {
                isServer = answer;

                if (isServer) {
                    // TODO: have only server init and only client init code to override

                    server = getNetService().newTCPServer(2000);
                    server.setOnConnected(conn -> {
                        connection = conn;

                        getExecutor().startAsyncFX(() -> {

                            //Spawn player1
                            SpawnData dataBottom = new SpawnData(
                                    (double) (APP_WIDTH - SPRITE_SIZE) / 2,
                                    APP_HEIGHT - SPRITE_SIZE);
                            dataBottom.put("facing", Directions.UP);
                            Entity playerBottom = spawn("Player", dataBottom);
                            moveComponentBottom = playerBottom.getComponent(MoveComponent.class);
                            blasterComponentBottom = playerBottom.getComponent(BlasterComponent.class);

                            //Spawn player 2
                            SpawnData dataTop = new SpawnData(
                                    (double) (APP_WIDTH - SPRITE_SIZE) / 2,
                                    0);
                            dataTop.put("facing", Directions.DOWN);
                            Entity playerTop = spawn("Player", dataTop);
                            moveComponentTop = playerTop.getComponent(MoveComponent.class);
                            blasterComponentTop = playerTop.getComponent(BlasterComponent.class);

                            //Spawn des aliens

                            alienBlockDown = new AlienBlock(4, 10, Directions.DOWN);
                            alienBlockUp = new AlienBlock(4, 10, Directions.UP);

                            // TODO: Make duration variable ? or at least a global constant
                            // TimerAction timerAction = getGameTimer().runAtInterval(alienBlockDown::move, Duration.seconds(0.5));
                            // TimerAction: timerAction.pause(); OR timerAction.resume(); OR timerAction.expire();
                            getGameTimer().runAtInterval(() -> {
                                alienBlockDown.move();
                                alienBlockDown.maybeBlast();
                                alienBlockUp.move();
                                alienBlockUp.maybeBlast();
                            }, Duration.seconds(0.5));

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

                        clientBus.addEventHandler(NetworkTestApp.CustomReplicationEvent.CUSTOM_EVENT, event -> {
                            getNotificationService().pushNotification(event.data + ": " + event.value);
                        });

                        getMPService().addEventReplicationReceiver(conn, clientBus);
                    });

                    client.connectAsync();

                    getInput().setProcessInput(false);
                }

            });

        }, Duration.seconds(0.5));

    }

    /**
     * Initialises global variables in the game. These variables can be tied to UI components to automatically update them.
     * @param vars
     *           Is dependency-injected.
     */
    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("alienLeft", -1);
        vars.put("endGame", false);
        vars.put("timerBegin", getGameTimer().getNow());
    }

    /**
     * Initialises the game's UI. Is called before each game starts.
     */
    @Override
    protected void initUI() {
        Text textScore1 = getUIFactoryService().newText("", Color.WHITE, 22);

        textScore1.setTranslateX(APP_WIDTH - 30);
        textScore1.setTranslateY(20);

        // This UI element is bound to the global variable and updated automatically
        textScore1.textProperty().bind(getWorldProperties().intProperty("alienLeft").asString());

        getGameScene().addUINodes(textScore1);
    }

    private MultiplayerService getMPService() {
        return getService(MultiplayerService.class);
    }

    /**
     * Initialises the game's physics. Registers collision handlers in our case.
     */
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

    /**
     * Updates the game state. Is called every frame.
     * @param tpf
     *          Time per frame. Is dependency-injected.
     */
    @Override
    protected void onUpdate(double tpf) {
        if (getWorldProperties().getInt("alienLeft") == 0) {
            getWorldProperties().setValue("endGame", true);
        }

        if (getWorldProperties().getBoolean("endGame")) {
            endGame();
        }
    }

    /**
     *  Called at the end of a game. Is used to log results and ask the player if they want to play again.
     */
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

    public static class CustomReplicationEvent extends ReplicationEvent {

        public static final EventType<NetworkTestApp.CustomReplicationEvent> CUSTOM_EVENT = new EventType<>(ReplicationEvent.ANY, "CUSTOM_EVENT");

        public String data;
        public double value;

        public CustomReplicationEvent(String data, double value) {
            super(CUSTOM_EVENT);

            this.data = data;
            this.value = value;
        }
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

    /**
     * Entry-point of the game.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
