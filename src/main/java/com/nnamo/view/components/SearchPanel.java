package com.nnamo.view.components;

import com.nnamo.view.customcomponents.GbcCustom;
import com.nnamo.view.customcomponents.SearchBar;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    SearchBar searchBar = new SearchBar();

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new GridBagLayout());
        add(searchBar, new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10));
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
