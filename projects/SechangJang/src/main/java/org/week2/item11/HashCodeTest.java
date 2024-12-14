package org.week2.item11;

import java.util.*;

/**
 * hashCode 구현의 영향을 테스트하는 실행 클래스
 */
public class HashCodeTest {
    public static void main(String[] args) {
        // 1. HashMap 작동 테스트
        Map<PhoneNumber, String> map = new HashMap<>();
        
        PhoneNumber number1 = new PhoneNumber(707, 867, 5309);
        PhoneNumber number2 = new PhoneNumber(707, 867, 5309);
        
        map.put(number1, "제니");
        
        // hashCode를 올바르게 재정의했다면 같은 값을 찾을 수 있음
        System.out.println("올바른 hashCode 구현시 조회: " + map.get(number2));

        // 2. 해시 충돌 테스트
        Set<PhoneNumber> numbers = new HashSet<>();
        numbers.add(number1);
        System.out.println("HashSet 포함 여부: " + numbers.contains(number2));

        // 3. 성능 비교 테스트
        int iterations = 1000000;
        
        // 일반 구현
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            number1.hashCode();
        }
        long normalTime = System.nanoTime() - start;
        
        // Objects.hash 사용
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            number1.hashCodeAlternative();
        }
        long alternativeTime = System.nanoTime() - start;
        
        System.out.println("\n성능 비교:");
        System.out.println("일반 구현: " + normalTime / 1000000.0 + "ms");
        System.out.println("Objects.hash: " + alternativeTime / 1000000.0 + "ms");
    }
}