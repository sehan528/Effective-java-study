package org.week3.item17;

import java.util.*;

/**
 * 불변성 테스트를 위한 실행 클래스
 */
public class ImmutabilityTest {
    public static void main(String[] args) {
        // 불변 복소수 테스트
        System.out.println("=== 불변 복소수 테스트 ===");
        Complex c1 = Complex.valueOf(3.0, 4.0);
        Complex c2 = Complex.valueOf(1.0, 2.0);
        System.out.println("복소수1: " + c1);
        System.out.println("복소수2: " + c2);
        System.out.println("덧셈 결과: " + c1.plus(c2));
        System.out.println("원본 불변 확인: " + c1);

        // 불변 vs 가변 객체 테스트
        System.out.println("\n=== 불변 객체 vs 가변 객체 테스트 ===");
        ImmutablePerson immutablePerson = new ImmutablePerson("John", 30);
        MutablePerson mutablePerson = new MutablePerson("John", 30);
        
        System.out.println("불변 Person: " + immutablePerson);
        System.out.println("가변 Person: " + mutablePerson);

        // 수정 시도
        mutablePerson.setAge(31);
        System.out.println("-- 수정 시도 후 --");
        System.out.println("불변 Person: " + immutablePerson + " (변경되지 않음)");
        System.out.println("가변 Person: " + mutablePerson + " (변경됨)");

        // 컬렉션 불변성 테스트
        System.out.println("\n=== 컬렉션 불변성 테스트 ===");
        MoneyHolder holder = new MoneyHolder(Arrays.asList(1000, 2000, 3000));
        System.out.println("초기 금액들: " + holder);

        try {
            holder.getAmountsSafe().add(4000);
        } catch (UnsupportedOperationException e) {
            System.out.println("안전한 리스트는 수정 불가");
        }

        List<Integer> defensive = holder.getAmountsDefensive();
        defensive.add(4000); // 방어적 복사본은 수정 가능

        System.out.println("수정 시도 후 원본: " + holder);
        System.out.println("방어적 복사본: " + defensive);
    }
}