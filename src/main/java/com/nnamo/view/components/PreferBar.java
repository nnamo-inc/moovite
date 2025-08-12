package com.nnamo.view.components;

import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomPreferButton;

import javax.swing.*;
import java.awt.*;

public class PreferBar extends JPanel {

    private CustomPreferButton preferButton = new CustomPreferButton("fermata");

    public PreferBar() {
        super(new GridBagLayout());

        add(preferButton, new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));

        setVisible(true);
    }

    public CustomPreferButton getPreferButton() {
        return preferButton;
    }

}
