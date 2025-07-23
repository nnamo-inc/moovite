package com.nnamo.controllers;

import java.sql.SQLException;

import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.view.LoginFrame;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class UserController {
    DatabaseService db;
    LoginFrame loginFrame;
    Argon2 hasher = Argon2Factory.create();

    public UserController(DatabaseService db) {
        this.db = db;
    }

    public void run() {
        this.loginFrame = new LoginFrame();

        loginFrame.setLoginBehaviour(new LoginBehaviour() {
            @Override
            public void login(String username, String password) throws SQLException {
                System.out.println(String.format("Login: %s %s", username, password));

                UserModel user = db.getUserByName(username);
                if (user != null && hasher.verify(user.getPasswordHash(), password.getBytes())) {
                    System.out.println("you logged in");

                    // TODO CREATE SESSION
                    createSession();
                }
            }
        });
        loginFrame.setRegisterBehaviour(new RegisterBehaviour() {
            @Override
            public void register(String username, String password) throws SQLException {
                String passwordHash = hasher.hash(10, 65536, 1, password.toCharArray());
                db.addUser(username, passwordHash);
            }
        });
    }

    private void createSession() {
    }
}
