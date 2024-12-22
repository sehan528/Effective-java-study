package org.week3.item22;

/**
 * 올바른 방법: 관련 클래스에 상수 포함
 */
public class Calculator {
    public static final double DEFAULT_PRECISION = 0.0001;
    private final double precision;

    public Calculator() {
        this(DEFAULT_PRECISION);
    }

    public Calculator(double precision) {
        this.precision = precision;
    }

    public double calculate(double value) {
        if (Math.abs(value) < precision) {
            return 0.0;
        }
        return value;
    }
}