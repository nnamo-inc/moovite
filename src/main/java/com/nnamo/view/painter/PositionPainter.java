package com.nnamo.view.painter;

import com.nnamo.enums.IconSize;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * An abstract class that extends {@link WaypointPainter} to provide
 * functionality for rendering
 * waypoints on a map with different icon sizes based on the zoom level.
 * This class manages a collection of icons and updates the displayed icon
 * according to the map's zoom level.
 *
 * @author Samuele Lombardi
 * @see WaypointPainter
 * @see JXMapViewer
 * @see IconSize
 */
public abstract class PositionPainter extends CustomPainter {
    public PositionPainter(JXMapViewer map) throws IOException {
        super(map);
    }

    public void repaint() {
        int zoom = map.getZoom();
        currentIcon = icons.get(IconSize.MEDIUM);
        final BufferedImage icon = (zoom < 1) ? icons.get(IconSize.MEDIUM) : icons.get(IconSize.SMALL);
        this.currentIcon = icon;

        // Update the waypoint painter with the new icon with an anonymous class
        this.setRenderer(new WaypointRenderer<Waypoint>() {
            @Override
            public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                int x = (int) point.getX() - icon.getWidth() / 2;
                int y = (int) point.getY() - icon.getHeight();
                g.drawImage(icon, x, y, null);
            }
        });
    }
}
