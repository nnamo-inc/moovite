package com.nnamo.view.customcomponents;

import com.nnamo.enums.DataType;
import com.nnamo.enums.ResetType;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.event.ActionListener;

public class CustomPreferButton extends JButton {

    // ATTRIBUTES //
    boolean favorite = false;
    FavoriteBehaviour favoriteBehaviour;
    String itemId;
    String itemName;
    DataType mode;

    // CONSTRUCTOR //
    public CustomPreferButton() {
        super();
        setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
        setBackground(CustomColor.GREEN);
        initListener();
    }

    // METHODS //
    public void update(boolean isFavorite) {
        setEnabled(true);
        this.favorite = isFavorite;
        if (isFavorite) {
            setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
            setBackground(CustomColor.RED);
        } else {
            setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
            setBackground(CustomColor.GREEN);
        }
    }

    // BEHAVIOUR METHODS //

    private void initListener() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                if (favorite) {
                    favoriteBehaviour.removeFavorite(itemId, mode);
                    favorite = false;
                    setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                    setBackground(CustomColor.GREEN);

                } else {
                    favoriteBehaviour.addFavorite(itemId, mode);
                    favorite = true;
                    setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                    setBackground(CustomColor.RED);
                }

            }
        });

    }

    public void setFavBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteBehaviour = behaviour;
        }
    }

    // GETTERS AND SETTERS //
    public void setDataType(DataType mode) {
        this.mode = mode;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
