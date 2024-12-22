package org.week3.item21;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * 동기화된 컬렉션 구현체
 * - 디폴트 메서드와 충돌할 수 있는 동기화 요구사항
 */
public class SynchronizedCollection<E> implements Collection<E> {
    private final List<E> elements = new ArrayList<>();
    private final Object lock = new Object();
    
    @Override
    public boolean add(E element) {
        synchronized(lock) {
            return elements.add(element);
        }
    }
    
    @Override
    public Iterator<E> iterator() {
        synchronized(lock) {
            return new ArrayList<>(elements).iterator();
        }
    }
    
    // removeIf를 재정의하여 동기화 보장
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        synchronized(lock) {
            return Collection.super.removeIf(filter);
        }
    }
    
    @Override
    public String toString() {
        synchronized(lock) {
            return elements.toString();
        }
    }
}