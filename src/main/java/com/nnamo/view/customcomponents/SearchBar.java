package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchBar extends JPanel {

    private final JTextField field = new JTextField(20);
    private final JLabel label = new JLabel("Search:");
    private final JButton button = new JButton("X");
    private final ArrayList<SearchBarListener> listeners = new ArrayList<>();

    // CONSTRUCTOR //
    public SearchBar() {
        super();
        setLayout(new GridBagLayout());
        add(label, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.WEST).setWeight(0, 1.0).setInsets(5, 5, 5, 5));
        add(field, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));
        button.setBackground(CustomColor.RED);
        add(button, new GbcCustom().setPosition(2, 0).setAnchor(GridBagConstraints.EAST).setWeight(0, 0.0).setInsets(5, 5, 5, 5));

        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyListeners(field.getText());
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.setText("");
                notifyListeners("");
            }
        });
    }

    // LISTENER HANDLE //
    public void addSearchListener(SearchBarListener listener) {
        listeners.add(listener);
    }

    public void removeSearchListener(SearchBarListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String searchText) {
        for (SearchBarListener listener : listeners) {
            listener.onSearch(searchText);
        }
    }

    // GETTERS AND SETTERS //
    public void setField(String field) {
        this.field.setText(field);
    }

    public JTextField getField() {
        return field;
    }

    public String getFieldText() {
        return field.getText();
    }

    public JButton getButton() {
        return button;
    }
}