package com.leads.microcube.tax.taxRebatePolicy;

public enum RebateType {
    TAXABLE_INCOME_REBATE(1),
    INVESTMENT_AMOUNT_REBATE(2),
    MAX_INVESTMENT_REBATE(3);

    private final int serialNo;

    RebateType(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getSerialNo() {
        return serialNo;
    }

    // Helper to get enum from serialNo
    public static RebateType fromSerial(int serialNo) {
        for (RebateType type : values()) {
            if (type.serialNo == serialNo) return type;
        }
        throw new IllegalArgumentException("Unknown rebate serial: " + serialNo);
    }
}

