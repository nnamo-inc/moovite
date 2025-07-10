package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "agencies")
public class AgencyModel {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String url;

    @DatabaseField
    private String timezone;

    @DatabaseField(canBeNull = true)
    private String phone = null;

    @DatabaseField(canBeNull = true)
    private String email = null;

    public AgencyModel() { // Empty constructor required by OrmLite
    }

    // TODO Add optional fields
    public AgencyModel(String id, String name, String timezone, String url) { // Empty constructor required by OrmLite
        this.id = id;
        this.name = name;
        this.timezone = timezone;
        this.url = url;
    }


}
