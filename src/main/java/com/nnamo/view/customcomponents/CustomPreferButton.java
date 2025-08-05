package com.nnamo.view.customcomponents;

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

    public CustomPreferButton(String itemName) {
        super();
        this.itemName = itemName;

        // Inizializza il testo e il colore di default
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
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setFavBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteBehaviour = behaviour;
        }
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
