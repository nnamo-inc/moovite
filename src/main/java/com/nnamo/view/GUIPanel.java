package com.nnamo.view;

import javax.swing.*;
import java.awt.*;

public class GUIPanel extends JPanel {

    StopPanel stopPanel = new StopPanel();
    SearchPanel searchPanel = new SearchPanel();

    // CONSTRUCTOR //
    public GUIPanel() {
        super();
        setLayout(new BorderLayout());
        add(stopPanel, BorderLayout.PAGE_END);
        add(searchPanel, BorderLayout.WEST);
        setVisible(true);
    }

    // GETTERS AND SETTERS //
    public StopPanel getStopPanel() {
        return stopPanel;
    }
}