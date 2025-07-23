package com.nnamo.controllers;

import java.awt.Dialog.ModalExclusionType;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import com.nnamo.interfaces.LoginBehaviour;
import com.nnamo.interfaces.RegisterBehaviour;
import com.nnamo.models.UserModel;
import com.nnamo.services.DatabaseService;
import com.nnamo.view.frame.LoginFrame;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

public class UserController {
    private DatabaseService db;
    private LoginFrame loginFrame;
    private AppDirs appDirs = AppDirsFactory.getInstance();
    private Argon2 hasher = Argon2Factory.create();

    private final String dataDir = appDirs.getUserDataDir("moovite", null, "nnamo");
    private final String sessionPath = dataDir + "/session.txt";

    public UserController(DatabaseService db) {
        this.db = db;
    }

    public void run() {

        // Open frame only if there's no active session
        if (getCurrentUserId() != -1) {
            loginFrame.close();
            return;
        }

        this.loginFrame = new LoginFrame();
        loginFrame.setLoginBehaviour(new LoginBehaviour() {
            @Override
            public void login(String username, String password) throws SQLException {
                System.out.println(String.format("Login: %s %s", username, password));

                UserModel user = db.getUserByName(username);
                if (user != null && hasher.verify(user.getPasswordHash(), password.getBytes())) {
                    System.out.println("you logged in");

                    // TODO CREATE SESSION
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

    private void createSession(UserModel user) {
        File file = new File(sessionPath);
        try {
            Files.createDirectories(Path.of(dataDir));
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(user.getId());
            writer.close();
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
