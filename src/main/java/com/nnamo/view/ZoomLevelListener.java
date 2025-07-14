package com.nnamo.view;

import java.awt.event.MouseWheelEvent;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;

public class ZoomLevelListener implements java.awt.event.MouseWheelListener {

    private JXMapViewer viewer;
    private Painter painter;

    public ZoomLevelListener(JXMapViewer viewer, Painter painter) {
        this.viewer = viewer;
        this.painter = painter;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int zoom = viewer.getZoom();
        if (zoom == 3) {
            viewer.setOverlayPainter(painter);
        } else if (zoom > 3) {
            viewer.setOverlayPainter(null);
        }
    }
}
