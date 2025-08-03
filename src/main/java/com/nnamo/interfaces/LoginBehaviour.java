package com.nnamo.interfaces;

import java.sql.SQLException;

import com.nnamo.enums.AuthResult;

public interface LoginBehaviour {
    AuthResult login(String username, String password) throws SQLException;
}
