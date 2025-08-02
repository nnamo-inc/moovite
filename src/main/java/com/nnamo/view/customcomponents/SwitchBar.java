package com.nnamo.view.customcomponents;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SwitchBar extends JPanel {

    private final JLabel switchLabel;
    private final JButton switchButton = new JButton("Toggle");
    private final ArrayList<SwitchBarListener> listeners = new ArrayList<>();

    private RealtimeStatus status;

    // CONSTRUCTOR //
    public SwitchBar() {
        super();
        this.status = RealtimeStatus.OFFLINE;
        this.switchLabel = new JLabel("Status: offline");
        handleLayout();
        handleActionListeners();
    }

    public SwitchBar(RealtimeStatus initialStatus) {
        this();
        setStatus(initialStatus);
    }

    private void handleLayout() {
        setLayout(new GridBagLayout());
        add(switchLabel, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.WEST).setWeight(0, 1.0)
                .setInsets(5, 5, 5, 5));
        add(switchButton, new GbcCustom().setPosition(1, 0).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 0.0)
                .setWeight(1.0, 1.0).setInsets(5, 5, 5, 5));
    }

    private void handleActionListeners() {
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleStatus();
            }
        });
    }

    private void notifyListeners(RealtimeStatus status) {
        for (SwitchBarListener listener : listeners) {
            listener.onSwitch(status);
        }
    }

    public void setStatus(RealtimeStatus status) {
        this.status = status;

        String initialText = "Status: ";
        switch (status) {
            case OFFLINE:
                System.out.println("prova1");
                this.switchLabel.setText(initialText + "offline");
                break;
            case ONLINE:
                System.out.println("prova2");
                this.switchLabel.setText(initialText + "online");
                break;
        }
        notifyListeners(status);
    }

    public void toggleStatus() {
        this.setStatus((status == RealtimeStatus.ONLINE)
                ? RealtimeStatus.OFFLINE
                : RealtimeStatus.ONLINE);
    }

    // METHODS //
    public void addSearchListener(SwitchBarListener listener) {
        listeners.add(listener);
    }

    public void removeSearchListener(SwitchBarListener listener) {
        listeners.remove(listener);
    }
}
