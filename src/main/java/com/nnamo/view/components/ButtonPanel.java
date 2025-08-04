package com.nnamo.view.components;

import com.nnamo.interfaces.PanelModeButtonClickListener;
import com.nnamo.view.customcomponents.PanelModeButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonPanel extends JPanel {

    private final ArrayList <PanelModeButton> buttons = new ArrayList<>();

    public ButtonPanel(HashMap<JPanel, Icon> panels) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (JPanel panel : panels.keySet()) {
            PanelModeButton button = new PanelModeButton("", panel);
            button.setIcon(panels.get(panel));

            button.setMargin(new Insets(5, 2, 5, 2));
            int width = panels.get(panel).getIconWidth();
            Dimension d = new Dimension(width, width);
            button.setPreferredSize(d);
            button.setMaximumSize(d);
            button.setMinimumSize(d);

            buttons.add(button);
            add(button);
        }
    }

    public void setPanelModeButtonClickListener(PanelModeButtonClickListener listener) {
        for (PanelModeButton button : buttons) {
            button.setPanelModeButtonClickListener(listener);
        }
    }

    public ArrayList<PanelModeButton> getButtons() {
        return buttons;
    }
}
