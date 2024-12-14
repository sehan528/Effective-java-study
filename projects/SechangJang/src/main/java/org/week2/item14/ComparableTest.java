package org.week2.item14;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class ComparableTest {
    public static void main(String[] args) {
        // PhoneNumber 비교 테스트
        System.out.println("=== PhoneNumber 비교 테스트 ===");
        PhoneNumber[] phones = {
            new PhoneNumber(123, 456, 7890),
            new PhoneNumber(123, 456, 7891),
            new PhoneNumber(123, 455, 7890)
        };

        System.out.println("정렬 전: " + Arrays.toString(phones));
        Arrays.sort(phones);
        System.out.println("정렬 후: " + Arrays.toString(phones));

        // BigDecimal 비교 테스트
        System.out.println("\n=== BigDecimal 비교 테스트 ===");
        BigDecimalExample bdExample = new BigDecimalExample();
        bdExample.compareEquality();

        // Comparator 구현 테스트
        System.out.println("\n=== Comparator 구현 테스트 ===");
        Object o1 = new Object();
        Object o2 = new Object();

        System.out.println("hashCode 비교(올바른 방법): " + 
            HashCodeComparator.hashCodeOrder.compare(o1, o2));
        System.out.println("hashCode 비교(모던 방법): " + 
            HashCodeComparator.hashCodeOrder_MODERN.compare(o1, o2));
    }
}