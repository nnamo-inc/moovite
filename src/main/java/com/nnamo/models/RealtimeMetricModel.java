package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.RealtimeMetricType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@DatabaseTable(tableName = "realtime_metrics")
public class RealtimeMetricModel {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private RealtimeMetricType type;

    @DatabaseField
    private int value;

    @DatabaseField
    private Date createdAt;

    public RealtimeMetricModel() {
    }

    public RealtimeMetricModel(RealtimeMetricType type, int value, LocalDateTime createdAt) {
        this.createdAt = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public RealtimeMetricType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
