package com.nnamo.view.customcomponents.custompreferbutton;

import com.nnamo.interfaces.FavoriteBehaviour;

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

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                favorite = !favorite;
                if (favorite) {
                    favoriteBehaviour.addFavorite(itemId);
                    setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");                } else {
                    favoriteBehaviour.removeFavorite(itemId);
                    setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");                }
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
            setText("Rimuovi " + itemName + " dai preferiti");
        }
        else {
            setText("Aggiungi " + itemName + " ai preferiti");
        }
    }
}
