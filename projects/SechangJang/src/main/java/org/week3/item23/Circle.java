package org.week3.item23;

/**
 * 원을 표현하는 구체 클래스
 */
public class Circle extends Figure {
    private final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }

    @Override
    String getDescription() {
        return String.format("원 (반지름: %.2f)", radius);
    }
}