package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.RouteDirection;
import com.nnamo.models.StopModel;

public class LeftPanel extends JPanel {

    // ATTRIBUTES //
    private SearchPanel searchPanel;
    private PreferPanel preferPanel;
    private StatisticsPanel statsPanel;
    private SettingsPanel settingsPanel;

    private JPanel modularPanel;
    private ButtonPanel buttonPanel;

    // CONSTRUCTOR //
    public LeftPanel() {
        setLayout(new BorderLayout());

        buttonPanel = new ButtonPanel(new LinkedHashMap<>() {
            {
                searchPanel = new SearchPanel();
                put(searchPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/search_small.png"))));

                preferPanel = new PreferPanel();
                put(preferPanel,
                        new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/favorite_small.png"))));

                statsPanel = new StatisticsPanel();
                put(statsPanel,
                        new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/statistics_small.png"))));

                settingsPanel = new SettingsPanel();
                put(settingsPanel,
                        new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/setting_small.png"))));
            }
        });

        modularPanel = new JPanel(new BorderLayout());
        add(modularPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);
    }

    // METHODS //
    public void updateModularPanel(JPanel panel, boolean isVisible) {
        for (Component comp : modularPanel.getComponents()) {
            comp.setVisible(false);
        }

        modularPanel.removeAll();

        if (isVisible) {
            modularPanel.add(panel, BorderLayout.CENTER);
            panel.setVisible(true);
        } else {
            panel.setVisible(false);
        }

        modularPanel.revalidate();
        modularPanel.repaint();
    }

    // GETTERS AND SETTERS //
    public SearchPanel getSearchPanel() {
        return this.searchPanel;
    }

    public ButtonPanel getButtonPanel() {
        return this.buttonPanel;
    }

    public PreferPanel getPreferPanel() {
        return preferPanel;
    }

    public StatisticsPanel getStatisticsPanel() {
        return statsPanel;
    }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.settingsPanel.setRealtimeStatus(status);
    }

    // BEHAVIOURS METHODS //
    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        searchPanel.setTableRowClickBehaviour(listener);
        preferPanel.setTableRowClickBehaviour(listener);
    }

    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        settingsPanel.setLogoutBehaviour(behaviour);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.settingsPanel.setRealtimeSwitchListener(listener);
    }

    // TRANSIT METHODS //
    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        preferPanel.updateFavStopTable(stop, updateMode);
    }

    public void updateFavRouteTable(List<RouteDirection> route, UpdateMode updateMode) {
        preferPanel.updateFavRouteTable(route, updateMode);
    }

    public void initPreferPanelPreferTable(List<StopModel> stops, List<RouteDirection> routes) {
        preferPanel.initPreferTable(stops, routes);
    }

}
