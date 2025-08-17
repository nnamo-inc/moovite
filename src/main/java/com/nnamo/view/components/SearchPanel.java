package com.nnamo.view.components;

import com.nnamo.enums.ColumnName;
import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomSearchBar;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.nnamo.enums.ColumnName.*;
import static com.nnamo.enums.DataType.*;

public class SearchPanel extends JPanel {

    // ATTRIBUTES //
    CustomSearchBar searchBar;
    CustomTable stopTable;
    CustomTable tableRoute;

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Title Label
        createTitleBar();

        // search bar
        createSearchBar();

        // border for table stop and table stop
        createStopTable();

        // border for table route and table route
        createRouteTable();

        setVisible(false);
    }

    // METHODS //
    private void createTitleBar() {
        JLabel titleLabel = new JLabel("Search");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

    }

    private void createSearchBar() {
        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (RouteType type : RouteType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == RouteType.ALL); // Default selected type
            buttons.add(button);
        }

        searchBar = new CustomSearchBar(buttons);
        add(searchBar,
                new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));
    }

    private void createStopTable() {
        stopTable = new CustomTable(new ColumnName[] { CODICE, NOME }, STOP);
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        stopTable.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(stopTable, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));
    }

    private void createRouteTable() {
        tableRoute = new CustomTable(new ColumnName[] { CODICE, TIPO, CAPOLINEA, DIREZIONE }, new ColumnName[] { DIREZIONE }, ROUTE);
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        tableRoute.setBorder(
                BorderFactory.createCompoundBorder(tableRouteBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(tableRoute, new CustomGbc().setPosition(0, 3).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));
    }

    public void updateView(List<StopModel> stopModels, List<RouteDirection> routeModels) {
        stopTable.clear();
        tableRoute.clear();

        for (StopModel stop : stopModels) {
            stopTable.addRow(new Object[] { stop.getId(), stop.getName() });
        }

        for (RouteDirection route : routeModels) {
            String shortName = route.getShortName() != null ? route.getShortName() : "";
            tableRoute.addRow(new Object[] {
                    shortName,
                    route.getType().name(),
                    route.getDirectionName(),
                    route.getDirection().name(),
            });
        }
    }

    // METHODS BEHAVIOUR //
    public void addSearchListener(SearchBarListener listener) {
        searchBar.addSearchListener(listener);
    }

    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        stopTable.setTableRowClickBehaviour(listener);
        tableRoute.setTableRowClickBehaviour(listener);
    }
}
