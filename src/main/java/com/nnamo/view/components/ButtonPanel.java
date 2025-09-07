package com.nnamo.view.components;

import com.nnamo.interfaces.ButtonPanelBehaviour;
import com.nnamo.view.customcomponents.CustomButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Custom {@link JPanel} that contains a list of {@link JButton}., each associated with a specific {@link JPanel} and an {@link Icon}.
 * It allows setting a behavior for the buttons through the {@link ButtonPanelBehaviour} interface.
 *
 * @author Riccardo Finocchiaro
 * @see JPanel
 * @see JButton
 * @see Icon
 * @see CustomButtonPanel
 * @see ButtonPanelBehaviour
 */
public class ButtonPanel extends JPanel {

    // ATTRIBUTES //
    private final ArrayList<CustomButtonPanel> buttons = new ArrayList<>();

    // CONSTRUCTOR //

    /**
     * Creates a {@link ButtonPanel} with a list of {@link JPanel} and their associated {@link Icon}. For every {@link JPanel} in the {@link HashMap}, a {@link JButton} is created with the specified {@link Icon}.
     *
     * @param panels a HashMap where the key is a {@link JPanel} and the value is an {@link Icon} to be displayed on the {@link JButton}.
     * @see JPanel
     * @see JButton
     * @see Icon
     * @see CustomButtonPanel
     * @see ButtonPanelBehaviour
     * @see HashMap
     */
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

    // BEHAVIOUR METHODS //

    /**
     * Sets the behavior to execute when any {@link JButton} in the {@link ButtonPanel} is clicked.
     *
     * @param listener the implementation of {@link ButtonPanelBehaviour} that defines the behavior for {@link JButton} clicks.
     * @see CustomButtonPanel
     * @see ButtonPanelBehaviour
     * @see JButton
     */
    public void setButtonPanelBehaviour(ButtonPanelBehaviour listener) {
        for (CustomButtonPanel button : buttons) {
            button.setButtonPanelBehaviour(listener);
        }
    }
}
