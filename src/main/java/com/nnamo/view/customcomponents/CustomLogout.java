package com.nnamo.view.customcomponents;

import javax.swing.*;

import com.nnamo.interfaces.LogoutBehaviour;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomLogout extends JPanel {

    // ATTRIBUTES //
    JButton button;
    LogoutBehaviour logoutBehaviour;

    // CONSTRUCTOR //
    public CustomLogout() {
        super();
        setLayout(new GridBagLayout());

        // Button
        button = new JButton("Logout");
        add(button, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));
        initListener();
    }

    // METHODS BEHAVIOUR //
    private void initListener() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (logoutBehaviour != null) {
                    logoutBehaviour.onLogout();
                }
            }
        });
    }

    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        this.logoutBehaviour = behaviour;
    }
}
