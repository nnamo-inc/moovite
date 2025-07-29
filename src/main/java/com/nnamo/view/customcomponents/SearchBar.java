package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.SearchBarListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchBar extends JPanel {

    private JTextField searchField = new JTextField(20);
    private final JLabel searchLabel = new JLabel("Search:");
    private JButton searchButton = new JButton("X");
    private ArrayList<SearchBarListener> listeners = new ArrayList<>();

    // CONSTRUCTOR //
    public SearchBar() {
        super();
        setLayout(new GridBagLayout());
        add(searchLabel, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.WEST).setWeight(0, 1.0).setInsets(5, 5, 5, 5));
        add(searchField, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));
        add(searchButton, new GbcCustom().setPosition(2, 0).setAnchor(GridBagConstraints.EAST).setWeight(0, 0.0).setInsets(5, 5, 5, 5));

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyListeners(searchField.getText());
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                notifyListeners("");
            }
        });
    }

    private void notifyListeners(String searchText) {
        for (SearchBarListener listener : listeners) {
            listener.onSearch(searchText);
        }
    }

    // METHODS //
    public void addSearchListener(SearchBarListener listener) {
        listeners.add(listener);
    }

    public void removeSearchListener(SearchBarListener listener) {
        listeners.remove(listener);
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