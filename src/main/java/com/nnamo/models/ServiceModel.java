package com.nnamo.models;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "services")
public class ServiceModel {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date date;

    @DatabaseField
    private int exception_type; // 0 or 1

    public ServiceModel() { // Empty constructor required by OrmLite
    }

    public ServiceModel(String id, Date date, int exception_type) { // Empty constructor required by OrmLite
        this.id = id;
        this.date = date;
        this.exception_type = exception_type;
    }
}
