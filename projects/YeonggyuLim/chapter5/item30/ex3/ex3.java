package chapter5.item30.ex3;

import java.util.Collection;
import java.util.Objects;

public class ex3 {
    //빈 컬렉션을 던져서 예외처리하게 할바에 그냥 Optional 쓰는게 더 좋긴 함

    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}
