package com.nnamo.view.components;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.LogoutBehaviour;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomSwitchBar;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private CustomSwitchBar switchBar = new CustomSwitchBar();
    private StatisticsPanel.CustomLogout logout = new StatisticsPanel.CustomLogout();

    // CONSTRUCTOR //
    public SettingsPanel() {
        super();
        setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, new CustomGbc().setPosition(0, 0)
                .setAnchor(GridBagConstraints.NORTH)
                .setInsets(5, 5, 5, 5));

        TitledBorder onlineStatus = new TitledBorder(new LineBorder(Color.lightGray, 2), "Online Status");
        switchBar.setBorder(
                BorderFactory.createCompoundBorder(onlineStatus, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(switchBar, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.NORTH).setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));

        TitledBorder logOut = new TitledBorder(new LineBorder(Color.lightGray, 2), "Logout");
        logout.setBorder(BorderFactory.createCompoundBorder(logOut, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(logout, new CustomGbc().setPosition(0, 2).setAnchor(GridBagConstraints.NORTH).setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));

        add(Box.createVerticalGlue(), new CustomGbc().setPosition(0, 3).setWeight(1.0, 1.0)
                .setFill(GridBagConstraints.VERTICAL));

        setVisible(false);
    }

    // METHODS //
    public void setRealtimeStatus(RealtimeStatus status) {
        switchBar.setStatus(status);
    }

    // BEHAVIOR //
    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        switchBar.addSwitchListener(listener);
    }

    public void setLogoutBehaviour(LogoutBehaviour behaviour) {
        logout.setLogoutBehaviour(behaviour);
    }
}
