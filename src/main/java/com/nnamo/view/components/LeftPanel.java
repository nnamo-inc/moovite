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
import com.nnamo.view.customcomponents.SwitchBar;

public class LeftPanel extends JPanel {
    JPanel modularPanel = new JPanel(new BorderLayout());
    SearchPanel searchPanel = new SearchPanel();
    PreferPanel preferPanel = new PreferPanel();
    SwitchBar onlineSwitchButton = new SwitchBar();

    // TODO: change to FavoritePanel
    SwitchBar favoritePanel = new SwitchBar();

    ButtonPanel buttonPanel = new ButtonPanel(new LinkedHashMap<>() {
        {
            put(searchPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/search_small.png"))));
            put(preferPanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/sidebar_stop_small.png"))));
            put(onlineSwitchButton,
                    new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/online_settings_small.png"))));
            put(favoritePanel, new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/favorite_small.png"))));
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

    public void setRealtimeStatus(RealtimeStatus status) {
        this.onlineSwitchButton.setStatus(status);
        System.out.println(this.onlineSwitchButton);
    }

    // SETTERS FOR LISTENERS //
    public void setButtonPanelGenericButtonBehaviour(LeftPanelGenericButtonBehaviour listener) {
        this.buttonPanel.setGenericButtonBehaviour(listener);
    }

    public void setButtonPanelPreferButtonBehaviour(LeftPanelPreferButtonBehaviour listener) {
        this.buttonPanel.setPreferButtonBehaviour(listener);
    }

    public void setSearchStopRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchStopRowClickBehaviour(listener);
    }

    public void setSearchRouteRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchRouteRowClickBehaviour(listener);
    }

    public void setFavStopRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.preferPanel.setFavStopRowClickBehaviour(behaviour);
    }

    public void setFavRouteRowClickBehaviour(TableRowClickBehaviour behaviour) {
        this.preferPanel.setFavRouteRowClickBehaviour(behaviour);
    }

    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        this.onlineSwitchButton.addSwitchListener(listener);
    }

    public void setFavStopBehaviour(FavoriteBehaviour behaviour) {
        this.preferPanel.setFavStopBehaviour(behaviour);
    }
}
