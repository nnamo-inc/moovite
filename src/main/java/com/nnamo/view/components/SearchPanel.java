package com.nnamo.view.components;

import com.nnamo.enums.ButtonMode;
import com.nnamo.enums.ColumnName;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomPreferButton;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomSearchBar;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;

public class SearchPanel extends JPanel {

    CustomSearchBar customSearchBar = new CustomSearchBar();
    CustomTable tableStop = new CustomTable(new ColumnName[] { NOME, CODICE }, CODICE);
    CustomTable tableRoute = new CustomTable(new ColumnName[] { LINEA, CODICE, TIPO, DIREZIONE }, CODICE);
    CustomPreferButton addRouteButton = new CustomPreferButton("Linea", ButtonMode.ADD);

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // search bar
        add(customSearchBar,
                new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));

        // border for table stop and table stop
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        tableStop.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        tableStop.setIsSearchable(false);
        add(tableStop, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        // border for table route and table route
        JPanel routePanel = new JPanel(new GridBagLayout());
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        routePanel.setBorder(
                BorderFactory.createCompoundBorder(tableRouteBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        tableRoute.setIsSearchable(false);
        tableRoute.setIsSearchable(false);
        routePanel.add(tableRoute,
                new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0)
                        .setInsets(2, 5, 2, 5));
        routePanel.add(addRouteButton,
                new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.1)
                        .setInsets(2, 5, 2, 5));
        add(routePanel, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        setVisible(false);
    }

    public void addSearchListener(SearchBarListener listener) {
        customSearchBar.addSearchListener(listener);
    }

    public void updateView(List<StopModel> stopModels, List<RouteDirection> routeModels) {
        tableStop.clear();
        tableRoute.clear();

        for (StopModel stop : stopModels) {
            tableStop.addRow(new Object[] { stop.getName(), stop.getId() });
        }

        for (RouteDirection route : routeModels) {
            String shortName = route.getShortName() != null ? route.getShortName() : "";
            String longName = route.getLongName() != null ? route.getLongName() : "";
            tableRoute.addRow(new Object[] {
                    longName,
                    shortName,
                    route.getType().toString(),
                    route.getDirectionName(),
            });
        }
    }

    public void setSearchStopRowClickBehaviour(TableRowClickBehaviour listener) {
        tableStop.setRowClickBehaviour(listener);
    }

    public void setSearchRouteRowClickBehaviour(TableRowClickBehaviour listener) {
        tableRoute.setRowClickBehaviour(listener);
    }

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour listener) {
        tableStop.setTableCheckIsFavBehaviour(listener);
        tableRoute.setTableCheckIsFavBehaviour(listener);
    }

    public CustomPreferButton getAddRouteButton() {
        return addRouteButton;
    }
}
