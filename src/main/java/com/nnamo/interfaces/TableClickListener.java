package com.nnamo.interfaces;

import java.sql.SQLException;

public interface TableClickListener {

    public void onRowClick(Object rowData) throws SQLException;
}
