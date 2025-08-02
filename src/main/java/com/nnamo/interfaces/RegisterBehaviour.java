package com.nnamo.interfaces;

import java.sql.SQLException;

public interface RegisterBehaviour {
    void register(String username, String password) throws SQLException;
}
