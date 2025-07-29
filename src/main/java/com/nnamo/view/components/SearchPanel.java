package com.nnamo.view.components;

import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.SearchBar;
import com.nnamo.view.customcomponents.SearchResults;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    SearchBar searchBar = new SearchBar();
    SearchResults searchResults = new SearchResults();

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        add(searchBar, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setInsets(10, 10, 10, 10));
        add(searchResults, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.BOTH).setWeight(1.0, 1.0).setInsets(10, 10, 10, 10));
    }

    // METHODS //
    private JPanel newLine(JLabel label, Component component) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(component);
        return panel;
    }
}
