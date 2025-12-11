package com.sgi.backend.model;

public enum TipoJornada {
    MANANA("Mañana"),
    TARDE("Tarde"),
    UNICA("Única");

    private final String displayName;

    TipoJornada(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
