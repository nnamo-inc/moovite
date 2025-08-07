package com.nnamo.view.components;

import com.nnamo.interfaces.ButtonPanelBehaviour;
import com.nnamo.view.customcomponents.CustomButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ButtonPanel extends JPanel {

    private final ArrayList <CustomButtonPanel> buttons = new ArrayList<>();

    public ButtonPanel(HashMap<JPanel, Icon> panels) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (JPanel panel : panels.keySet()) {
            CustomButtonPanel button = new CustomButtonPanel("", panel);
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

    public void setButtonPanelBehaviour(ButtonPanelBehaviour listener) {
        for (CustomButtonPanel button : buttons) {
            button.setButtonPanelBehaviour(listener);
        }
    }
}
