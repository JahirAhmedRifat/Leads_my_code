package com.leads.microcube.base;

public enum ActionType {

    CREATE(0),
    UPDATE(1),
    DELETE(2);

    private final int value;

    ActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Optional: get enum from int value
    public static ActionType fromValue(int value) {
        for (ActionType status : ActionType.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
