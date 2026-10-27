package com.rosales.vetgroom.model;

public enum Role {
    PET_OWNER("Pet Owner"),
    VETERINARIAN("Veterinarian"),
    GROOMER("Groomer"),
    ADMINISTRATOR("Clinic Owner / Administrator");

    private final String label;

    Role(String label) { this.label = label; }

    public String getLabel() { return label; }
}
