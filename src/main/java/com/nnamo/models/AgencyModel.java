package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Represents a GTFS agency entity.
 * Used with ORMLite for database persistence.
 */
@DatabaseTable(tableName = "agencies")
public class AgencyModel {

    /**
     * Unique identifier for the agency.
     */
    @DatabaseField(id = true)
    private String id;

    /**
     * Name of the agency.
     */
    @DatabaseField
    private String name;

    /**
     * URL of the agency.
     */
    @DatabaseField
    private String url;

    /**
     * Timezone of the agency.
     */
    @DatabaseField
    private String timezone;

    /**
     * Optional phone number of the agency.
     */
    @DatabaseField(canBeNull = true)
    private final String phone = null;

    /**
     * Optional email address of the agency.
     */
    @DatabaseField(canBeNull = true)
    private final String email = null;

    /**
     * Empty constructor required by ORMLite.
     */
    public AgencyModel() {
    }

    /**
     * Constructs an AgencyModel with required fields.
     *
     * @param id       the unique identifier
     * @param name     the agency name
     * @param timezone the agency timezone
     * @param url      the agency URL
     */
    public AgencyModel(String id, String name, String timezone, String url) {
        this.id = id;
        this.name = name;
        this.timezone = timezone;
        this.url = url;
    }

    /**
     * Gets the agency ID.
     *
     * @return the agency ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the agency name.
     *
     * @return the agency name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the agency URL.
     *
     * @return the agency URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the agency timezone.
     *
     * @return the agency timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Gets the agency phone number.
     *
     * @return the agency phone number, or null if not set
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Gets the agency email address.
     *
     * @return the agency email address, or null if not set
     */
    public String getEmail() {
        return email;
    }
}
