package com.nnamo.view.painter;

import com.nnamo.enums.IconSize;
import org.jxmapviewer.JXMapViewer;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * A specialized {@link PositionPainter} for rendering real-time vehicle positions on a map.
 * This class extends the base functionality to include specific icons for vehicles.
 *
 * @author Samuele Lombardi
 * @see PositionPainter
 * @see JXMapViewer
 * @see IconSize
 */
public class RealtimePositionPainter extends PositionPainter {
    public RealtimePositionPainter(JXMapViewer map) throws IOException {
        super(map);

        this.getIcons().put(IconSize.EXTRA_SMALL, ImageIO
                .read(Objects
                        .requireNonNull(getClass().getResourceAsStream("/images/vehicles/vehicle_extra_small.png"))));
        this.getIcons().put(IconSize.SMALL, ImageIO
                .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicles/vehicle_small.png"))));
        this.getIcons().put(IconSize.MEDIUM, ImageIO
                .read(Objects
                        .requireNonNull(getClass().getResourceAsStream("/images/vehicles/vehicle_flame_medium.png"))));
        // icons.put(Sizes.LARGE, ImageIO
        // .read(Objects.requireNonNull(getClass().getResourceAsStream("/images/vehicle_medium.png"))));
    }
}
