package com.leads.microcube.base;


public enum ApprovalStatus {
    PENDING(0),
    APPROVED(1),
    DECLINED(2),
    SEND_BACK(3),
    DISCARD(4),
    DEFAULT(5);

    private final int value;

    ApprovalStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Optional: get enum from int value
    public static ApprovalStatus fromValue(int value) {
        for (ApprovalStatus status : ApprovalStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
