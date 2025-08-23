package com.nnamo.view.components;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.LogoutBehaviour;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomRoundedBorder;
import com.nnamo.view.customcomponents.CustomSwitchBar;
import com.nnamo.view.customcomponents.CustomTitle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Custom {@link JPanel} that provides a settings interface, including online status control and logout functionality.
 *
 * @author Riccardo Finocchiaro
 * @author Samuele Lombardi
 * @author Davide Galilei
 *
 * @see JPanel
 * @see CustomSwitchBar
 * @see StatisticsPanel.CustomLogout
 * @see RealtimeStatus
 * @see SwitchBarListener
 * @see LogoutBehaviour
 */
public class SettingsPanel extends JPanel {

    private CustomSwitchBar switchBar;
    private StatisticsPanel.CustomLogout logout;
    private CustomTitle title;

    // CONSTRUCTOR //
    /**
     * Creates a {@link SettingsPanel} with sections for online status and logout.
     * Initializes the layout, titles, and custom components for user interaction.
     *
     * @see JPanel
     * @see CustomSwitchBar
     * @see StatisticsPanel.CustomLogout
     */
    public SettingsPanel() {
        super();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Title
        createTitleBar();
        // Switch Bar for online status
        createSwitchBar();
        // Logout section
        createLogout();
        // Add vertical glue to fill remaining space
        add(Box.createVerticalGlue(), new CustomGbc().setPosition(0, 3).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.VERTICAL));

        setVisible(false);
    }

    // METHODS //
    private void createTitleBar() {
        title = new CustomTitle("Settings");
        add(title, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));
    }

    private void createSwitchBar() {
        JPanel switchPanel = new JPanel();
        switchPanel.setLayout(new GridBagLayout());
        switchPanel.setBorder(new CustomRoundedBorder(15));

        JLabel routeLabel = new JLabel("Online Status");
        routeLabel.setFont(new CustomFont());
        switchPanel.add(routeLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.NONE)
                .setInsets(2, 5, 2, 5));

        switchBar = new CustomSwitchBar();
        switchPanel.add(switchBar, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 2, 5));

        add(switchPanel, new CustomGbc().setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 2, 5));
    }

    private void createLogout() {
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new GridBagLayout());
        logoutPanel.setBorder(new CustomRoundedBorder(15));

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new CustomFont());
        logoutPanel.add(logoutLabel, new CustomGbc()
                .setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.NONE)
                .setInsets(2, 5, 2, 5));

        logout = new StatisticsPanel.CustomLogout();
        logoutPanel.add(logout, new CustomGbc()
                .setPosition(0, 1)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 2, 5));

        add(logoutPanel, new CustomGbc()
                .setPosition(0, 3)
                .setAnchor(GridBagConstraints.NORTH)
                .setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL)
                .setInsets(2, 5, 2, 5));
    }
    /**
     * Sets the current {@link RealtimeStatus} for the online status switch bar.
     *
     * @param status the {@link RealtimeStatus} to display and control
     *
     * @see RealtimeStatus
     * @see CustomSwitchBar
     */
    public void setRealtimeStatus(RealtimeStatus status) {
        switchBar.setStatus(status);
    }

    // BEHAVIOR //
    /**
     * Sets the listener for changes to the real-time status switch.
     *
     * @param listener the implementation of {@link SwitchBarListener} to handle switch events
     *
     * @see SwitchBarListener
     * @see CustomSwitchBar
     */
    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        switchBar.addSwitchListener(listener);
    }

    /**
     * Sets the behavior to execute when the logout action is triggered.
     *
     * @param behaviour the implementation of {@link LogoutBehaviour} to handle logout events
     *
     * @see LogoutBehaviour
     * @see StatisticsPanel.CustomLogout
     */
    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        logout.setLogoutBehaviour(behaviour);
    }
}
