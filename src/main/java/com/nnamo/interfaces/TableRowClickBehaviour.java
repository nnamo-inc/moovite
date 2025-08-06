package com.nnamo.interfaces;

import java.io.IOException;
import java.sql.SQLException;

public interface TableRowClickBehaviour {

    void onRowClick(Object rowData) throws SQLException, IOException;
}
