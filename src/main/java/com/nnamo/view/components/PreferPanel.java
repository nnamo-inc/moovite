package com.nnamo.view.components;

import com.nnamo.enums.ButtonMode;
import com.nnamo.enums.ColumnName;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomPreferButton;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomGbc;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;

public class PreferPanel extends JPanel {

    JPanel stopContainer = new JPanel(new GridBagLayout());
    CustomTable stopTable = new CustomTable(new ColumnName[] { NOME, CODICE }, CODICE);
    CustomPreferButton removeStopButton = new CustomPreferButton("Fermata", ButtonMode.REMOVE);

    JPanel routeContainer = new JPanel(new GridBagLayout());
    CustomTable routeTable = new CustomTable(new ColumnName[] { LINEA, CODICE, TIPO }, CODICE);
    CustomPreferButton removeRouteButton = new CustomPreferButton("Linea", ButtonMode.REMOVE);

    // CONSTRUCTOR //
    public PreferPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Table Stop
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        stopContainer.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        stopTable.setSearchColumns(NOME, CODICE);
        stopContainer.add(stopTable, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        routeTable.setSearchColumns(LINEA, CODICE);
        stopContainer.add(removeStopButton, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));
        add(stopContainer, new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        // Table Route
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        routeContainer.setBorder(BorderFactory.createCompoundBorder(
                tableRouteBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        routeContainer.add(routeTable, new CustomGbc().setPosition(0, 4).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        routeContainer.add(removeRouteButton, new CustomGbc().setPosition(0, 5).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));
        add(routeContainer, new CustomGbc().setPosition(0, 3).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        setVisible(false);
    }

    // METHODS //
    public CustomTable getRouteTable() {
        return routeTable;
    }

    public CustomPreferButton getRemoveRouteButton() {
        return removeRouteButton;
    }

    public CustomPreferButton getRemoveStopButton() {
        return removeStopButton;
    }


    // LISTENERS METHODS //
    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour listener)
    {
        this.stopTable.setTableCheckIsFavBehaviour(listener);
        this.routeTable.setTableCheckIsFavBehaviour(listener);
    }

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
                    routeTable.addRow(new Object[] { route.getLongName() != null ? route.getLongName() : "", route.getId(), route.getType() });; break;
                case REMOVE:
                    routeTable.removeRow(route.getId()); break;
            }
    }

    public void updatePreferStopButton(Boolean isFavorite, String stopId) {
        removeStopButton.setItemId(stopId);
    }

    public void updatePreferRouteButton(Boolean isFavorite, String routeNumber) {
        removeRouteButton.setItemId(routeNumber);
    }

    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.stopTable.setRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.routeTable.setRowClickBehaviour(behaviour);
    }

    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        this.removeStopButton.setFavBehaviour(behaviour);
    }

}
