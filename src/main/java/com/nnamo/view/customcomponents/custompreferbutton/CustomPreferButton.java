package com.nnamo.view.customcomponents.custompreferbutton;

import com.nnamo.interfaces.FavoriteBehaviour;

import javax.swing.*;
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
                    setText("Rimuovi " + itemName + " dai preferiti");
                } else {
                    favoriteBehaviour.removeFavorite(itemId);
                    setText("Aggiungi " + itemName + " ai preferiti");
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
            setText("Rimuovi " + itemName + " dai preferiti");
        }
        else {
            setText("Aggiungi " + itemName + " ai preferiti");
        }
    }
}
