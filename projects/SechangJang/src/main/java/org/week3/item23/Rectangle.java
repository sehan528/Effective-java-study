package org.week3.item23;

/**
 * 사각형을 표현하는 구체 클래스
 */
public class Rectangle extends Figure {
    private final double length;
    private final double width;

    public Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }

    @Override
    String getDescription() {
        return String.format("사각형 (길이: %.2f, 너비: %.2f)", length, width);
    }
}