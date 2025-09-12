package com.nnamo.view.painter;

import com.nnamo.enums.IconSize;
import com.nnamo.view.waypoints.StopWaypoint;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A specialized {@link WaypointPainter} for rendering stop waypoints on a map.
 * This class manages different icon sizes and updates the displayed icon based
 * on the map's zoom level.
 * It also highlights a specific stop if its ID is provided during repainting by
 * using a larger icon.
 *
 * @author Samuele Lombardi
 * @see WaypointPainter
 * @see JXMapViewer
 * @see StopWaypoint
 */
public class StopPainter extends CustomPainter {
    public StopPainter(JXMapViewer map) throws IOException {
        super(map);
        icons.put(IconSize.EXTRA_SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_extra_small.png"))));
        icons.put(IconSize.SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_small.png"))));
        icons.put(IconSize.MEDIUM, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_medium.png"))));
        icons.put(IconSize.LARGE, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_large.png"))));
    }

    public void repaint() {
        repaint(null);
    }

    public void repaint(String stopId) {
        int zoom = map.getZoom();
        final BufferedImage icon = (zoom < 1) ? icons.get(IconSize.MEDIUM) : icons.get(IconSize.SMALL);
        final BufferedImage biggerIcon = (zoom <= 1) ? icons.get(IconSize.LARGE) : icons.get(IconSize.MEDIUM);
        this.currentIcon = icon;
        // Update the waypoint painter with the new icon with an anonymous class
        this.setRenderer(new WaypointRenderer<Waypoint>() {
            @Override
            public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                int x = (int) point.getX() - icon.getWidth() / 2;
                int y = (int) point.getY() - icon.getHeight();

                if (waypoint instanceof StopWaypoint stopWaypoint) {
                    String waypointStopId = stopWaypoint.getStopId();
                    if (waypointStopId != null && waypointStopId.equals(stopId)) {
                        g.drawImage(biggerIcon, x, y, null);
                    } else {
                        g.drawImage(icon, x, y, null);
                    }
                }
            }
        });
    }
}
