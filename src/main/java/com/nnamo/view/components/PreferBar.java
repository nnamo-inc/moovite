package com.nnamo.view.components;

import com.nnamo.enums.DataType;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomPreferButton;

import javax.swing.*;
import java.awt.*;

public class PreferBar extends JPanel {

    // ATTRIBUTES //
    private CustomPreferButton preferButton;

    // CONSTRUCTOR //
    public PreferBar() {
        super(new GridBagLayout());

        preferButton = new CustomPreferButton();
        add(preferButton, new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));

        setVisible(false);
    }

    // METHODS //
    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    public void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        switch (dataType) {
            case STOP:
                preferButton.setItemName("fermata");
                break;
            case ROUTE:
                preferButton.setItemName("linea");
                break;
        }
        preferButton.setDataType(dataType);
        preferButton.setItemId(itemId);
        preferButton.update(isFav);
    }

    // BEHAVIOUR METHODS //
    public void setGeneralFavBehaviour(FavoriteBehaviour favoriteBehaviour) {
        preferButton.setFavBehaviour(favoriteBehaviour);

    }
}
