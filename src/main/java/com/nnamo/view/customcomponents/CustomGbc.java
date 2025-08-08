package com.nnamo.view.customcomponents;

import java.awt.*;

public class CustomGbc extends GridBagConstraints {

    public CustomGbc setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    public CustomGbc setFill(int fill) {
        this.fill = fill;
        return this;
    }

    public CustomGbc setHeight(int height) {
        this.gridheight = height;
        return this;
    }

    public CustomGbc setWidth(int width) {
        this.gridwidth = width;
        return this;
    }

    public CustomGbc setPosition(int x, int y) {
        this.gridx = x;
        this.gridy = y;
        return this;
    }

    public CustomGbc setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public CustomGbc setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }

    public CustomGbc setWeight(double weightX, double weightY) {
        this.weightx = weightX;
        this.weighty = weightY;
        return this;
    }
}