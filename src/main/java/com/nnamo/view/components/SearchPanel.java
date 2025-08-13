package com.nnamo.view.components;

import com.nnamo.enums.ColumnName;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.interfaces.TableCheckIsFavBehaviour;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteDirection;
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
import static com.nnamo.enums.DataType.*;

public class SearchPanel extends JPanel {

    CustomSearchBar customSearchBar = new CustomSearchBar();
    CustomTable tableStop = new CustomTable(
            new ColumnName[] { CODICE, NOME },
            STOP);
    CustomTable tableRoute = new CustomTable(
            new ColumnName[] { CODICE, TIPO, CAPOLINEA, DIREZIONE },
            ROUTE);

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel("Search");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

        // search bar
        add(customSearchBar,
                new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setInsets(5, 5, 5, 5));

        // border for table stop and table stop
        TitledBorder tableStopBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Fermate");
        tableStop.setBorder(
                BorderFactory.createCompoundBorder(tableStopBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        tableStop.setIsSearchable(false);
        add(tableStop, new CustomGbc().setPosition(0, 2).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
                .setInsets(2, 5, 2, 5));

        // border for table route and table route
        TitledBorder tableRouteBorder = new TitledBorder(new LineBorder(Color.lightGray, 2), "Linee");
        tableRoute.setBorder(
                BorderFactory.createCompoundBorder(tableRouteBorder, BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        tableRoute.setIsSearchable(false);
        add(tableRoute, new CustomGbc().setPosition(0, 3).setFill(GridBagConstraints.BOTH).setWeight(1.0, 0.5)
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
            tableStop.addRow(new Object[] { stop.getId(), stop.getName() });
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

    public void setGenericTableRowClickBehaviour(TableRowClickBehaviour listener) {
        tableStop.setRowClickBehaviour(listener);
        tableRoute.setRowClickBehaviour(listener);
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
}
