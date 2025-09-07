package com.nnamo.view.painter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.jxmapviewer.JXMapViewer;

import com.nnamo.enums.IconSize;

/**
 * A specialized {@link PositionPainter} for rendering static vehicle positions on a map.
 * This class extends the base functionality to include specific icons for offline vehicles.
 *
 * @see PositionPainter
 * @see JXMapViewer
 * @see IconSize
 *
 * @author Samuele Lombardi
 */
public class StaticPositionPainter extends PositionPainter {
    public StaticPositionPainter(JXMapViewer map) throws IOException {
        super(map);

        super.icons.put(IconSize.EXTRA_SMALL, ImageIO
                        .read(Objects.requireNonNull(
                                        getClass().getResourceAsStream(
                                                        "/images/vehicles/vehicle_extra_small_offline.png"))));
        super.icons.put(IconSize.SMALL, ImageIO
                        .read(Objects.requireNonNull(
                                        getClass().getResourceAsStream(
                                                        "/images/vehicles/vehicle_small_offline.png"))));
        super.getIcons().put(IconSize.MEDIUM, ImageIO
                        .read(Objects.requireNonNull(
                                        getClass().getResourceAsStream(
                                                        "/images/vehicles/vehicle_flame_medium_offline.png"))));
        // icons.put(Sizes.LARGE, ImageIO
        // .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_medium.png"))));
    }
}
