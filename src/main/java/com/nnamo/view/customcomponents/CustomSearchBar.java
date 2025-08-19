package com.nnamo.view.customcomponents;

import com.nnamo.enums.RouteType;
import com.nnamo.interfaces.SearchBarListener;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Custom {@link JPanel} that provides a search bar with a {@link JTextField}, a clear {@link JButton}, and a set of {@link JRadioButton} for filtering search results.
 *
 * This component allows users to input search queries, clear the input, and select a route type for filtering results.
 *
 * @author Riccardo Finocchiaro
 * @author Davide Galilei
 *
 * @see JPanel
 * @see JTextField
 * @see JButton
 * @see JRadioButton
 */
public class CustomSearchBar extends JPanel {

    // ATTRIBUTES //
    private ButtonGroup buttonGroup;
    private ArrayList<JRadioButton> radioButtons;
    private JTextField field;
    private JLabel Label;
    private JButton Button;
    private ArrayList<SearchBarListener> listeners;

    // CONSTRUCTOR //
    /**
     * Creates a {@link CustomSearchBar} with a {@link JLabel} for the {@link JTextField}, a {@link JTextField} for input, and a {@link JButton} to clear the input.
     * The search bar is designed to be used in applications where users need to search for items or data.
     *
     * @see CustomSearchBar
     * @see GridBagLayout
     * @see JLabel
     * @see JTextField
     * @see JButton
     */
    public CustomSearchBar() {
        super();
        // Set layout
        setLayout(new GridBagLayout());

        // Title Label
        Label = new JLabel("Search:");
        add(Label, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.WEST)
                .setWeight(0.0, 1.0).setInsets(5, 5, 5, 5));

        // Search Field
        field = new JTextField(20);
        add(field, new CustomGbc().setPosition(2, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.0).setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));

        // Clear Button
        Button = new JButton("X");
        Button.setBackground(CustomColor.RED);
        add(Button, new CustomGbc().setPosition(3, 0).setAnchor(GridBagConstraints.EAST)
                .setWeight(0.0, 0.0).setInsets(5, 5, 5, 5));

        initListener();
    }

    /**
     * Creates a {@link CustomSearchBar} using the default constructor with a set of {@link JRadioButton} for filtering route search results based on {@link RouteType}.
     * The radio buttons allow users to select a specific route type for their search.
     *
     * @param radioButtons an {@link ArrayList} of {@link JRadioButton} representing different route types.
     *
     * @see CustomSearchBar
     * @see JRadioButton
     * @see RouteType
     * @see JRadioButton
     * @see ArrayList
     */
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
    /**
     * Initializes the action listeners for the search bar components.
     * The {@link JTextField} listens for text input, and the {@link JButton} listens for clicks to clear the input.
     * When the text field is updated or the button is clicked, it notifies all registered listeners with the current search text and selected route type.
     *
     * @see ActionListener
     * @see JTextField
     * @see JButton
     */
    public void initListener() {
        listeners = new ArrayList<>();
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notifyListeners(field.getText());
            }
        });

        Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field.setText("");
                notifyListeners("");
            }
        });
    }

    /**
     * Adds a {@link SearchBarListener} to the list of listeners that will be notified when the search text changes.
     *
     * @param listener the {@link SearchBarListener} to add
     */
    public void addSearchListener(SearchBarListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String searchText) {
        for (SearchBarListener listener : listeners) {
            listener.onSearch(searchText, getSelectedRouteType());
        }
    }
    // GETTERS AND SETTERS //
    /**
     * Gets the {@link JTextField} used for input in the {@link CustomSearchBar}.
     *
     * @return the {@link JTextField} of the search bar
     *
     * @see JTextField
     * @see CustomSearchBar
     */
    public JTextField getField() {
        return field;
    }

    /**
     * Sets the text of the {@link JTextField} in the {@link CustomSearchBar}.
     *
     * @param field the text to set in the {@link JTextField}
     *
     * @see JTextField
     * @see CustomSearchBar
     */
    public void setField(String field) {
        this.field.setText(field);
    }

    /**
     * Gets the text currently entered in the {@link JTextField} of the {@link CustomSearchBar}.
     *
     * @return the text from the {@link JTextField}
     *
     * @see JTextField
     * @see CustomSearchBar
     */
    public String getFieldText() {
        return field.getText();
    }

    /**
     * Gets the {@link JButton} used to clear the input in the {@link CustomSearchBar}.
     *
     * @return the {@link JButton} of the search bar
     *
     * @see JButton
     * @see CustomSearchBar
     */
    public JButton getButton() {
        return Button;
    }
}