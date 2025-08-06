package com.nnamo.interfaces;

import java.sql.SQLException;

import com.nnamo.enums.RegisterResult;

public interface RegisterBehaviour {
    RegisterResult register(String username, String password) throws SQLException;
}
