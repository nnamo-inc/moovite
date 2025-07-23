package com.nnamo.interfaces;

import java.sql.SQLException;

public interface RegisterBehaviour {
    public void register(String username, String password) throws SQLException;
}
