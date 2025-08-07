package com.nnamo.view.components;

import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomPreferButton;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.GbcCustom;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class PreferPanel extends JPanel {

    JPanel stopContainer = new JPanel(new GridBagLayout());
    CustomTable stopTable = new CustomTable(new String[] { "Nome", "Codice" }, true);
    CustomPreferButton removeStopButton = new CustomPreferButton("Fermata");

    JPanel routeContainer = new JPanel(new GridBagLayout());
    CustomTable routeTable = new CustomTable(new String[] { "Linea", "Codice" }, true);
    CustomPreferButton removeRouteButton = new CustomPreferButton("Linea");

    // CONSTRUCTOR //
    public PreferPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Table Stop
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        stopContainer.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        stopTable.setSearchColumns(0, 1);
        stopContainer.add(stopTable, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        routeTable.setSearchColumns(0, 1);
        stopContainer.add(removeStopButton, new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));
        add(stopContainer, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        // Table Route
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        routeContainer.setBorder(BorderFactory.createCompoundBorder(
                tableRouteBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        routeContainer.add(routeTable, new GbcCustom().setPosition(0, 4).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        routeContainer.add(removeRouteButton, new GbcCustom().setPosition(0, 5).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));
        add(routeContainer, new GbcCustom().setPosition(0, 3).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        setPreferredSize(new Dimension(300, 600));
        setVisible(false);
    }

    // METHODS //
    public CustomTable getRouteTable() {
        return routeTable;
    }

    public CustomPreferButton getRemoveRouteButton() {
        return removeRouteButton;
    }


    // LISTENERS METHODS //
    public void initPreferTable(List<StopModel> stops, List<RouteModel> routes) {
        for (StopModel stop : stops) {
            stopTable.addRow(new Object[] { stop.getName(), stop.getId() });
        }
        for (RouteModel route : routes) {
            routeTable.addRow(new Object[] { route.getShortName(), route.getId() });
        }
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        switch (updateMode) {
            case ADD:
                stopTable.addRow(new Object[] { stop.getName(), stop.getId() }); break;
            case REMOVE:
                stopTable.removeRow(stop.getId()); break;
        }
    }

    public void updateFavRouteTable(RouteModel route, UpdateMode updateMode) {
            switch (updateMode) {
                case ADD:
                    routeTable.addRow(new Object[] { route.getShortName(), route.getId() });; break;
                case REMOVE:
                    routeTable.removeRow(route.getId()); break;
            }
    }

    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.stopTable.setRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.routeTable.setRowClickBehaviour(behaviour);
    }

}
