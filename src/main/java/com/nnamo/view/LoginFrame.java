package com.nnamo.view;

import javax.swing.*;

import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.view.components.GbcCustom;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton login = new JButton("Login");
    private JButton register = new JButton("Registrati");
    private JLabel errore;

    private LoginBehaviour loginBehavior;
    private RegisterBehaviour registerBehavior;

    public LoginFrame() {
        super();
        setSize(200, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new GridBagLayout());

        usernameField.setEditable(true);
        add(usernameField, new GbcCustom()
                .setAnchor(GridBagConstraints.WEST)
                .setPosition(0, 0)
                .setWeight(0.5, 0)
                .setWidth(1)
                .setHeight(1));

        passwordField.setEditable(true);
        add(passwordField, new GbcCustom()
                .setAnchor(GridBagConstraints.WEST)
                .setPosition(0, 1)
                .setWeight(0.5, 0)
                .setWidth(1)
                .setHeight(1));

        add(login, new GbcCustom()
                .setAnchor(GridBagConstraints.CENTER)
                .setPosition(0, 2)
                .setWeight(0, 0)
                .setWidth(2)
                .setHeight(1));

        add(register, new GbcCustom()
                .setAnchor(GridBagConstraints.CENTER)
                .setPosition(1, 2)
                .setWeight(0, 0)
                .setWidth(2)
                .setHeight(1));

        handleButtonListeners();
        setVisible(true);
    }

    private void handleButtonListeners() {
        this.login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (loginBehavior != null) {
                    try {
                        loginBehavior.login(username, password);
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        });

        this.register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (registerBehavior != null) {
                    try {
                        registerBehavior.register(username, password);
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        });
    }

    public void setLoginBehaviour(LoginBehaviour behaviour) {
        this.loginBehavior = behaviour;
    }

    public void setRegisterBehaviour(RegisterBehaviour behaviour) {
        this.registerBehavior = behaviour;
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    public void setError(String error) {
        errore.setText(error);
    }

}
