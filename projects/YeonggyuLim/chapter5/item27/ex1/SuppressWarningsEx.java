package chapter5.item27.ex1;

import java.util.Arrays;

public class SuppressWarningsEx {
   private Object[] elements;
   private int size;


    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked") T[] result =
                    (T[]) Arrays.copyOf(elements, size, a.getClass());
            //a.getClass()로 어떤 타입이 반환될지 유추
            //또한 a.getClass()로 어떤 타입이 반환될지 알 수 있으므로 타입안정성이 있음
            return result;
        }

        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
}
