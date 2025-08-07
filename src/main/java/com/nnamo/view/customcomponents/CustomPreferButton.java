package com.nnamo.view.customcomponents;

import com.nnamo.enums.ButtonMode;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomPreferButton extends JButton {

    boolean favorite = false;
    FavoriteBehaviour favoriteBehaviour;
    String itemId;
    String itemName;
    ButtonMode mode;

    // CONSTRUCTOR //
    public CustomPreferButton(String itemName, ButtonMode buttonMode) {
        super();
        this.itemName = itemName;

        switch (buttonMode) {
            case ADD -> {

                this.mode = ButtonMode.ADD;
                setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                setBackground(CustomColor.RED);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                            favoriteBehaviour.addFavorite(itemId);
                        }
                });

            }
            case REMOVE -> {

                this.mode = ButtonMode.REMOVE;
                setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                setBackground(CustomColor.RED);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        favoriteBehaviour.removeFavorite(itemId);
                    }
                });


            }
            case BOTH -> {

                this.mode = ButtonMode.BOTH;
                setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                setBackground(CustomColor.GREEN);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        favorite = !favorite;
                        if (favorite) {
                            favoriteBehaviour.addFavorite(itemId);
                            setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                            setBackground(CustomColor.RED); // Aggiungi il cambio colore qui
                        } else {
                            favoriteBehaviour.removeFavorite(itemId);
                            setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                            setBackground(CustomColor.GREEN); // Aggiungi il cambio colore qui
                        }
                    }
                });
            }
        }

    }

    // BEHAVIOUR LISTENER //
    public void setFavBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteBehaviour = behaviour;
        }
    }

    // GETTERS AND SETTERS //

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        if (favorite) {
            setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
            setBackground(CustomColor.RED);
        }
        else {
            setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
            setBackground(CustomColor.GREEN);
        }
    }
}
