package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;

public class SearchBar extends JPanel {

    private JTextField searchField = new JTextField(20);
    private JLabel searchLabel = new JLabel("Search:");
    private JButton searchButton = new JButton("X");

    // CONSTRUCTOR //
    public SearchBar() {
        super();
        setLayout(new GridBagLayout());
        add(searchLabel, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.WEST).setWeight(0, 1.0).setInsets(5, 5, 5, 5));
        add(searchField, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));
        add(searchButton, new GbcCustom().setPosition(2, 0).setAnchor(GridBagConstraints.EAST).setWeight(0, 0.0).setInsets(5, 5, 5, 5));
    }

    // GETTERS AND SETTERS //
    public JTextField getSearchField() {
        return searchField;
    }

    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    public JLabel getSearchLabel() {
        return searchLabel;
    }

    public void setSearchLabel(JLabel searchLabel) {
        this.searchLabel = searchLabel;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(JButton searchButton) {
        this.searchButton = searchButton;
    }
}
