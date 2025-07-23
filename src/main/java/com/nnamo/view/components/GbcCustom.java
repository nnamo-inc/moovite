package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;

public class GbcCustom extends GridBagConstraints {

    public GbcCustom setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    public GbcCustom setFill(int fill) {
        this.fill = fill;
        return this;
    }

    public GbcCustom setHeight(int height) {
        this.gridheight = height;
        return this;
    }

    public GbcCustom setWidth(int width) {
        this.gridwidth = width;
        return this;
    }

    public GbcCustom setPosition(int x, int y) {
        this.gridx = x;
        this.gridy = y;
        return this;
    }

    public GbcCustom setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GbcCustom setIpad(int ipadx, int ipady) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }

    public GbcCustom setWeight(double weightX, double weightY) {
        this.weightx = weightX;
        this.weighty = weightY;
        return this;
    }
}