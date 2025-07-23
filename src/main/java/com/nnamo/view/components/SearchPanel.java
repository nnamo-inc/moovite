package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    JTextField searchField = new JTextField(20);
    JLabel searchLabel = new JLabel("Search:");

    // CONSTRUCTOR //
    public SearchPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(newLine(searchLabel, searchField));
        setVisible(true);
        GridBagConstraints gbc = new GbcCustom().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0).setInsets(10, 10, 10, 10);
    }

    // METHODS //
    private JPanel newLine(JLabel label, Component component) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(component);
        return panel;
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
}
