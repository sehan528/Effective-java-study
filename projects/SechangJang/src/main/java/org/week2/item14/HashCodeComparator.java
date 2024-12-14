package org.week2.item14;

import java.util.Comparator;

public class HashCodeComparator {
    // 잘못된 구현 - 정수 오버플로우 위험
    static Comparator<Object> hashCodeOrder_BROKEN = new Comparator<>() {
        public int compare(Object o1, Object o2) {
            return o1.hashCode() - o2.hashCode(); // 위험!
        }
    };

    // 올바른 구현 1 - 정적 compare 메서드 활용
    static Comparator<Object> hashCodeOrder = new Comparator<>() {
        public int compare(Object o1, Object o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };

    // 올바른 구현 2 - Comparator의 comparing 메서드 활용
    static Comparator<Object> hashCodeOrder_MODERN = 
        Comparator.comparingInt(Object::hashCode);
}