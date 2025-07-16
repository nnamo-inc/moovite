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

    private JXMapViewer viewer;
    private WaypointPainter waypointPainter;
    private Painter mapPainter;

    private HashMap<Sizes, BufferedImage> icons = new HashMap<Sizes, BufferedImage>();
    private BufferedImage currentIcon;

    private ZoomLevelListener zoomListener;

    public enum Sizes {
        EXTRA_SMALL,
        SMALL,
        MEDIUM,
        LARGE,
    }

    public StopPainter(JXMapViewer viewer, Painter mapPainter, WaypointPainter painter) throws IOException {
        this.viewer = viewer;
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

    public BufferedImage getIcon(Sizes size) {
        return icons.get(size);
    }

    public BufferedImage getCurrentIcon() {
        return this.zoomListener.getCurrentIcon();
    }

    public ZoomLevelListener getZoomLevelListener() {
        return this.zoomListener;
    }

    private class ZoomLevelListener implements java.awt.event.MouseWheelListener {

        protected WaypointPainter waypointPainter;
        protected BufferedImage currentIcon;
        protected Painter mapPainter;

        public ZoomLevelListener(WaypointPainter waypointPainter, Painter mapPainter) {
            this.waypointPainter = waypointPainter;
            this.mapPainter = mapPainter;
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            int zoom = viewer.getZoom();

            final BufferedImage icon = (zoom <= 1) ? icons.get(Sizes.MEDIUM) : icons.get(Sizes.SMALL);
            System.out.println(icon);
            currentIcon = icon;
            System.out.println(currentIcon);

            this.waypointPainter.setRenderer(new WaypointRenderer<Waypoint>() {
                @Override
                public void paintWaypoint(Graphics2D g, JXMapViewer viewer, Waypoint waypoint) {
                    Point2D point = viewer.getTileFactory().geoToPixel(waypoint.getPosition(), viewer.getZoom());
                    int x = (int) point.getX() - icon.getWidth() / 2;
                    int y = (int) point.getY() - icon.getHeight();
                    g.drawImage(icon, x, y, null);
                }
            });

            if (zoom == 4) {
                viewer.setOverlayPainter(mapPainter);
            } else if (zoom > 4) {
                viewer.setOverlayPainter(null);
            }
        }

        public BufferedImage getCurrentIcon() {
            return this.currentIcon;
        }
    }
}
