package com.nnamo.view.frame;

import com.nnamo.enums.AuthResult;
import com.nnamo.enums.RegisterResult;
import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomInfoBar;
import com.nnamo.view.customcomponents.PasswordBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Login window {@link JFrame} for user authentication and registration.
 * Provides fields for username and password, and buttons for login and registration.
 * Handles user input validation and delegates authentication logic to provided behaviours.
 *
 * @see JFrame
 * @see LoginBehaviour
 * @see RegisterBehaviour
 * @see CustomInfoBar
 * @see PasswordBar
 */
public class LoginFrame extends MainFrame {

    private final CustomInfoBar usernameField = new CustomInfoBar("Username:");
    private final PasswordBar passwordField = new PasswordBar();
    private final JButton buttonLogin = new JButton("Login");
    private final JButton buttonRegister = new JButton("Registrati");
    private final JLabel infoLabel = new JLabel("");

    private LoginBehaviour loginBehavior;
    private RegisterBehaviour registerBehavior;

    /**
     * Constructs the login frame, initializing UI components and layout.
     * Sets up listeners for login and registration actions.
     *
     * @see JFrame
     * @see CustomInfoBar
     */
    public LoginFrame() throws IOException {
        super();
        setLayout(new GridBagLayout());
        setSize(400, 250);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        usernameField.getJTextField().setEditable(true);
        add(usernameField, new CustomGbc().setPosition(0, 0).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(10, 10, 5, 10));
        add(passwordField, new CustomGbc().setPosition(0, 1).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        add(buttonLogin, new CustomGbc().setPosition(0, 2).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonRegister);
        add(buttonPanel, new CustomGbc().setPosition(0, 3).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));
        add(infoLabel, new CustomGbc().setPosition(0, 4).setWeight(1.0, 0.0)
                .setAnchor(GridBagConstraints.CENTER).setInsets(5, 10, 5, 10));

        handleButtonListeners();
    }

    /**
     * Makes the login frame visible and resets input fields.
     */
    public void open() {
        resetFields();
        setVisible(true);
    }

    /**
     * Hides the login frame and resets input fields.
     */
    public void close() {
        resetFields();
        setVisible(false);
    }

    private void handleButtonListeners() {
        this.buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getJTextField().getText();
                String password = passwordField.getPasswordField();
                if (loginBehavior != null) {
                    if (username == null || username.isEmpty()) {
                        infoLabel.setText("Username field is empty");
                        return;
                    }

                    if (password == null || password.isEmpty()) {
                        infoLabel.setText("Password field is empty");
                        return;
                    }

                    try {
                        AuthResult loginResult = loginBehavior.login(username, password);
                        if (loginResult == AuthResult.WRONG_CREDENTIALS) {
                            infoLabel.setText("Wrong username or password. Try again!");
                        }
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
                String username = usernameField.getJTextField().getText();
                String password = passwordField.getPasswordField();
                if (registerBehavior != null) {
                    try {
                        if (username == null || username.isEmpty()) {
                            infoLabel.setText("Username field is empty");
                            return;
                        }

                        if (password == null || password.isEmpty()) {
                            infoLabel.setText("Password field is empty");
                            return;
                        }
                        RegisterResult registerResult = registerBehavior.register(username, password);
                        switch (registerResult) {
                            case SUCCESS:
                                infoLabel.setText("User " + username + " created");
                                break;
                            case USER_ALREADY_EXISTS:
                                infoLabel.setText("User " + username + " already exists");
                                break;
                        }
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        });
    }

    /**
     * Sets the login behaviour to handle authentication logic.
     *
     * @param behaviour the {@link LoginBehaviour} implementation
     * @see LoginBehaviour
     */
    public void setLoginBehaviour(LoginBehaviour behaviour) {
        this.loginBehavior = behaviour;
    }

    /**
     * Sets the register behaviour to handle user registration logic.
     *
     * @param behaviour the {@link RegisterBehaviour} implementation
     * @see RegisterBehaviour
     */
    public void setRegisterBehaviour(RegisterBehaviour behaviour) {
        this.registerBehavior = behaviour;
    }

    /**
     * Resets the username, password, and info fields to empty.
     */
    public void resetFields() {
        usernameField.setTextField("");
        passwordField.setText("");
        infoLabel.setText("");
    }
}
