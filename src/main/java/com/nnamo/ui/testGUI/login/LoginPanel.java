package testGUI.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LoginPanel extends JPanel {

    private JTextField username;
    private JPasswordField password;
    private JButton login;
    private JLabel errore;

    private ArrayList<LoginListener> listeners;

    public LoginPanel()
    {

        listeners = new ArrayList<>();

        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setSize(300,300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        username = new JTextField(20);
        username.setMaximumSize(new Dimension(150, 30));
        username.setAlignmentX(Component.CENTER_ALIGNMENT);

        password = new JPasswordField(20);
        password.setMaximumSize(new Dimension(150, 30));
        password.setAlignmentX(Component.CENTER_ALIGNMENT);

        login = new JButton("Login");
        login.setMaximumSize(new Dimension(100, 30));
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!username.getText().isEmpty() && password.getPassword().length > 0) {
                    LoginEvent loginEvent = new LoginEvent(this, username.getText(), password.getPassword());
                    for (LoginListener listener : listeners) {
                        listener.tryLogin(loginEvent);
                    }
                } else {
                    errore.setText("Compila tutti i campi");
                }
            }
        });

        errore = new JLabel("Inserisci le tue credenziali");
        errore.setAlignmentX(Component.CENTER_ALIGNMENT);
        errore.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(username);
        add(Box.createVerticalStrut(20));
        add(password);
        add(Box.createVerticalStrut(20));
        add(login);
        add(Box.createVerticalStrut(20));
        add(errore);
        add(Box.createVerticalGlue());
    }

    public void addLoginListener(LoginListener listener) {
        listeners.add(listener);
    }

    public void removeLoginListener(LoginListener listener) {
        listeners.remove(listener);
    }

    public void resetFields() {
        username.setText("");
        password.setText("");
    }

    public void setError(String error) {
        errore.setText(error);
    }

}
