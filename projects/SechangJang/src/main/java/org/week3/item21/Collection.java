package org.week3.item21;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 컬렉션 인터페이스 예제
 * - 디폴트 메서드의 위험성을 보여주는 예제
 */
public interface Collection<E> {
    boolean add(E element);
    Iterator<E> iterator();
    
    // 위험할 수 있는 디폴트 메서드
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (filter.test(it.next())) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }
}