package com.nnamo.view.painter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;

import com.nnamo.enums.IconSize;

public class RealtimePositionPainter extends PositionPainter {
    public RealtimePositionPainter(JXMapViewer map) throws IOException {
        super(map);

        this.getIcons().put(IconSize.EXTRA_SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_extra_small.png"))));
        this.getIcons().put(IconSize.SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_small.png"))));
        this.getIcons().put(IconSize.MEDIUM, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_flame_medium.png"))));
        // icons.put(Sizes.LARGE, ImageIO
        // .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_medium.png"))));
    }
}
