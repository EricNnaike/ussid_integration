package com.example.mfb_ussd_process_flow.enums;

public enum StatusConstant {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String abbreviate;

    private StatusConstant(String abbreviate) {
        this.abbreviate = abbreviate;
    }

    public String getAbbreviate() {
        return abbreviate;
    }
}
