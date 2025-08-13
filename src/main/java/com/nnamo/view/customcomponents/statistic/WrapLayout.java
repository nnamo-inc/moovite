package com.nnamo.view.customcomponents.statistic;

import javax.swing.*;
import java.awt.*;

/**
 * A layout manager that arranges components in a left-to-right flow, similar to FlowLayout,
 * but wraps components to new rows when they don't fit horizontally.
 * This behaves similarly to CSS flexbox with flex-wrap enabled.
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Calculates the layout size based on the target container and whether to use preferred sizes.
     * This method properly handles wrapping within scroll panes and other containers.
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            Container container = target;

            // Find the actual available width by traversing up the component hierarchy
            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }
            targetWidth = container.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            // Calculate layout by simulating component placement
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    // Check if component needs to wrap to new row
                    if (rowWidth + d.width > maxWidth && rowWidth > 0) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = d.width;
                        rowHeight = d.height;
                    } else {
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
            }

            // Add the last row
            addRow(dim, rowWidth, rowHeight);

            dim.width = Math.max(dim.width, maxWidth);
            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            // Special handling for scroll panes
            Container scrollPane = target.getParent();
            if (scrollPane instanceof JViewport) {
                if (scrollPane.getParent() instanceof JScrollPane) {
                    dim.width -= (hgap + 1);
                }
            }

            return dim;
        }
    }

    /**
     * Adds a row's dimensions to the overall layout dimension.
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}
