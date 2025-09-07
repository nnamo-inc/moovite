package com.nnamo.view.customcomponents;

import com.nnamo.enums.RouteType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class CustomRadioButtonsPanel extends JPanel {

    private final JLabel filterLabel;
    private final ButtonGroup buttonGroup;
    private final ArrayList<JRadioButton> radioButtons;

    public CustomRadioButtonsPanel(ArrayList<JRadioButton> radioButtons, String itemName) {

        setLayout(new GridBagLayout());

        filterLabel = new JLabel(itemName + " filters: ");
        add(filterLabel, new CustomGbc()
                .setPosition(0, 0)
                .setWeight(0.0, 1.0)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(0, 5, 0, 0));

        JPanel radioButtonPanel = new JPanel();
        this.radioButtons = radioButtons;
        radioButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.buttonGroup = new ButtonGroup();
        for (JRadioButton rb : radioButtons) {
            buttonGroup.add(rb);
            radioButtonPanel.add(rb);
        }

        JScrollPane scrollPane = new JScrollPane(radioButtonPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(5, 6));
        scrollPane.setWheelScrollingEnabled(true);

        scrollPane.setMinimumSize(new Dimension(0, 25));
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setOpaque(false);


        add(scrollPane, new CustomGbc()
                .setPosition(1, 0)
                .setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.BOTH)
                .setInsets(4, 4, 4, 5));

        setBorder(new CustomRoundedBorder(20, 0.3f));
        setOpaque(false);

    }

    // METHODS BEHAVIOUR //
    private RouteType getSelectedRouteType() {
        for (JRadioButton rb : radioButtons) {
            if (rb.isSelected()) {
                return RouteType.fromString(rb.getText());
            }
        }
        return RouteType.ALL; // Default type if none selected
    }

    // GETTERS AND SETTERS //

    public ArrayList<JRadioButton> getRadioButtons() {
        return radioButtons;
    }
}
