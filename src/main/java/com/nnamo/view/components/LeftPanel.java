package com.nnamo.view.components;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.view.StopPainter;
import com.nnamo.view.customcomponents.SwitchBar;

public class LeftPanel extends JPanel {
    JPanel modularPanel = new JPanel(new BorderLayout());
    SearchPanel searchPanel = new SearchPanel();
    PreferPanel preferPanel = new PreferPanel();
    SwitchBar onlineSwitchButton = new SwitchBar();
    ButtonPanel buttonPanel = new ButtonPanel(new HashMap<>() {
        {
            put(searchPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/stop_medium.png"))));
            put(preferPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/stop_medium.png"))));
            put(onlineSwitchButton, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/stop_medium.png"))));
        }
    });

    public LeftPanel() {
        setLayout(new BorderLayout());

        add(modularPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);

    }

    public void updateModularPanel(JPanel panel, boolean isVisible) {
        for (Component comp : modularPanel.getComponents()) { comp.setVisible(false); }
        modularPanel.removeAll();
        if (isVisible) {
            modularPanel.add(panel, BorderLayout.CENTER);
            panel.setVisible(true);
        }
        else {
            panel.setVisible(false);
        }
        modularPanel.revalidate();
        modularPanel.repaint();
    }

    public SearchPanel getSearchPanel() {
        return this.searchPanel;
    }

    public ButtonPanel getButtonPanel() {
        return this.buttonPanel;
    }

    public void setSearchStopTableClickListener(TableRowClickListener listener) {
        this.searchPanel.setSearchStopTableClickListener(listener);
    }

    public void setSearchRouteTableClickListener(TableRowClickListener listener) {
        this.searchPanel.setSearchRouteTableClickListener(listener);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.onlineSwitchButton.addSwitchListener(listener);
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.onlineSwitchButton.setStatus(status);
        System.out.println(this.onlineSwitchButton);
    }
}
