package org.enstabretagne.invaders;

import javafx.scene.input.KeyCode;

import java.io.Serializable;

public class RequestMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public final KeyCode[] keys;

    public RequestMessage(javafx.scene.input.KeyCode[] keys) {
        this.keys = keys;
    }

}
