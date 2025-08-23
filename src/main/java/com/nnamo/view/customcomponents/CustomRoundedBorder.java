package com.nnamo.view.customcomponents;

import com.nnamo.utils.CustomColor;

import javax.swing.border.Border;
import java.awt.*;

public class CustomRoundedBorder implements Border {
    private int arc;
    private float thickness;

    public CustomRoundedBorder(int arc, float thickness) {
        super();
        this.arc = arc;
        this.thickness = thickness;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(CustomColor.LIGHTGRAY);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, width-2, height-2, arc, arc);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    };

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}