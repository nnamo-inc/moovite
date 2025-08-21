package com.nnamo.view.customcomponents;

import com.nnamo.utils.CustomColor;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import java.awt.*;

public class RoundedTitledBorder implements Border {
    private int arc;

    public RoundedTitledBorder(int arc) {
        super();
        this.arc = arc;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(CustomColor.LIGHTGRAY);
        g2.setStroke(new BasicStroke(1.7f));
        g2.drawRoundRect(x+2, y+2, width-5, height-3, arc, arc);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    };

    @Override
    public boolean isBorderOpaque() {
        return false;
    };
}