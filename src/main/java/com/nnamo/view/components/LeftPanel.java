package com.nnamo.view.components;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.interfaces.TableRowClickListener;
import com.nnamo.view.customcomponents.SwitchBar;

public class LeftPanel extends JPanel {
    SearchPanel searchPanel = new SearchPanel();
    SwitchBar onlineSwitchButton = new SwitchBar();

    public LeftPanel() {
        setLayout(new BorderLayout());

        // Set fixed sizes
        searchPanel.setPreferredSize(new Dimension(200, 350));
        onlineSwitchButton.setPreferredSize(new Dimension(200, 50));

        add(searchPanel, BorderLayout.CENTER);
        add(onlineSwitchButton, BorderLayout.SOUTH);
    }

    public SearchPanel getSearchPanel() {
        return this.searchPanel;
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
