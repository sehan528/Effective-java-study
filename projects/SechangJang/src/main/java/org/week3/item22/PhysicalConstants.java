package org.week3.item22;

/**
 * 올바른 방법: 유틸리티 클래스로 상수 관리
 */
public final class PhysicalConstants {
    private PhysicalConstants() {} // 인스턴스화 방지

    public static final double PI = Math.PI;
    public static final double E = Math.E;
    public static final double GOLDEN_RATIO = 1.618033988749895;

    public static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
}