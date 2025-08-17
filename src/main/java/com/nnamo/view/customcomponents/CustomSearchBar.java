package com.nnamo.view.customcomponents;

import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CustomSearchBar extends JPanel {

    // ATTRIBUTES //
    private final GridBagLayout layout;
    private ButtonGroup buttonGroup;
    private ArrayList<JRadioButton> radioButtons;
    private final JTextField field;
    private final JLabel label;
    private final JButton button;
    private ArrayList<SearchBarListener> listeners;

    // CONSTRUCTOR //
    public CustomSearchBar() {
        super();
        // Set layout
        layout = new GridBagLayout();
        setLayout(layout);

        // Title Label
        label = new JLabel("Search:");
        add(label, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.WEST)
                .setWeight(0.0, 1.0).setInsets(5, 5, 5, 5));

        // Search Field
        field = new JTextField(20);
        add(field, new CustomGbc().setPosition(2, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));

        // Clear Button
        button = new JButton("X");
        button.setBackground(CustomColor.RED);
        add(button, new CustomGbc().setPosition(3, 0).setAnchor(GridBagConstraints.EAST)
                .setWeight(0.0, 0.0).setInsets(5, 5, 5, 5));

        initListener();
    }

    public CustomSearchBar(ArrayList<JRadioButton> radioButtons) {
        this();

        ArrayList<JRadioButton> buttons = new ArrayList<>();
        for (RouteType type : RouteType.values()) {
            JRadioButton button = new JRadioButton(type.getValue());
            button.setSelected(type == RouteType.ALL); // Default selected type
            buttons.add(button);
        }

        this.radioButtons = radioButtons;
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.setLayout(new BoxLayout(radioButtonPanel, BoxLayout.X_AXIS));

        this.buttonGroup = new ButtonGroup();
        for (JRadioButton rb : radioButtons) {
            buttonGroup.add(rb);
            radioButtonPanel.add(rb);
            // on button click notify listeners
            rb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    notifyListeners(field.getText());
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane(radioButtonPanel);
        scrollPane.setMinimumSize(new Dimension(Integer.MAX_VALUE, 40));

        add(scrollPane, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 1.0).setWidth(4).setInsets(5, 5, 5, 5));
    }

    // METHODS //
    private RouteType getSelectedRouteType() {
        for (JRadioButton rb : radioButtons) {
            if (rb.isSelected()) {
                return RouteType.fromString(rb.getText());
            }
        }
        return RouteType.ALL; // Default type if none selected
    }

    // METHODS BEHAVIOUR //
    public void initListener() {
        listeners = new ArrayList<>();
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

    public void addSearchListener(SearchBarListener listener) {
        listeners.add(listener);
    }

    public void removeSearchListener(SearchBarListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String searchText) {
        for (SearchBarListener listener : listeners) {
            listener.onSearch(searchText, getSelectedRouteType());
        }
    }
    // GETTERS AND SETTERS //
    public JTextField getField() {
        return field;
    }

    public void setField(String field) {
        this.field.setText(field);
    }

    public String getFieldText() {
        return field.getText();
    }

    public JButton getButton() {
        return button;
    }
}