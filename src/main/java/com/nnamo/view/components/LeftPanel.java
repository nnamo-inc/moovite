package com.nnamo.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.enums.UpdateMode;
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

    public LeftPanel() {
        setLayout(new BorderLayout());

        add(modularPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.WEST);

    }

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

    public void initPreferPanelPreferTable(List<StopModel> stops, List<RouteModel> routes) {
        preferPanel.initPreferTable(stops, routes);
    }

    public void updateFavStopTable(StopModel stop, UpdateMode updateMode) {
        preferPanel.updateFavStopTable(stop, updateMode);
    }

    public void updateFavRouteTable(RouteModel route, UpdateMode updateMode) {
        preferPanel.updateFavRouteTable(route, updateMode);
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

    public void setSearchStopRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setSearchStopRowClickBehaviour(listener);
    }

    public void setRouteStopRowClickBehaviour(TableRowClickBehaviour listener) {
        this.searchPanel.setRouteStopRowClickBehaviour(listener);
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

    public void setRealtimeStatus(RealtimeStatus status) {
        this.onlineSwitchButton.setStatus(status);
        System.out.println(this.onlineSwitchButton);
    }

    public PreferPanel getPreferPanel() {
        return preferPanel;
    }
}
