package com.nnamo.view.components;

import com.nnamo.interfaces.PanelModeButtonClickListener;
import com.nnamo.view.customcomponents.PanelModeButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ButtonPanel extends JPanel {

    private final ArrayList <PanelModeButton> buttons = new ArrayList<>();
    private List <JPanel> panels = new ArrayList<>();

    public ButtonPanel(List<JPanel> panels) {
        this.panels = panels;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (JPanel panel : panels) {
            PanelModeButton button = new PanelModeButton(panel.getClass().getSimpleName(), panel);
            buttons.add(button);
            add(button);
        }
    }

    public void setPanelModeButtonClickListener(PanelModeButtonClickListener listener) {
        for (PanelModeButton button : buttons) {
            button.setPanelModeButtonClickListener(listener);
        }
    }

    public ArrayList<PanelModeButton> getButtons() {
        return buttons;
    }

    public List<JPanel> getPanels() {
        return panels;
    }
}
