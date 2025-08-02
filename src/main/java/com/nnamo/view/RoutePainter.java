package com.nnamo.view;

import com.nnamo.models.StopModel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * Custom painter to draw route lines connecting consecutive stops on the map.
 */
public class RoutePainter implements Painter<JXMapViewer> {
    
    private List<StopModel> stops;
    private Color lineColor;
    private int lineWidth;
    
    public RoutePainter(List<StopModel> stops) {
        this.stops = stops;
        this.lineColor = Color.BLUE;
        this.lineWidth = 3;
    }
    
    public RoutePainter(List<StopModel> stops, Color lineColor, int lineWidth) {
        this.stops = stops;
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
    }
    
    @Override
    public void paint(Graphics2D g, JXMapViewer map, int width, int height) {
        System.out.println("RoutePainter.paint() called with " + (stops != null ? stops.size() : 0) + " stops");
        
        if (stops == null || stops.size() < 2) {
            System.out.println("Not enough stops to draw route lines");
            return;
        }
        
        g = (Graphics2D) g.create();
        
        // Configure line appearance
        g.setColor(lineColor);
        g.setStroke(new BasicStroke(lineWidth));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        System.out.println("Drawing route lines with color: " + lineColor + ", width: " + lineWidth);
        
        // Draw lines between consecutive stops
        for (int i = 0; i < stops.size() - 1; i++) {
            StopModel currentStop = stops.get(i);
            StopModel nextStop = stops.get(i + 1);
            
            // Skip if either stop is null or has invalid coordinates
            if (currentStop == null || nextStop == null ||
                currentStop.getLatitude() == 0.0 || currentStop.getLongitude() == 0.0 ||
                nextStop.getLatitude() == 0.0 || nextStop.getLongitude() == 0.0) {
                System.out.println("Skipping line " + i + " due to invalid coordinates");
                continue;
            }
            
            // Convert geo positions to screen coordinates
            GeoPosition currentGeo = new GeoPosition(currentStop.getLatitude(), currentStop.getLongitude());
            GeoPosition nextGeo = new GeoPosition(nextStop.getLatitude(), nextStop.getLongitude());
            
            Point2D currentPoint = map.convertGeoPositionToPoint(currentGeo);
            Point2D nextPoint = map.convertGeoPositionToPoint(nextGeo);
            
            System.out.println("Drawing line from (" + currentPoint.getX() + ", " + currentPoint.getY() + 
                             ") to (" + nextPoint.getX() + ", " + nextPoint.getY() + ")");
            
            // Draw line between the two points
            g.drawLine((int)currentPoint.getX(), (int)currentPoint.getY(), 
                      (int)nextPoint.getX(), (int)nextPoint.getY());
        }
        
        g.dispose();
    }
    
    // Getters and setters
    public List<StopModel> getStops() {
        return stops;
    }
    
    public void setStops(List<StopModel> stops) {
        this.stops = stops;
    }
    
    public Color getLineColor() {
        return lineColor;
    }
    
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
    
    public int getLineWidth() {
        return lineWidth;
    }
    
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
}
