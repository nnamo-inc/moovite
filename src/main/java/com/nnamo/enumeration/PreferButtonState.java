package com.nnamo.enumeration;

public enum PreferButtonState {

    ADDMODE(0, "Add"),
    REMOVEMODE(1, "Remove");

    private final int value;
    private final String text;

    PreferButtonState(int value, String text) {
        this.value = value;
        this.text = text;
    }
}
