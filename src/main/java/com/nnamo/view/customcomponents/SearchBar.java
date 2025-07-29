package com.nnamo.view.customcomponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchBar extends JPanel {

    private JTextField searchField = new JTextField(20);
    private final JLabel searchLabel = new JLabel("Search:");
    private JButton searchButton = new JButton("X");

    // CONSTRUCTOR //
    public SearchBar() {
        super();
        setLayout(new GridBagLayout());
        add(searchLabel, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.WEST).setWeight(0, 1.0).setInsets(5, 5, 5, 5));
        add(searchField, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));
        add(searchButton, new GbcCustom().setPosition(2, 0).setAnchor(GridBagConstraints.EAST).setWeight(0, 0.0).setInsets(5, 5, 5, 5));
    }

    // METHODS //
    public void changeButtonAction(ActionListener actionListener) {
        for (ActionListener listener : searchButton.getActionListeners()) {
            searchButton.removeActionListener(listener); // Remove existing action listeners
        }
        searchButton.addActionListener(actionListener); // Add the new action listener
    }
    // GETTERS AND SETTERS //
    public JTextField getSearchField() {
        return searchField;
    }

    public String getText() {
        return searchField.getText();
    }

    public void setText(String text) {
        searchField.setText(text);
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}