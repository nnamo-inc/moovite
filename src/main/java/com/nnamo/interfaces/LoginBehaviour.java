package com.nnamo.interfaces;

import java.sql.SQLException;

public interface LoginBehaviour {
    void login(String username, String password) throws SQLException;
}
