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

    @DatabaseField(uniqueCombo = true)
    private String service_id;

    @DatabaseField(dataType = DataType.DATE_STRING, uniqueCombo = true)
    private Date date;

    @DatabaseField
    private int exception_type; // 1 if date is added, 2 if date is deleted

    public enum ExceptionType {
        ADDED,
        DELETED,
        UNKNOWN
    }

    public ServiceModel() { // Empty constructor required by OrmLite
    }

    public ServiceModel(String service_id, Date date, int exception_type) {
        this.service_id = service_id;
        this.date = date;
        this.exception_type = exception_type;
    }

    public String getServiceId() {
        return service_id;
    }

    public Date getDate() {
        return date;
    }

    public ExceptionType getExceptionType() {
        switch (exception_type) {
            case 1:
                return ExceptionType.ADDED;
            case 2:
                return ExceptionType.DELETED;
            default:
                return ExceptionType.UNKNOWN; // Invalid exception type
        }
    }
}
