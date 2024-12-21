package chapter4.item23.ex1;

//태그 달린 클래스 사용시 비효율 적인 코드들이 많고 필요없는 필드를 초기화 해야함
class Figure {
    enum Shape {RECTANGLE, CIRCLE}

    // 태그 필드 -- 현재 모양
    final Shape shape;

    //사각형일 때만 쓰이는 필드
    double length;
    double width;

    //원일때 사용되는 필드
    double radius;

    //원 생성자
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    //사각형 생성자
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * length;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError();
        }
    }
}
