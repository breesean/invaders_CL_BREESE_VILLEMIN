package org.enstabretagne.invaders;

import org.enstabretagne.invaders.components.BlasterComponent;

/**
 * Une API simple, qui représente la stratégie (générique) pour la création d'un alien
 *
 */
public interface IComportement {
    /**
     * méthode de tir vide, puisque l'alien ne tire pas
     * @param BL blaster de l'alien considéré
     */
    public void execute(BlasterComponent BL);
}
