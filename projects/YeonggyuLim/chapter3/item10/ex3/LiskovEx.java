package chapter3.item10.ex3;

public class LiskovEx {
    public static void main(String[] args) {
        Point p = new Point(1, 0);
        CounterPoint cp = new CounterPoint(1, 0);

        System.out.println(Point.onUnitCircle(p));
        System.out.println(Point.onUnitCircle(cp));

    }
}
