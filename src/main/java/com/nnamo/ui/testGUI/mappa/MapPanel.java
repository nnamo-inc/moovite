package testGUI.Mappa;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;

import javax.swing.event.MouseInputListener;
public class MapPanel extends JXMapViewer{

    MouseInputListener mouseClick;
    ZoomMouseWheelListenerCenter mouseWheel;

    public MapPanel() {

        super();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        setTileFactory(tileFactory);

        mouseClick = new PanMouseInputListener(this);
        addMouseListener(mouseClick);
        addMouseMotionListener(mouseClick);
        mouseWheel = new ZoomMouseWheelListenerCenter(this);
        addMouseWheelListener(mouseWheel);

        GeoPosition roma = new GeoPosition(41.9028, 12.4964);
        setZoom(5); // zoom: 0=mappamondo, più alto=più vicino
        setAddressLocation(roma);
    }
}
