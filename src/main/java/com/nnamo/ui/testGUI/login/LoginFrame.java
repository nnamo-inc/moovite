package testGUI.login;

import testGUI.Mappa.MapFrame;

import javax.swing.*;
import java.util.Arrays;

public class LoginFrame extends JFrame implements LoginListener {


    LoginPanel panel;

    public LoginFrame() {

        super("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        panel = new LoginPanel();

        panel.addLoginListener(this);
        setContentPane(panel);

        setVisible(true);

    }

    public void tryLogin(LoginEvent loginEvent) {
        char[] psw = {'c', 'o', 'r', 'r', 'e', 't', 't', 'o' };
        if (loginEvent.getUsername().equals("accesso") && Arrays.equals(loginEvent.getPassword(), psw)) {
            dispose();
            new MapFrame();
        }
        else {
            panel.setError("Credenziali invalide");
            panel.resetFields();
        }
    }
}
