package com.nnamo.view.components;

import com.nnamo.interfaces.LeftPanelGenericButtonBehaviour;
import com.nnamo.interfaces.LeftPanelPreferButtonBehaviour;
import com.nnamo.view.customcomponents.LeftPanelButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ButtonPanel extends JPanel {

    private final ArrayList <LeftPanelButton> buttons = new ArrayList<>();

    public ButtonPanel(HashMap<JPanel, Icon> panels) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (JPanel panel : panels.keySet()) {
            LeftPanelButton button = new LeftPanelButton("", panel);
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

    public void setGenericButtonBehaviour(LeftPanelGenericButtonBehaviour listener) {
        for (LeftPanelButton button : buttons) {
            button.setGenericButtonClickListener(listener);
        }
    }

    public void setPreferButtonBehaviour(LeftPanelPreferButtonBehaviour listener) {
        for (LeftPanelButton button : buttons) {
            if (button.getPanel() instanceof PreferPanel) {
                button.setPreferButtonClickListener(listener);
            }
        }
    }
}
