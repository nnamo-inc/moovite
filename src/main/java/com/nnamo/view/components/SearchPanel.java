package com.nnamo.view.components;

import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.SearchBar;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    SearchBar searchBar = new SearchBar();
    CustomTable tableStop = new CustomTable(new String[] {"Colonna 1", "Colonna 2", "Colonna 3"}, false);
    CustomTable tableRoute = new CustomTable(new String[] {"Colonna 1", "Colonna 2", "Colonna 3"}, false);
    SearchBarListener searchBarListener;

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        add(searchBar, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        add(tableStop, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
        add(tableRoute, new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
    }
}
