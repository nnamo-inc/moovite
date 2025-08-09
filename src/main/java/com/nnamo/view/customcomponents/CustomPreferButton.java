package com.nnamo.view.customcomponents;

import com.nnamo.enums.ButtonMode;
import com.nnamo.enums.ResetType;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
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
//                setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
                setEnabled(false);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        favoriteBehaviour.addFavorite(itemId);
                        reset();
                    }
                });

            }
            case REMOVE -> {

                this.mode = ButtonMode.REMOVE;
//                setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
                setEnabled(false);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        favoriteBehaviour.removeFavorite(itemId);
                        reset();
                    }
                });

            }
            case BOTH -> {

                this.mode = ButtonMode.BOTH;
                setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
                setBackground(CustomColor.GREEN);

                addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (favorite) {
                            favoriteBehaviour.removeFavorite(itemId);
                            // Aggiorna UI solo se l'operazione ha successo
                            favorite = false;
                            setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                            setBackground(CustomColor.GREEN);
                        } else {
                            favoriteBehaviour.addFavorite(itemId);
                            // Aggiorna UI solo se l'operazione ha successo
                            favorite = true;
                            setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                            setBackground(CustomColor.RED);
                        }
                    }
                });
            }
        }

    }

    // METHODS //
    public void update(boolean isFavorite) {
        switch (mode) {

            case ADD -> {
                if (isFavorite) {
                    setText(itemName + "<html><p>già nei</p><p>" + itemName + "</p><p>tuoi Preferiti</p></html>");
                    setEnabled(false);
                } else {
                    setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                    setEnabled(true);
                    setBackground(CustomColor.GREEN);
                }
            }

            case REMOVE -> {
                setEnabled(true);
                setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                setBackground(CustomColor.RED);
            }

            case BOTH -> {
                setEnabled(true);
                this.favorite = isFavorite; // Sincronizza lo stato interno
                if (isFavorite) {
                    setText("<html><p>Rimuovi</p><p>" + itemName + "</p><p>dai Preferiti</p></html>");
                    setBackground(CustomColor.RED);
                } else {
                    setText("<html><p>Aggiungi</p><p>" + itemName + "</p><p>ai Preferiti</p></html>");
                    setBackground(CustomColor.GREEN);
                }
            }

        }
    }

    public void reset() {
        this.favorite = false;
        setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
        setEnabled(false);
    }

    public void reset(ResetType resetType) {
        switch (resetType) {
            case GENERIC -> {
                this.favorite = false;
                setText("<html></p><p>Clicca su una riga</p><p>della tabella per</p><p>attivare il bottone</p></html>");
                setEnabled(false);
            }
            case STOP -> {
                this.favorite = false;
                setText("<html></p><p>Clicca su una fermata</p><p>qualunque per</p><p>attivare il bottone</p></html>");
                setEnabled(false);
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

    public String getItemId() {
        return itemId;
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
