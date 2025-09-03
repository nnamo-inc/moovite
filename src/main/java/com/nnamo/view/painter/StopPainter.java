package com.nnamo.view.painter;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import com.nnamo.view.waypoints.StopWaypoint;

public class StopPainter extends WaypointPainter<Waypoint> {
    private final JXMapViewer map;
    private final HashMap<Sizes, BufferedImage> icons = new HashMap<Sizes, BufferedImage>();
    private BufferedImage currentIcon;

    // Inner enum to define the sizes of the stop icons
    public enum Sizes {
        EXTRA_SMALL,
        SMALL,
        MEDIUM,
        LARGE,
    }

    public StopPainter(JXMapViewer map) throws IOException {
        super();
        this.map = map;

        icons.put(Sizes.EXTRA_SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_extra_small.png"))));
        icons.put(Sizes.SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_small.png"))));
        icons.put(Sizes.MEDIUM, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_medium.png"))));
        icons.put(Sizes.LARGE, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stops/stop_large.png"))));
    }

    // GETTERS AND SETTERS OUTER CLASS //
    public BufferedImage getIcon(Sizes size) {
        return icons.get(size);
    }

    public BufferedImage getCurrentIcon() {
        return this.currentIcon;
    }

    public void repaint() {
        repaint(null);
    }

    public void repaint(String stopId) {
        int zoom = map.getZoom();
        final BufferedImage icon = (zoom < 1) ? icons.get(Sizes.MEDIUM) : icons.get(Sizes.SMALL);
        final BufferedImage biggerIcon = (zoom <= 1) ? icons.get(Sizes.LARGE) : icons.get(Sizes.MEDIUM);
        this.currentIcon = icon;
        // Update the waypoint painter with the new icon with an anonymous class
        this.setRenderer(new WaypointRenderer<Waypoint>() {
            @Override
            public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                int x = (int) point.getX() - icon.getWidth() / 2;
                int y = (int) point.getY() - icon.getHeight();

                if (waypoint instanceof StopWaypoint) {
                    StopWaypoint stopWaypoint = (StopWaypoint) waypoint;
                    String waypointStopId = stopWaypoint.getStopId();
                    if (stopId != null && waypointStopId != null && waypointStopId.equals(stopId)) {
                        g.drawImage(biggerIcon, x, y, null);
                    } else {
                        g.drawImage(icon, x, y, null);
                    }
                }
            }
        });
    }
}
