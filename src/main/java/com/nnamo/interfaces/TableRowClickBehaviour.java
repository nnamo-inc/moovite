package com.nnamo.interfaces;

import com.nnamo.enums.ColumnName;

import java.io.IOException;
import java.sql.SQLException;

public interface TableRowClickBehaviour {

    void onRowClick(Object rowData, int columnIndex) throws SQLException, IOException;
}
