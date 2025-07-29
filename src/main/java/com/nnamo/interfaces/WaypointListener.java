package com.nnamo.interfaces;

import org.jxmapviewer.viewer.GeoPosition;

import java.io.IOException;
import java.sql.SQLException;

public interface WaypointListener {

    public void onWaypointClick(GeoPosition geo) throws SQLException, IOException;

}