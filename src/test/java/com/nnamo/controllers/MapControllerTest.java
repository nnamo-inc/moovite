// src/test/java/com/nnamo/controllers/MapControllerTest.java

package com.nnamo.controllers;

import com.nnamo.models.StopModel;
import junit.framework.TestCase;
import org.jxmapviewer.viewer.GeoPosition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapControllerTest extends TestCase {

    public void testCalculateBarycenterSingleStop() {
        StopModel stop = new StopModel("1", "Stop1", 45.0, 9.0);
        List<StopModel> stops = Collections.singletonList(stop);

        GeoPosition barycenter = MapController.calculateBarycenter(stops);

        assertEquals(45.0, barycenter.getLatitude());
        assertEquals(9.0, barycenter.getLongitude());
    }

    public void testCalculateBarycenterTwoStops() {
        StopModel stop1 = new StopModel("1", "Stop1", 45.0, 9.0);
        StopModel stop2 = new StopModel("2", "Stop2", 47.0, 11.0);
        List<StopModel> stops = Arrays.asList(stop1, stop2);

        GeoPosition barycenter = MapController.calculateBarycenter(stops);

        assertEquals(46.0, barycenter.getLatitude());
        assertEquals(10.0, barycenter.getLongitude());
    }

    public void testCalculateBarycenterMultipleStops() {
        StopModel stop1 = new StopModel("1", "Stop1", 45.0, 9.0);
        StopModel stop2 = new StopModel("2", "Stop2", 47.0, 11.0);
        StopModel stop3 = new StopModel("3", "Stop3", 46.0, 10.0);
        List<StopModel> stops = Arrays.asList(stop1, stop2, stop3);

        GeoPosition barycenter = MapController.calculateBarycenter(stops);

        assertEquals(46.0, barycenter.getLatitude());
        assertEquals(10.0, barycenter.getLongitude());
    }

    public void testCalculateBarycenterEmptyList() {
        List<StopModel> stops = Collections.emptyList();
        assertThrows(ArithmeticException.class, () -> MapController.calculateBarycenter(stops));
    }

    public void testCalculateZoomLevelSingleStop() {
        StopModel stop = new StopModel("1", "Stop1", 45.0, 9.0);
        List<StopModel> stops = Collections.singletonList(stop);
        assertEquals(1, MapController.calculateZoomLevel(stops));
    }

    public void testCalculateZoomLevel_SmallDifference() {
        StopModel stop1 = new StopModel("1", "Stop1", 45.000, 9.000);
        StopModel stop2 = new StopModel("2", "Stop2", 45.010, 9.010);
        List<StopModel> stops = Arrays.asList(stop1, stop2);
        assertEquals(2, MapController.calculateZoomLevel(stops));
    }

    public void testCalculateZoomLevelMediumDifference() {
        StopModel stop1 = new StopModel("1", "Stop1", 45.000, 9.000);
        StopModel stop2 = new StopModel("2", "Stop2", 45.030, 9.030);
        List<StopModel> stops = Arrays.asList(stop1, stop2);
        assertEquals(5, MapController.calculateZoomLevel(stops));
    }

    public void testCalculateZoomLevelLargeDifference() {
        StopModel stop1 = new StopModel("1", "Stop1", 45.000, 9.000);
        StopModel stop2 = new StopModel("2", "Stop2", 45.200, 9.200);
        List<StopModel> stops = Arrays.asList(stop1, stop2);
        assertEquals(7, MapController.calculateZoomLevel(stops));
    }

    public void testCalculateZoomLevelEmptyList() {
        List<StopModel> stops = Collections.emptyList();
        assertEquals(1, MapController.calculateZoomLevel(stops));
    }
}