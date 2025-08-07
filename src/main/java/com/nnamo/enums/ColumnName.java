package com.nnamo.enums;

public enum ColumnName {
    LINEA("Linea"),
    CODICE("Codice"),
    NOME("Nome"),
    ID("ID"),
    ORARIO("Orario"),
    LATITUDINE("Latitudine");

    String name;

    ColumnName(String name) {
        this.name = name;
    }
}
