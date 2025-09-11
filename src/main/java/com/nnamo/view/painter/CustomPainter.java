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
import java.util.HashMap;

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
public abstract class CustomPainter extends WaypointPainter<Waypoint> {
    protected final JXMapViewer map;
    protected final HashMap<IconSize, BufferedImage> icons = new HashMap<IconSize, BufferedImage>();
    protected BufferedImage currentIcon;

    public CustomPainter(JXMapViewer map) throws IOException {
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

    public abstract void repaint();
}
