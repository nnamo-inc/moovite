package com.nnamo.view.customcomponents;

import com.nnamo.utils.CustomColor;
import junit.framework.TestCase;
public class CustomPreferButtonTest extends TestCase {

    CustomPreferButton button = new CustomPreferButton();
    public void testUpdateTrue() {
        button.update(true);
        assertEquals("<html><p>Rimuovi</p><p>Test Item</p><p>dai Preferiti</p></html>", button.getText());
        assertEquals(CustomColor.RED, button.getBackground());
    }

    public void testUpdateFalse() {
        button.update(false);
        assertEquals("<html><p>Aggiungi</p><p>Test Item</p><p>ai Preferiti</p></html>", button.getText());
        assertEquals(CustomColor.GREEN, button.getBackground());
    }
}