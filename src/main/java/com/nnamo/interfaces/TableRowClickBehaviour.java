package com.nnamo.interfaces;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.DataType;

import java.io.IOException;
import java.sql.SQLException;

public interface TableRowClickBehaviour {

    void onRowClick(Object rowData, ColumnName[] tableColumns, DataType dataType)
            throws SQLException, IOException;
}
