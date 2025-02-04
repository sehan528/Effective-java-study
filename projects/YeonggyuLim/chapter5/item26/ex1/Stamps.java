package chapter5.item26.ex1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Stamps {
    public static Collection stamps = new ArrayList();

    public static void main(String[] args) {
        stamps.add(new Coins());

        for (Iterator i = stamps.iterator(); i.hasNext();) {
            Stamps stamp = (Stamps) i.next(); //꺼내기 전까지 의도한 타입이 들어간지 모름
        }

    }

    private static class Coins {

    }
}
