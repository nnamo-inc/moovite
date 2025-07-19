package com.nnamo.view;

import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

public class StopPainter {
    // Map and Painter references
    private JXMapViewer map;
    private Painter mapPainter;
    private WaypointPainter waypointPainter;
    // Icons for different zoom levels
    private HashMap<Sizes, BufferedImage> icons = new HashMap<Sizes, BufferedImage>();
    private BufferedImage currentIcon;
    // Zoom level listener to handle zoom changes
    private ZoomLevelListener zoomListener;
    // Zoom limit
    private int zoomLimit = 4;
    // Inner enum to define the sizes of the stop icons
    public enum Sizes {
        EXTRA_SMALL,
        SMALL,
        MEDIUM,
        LARGE,
    }
    // CONSTRUCTOR OUTHER CLASS //
    public StopPainter(JXMapViewer map, Painter mapPainter, WaypointPainter painter) throws IOException {
        this.map = map;
        this.waypointPainter = painter;
        this.mapPainter = mapPainter;
        this.zoomListener = new ZoomLevelListener(waypointPainter, mapPainter);

        icons.put(Sizes.EXTRA_SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stop_extra_small.png"))));
        icons.put(Sizes.SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stop_small.png"))));
        icons.put(Sizes.MEDIUM, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stop_medium.png"))));
        icons.put(Sizes.LARGE, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/stop_large.png"))));
    }
    // GETTERS AND SETTERS OUTHER CLASS //
    public BufferedImage getIcon(Sizes size) {
        return icons.get(size);
    }

    public BufferedImage getCurrentIcon() {
        return this.zoomListener.getCurrentIcon();
    }

    public ZoomLevelListener getZoomLevelListener() {
        return this.zoomListener;
    }

    public int getZoomLimit() {
        return this.zoomLimit;
    }

    // Inner class to handle zoom level changes
    private class ZoomLevelListener implements java.awt.event.MouseWheelListener {

        protected WaypointPainter waypointPainter;
        protected BufferedImage currentIcon;
        protected Painter mapPainter;
        // CONSTRUCTOR INNER CLASS //
        public ZoomLevelListener(WaypointPainter waypointPainter, Painter mapPainter) {
            this.waypointPainter = waypointPainter;
            this.mapPainter = mapPainter;
        }

        // METHODS INNER CLASS //
        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            // Set the current icon based on the zoom level
            int zoom = map.getZoom();
            currentIcon = icons.get(Sizes.MEDIUM);
            final BufferedImage icon = (zoom <= 1) ? icons.get(Sizes.MEDIUM) : icons.get(Sizes.SMALL);
            currentIcon = icon;
            // Update the waypoint painter with the new icon with an anonymous class
            this.waypointPainter.setRenderer(new WaypointRenderer<Waypoint>() {
                @Override
                public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                    Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                    int x = (int) point.getX() - icon.getWidth() / 2;
                    int y = (int) point.getY() - icon.getHeight();
                    g.drawImage(icon, x, y, null);
                }
            });
            // Set or no visibility of the waypoint painter based on zoom level
            if (zoom == zoomLimit) { map.setOverlayPainter(mapPainter); }
            else if (zoom > zoomLimit) { map.setOverlayPainter(null); }
        }

        // GETTERS AND SETTERS INNER CLASS //
        public BufferedImage getCurrentIcon() {
            return this.currentIcon;
        }
    }
}
