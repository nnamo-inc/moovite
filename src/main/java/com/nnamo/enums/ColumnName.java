package com.nnamo.enums;

public enum ColumnName {
    LINEA("Linea"),
    DIREZIONE("Direzione"),
    ORARIO("Orario"),
    STATO("Stato"),
    MINUTIRIMAMENTI("Minuti Rimanenti"),
    POSTIDISPONIBILI("Posti Disponibili"),
    NOME("Nome"),
    CODICE("Codice"),
    TIPO("Tipo");


    String name;

    ColumnName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
