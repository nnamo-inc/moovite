package com.nnamo.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUIPanel extends JPanel {

    StopPanel stopPanel = new StopPanel();
    SearchPanel searchPanel = new SearchPanel();
    ArrayList<JComponent> components = new ArrayList<>();

    // CONSTRUCTOR //
    public GUIPanel() {
        super();
        setLayout(new BorderLayout());
        add(stopPanel, BorderLayout.PAGE_END);
        add(searchPanel, BorderLayout.WEST);
        setVisible(true);

        components.add(stopPanel);
        components.add(searchPanel);
    }

    // GETTERS AND SETTERS //
    public StopPanel getStopPanel() {
        return stopPanel;
    }
}