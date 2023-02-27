package org.enstabretagne.invaders;

import org.enstabretagne.invaders.components.BlasterComponent;

/**
 * Une API simple, qui représente la stratégie (générique) pour la création d'un alien
 *
 */
public interface IComportement {
    public void execute(BlasterComponent BL);
}
