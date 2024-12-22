package org.week3.item18;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 컴포지션을 사용한 예제
 * - HashSet을 래핑하여 기능을 확장
 */
public class CompositionSet<E> {
    private final Set<E> set;  // 컴포지션
    private int addCount = 0;  // 요소가 추가된 횟수

    public CompositionSet() {
        this.set = new HashSet<>();
    }

    public boolean add(E e) {
        addCount++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return set.addAll(c);
    }

    public int size() {
        return set.size();
    }

    public int getAddCount() {
        return addCount;
    }

    @Override
    public String toString() {
        return set.toString();
    }
}