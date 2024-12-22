package org.week3.item16.good;

public class ColorPoint {
    // private 중첩 클래스 활용
    private static class Point {
        public double x;
        public double y;
    }

    private final Point point;
    private final String color;

    public ColorPoint(double x, double y, String color) {
        point = new Point();
        point.x = x;
        point.y = y;
        this.color = color;
    }

    public double getX() { return point.x; }
    public double getY() { return point.y; }
    public String getColor() { return color; }
}