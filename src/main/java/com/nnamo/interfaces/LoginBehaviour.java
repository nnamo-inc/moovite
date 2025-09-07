package com.nnamo.interfaces;

import com.nnamo.enums.AuthResult;

import java.sql.SQLException;

public interface LoginBehaviour {
    AuthResult login(String username, String password) throws SQLException;
}
