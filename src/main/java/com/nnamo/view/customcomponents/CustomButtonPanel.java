package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.ButtonPanelBehaviour;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom {@link JPanel} that triggers a specific {@link ButtonPanelBehaviour} when clicked.
 * It is designed to work with a {@link JPanel} and a {@link ButtonPanelBehaviour} interface.
 *
 * @author Riccardo Finocchiaro
 * @see JButton
 * @see ButtonPanelBehaviour
 */
public class CustomButtonPanel extends JButton {

    private final JPanel PANEL;
    private ButtonPanelBehaviour buttonPanelBehaviour;

    /**
     * Creates a {@link CustomButtonPanel} with a specified name and a {@link JPanel} to interact with.
     *
     * @param nome  the button label that will be displayed on the button
     * @param panel the JPanel the button will interact with
     * @see JButton
     * @see JPanel
     */
    public CustomButtonPanel(String nome, JPanel panel) {
        super(nome);
        this.PANEL = panel;
        initListener();
    }

    private void initListener() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonPanelBehaviour != null) {
                    buttonPanelBehaviour.onButtonPanelClick(PANEL);
                }
            }
        });
    }

    /**
     * Sets the behavior to execute when the button is clicked.
     *
     * @param buttonPanelBehaviour the behavior implementation
     * @see ButtonPanelBehaviour
     */
    public void setButtonPanelBehaviour(ButtonPanelBehaviour buttonPanelBehaviour) {
        this.buttonPanelBehaviour = buttonPanelBehaviour;
    }
}