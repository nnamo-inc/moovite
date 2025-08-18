package com.nnamo.interfaces;

import javax.swing.*;

/**
 * Interface that defines the behavior for a click on a {@link com.nnamo.view.customcomponents.CustomButtonPanel} of the {@link com.nnamo.view.components.ButtonPanel}.
 *
 * @see com.nnamo.view.customcomponents.CustomButtonPanel
 * @see com.nnamo.view.components.ButtonPanel
 */
public interface ButtonPanelBehaviour {

    /**
     * Method to be executed when the {@link com.nnamo.view.customcomponents.CustomButtonPanel} of the of the {@link com.nnamo.view.components.ButtonPanel} is clicked.
     *
     * @param panel the JPanel that will be affected by the click
     *
     * @see com.nnamo.view.customcomponents.CustomButtonPanel
     * @see com.nnamo.view.components.ButtonPanel
     * @see javax.swing.JPanel
     */
    void onButtonPanelClick(JPanel panel);

}
