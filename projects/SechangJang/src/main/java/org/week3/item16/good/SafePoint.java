package org.week3.item16.good;

public class SafePoint {
    private double x;
    private double y;

    public SafePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { 
        validateCoordinate(x);
        this.x = x; 
    }
    
    public void setY(double y) { 
        validateCoordinate(y);
        this.y = y; 
    }

    private void validateCoordinate(double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("좌표값이 유효하지 않습니다.");
        }
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}