package org.week3.item23;


/**
 * 태그 달린 클래스 - 안티패턴
 * 이런 방식은 피해야 합니다!
 */
public class TaggedFigure {
    enum Shape { RECTANGLE, CIRCLE }

    // 태그 필드
    final Shape shape;

    // 사각형 전용 필드
    double length;
    double width;

    // 원 전용 필드
    double radius;

    // 원용 생성자
    public TaggedFigure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사각형용 생성자
    public TaggedFigure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}