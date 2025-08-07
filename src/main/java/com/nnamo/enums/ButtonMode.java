package com.nnamo.enums;

public enum ButtonMode {

    ADD("Add"),
    REMOVE("Remove"),
    BOTH("Add/Remove");

    String text;

    ButtonMode(String text) {
        this.text = text;
    }
}
