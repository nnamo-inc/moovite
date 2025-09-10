package com.nnamo.view.customcomponents;

import com.nnamo.utils.CustomColor;
import junit.framework.TestCase;

import java.awt.*;

public class CustomPreferButtonTest extends TestCase {

    private CustomPreferButton button;

    private boolean isHeadless() {
        return GraphicsEnvironment.isHeadless();
    }

    public void testUpdateTrueOnStop() {
        if (isHeadless()) return;
        button = new CustomPreferButton();
        button.setItemName("fermata");
        button.update(true);
        assertEquals("Rimuovi fermata dai Preferiti", button.getText());
        assertEquals(CustomColor.RED, button.getBackground());
    }

    public void testUpdateFalseOnStop() {
        if (isHeadless()) return;
        button = new CustomPreferButton();
        button.setItemName("fermata");
        button.update(false);
        assertEquals("Aggiungi fermata ai Preferiti", button.getText());
        assertEquals(CustomColor.GREEN, button.getBackground());
    }

    public void testUpdateTrueOnRoute() {
        if (isHeadless()) return;
        button = new CustomPreferButton();
        button.setItemName("linea");
        button.update(true);
        assertEquals("Rimuovi linea dai Preferiti", button.getText());
        assertEquals(CustomColor.RED, button.getBackground());
    }

    public void testUpdateFalseOnRoute() {
        if (isHeadless()) return;
        button = new CustomPreferButton();
        button.setItemName("linea");
        button.update(false);
        assertEquals("Aggiungi linea ai Preferiti", button.getText());
        assertEquals(CustomColor.GREEN, button.getBackground());
    }
}
