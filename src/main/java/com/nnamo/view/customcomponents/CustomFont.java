package com.nnamo.view.customcomponents;

import com.nnamo.interfaces.ButtonPanelBehaviour;

import javax.swing.*;
import java.awt.*;

/**
 * Custom {@link Font} utilized across the application, specifically set to value that are used in multiple components.
 *
 * @author Riccardo Finocchiaro
 * @see JPanel
 * @see JButton
 * @see Icon
 * @see CustomButtonPanel
 * @see ButtonPanelBehaviour
 */
public class CustomFont extends Font {
    public CustomFont() {
        super("Arial", Font.BOLD, 15);
    }
}
