package com.nnamo.view.customcomponents;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SwitchBar extends JPanel {

    private final JLabel label;
    private final JButton button = new JButton("Toggle");
    private final ArrayList<SwitchBarListener> listeners = new ArrayList<>();

    private RealtimeStatus status;

    // CONSTRUCTORS //
    public SwitchBar() {
        super();
        this.status = RealtimeStatus.OFFLINE;
        this.label = new JLabel("Status: offline");
        handleLayout();
        handleActionListeners();
    }

    public SwitchBar(RealtimeStatus initialStatus) {
        this();
        setStatus(initialStatus);
    }

    // METHODS //
    private void handleLayout() {
        setLayout(new GridBagLayout());
        add(label, new GbcCustom().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0)
                .setInsets(2, 5, 2, 5));
        button.setMinimumSize(new Dimension(50, Integer.MAX_VALUE));
        add(button, new GbcCustom().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 1.0)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));
        setVisible(false);
    }

    private void handleActionListeners() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleStatus();
            }
        });
    }

    public void toggleStatus() {
        this.setStatus((status == RealtimeStatus.ONLINE)
                ? RealtimeStatus.OFFLINE
                : RealtimeStatus.ONLINE);
    }

    public void setStatus(RealtimeStatus status) {
        this.status = status;

        String initialText = "Status: ";
        switch (status) {
            case OFFLINE:
                this.label.setText(initialText + "offline");
                break;
            case ONLINE:
                this.label.setText(initialText + "online");
                break;
        }
        notifyListeners(status);
    }

    // LISTENER HANDLE //
    private void notifyListeners(RealtimeStatus status) {
        for (SwitchBarListener listener : listeners) {
            listener.onSwitch(status);
        }
    }

    public void addSwitchListener(SwitchBarListener listener) {
        listeners.add(listener);
    }

    public void removeSwitchListener(SwitchBarListener listener) {
        listeners.remove(listener);
    }
}
