package com.nnamo.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.nnamo.enums.RealtimeMetricType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Represents a real-time metric entry, including type, value, and creation
 * timestamp.
 */
@DatabaseTable(tableName = "realtime_metrics")
public class RealtimeMetricModel {

    /**
     * Unique identifier for the metric entry (auto-generated).
     */
    @DatabaseField(generatedId = true)
    private int id;

    /**
     * Type of the metric.
     */
    @DatabaseField
    private RealtimeMetricType type;

    /**
     * Value of the metric.
     */
    @DatabaseField
    private int value;

    /**
     * Timestamp when the metric was created.
     */
    @DatabaseField
    private Date createdAt;

    /**
     * Empty constructor required by OrmLite.
     */
    public RealtimeMetricModel() {
    }

    /**
     * Constructs a RealtimeMetricModel with the specified type, value, and creation
     * time.
     *
     * @param type      the metric type
     * @param value     the metric value
     * @param createdAt the creation time as LocalDateTime
     */
    public RealtimeMetricModel(RealtimeMetricType type, int value, LocalDateTime createdAt) {
        this.createdAt = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.type = type;
        this.value = value;
    }

    /**
     * Gets the unique identifier for the metric entry.
     *
     * @return the metric ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the type of the metric.
     *
     * @return the metric type
     */
    public RealtimeMetricType getType() {
        return type;
    }

    /**
     * Gets the value of the metric.
     *
     * @return the metric value
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the creation timestamp of the metric.
     *
     * @return the creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
}
