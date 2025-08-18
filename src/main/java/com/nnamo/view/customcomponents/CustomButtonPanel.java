package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.ButtonPanelBehaviour;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Custom JButton that triggers a specific {@link ButtonPanelBehaviour} when clicked.
 * It is designed to work with a JPanel and a ButtonPanelBehaviour interface.
 *
 * @see JButton
 * @see ButtonPanelBehaviour
 */
public class CustomButtonPanel extends JButton {

    private JPanel panel;
    private ButtonPanelBehaviour buttonPanelBehaviour;

    /**
     * Creates a CustomButtonPanel.
     *
     * @param nome  the button label that will be displayed on the button
     * @param panel the JPanel the button will interact with
     */
    public CustomButtonPanel(String nome, JPanel panel) {
        super(nome);
        this.panel = panel;
        initListener();
    }
    /**
     * Handle the button click event by executing the behavior defined in the {@link ButtonPanelBehaviour}.
     *
     * @return void
     *
     * @see ButtonPanelBehaviour
     */
    private void initListener() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (buttonPanelBehaviour != null) {
                    buttonPanelBehaviour.onButtonPanelClick(panel);
                }
            }
        });
    }

    /**
     * Sets the behavior to execute when the button is clicked.
     *
     * @param buttonPanelBehaviour the behavior implementation
     *
     * @see ButtonPanelBehaviour
     */
    public void setButtonPanelBehaviour(ButtonPanelBehaviour buttonPanelBehaviour) {
        this.buttonPanelBehaviour = buttonPanelBehaviour;
    }
}