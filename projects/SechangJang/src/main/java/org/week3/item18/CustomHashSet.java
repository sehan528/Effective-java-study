package org.week3.item18;

import java.util.Collection;
import java.util.HashSet;

/**
 * 상속을 잘못 사용한 예제
 * - addAll 메서드 호출 시 add가 중복으로 호출되는 문제 발생
 */
public class CustomHashSet<E> extends HashSet<E> {
    private int addCount = 0;  // 요소가 추가된 횟수

    public CustomHashSet() {
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();  // 컬렉션의 크기만큼 증가
        return super.addAll(c);  // addAll 내부에서 add가 호출되어 중복 카운트 발생
    }

    public int getAddCount() {
        return addCount;
    }
}