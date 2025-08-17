package com.nnamo.view.components;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.UpdateMode;
import com.nnamo.enums.VehicleType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomSearchBar;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomGbc;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.*;
import static com.nnamo.enums.VehicleType.*;

public class PreferPanel extends JPanel {

    CustomSearchBar customSearchBar;

    JPanel stopContainer;
    CustomTable stopTable;

    JPanel routeContainer;
    CustomTable routeTable;

    // CONSTRUCTOR //
    public PreferPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("Favorites");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

        // Search Bar
        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (VehicleType type : VehicleType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == VehicleType.BUS); // Default selected type
            buttons.add(button);
        }
        customSearchBar = new CustomSearchBar(buttons);
        add(customSearchBar,
                new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));

        // Stop Table
        stopContainer = new JPanel(new GridBagLayout());
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        stopContainer.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        stopTable = new CustomTable(new ColumnName[] { NOME, CODICE }, STOP);
        stopTable.setSearchColumns(NOME, CODICE);
        stopContainer.add(stopTable, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        add(stopContainer, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        // Route Table
        routeContainer = new JPanel(new GridBagLayout());
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        routeContainer.setBorder(BorderFactory.createCompoundBorder(tableRouteBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        routeTable = new CustomTable(new ColumnName[] { LINEA, CODICE, TIPO, CAPOLINEA, DIREZIONE }, new ColumnName[] { DIREZIONE }, ROUTE);
        routeTable.setSearchColumns(LINEA, CODICE);
        routeContainer.add(routeTable, new CustomGbc().setPosition(0, 4).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        add(routeContainer, new CustomGbc().setPosition(0, 3).setFill(GridBagConstraints.BOTH)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        setVisible(false);
    }

    // LISTENERS METHODS //
    public void addSearchListener(SearchBarListener listener) {
        this.customSearchBar.addSearchListener(listener);
    }

    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        this.stopTable.setRowClickBehaviour(listener);
        this.routeTable.setRowClickBehaviour(listener);
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour listener) {
        this.stopTable.setTableCheckIsFavBehaviour(listener);
        this.routeTable.setTableCheckIsFavBehaviour(listener);
    }

    public void initPreferTable(List<StopModel> stops, List<RouteDirection> directedRoutes) {
        for (StopModel stop : stops) {
            updateFavStopTable(stop, UpdateMode.ADD);
        }
        updateFavRouteTable(directedRoutes, UpdateMode.ADD);
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        switch (updateMode) {
            case ADD:
                stopTable.addRow(new Object[] { stop.getName(), stop.getId() });
                break;
            case REMOVE:
                stopTable.removeRow(stop.getId());
                break;
        }
    }

    public void updateFavRouteTable(List<RouteDirection> directedRoutes, UpdateMode updateMode) {
        for (RouteDirection route : directedRoutes) {
            switch (updateMode) {
                case ADD:
                    routeTable.addRow(new Object[] {
                            route.getLongName() != null ? route.getLongName() : route.getShortName(),
                            route.getId(),
                            route.getType(),
                            route.getDirectionName(),
                            route.getDirection().name(),

                    });
                    break;
                case REMOVE:
                    routeTable.removeRow(route.getId());
                    break;
            }
        }
    }

    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.stopTable.setRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.routeTable.setRowClickBehaviour(behaviour);
    }

}
