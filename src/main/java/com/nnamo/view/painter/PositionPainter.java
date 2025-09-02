package com.nnamo.view.painter;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;
import org.jxmapviewer.viewer.WaypointRenderer;

import com.nnamo.enums.IconSize;

public abstract class PositionPainter extends WaypointPainter<Waypoint> {
    private final JXMapViewer map;
    protected final HashMap<IconSize, BufferedImage> icons = new HashMap<IconSize, BufferedImage>();
    private BufferedImage currentIcon;

    public PositionPainter(JXMapViewer map) throws IOException {
        super();
        this.map = map;
    }

    // GETTERS AND SETTERS OUTER CLASS //
    public BufferedImage getIcon(IconSize size) {
        return icons.get(size);
    }

    public BufferedImage getCurrentIcon() {
        return this.currentIcon;
    }

    protected HashMap<IconSize, BufferedImage> getIcons() {
        return this.icons;
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
