package com.nnamo.view.components;

import com.nnamo.view.customcomponents.CustomPreferButton;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.GbcCustom;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PreferPanel extends JPanel {

    CustomTable tableStop = new CustomTable(new String[] { "Nome", "Codice" }, true);
    CustomTable tableRoute = new CustomTable(new String[] { "Linea", "Codice" }, true);

    private final CustomPreferButton favoriteStopButton = new CustomPreferButton("Fermata");
    private final CustomPreferButton favoriteRouteButton = new CustomPreferButton("Linea");

    public PreferPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Table Stop
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        tableStop.setBorder(BorderFactory.createCompoundBorder(
                tableStopBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(tableStop, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5).setInsets(2, 5, 2, 5));
        add(newButtonRow(), new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));

        // Table Route
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        tableRoute.setBorder(BorderFactory.createCompoundBorder(
                tableRouteBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(tableRoute, new GbcCustom().setPosition(0, 4).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.5).setInsets(2, 5, 2, 5));
        add(newButtonRow(), new GbcCustom().setPosition(0, 5).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));

        setPreferredSize(new Dimension(300, 600));
        setVisible(false);
    }

    private JPanel newButtonRow() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(favoriteRouteButton, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
                .setInsets(2, 5, 2, 5));
        panel.add(favoriteStopButton, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
                .setInsets(2, 5, 2, 5));
        return panel;
    }
}
