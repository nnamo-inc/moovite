package com.nnamo.view.customcomponents;

import javax.swing.*;

public class SearchResults extends JScrollPane {
    // CONSTRUCTOR //
    public SearchResults() {
        super();
        setBorder(BorderFactory.createEmptyBorder());
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        getViewport().setBackground(UIManager.getColor("Panel.background"));
    }

    // METHODS //
    public void clear() {
        getViewport().removeAll();
    }
}
