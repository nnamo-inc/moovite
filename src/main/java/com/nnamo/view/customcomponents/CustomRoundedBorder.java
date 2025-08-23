package com.nnamo.view.customcomponents;

import com.nnamo.utils.CustomColor;

import javax.swing.border.Border;
import java.awt.*;

public class CustomRoundedBorder implements Border {
    private int arc;

    public CustomRoundedBorder(int arc) {
        super();
        this.arc = arc;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(CustomColor.LIGHTGRAY);
        g2.setStroke(new BasicStroke(1.7f));
        g2.drawRoundRect(x + 9, y + 3, width - 18, height - 8, arc, arc);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(10, 10, 10, 10);
    };

    @Override
    public boolean isBorderOpaque() {
        return false;
    };
}