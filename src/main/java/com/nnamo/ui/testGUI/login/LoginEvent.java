package testGUI.login;

import java.util.EventObject;

public class LoginEvent extends EventObject {

    private String username;
    private char[] password;

    public LoginEvent(Object source, String username, char[] password) {
        super(source);
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public char[] getPassword() { return password; }

    public void reset() { username = ""; password = new char[0]; }
}
