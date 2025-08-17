package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.ButtonPanelBehaviour;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomButtonPanel extends JButton {

    // ATTRIBUTES //
    JPanel panel;
    ButtonPanelBehaviour buttonPanelBehaviour;

    // CONSTRUCTOR //
    public CustomButtonPanel(String nome, JPanel panel) {
        super(nome);
        this.panel = panel;
        initListener();
    }

    // METHODS BEHAVIOUR //
    private void initListener() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPanelBehaviour.onButtonPanelClick(panel);

            }
        });
    }

    public void setButtonPanelBehaviour(ButtonPanelBehaviour buttonPanelBehaviour) {
        this.buttonPanelBehaviour = buttonPanelBehaviour;
    }
}
