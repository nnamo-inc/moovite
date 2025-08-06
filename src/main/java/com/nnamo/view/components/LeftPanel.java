package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.LeftPanelGenericButtonBehaviour;
import com.nnamo.interfaces.LeftPanelPreferButtonBehaviour;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.interfaces.TableRowClickBehaviour;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.SwitchBar;

public class LeftPanel extends JPanel {
    JPanel modularPanel = new JPanel(new BorderLayout());
    SearchPanel searchPanel = new SearchPanel();
    PreferPanel preferPanel = new PreferPanel();
    SwitchBar onlineSwitchButton = new SwitchBar();
    ButtonPanel buttonPanel = new ButtonPanel(new LinkedHashMap<>() {
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

    public void initPreferPanelPreferTable(List<StopModel> stops, List<RouteModel> routes) {
        preferPanel.initPreferTable(stops, routes);
    }

    public void addPreferPanelStopTable(StopModel stop) {
        preferPanel.addStop(stop);
    }

    public void removePreferPanelStopTable(StopModel route) {
        preferPanel.removeStop(route);
    }

    public void addPreferPanelRouteTable(RouteModel route) {
        preferPanel.addRoute(route);
    }

    public void removePreferPanelRouteTable(RouteModel route) {
        preferPanel.removeRoute(route);
    }



    public SearchPanel getSearchPanel() {
        return this.searchPanel;
    }

    public ButtonPanel getButtonPanel() {
        return this.buttonPanel;
    }

    public void setButtonPanelGenericButtonBehaviour(LeftPanelGenericButtonBehaviour listener) {
        this.buttonPanel.setGenericButtonBehaviour(listener);
    }

    public void setButtonPanelPreferButtonBehaviour(LeftPanelPreferButtonBehaviour listener) {
        this.buttonPanel.setPreferButtonBehaviour(listener);
    }

    public void setSearchStopTableClickListener(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchStopTableClickListener(listener);
    }

    public void setSearchRouteTableClickListener(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchRouteTableClickListener(listener);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.onlineSwitchButton.addSwitchListener(listener);
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.onlineSwitchButton.setStatus(status);
        System.out.println(this.onlineSwitchButton);
    }

    public PreferPanel getPreferPanel() {
        return preferPanel;
    }
}
