package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.PanelModeButtonClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelModeButton extends JButton {

    boolean isOpen = false;
    JPanel panel;
    PanelModeButtonClickListener panelModeButtonClickListener;

    public PanelModeButton(String nome, JPanel panel) {
        super(nome);
        this.panel = panel;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelModeButtonClickListener.onPanelModeButtonClick(panel);
            }
        });
    }

    public void setPanelModeButtonClickListener(PanelModeButtonClickListener panelModeButtonClickListener) {
        System.out.println("Setting PanelModeButtonClickListener for button: " + getText());
        this.panelModeButtonClickListener = panelModeButtonClickListener;
    }
}
