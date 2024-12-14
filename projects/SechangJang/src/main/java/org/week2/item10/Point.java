package org.week2.item10;

/**
 * 기본적인 2차원 좌표를 나타내는 클래스
 * Learning Point: equals의 기본 구현
 */
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        // 1. == 연산자를 사용해 자기 자신의 참조인지 확인
        if (this == o) return true;
        
        // 2. instanceof 연산자로 입력이 올바른 타입인지 확인
        if (!(o instanceof Point)) return false;
        
        // 3. 입력을 올바른 타입으로 형변환
        Point point = (Point) o;
        
        // 4. 핵심 필드들이 모두 일치하는지 검사
        return x == point.x && y == point.y;
    }

    // equals를 재정의할 때는 hashCode도 반드시 재정의
    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
