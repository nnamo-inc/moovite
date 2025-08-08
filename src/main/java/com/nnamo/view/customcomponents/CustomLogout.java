package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class CustomLogout extends JPanel {

    JButton button = new JButton("Logout");

    // CONSTRUCTOR //
    public CustomLogout() {
        super();
        setLayout(new GridBagLayout());
        add(button, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));
    }
}
