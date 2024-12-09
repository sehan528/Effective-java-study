package chapter3.item10.ex2;

import java.awt.*;

public class ColorPoint extends Point{
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    //cp의 비교 로직에서 equals 가 cp 일때 비교 하지만 p는 cp가 아님
    //즉 p에서 p의 equals 로직이 작동됨
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof ColorPoint))
//            return false;
//
//        return super.equals(o) && ((ColorPoint) o).color == color;
//    }


    // Point 는 좌표만 비교 ColorPint 는 좌표 + 색상 비교 기준일 일관 되지 않음
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        //o가 일반 Point 면 색상 무시 비교
        if (!(o instanceof ColorPoint))
            return o.equals(this);

        return super.equals(o) && ((ColorPoint) o).color == color;
    }
}
