package org.week2.item10;
import java.util.Objects;
/**
 * 잘못된 상속 관계 예시
 * Learning Point: equals의 대칭성과 추이성 위배 케이스
 */
public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = Objects.requireNonNull(color);
    }

    // 잘못된 equals 구현 - 대칭성 위배!
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) return false;
        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}