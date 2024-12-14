package org.week2.item10;

import java.util.Objects;

/**
 * 컴포지션을 사용한 올바른 구현 예시
 * Learning Point: 상속 대신 컴포지션을 사용하여 equals 규약 준수
 */
public class ColorPointComposition {
    private final Point point;
    private final Color color;

    public ColorPointComposition(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    // 뷰 메서드
    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPointComposition)) return false;
        ColorPointComposition cp = (ColorPointComposition) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, color);
    }
}