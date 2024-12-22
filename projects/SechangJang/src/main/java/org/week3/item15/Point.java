package org.week3.item15;

/**
 * 잘못된 캡슐화와 올바른 캡슐화를 보여주는 예제
 */
class BadPoint {
    // 안티패턴: public 필드 노출
    public double x;
    public double y;
}

class GoodPoint {
    // 올바른 캡슐화: private 필드와 접근자 메서드
    private double x;
    private double y;

    public GoodPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    
    // 변경이 필요한 경우 검증 로직 추가 가능
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
            throw new IllegalArgumentException("좌표값은 숫자여야 합니다.");
        }
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
