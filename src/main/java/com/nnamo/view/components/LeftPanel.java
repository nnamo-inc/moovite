package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.view.customcomponents.SwitchBar;

public class LeftPanel extends JPanel {
    JPanel modularPanel = new JPanel(new BorderLayout());
    SearchPanel searchPanel = new SearchPanel();
    SwitchBar onlineSwitchButton = new SwitchBar();
    ButtonPanel buttonPanel = new ButtonPanel(new ArrayList<>() {
        {
            add(searchPanel);
            add(onlineSwitchButton);
        }
    });

    public LeftPanel() {
        setLayout(new BorderLayout());

        add(modularPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);

    }

    public void updateModularPanel(JPanel panel, boolean isVisible) {
        modularPanel.removeAll();
        if (isVisible) {
            modularPanel.add(panel, BorderLayout.CENTER);
            panel.setVisible(true);
        }
        else { panel.setVisible(false); }
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
