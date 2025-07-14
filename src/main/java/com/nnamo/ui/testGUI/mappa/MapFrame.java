package testGUI.Mappa;

import javax.swing.*;
import java.awt.*;

public class MapFrame extends JFrame {

    MapPanel mapPanel;
    JButton zoomInButton, zoomOutButton;
    JPanel lateralPanel;
    Dimension screenSize;

    public MapFrame() {

        super("Mappa");

        JLayeredPane layeredPane = new JLayeredPane();
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        layeredPane.setPreferredSize(screenSize);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(screenSize = new Dimension(800, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mapPanel = new MapPanel();
        mapPanel.setBounds(0, 0, screenSize.width, screenSize.height); // OCCUPA TUTTO IL PANNELLO

        lateralPanel = new JPanel();

        zoomInButton = new JButton("+");
        zoomInButton.setBounds(1000, 800, 50, 50); // in alto a sinistra
        zoomOutButton = new JButton("-");
        zoomInButton.setBounds(60, 10, 25, 25); // in alto a sinistra

        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(zoomInButton, JLayeredPane.POPUP_LAYER);

        add(layeredPane, BorderLayout.CENTER);
        add(lateralPanel, BorderLayout.WEST);

        setVisible(true);

    }

}
