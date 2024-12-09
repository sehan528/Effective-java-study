package chapter3.item10.ex2;

import java.awt.*;

public class ColorEx {
    public static void main(String[] args) {
        ColorPoint cp = new ColorPoint(1, 2, Color.RED);
        Point p = new Point(1, 2);


//        System.out.println(cp.equals(p));
//        System.out.println(p.equals(cp));

        ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

        System.out.println(p1.equals(p2));
        System.out.println(p2.equals(p3));
        System.out.println(p1.equals(p3));
    }
}
