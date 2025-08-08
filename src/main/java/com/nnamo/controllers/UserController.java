package com.nnamo.controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import com.nnamo.enums.AuthResult;
import com.nnamo.enums.RegisterResult;
import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.interfaces.SessionListener;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.utils.UserDataUtils;
import com.nnamo.view.frame.LoginFrame;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class UserController {
    private final DatabaseService db;
    private final LoginFrame loginFrame;
    private final Argon2 hasher = Argon2Factory.create();

    private final String sessionPath = UserDataUtils.getSessionPath();
    private final String dataDir = UserDataUtils.getDataDir();

    private SessionListener sessionListener;

    public UserController(DatabaseService db) {
        this.db = db;
        this.loginFrame = new LoginFrame();
    }

    public void run() throws SQLException {
        // Open frame only if there's no active session
        int userId = getCurrentUserId();
        if (sessionExists()) {
            UserModel user = db.getUserById(userId);

            // If session id is stored but the user is not in the database: session.txt gets
            // deleted
            if (user == null) {
                deleteCurrentSession();
            }

            if (sessionListener == null) {
                System.out.println("[!] Session listener is not implemented");
                return;
            }

            if (sessionListener != null && user != null) {
                sessionListener.onSessionCreated(userId);
                return;
            }
        }

        loginFrame.setLoginBehaviour(new LoginBehaviour() {
            @Override
            public AuthResult login(String username, String password) throws SQLException {
                UserModel user = db.getUserByName(username);
                if (user != null && hasher.verify(user.getPasswordHash(), password.getBytes())) {
                    createSession(user);
                    return AuthResult.SUCCESS;
                }
                return AuthResult.WRONG_CREDENTIALS;
            }
        });
        loginFrame.setRegisterBehaviour(new RegisterBehaviour() {
            @Override
            public RegisterResult register(String username, String password) throws SQLException {
                String passwordHash = hasher.hash(10, 65536, 1, password.toCharArray());
                UserModel user = db.getUserByName(username);
                if (user != null) {
                    return RegisterResult.USER_ALREADY_EXISTS;
                }

                db.addUser(username, passwordHash);
                return RegisterResult.SUCCESS;
            }
        });
        loginFrame.open();
    }

    public void addSessionListener(SessionListener listener) {
        if (listener != null) {
            this.sessionListener = listener;
        }
    }

    private void createSession(UserModel user) {
        File file = new File(sessionPath);
        int userId = user.getId();
        try {
            Files.createDirectories(Path.of(dataDir));
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(userId);
            writer.close();
            if (sessionListener != null) {
                sessionListener.onSessionCreated(userId);
            }
            loginFrame.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void deleteCurrentSession() {
        try {
            Files.deleteIfExists(Path.of(sessionPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean sessionExists() {
        return getCurrentUserId() != -1;
    }

    public int getCurrentUserId() {
        int userId = -1;
        try {
            if (Files.exists(Path.of(sessionPath))) {
                File file = new File(sessionPath);
                FileReader reader = new FileReader(file);
                userId = reader.read();
                reader.close();
                return userId;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return userId;
    }
}
