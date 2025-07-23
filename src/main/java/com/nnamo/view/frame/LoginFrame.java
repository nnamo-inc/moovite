package com.nnamo.view.frame;

import javax.swing.*;

import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.InfoBar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private InfoBar username = new InfoBar("Username:", "Inserisci il tuo username");
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton login = new JButton("Login");
    private JButton register = new JButton("Registrati");
    private JLabel errore;

    private LoginBehaviour loginBehavior;
    private RegisterBehaviour registerBehavior;

    public LoginFrame() {
        super();
        setLayout(new BorderLayout());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        add(centerPanel, BorderLayout.CENTER);

        username.getText().setEditable(true);
        centerPanel.add(username, new GbcCustom().setPosition(0, 0).setWeight(1.0, 1.0).setAnchor(GridBagConstraints.CENTER)
                .setInsets(2, 5, 2, 5));



        /*username.getText().setEditable(true);
        add(username, new GbcCustom().setAnchor(GridBagConstraints.WEST).setPosition(0, 0).setWeight(0.5, 0).setWidth(1).setHeight(1));

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
*/
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
