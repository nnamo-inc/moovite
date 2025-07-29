package com.nnamo.view.components;

import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.view.customcomponents.CustomTable;
import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.SearchBar;
import com.nnamo.view.customcomponents.SearchResults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {

    SearchBar searchBar = new SearchBar();
<<<<<<< HEAD
    SearchResults searchResults = new SearchResults();
=======
    CustomTable tableStop = new CustomTable(new String[] {"Colonna 1", "Colonna 2", "Colonna 3"}, true);
    CustomTable tableRoute = new CustomTable(new String[] {"Colonna 1", "Colonna 2", "Colonna 3"}, true);
    SearchBarListener searchBarListener;
>>>>>>> 523782e6de9a1cb4c1d9e3893fe5cb4115d67653

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
<<<<<<< HEAD
        add(searchBar, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        add(searchResults, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(10, 10, 10, 10));
    }
=======
        add(searchBar, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
        add(tableStop, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
        add(tableRoute, new GbcCustom().setPosition(0, 2).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
>>>>>>> 523782e6de9a1cb4c1d9e3893fe5cb4115d67653

    }
}
