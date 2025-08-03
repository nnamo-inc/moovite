package com.nnamo.view.components;

import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.SearchBar;
import com.nnamo.view.customcomponents.SwitchBar;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class SearchPanel extends JPanel {

    SearchBar searchBar = new SearchBar();
    CustomTable tableStop = new CustomTable(new String[] { "Nome", "Codice" }, false);
    CustomTable tableRoute = new CustomTable(new String[] { "Linea", "Codice" }, false);

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(searchBar,
                new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));
        // Add the tables to the panel
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        tableStop.setBorder(BorderFactory.createCompoundBorder(
                tableStopBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(tableStop, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        tableRoute.setBorder(BorderFactory.createCompoundBorder(
                tableRouteBorder,
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(tableRoute, new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));
    }

    public void addSearchListener(SearchBarListener listener) {
        searchBar.addSearchListener(listener);
    }

    public void removeSearchListener(SearchBarListener listener) {
        searchBar.removeSearchListener(listener);
    }

    public void updateView(List<StopModel> stopModels, List<RouteModel> routeModels) {
        tableStop.clear();
        tableRoute.clear();

        for (StopModel stop : stopModels) {
            tableStop.addRow(new Object[] { stop.getName(), stop.getId() });
        }

        for (RouteModel route : routeModels) {
            String shortName = route.getShortName() != null ? route.getShortName() : "";
            String longName = route.getLongName() != null ? route.getLongName() : "";
            tableRoute.addRow(new Object[] { longName, shortName });
        }
    }

    public void setSearchStopTableClickListener(TableRowClickListener listener) {
        tableStop.setTableRowClickListener(listener);
    }

    public void setSearchRouteTableClickListener(TableRowClickListener listener) {
        tableRoute.setTableRowClickListener(listener);
    }
}
