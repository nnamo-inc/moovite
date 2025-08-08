package com.nnamo.view.components;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;
import com.nnamo.view.customcomponents.CustomGbc;
import com.nnamo.view.customcomponents.CustomLogout;
import com.nnamo.view.customcomponents.CustomSwitchBar;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingPanel extends JPanel {

    CustomSwitchBar switchBar = new CustomSwitchBar();
    CustomLogout logout = new CustomLogout();

    // CONSTRUCTOR //
    public SettingPanel() {
        super();
        setLayout(new GridBagLayout());

        TitledBorder onlineStatus = new TitledBorder(new LineBorder(Color.lightGray, 2), "Online Status");
        switchBar.setBorder(BorderFactory.createCompoundBorder(onlineStatus, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(switchBar, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));

        TitledBorder logOut = new TitledBorder(new LineBorder(Color.lightGray, 2), "Logout");
        logout.setBorder(BorderFactory.createCompoundBorder(logOut, BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        add(logout, new CustomGbc().setPosition(0, 1).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 0.0)
                .setFill(GridBagConstraints.HORIZONTAL).setInsets(2, 5, 2, 5));


        setVisible(false);
    }

    // METHODS //
    public void setRealtimeStatus(RealtimeStatus status) {
        switchBar.setStatus(status);
        System.out.println(switchBar);
    }

    // BEHAVIOR //
    public void setRealtimeSwitchListener(SwitchBarListener listener) {
        switchBar.addSwitchListener(listener);
    }
}
