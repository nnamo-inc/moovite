package com.nnamo.view.customcomponents;

import com.nnamo.enums.DataType;
import com.nnamo.utils.CustomColor;
import junit.framework.TestCase;
public class CustomPreferButtonTest extends TestCase {

    CustomPreferButton button = new CustomPreferButton();

    public void testUpdateTrueOnStop() {
        button.setItemName("fermata");
        button.update(true);
        assertEquals("Rimuovi fermata dai Preferiti", button.getText());
        assertEquals(CustomColor.RED, button.getBackground());
    }

    public void testUpdateFalseOnStop() {
        button.setItemName("fermata");
        button.update(false);
        assertEquals("Aggiungi fermata ai Preferiti", button.getText());
        assertEquals(CustomColor.GREEN, button.getBackground());
    }

    public void testUpdateTrueOnRoute() {
        button.setItemName("linea");
        button.update(true);
        assertEquals("Rimuovi linea dai Preferiti", button.getText());
        assertEquals(CustomColor.RED, button.getBackground());
    }

    public void testUpdateFalseOnRoute() {
        button.setItemName("linea");
        button.update(false);
        assertEquals("Aggiungi linea ai Preferiti", button.getText());
        assertEquals(CustomColor.GREEN, button.getBackground());
    }
}