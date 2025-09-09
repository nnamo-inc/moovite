package com.nnamo.view.customcomponents;

import com.nnamo.enums.RealtimeStatus;
import com.nnamo.interfaces.SwitchBarListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Custom {@link JPanel} that provides a switch bar with a {@link JLabel} and a {@link JButton} to toggle the status between online and offline.
 * It notifies registered {@link SwitchBarListener} when the status changes.
 *
 * @author Samuele Lombardi
 * @see JPanel
 * @see RealtimeStatus
 * @see SwitchBarListener
 */
public class CustomSwitchBar extends JPanel {

    // ATTRIBUTES //
    private final JLabel label;
    private final JButton button;
    private final ArrayList<SwitchBarListener> listeners = new ArrayList<>();

    private RealtimeStatus status;

    // CONSTRUCTORS //

    /**
     * Creates a {@link CustomSwitchBar} with a default status of {@link RealtimeStatus#OFFLINE}.
     * The bar contains a {@link JLabel} to display the current status and a {@link JButton} to toggle the status.
     *
     * @see RealtimeStatus
     * @see JLabel
     * @see JButton
     */
    public CustomSwitchBar() {
        super();

        this.status = RealtimeStatus.OFFLINE;
        this.label = new JLabel("Status: offline");

        setLayout(new GridBagLayout());
        // Label
        add(label, new CustomGbc().setPosition(0, 0).setAnchor(GridBagConstraints.CENTER).setWeight(1.0, 1.0)
                .setInsets(2, 5, 2, 5));

        // Button
        button = new JButton("Toggle");
        button.setMinimumSize(new Dimension(50, Integer.MAX_VALUE));
        add(button, new CustomGbc().setPosition(0, 1).setFill(GridBagConstraints.HORIZONTAL).setWeight(1.0, 1.0)
                .setWeight(1.0, 1.0).setInsets(2, 5, 2, 5));

        handleListeners();
    }

    // METHODS //

    /**
     * Toggles the status between {@link RealtimeStatus#ONLINE} and {@link RealtimeStatus#OFFLINE}.
     * If the current status is {@link RealtimeStatus#ONLINE}, it changes to {@link RealtimeStatus#OFFLINE} and vice versa.
     *
     * @see RealtimeStatus
     */
    public void toggleStatus() {
        this.setStatus((status == RealtimeStatus.ONLINE)
                ? RealtimeStatus.OFFLINE
                : RealtimeStatus.ONLINE);
    }

    /**
     * Sets the status of the switch bar to the specified {@link RealtimeStatus}.
     * Updates the label text accordingly and notifies all registered {@link SwitchBarListener} of the status change.
     *
     * @param status The new status to set.
     * @see RealtimeStatus
     * @see SwitchBarListener
     */
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

    // BEHAVIOUR METHODS //
    private void handleListeners() {
        button.addActionListener(new ActionListener() {
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

    /**
     * Adds a {@link SwitchBarListener} to the list of listeners.
     * The listener will be notified when the status of the switch bar changes.
     *
     * @param listener
     * @see SwitchBarListener
     */
    public void addSwitchListener(SwitchBarListener listener) {
        listeners.add(listener);
    }
}
