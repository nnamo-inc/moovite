package com.nnamo.view;

import javax.swing.*;
import java.awt.*;

public class GUIPanel extends JPanel {

    StopPanel stopPanel = new StopPanel();
    SearchPanel searchPanel = new SearchPanel();
    GridBagConstraints gbc = new GridBagConstraints();

    // CONSTRUCTOR //
    public GUIPanel() {
        super();
        setLayout(new GridBagLayout());
        gbc.anchor = GridBagConstraints.CENTER;
        add(stopPanel, gbc);
        add(searchPanel, BorderLayout.WEST);
        setVisible(true);
    }

    // GETTERS AND SETTERS //
    public StopPanel getStopPanel() {
        return stopPanel;
    }
}
