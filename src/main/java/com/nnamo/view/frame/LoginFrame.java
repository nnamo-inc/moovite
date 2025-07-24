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

    private InfoBar fieldUsername = new InfoBar("Username:");
    private PasswordBar fieldPassword = new PasswordBar();
    private JButton buttonLogin = new JButton("Login");
    private JButton buttonRegister = new JButton("Registrati");
    private JLabel errore = new JLabel(" ");

    private LoginBehaviour loginBehavior;
    private RegisterBehaviour registerBehavior;

    public LoginFrame() {
        super();
        setLayout(new GridBagLayout());
        setSize(400, 250);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fieldUsername.getJTextField().setEditable(true);
        add(fieldUsername, new GbcCustom().setPosition(0, 0).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(10, 10, 5, 10));
        add(fieldPassword, new GbcCustom().setPosition(0, 1).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        add(buttonLogin, new GbcCustom().setPosition(0, 2).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        JPanel buttonPanel = new JPanel(); buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonLogin); buttonPanel.add(buttonRegister);
        add(buttonPanel, new GbcCustom().setPosition(0, 3).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        add(errore, new GbcCustom().setPosition(0, 4).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));

        handleButtonListeners();
    }

    public void open() {
        setVisible(true);
    }

    public void close() {
        setVisible(false);
    }

    private void handleButtonListeners() {
        this.buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = fieldUsername.getJTextField().getText();
                String password = new String(fieldPassword.getJPasswordField().getPassword());
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

        this.buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = fieldUsername.getJTextField().getText();
                String password = new String(fieldPassword.getJPasswordField().getPassword());
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
        fieldUsername.getJTextField().setText("");
        fieldPassword.getJPasswordField().setText("");
    }

    public void setError(String error) {
        errore.setText(error);
    }

    private class PasswordBar extends JPanel {
        private JLabel label;
        private JPasswordField password;

        // CONSTRUCTOR //
        public PasswordBar() {
            super();
            label = new JLabel("Password:");
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            password = new JPasswordField(20);
            password.setHorizontalAlignment(JTextField.LEFT);
            password.setEditable(true);
            setLayout(new GridBagLayout());
            add(label, new GbcCustom().setPosition(0, 0).setWeight(0.0, 0.0)
                    .setInsets(2, 5, 2, 5).setAnchor(GridBagConstraints.EAST));
            add(password, new GbcCustom().setPosition(1, 0).setWeight(1.0, 1.0)
                    .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 0, 2, 10).setAnchor(GridBagConstraints.WEST));
        }

        // GETTERS AND SETTERS //
        public JPasswordField getJPasswordField() {
            return password;
        }
    }

}
