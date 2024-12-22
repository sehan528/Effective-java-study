package org.week3.item21;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 기본 컬렉션 구현체
 * - 디폴트 메서드를 그대로 상속
 */
public class CustomCollection<E> implements Collection<E> {
    private final List<E> elements = new ArrayList<>();
    
    @Override
    public boolean add(E element) {
        return elements.add(element);
    }
    
    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }
    
    @Override
    public String toString() {
        return elements.toString();
    }
}