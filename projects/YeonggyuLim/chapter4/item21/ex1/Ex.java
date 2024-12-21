package chapter4.item21.ex1;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

//디폴트메서드를 추가해도 나중에 불변식을 해치지 않을지 고민하라
public interface Ex<E> extends Iterable<E>{
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean result = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if (filter.test(it.next())) {
                it.remove();
                result = true;
            }
        }
        return result;
    }
}
