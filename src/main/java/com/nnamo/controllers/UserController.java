package com.nnamo.controllers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.interfaces.SessionListener;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.utils.UserDataUtils;
import com.nnamo.view.frame.LoginFrame;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

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

    public void run() {
        // Open frame only if there's no active session
        int userId = getCurrentUserId();
        if (sessionExists()) {
            if (sessionListener != null) {
                sessionListener.onSessionCreated(userId);
            }
            return;
        }

        loginFrame.setLoginBehaviour(new LoginBehaviour() {
            @Override
            public void login(String username, String password) throws SQLException {
                System.out.printf("Login: %s %s%n", username, password);

                UserModel user = db.getUserByName(username);
                if (user != null && hasher.verify(user.getPasswordHash(), password.getBytes())) {
                    createSession(user);
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
