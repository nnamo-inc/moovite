package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
import com.nnamo.interfaces.*;
import com.nnamo.models.RouteModel;
import com.nnamo.models.StopModel;
import com.nnamo.view.customcomponents.CustomSwitchBar;

public class LeftPanel extends JPanel {
    private JPanel modularPanel = new JPanel(new BorderLayout());
    private SearchPanel searchPanel = new SearchPanel();
    private PreferPanel preferPanel = new PreferPanel();
    private StatisticsPanel statsPanel = new StatisticsPanel();
    private SettingPanel settingPanel = new SettingPanel();

    ButtonPanel buttonPanel = new ButtonPanel(new LinkedHashMap<>() {
        {
            put(searchPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/search_small.png"))));
            put(preferPanel,
                    new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/favorite_small.png"))));
            put(statsPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/statistics_small.png"))));
            put(settingPanel,
                    new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/setting_small.png"))));
        }
    });

    // CONSTRUCTOR //
    public LeftPanel() {
        setLayout(new BorderLayout());

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

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        preferPanel.updateFavStopTable(stop, updateMode);
    }

    public void updateFavRouteTable(RouteModel route, UpdateMode updateMode) {
        preferPanel.updateFavRouteTable(route, updateMode);
    }

    public void updatePreferRouteButton(Boolean isFavorite, String routeNumber) {
        this.preferPanel.updatePreferRouteButton(isFavorite, routeNumber);
    }

    public void updatePreferStopButton(Boolean isFavorite, String routeId) {
        this.preferPanel.updatePreferStopButton(isFavorite, routeId);
    }

    public void initPreferPanelPreferTable(List<StopModel> stops, List<RouteModel> routes) {
        preferPanel.initPreferTable(stops, routes);
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

    public StatisticsPanel getStatisticsPanel() { return statsPanel; }

    public void setRealtimeStatus(RealtimeStatus status) {
        this.settingPanel.setRealtimeStatus(status);
    }

    // BEHAVIOUR //

    public void setTableCheckIsFavBehaviour(TableCheckIsFavBehaviour listener) {
        this.searchPanel.setTableCheckIsFavBehaviour(listener);
        this.preferPanel.setTableCheckIsFavBehaviour(listener);
    }

    public void setTableRowClickBehaviour(TableRowClickBehaviour listener) {
        searchPanel.setTableRowClickBehaviour(listener);
        preferPanel.setTableRowClickBehaviour(listener);
    }


    // Stop panel behaviour //
    public void setButtonPanelGeneralBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    public void setButtonPanelPreferBehaviour(ButtonPanelBehaviour listener) {
        this.buttonPanel.setButtonPanelBehaviour(listener);
    }

    // Search panel behaviour //
    public void setSearchStopRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchStopRowClickBehaviour(listener);
    }

    public void setSearchRouteRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchRouteRowClickBehaviour(listener);
    }

    // Prefer panel behaviour //
    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.preferPanel.setFavStopRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.preferPanel.setFavRouteRowClickBehaviour(behaviour);
    }

    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        this.preferPanel.setFavStopBehaviour(behaviour);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.settingPanel.setRealtimeSwitchListener(listener);
    }

    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        settingPanel.setLogoutBehaviour(behaviour);
    }

}
