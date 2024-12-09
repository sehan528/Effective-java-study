package chapter3.item10.ex4;

import java.awt.*;
import java.util.Objects;

//구체 클래스에서 상속받는 대신 컴포지션(포인트를 필드로 갖음)을 사용해서 equals 규약을 지킴
//추상 클래스를 활용해도 equals 규약을 지키면서 값을 추가 할 수 있음
public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }


    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint)) {
            return false;
        }

        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}
