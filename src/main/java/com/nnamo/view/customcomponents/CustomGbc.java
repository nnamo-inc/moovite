package com.nnamo.view.customcomponents;

import java.awt.*;

/**
 *Custom {@link GridBagConstraints} class that provides a more manageable way to set the properties of a {@link GridBagConstraints} object.
 *
 * This class allows you to chain method calls to set the properties of the {@link GridBagConstraints} object, making it easier to read and maintain, with a simil Builder Pattern.
 *
 * @author Riccardo Finocchiaro
 *
 * @see GridBagConstraints
 */
public class CustomGbc extends GridBagConstraints {


    /**
     * set the anchor of the {@link GridBagConstraints} object.
     *
     * @param anchor the anchor value to set
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
     * set the fill of the {@link GridBagConstraints} object.
     *
     * @param fill the fill value to set
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setFill(int fill) {
        this.fill = fill;
        return this;
    }

    /**
     * set the height of the {@link GridBagConstraints} object.
     *
     * @param height the height value to set
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setHeight(int height) {
        this.gridheight = height;
        return this;
    }

    /**
     * set the width of the {@link GridBagConstraints} object.
     *
     * @param width the width value to set
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setWidth(int width) {
        this.gridwidth = width;
        return this;
    }

    /**
     * set the position of the {@link GridBagConstraints} object.
     *
     * @param x the x value to set
     * @param y the y value to set
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setPosition(int x, int y) {
        this.gridx = x;
        this.gridy = y;
        return this;
    }

    /**
     * set the insets of the {@link GridBagConstraints} object.
     *
     * @param top the top inset value
     * @param left the left inset value
     * @param bottom the bottom inset value
     * @param right the right inset value
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    /**
     * set the ipad of the {@link GridBagConstraints} object.
     *
     * @param ipadx the horizontal internal padding
     * @param ipady the vertical internal padding
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }

    /**
     * set the weight of the {@link GridBagConstraints} object.
     *
     * @param weightX the weight in the x direction
     * @param weightY the weight in the y direction
     *
     * @return CustomGbc
     *
     * @see GridBagConstraints
     */
    public CustomGbc setWeight(double weightX, double weightY) {
        this.weightx = weightX;
        this.weighty = weightY;
        return this;
    }
}