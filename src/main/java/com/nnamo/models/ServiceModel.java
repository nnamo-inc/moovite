package com.nnamo.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

// Table that specifies dates when a trip exception occurs (trip added or deleted for that date)
@DatabaseTable(tableName = "services")
public class ServiceModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String service_id;

    @DatabaseField(dataType = DataType.DATE_STRING)
    private Date date;

    @DatabaseField
    private int exception_type; // 0 or 1

    public ServiceModel() { // Empty constructor required by OrmLite
    }

    public ServiceModel(String service_id, Date date, int exception_type) { // Empty constructor required by OrmLite
        this.service_id = service_id;
        this.date = date;
        this.exception_type = exception_type;
    }
}
