package com.nnamo.interfaces;

import com.nnamo.enums.RegisterResult;

import java.sql.SQLException;

public interface RegisterBehaviour {
    RegisterResult register(String username, String password) throws SQLException;
}
