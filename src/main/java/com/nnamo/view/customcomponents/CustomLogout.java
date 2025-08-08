package com.nnamo.view.customcomponents;

import javax.swing.*;

import com.nnamo.interfaces.LogoutBehaviour;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomLogout extends JPanel {

    JButton button = new JButton("Logout");
    LogoutBehaviour logoutBehaviour;

    // CONSTRUCTOR //
    public CustomLogout() {
        super();
        setLayout(new GridBagLayout());
        add(button, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));

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
