package com.nnamo.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Table that specifies dates when a trip exception occurs (trip added or
 * deleted for that date). It is specified by GTFS
 */
@DatabaseTable(tableName = "services")
public class ServiceModel {

    /**
     * Primary key, auto-generated.
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Service identifier. Part of unique combination.
     */
    @DatabaseField(uniqueCombo = true)
    private String service_id;

    /**
     * Date of the exception. Part of unique combination.
     */
    @DatabaseField(dataType = DataType.DATE_STRING, uniqueCombo = true)
    private Date date;

    /**
     * Exception type: 1 if date is added, 2 if date is deleted.
     */
    @DatabaseField
    private int exception_type;

    /**
     * Enum representing possible exception types.
     */
    public enum ExceptionType {
        ADDED,
        DELETED,
        UNKNOWN
    }

    /**
     * Empty constructor required by OrmLite.
     */
    public ServiceModel() {
    }

    /**
     * Constructs a ServiceModel with the specified service ID, date, and exception
     * type.
     *
     * @param service_id     the service identifier
     * @param date           the date of the exception
     * @param exception_type the exception type (1 for added, 2 for deleted)
     */
    public ServiceModel(String service_id, Date date, int exception_type) {
        this.service_id = service_id;
        this.date = date;
        this.exception_type = exception_type;
    }

    /**
     * Gets the service identifier.
     *
     * @return the service ID
     */
    public String getServiceId() {
        return service_id;
    }

    /**
     * Gets the date of the exception.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the exception type as an enum.
     *
     * @return the exception type
     */
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
