package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.LeftPanelGenericButtonBehaviour;
import com.nnamo.interfaces.LeftPanelPreferButtonBehaviour;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeftPanelButton extends JButton {

    boolean isOpen = false;
    JPanel panel;
    LeftPanelGenericButtonBehaviour leftPanelGenericButtonBehaviour;
    LeftPanelPreferButtonBehaviour leftPanelPreferButtonBehaviour;

    public LeftPanelButton(String nome, JPanel panel) {
        super(nome);
        this.panel = panel;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPanelGenericButtonBehaviour.onPanelModeButtonClick(panel);
                if (leftPanelPreferButtonBehaviour != null) {
                    leftPanelPreferButtonBehaviour.onButtonPanelClick(panel);
                }
            }
        });
    }

    public void setGenericButtonClickListener(LeftPanelGenericButtonBehaviour leftPanelGenericButtonBehaviour) {
        System.out.println("Setting PanelModeButtonClickListener for button: " + getText());
        this.leftPanelGenericButtonBehaviour = leftPanelGenericButtonBehaviour;
    }

    public void setPreferButtonClickListener(LeftPanelPreferButtonBehaviour leftPanelPreferButtonBehaviour) {
        System.out.println("Setting PreferButtonClickListener for button: " + getText());
        this.leftPanelPreferButtonBehaviour = leftPanelPreferButtonBehaviour;

    }

    public JPanel getPanel() {
        return panel;
    }
}
