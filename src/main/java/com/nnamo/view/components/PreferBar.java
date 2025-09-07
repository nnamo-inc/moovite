package com.nnamo.view.components;

import com.nnamo.enums.DataType;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomPreferButton;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link JPanel} that provides a bar for managing the favorite status of a stop or route.
 *
 * @author Riccardo Finocchiaro
 * @see JPanel
 * @see CustomPreferButton
 * @see FavoriteBehaviour
 * @see DataType
 */
public class PreferBar extends JPanel {

    // ATTRIBUTES //
    private final CustomPreferButton preferButton;

    // CONSTRUCTOR //

    /**
     * Creates a {@link PreferBar} with a {@link CustomPreferButton} for managing favorite status.
     *
     * @see JPanel
     * @see CustomPreferButton
     */
    public PreferBar() {
        super(new GridBagLayout());

        preferButton = new CustomPreferButton();
        add(preferButton, new CustomGbc().setPosition(0, 0).setFill(GridBagConstraints.HORIZONTAL)
                .setWeight(1.0, 0.1).setInsets(2, 5, 2, 5));

        setVisible(false);
    }

    // METHODS //

    /**
     * Makes the {@link PreferBar} visible.
     *
     * @see JPanel
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Hides the {@link PreferBar}.
     *
     * @see JPanel
     */
    public void close() {
        setVisible(false);
    }

    /**
     * Updates the {@link CustomPreferButton} with the specified item ID, favorite status, and data type.
     * Adjusts the button label and state accordingly.
     *
     * @param itemId   the ID of the item (stop or route)
     * @param isFav    whether the item is currently marked as favorite
     * @param dataType the {@link DataType} of the item (STOP or ROUTE)
     * @see CustomPreferButton
     * @see DataType
     */
    public void updatePreferButton(String itemId, boolean isFav, DataType dataType) {
        switch (dataType) {
            case STOP:
                preferButton.setItemName("fermata");
                break;
            case ROUTE:
                preferButton.setItemName("linea");
                break;
        }
        preferButton.setDataType(dataType);
        preferButton.setItemId(itemId);
        preferButton.update(isFav);
    }

    // BEHAVIOUR METHODS //

    /**
     * Sets the behavior to execute when the favorite button is toggled.
     *
     * @param favoriteBehaviour the implementation of {@link FavoriteBehaviour} to handle favorite actions
     * @see FavoriteBehaviour
     * @see CustomPreferButton
     */
    public void setGeneralFavBehaviour(FavoriteBehaviour favoriteBehaviour) {
        preferButton.setFavBehaviour(favoriteBehaviour);

    }
}
