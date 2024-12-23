package chapter3.item10.ex3;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterPoint extends Point{
    private static final AtomicInteger counter = new AtomicInteger();

    public CounterPoint(int x, int y) {
        super(x, y);
        counter.incrementAndGet();
    }

    public static int numberCreated() {
        return counter.get();
    }
}
