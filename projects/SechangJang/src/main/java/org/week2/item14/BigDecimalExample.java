package org.week2.item14;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.TreeSet;

public class BigDecimalExample {
    public void compareEquality() {
        BigDecimal bd1 = new BigDecimal("1.0");
        BigDecimal bd2 = new BigDecimal("1.00");

        // HashSet은 equals 메서드로 비교
        HashSet<BigDecimal> hashSet = new HashSet<>();
        hashSet.add(bd1);
        hashSet.add(bd2);

        // TreeSet은 compareTo 메서드로 비교
        TreeSet<BigDecimal> treeSet = new TreeSet<>();
        treeSet.add(bd1);
        treeSet.add(bd2);

        System.out.println("HashSet 크기: " + hashSet.size()); // 2 출력
        System.out.println("TreeSet 크기: " + treeSet.size()); // 1 출력
    }
}