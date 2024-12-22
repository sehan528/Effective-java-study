package org.week3.item22;

/**
 * 올바른 방법: 열거 타입 사용
 */
public enum PaymentType {
    CREDIT_CARD(3.5),
    DEBIT_CARD(2.5),
    BANK_TRANSFER(1.0);

    private final double feePercentage;

    PaymentType(double feePercentage) {
        this.feePercentage = feePercentage;
    }

    public double calculateFee(double amount) {
        return amount * (feePercentage / 100.0);
    }
}