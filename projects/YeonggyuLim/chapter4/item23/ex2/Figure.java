package chapter4.item23.ex2;

abstract class Figure {
    //태그 달린 클래스를 계층 구조로
    abstract double area();
}

class Circle extends Figure {
    final double radius;
    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}

class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}
