package com.nnamo.view.customcomponents;

import com.nnamo.enums.DataType;
import com.nnamo.interfaces.FavoriteBehaviour;
import com.nnamo.utils.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Custom {@link JButton} that allows users to add or remove items from their favorites.
 * It updates its text and background color based on the favorite status of the item.
 *
 * @author Riccardo Finocchiaro
 * @author Davide Galilei
 * @see JButton
 */
public class CustomPreferButton extends JButton {

    // ATTRIBUTES //
    private boolean favorite = false;
    private FavoriteBehaviour favoriteBehaviour;
    private String itemId;
    private String itemName;
    private DataType mode;

    private final ImageIcon favoriteIcon;
    private final ImageIcon unfavoriteIcon;

    // CONSTRUCTOR //

    /**
     * Creates a {@link CustomPreferButton} with a default text and background {@link CustomColor}.
     * The {@link JButton} is initially disabled and not visible until an item is selected.
     *
     * @see CustomPreferButton
     * @see JButton
     * @see CustomColor
     */
    public CustomPreferButton() {
        super();

        ImageIcon originalFavoriteIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/panels/favorite_large.png")));
        ImageIcon originalUnfavoriteIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/panels/unfavorite_large.png")));

        favoriteIcon = scaleIcon(originalFavoriteIcon, 32, 32);
        unfavoriteIcon = scaleIcon(originalUnfavoriteIcon, 32, 32);

        setText("Clicca su una riga della tabella per attivare il bottone");
        setIcon(null);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(10);
        setBackground(CustomColor.GREEN);
        setFont(getFont().deriveFont(Font.PLAIN, 14f));
        setPreferredSize(new Dimension(getPreferredSize().width, 50));
        initListener();
    }

    private ImageIcon scaleIcon(ImageIcon originalIcon, int width, int height) {
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    // METHODS //

    /**
     * Updates the {@link JButton} text and background {@link CustomColor} based on the favorite status of the item.
     * If the item is a favorite, it shows "Remove from Favorites" and sets the background to red.
     * If not, it shows "Add to Favorites" and sets the background {@link CustomColor} to green.
     *
     * @param isFavorite true if the item is a favorite, false otherwise
     * @see JButton
     * @see CustomColor
     */
    public void update(boolean isFavorite) {
        setEnabled(true);
        this.favorite = isFavorite;
        if (isFavorite) {
            setText("Rimuovi " + itemName + " dai Preferiti");
            setIcon(favoriteIcon);
            setBackground(CustomColor.RED);
        } else {
            setText("Aggiungi " + itemName + " ai Preferiti");
            setIcon(unfavoriteIcon);
            setBackground(CustomColor.GREEN);
        }
    }

    // BEHAVIOUR METHODS //
    private void initListener() {
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                if (favorite) {
                    favoriteBehaviour.removeFavorite(itemId, mode);
                    favorite = false;
                    setText("Aggiungi " + itemName + " ai Preferiti");
                    setIcon(unfavoriteIcon);
                    setBackground(CustomColor.GREEN);

                } else {
                    favoriteBehaviour.addFavorite(itemId, mode);
                    favorite = true;
                    setText("Rimuovi " + itemName + " dai Preferiti");
                    setIcon(favoriteIcon);
                    setBackground(CustomColor.RED);
                }

            }
        });

    }

    /**
     * Sets the behavior to execute when the {@link JButton} is clicked.
     *
     * @param behaviour the implementation of {@link FavoriteBehaviour} that defines the behavior for favorite actions.
     * @see FavoriteBehaviour
     * @see JButton
     */
    public void setFavBehaviour(FavoriteBehaviour behaviour) {
        if (behaviour != null) {
            this.favoriteBehaviour = behaviour;
        }
    }

    /**
     * Sets the data type for the item associated with this {@link JButton}.
     *
     * @param mode set the {@link DataType} of the item, which can be one of the types defined in the {@link DataType} enum.
     *             This is used to determine the type of data the item represents, such as a route, user, or other entities.
     * @see DataType
     * @see JButton
     */
    public void setDataType(DataType mode) {
        this.mode = mode;
    }

    /**
     * Sets the ID and name of the item associated with this {@link JButton}.
     * This is used to uniquely identify the item that the {@link JButton} is currently representing.
     *
     * @param itemId the unique identifier for the item
     * @see JButton
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Sets the name of the item associated with this {@link JButton}.
     * This is used to display the name of the item in the button text.
     *
     * @param itemName the name of the item
     * @see JButton
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
