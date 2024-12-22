package org.week3.item19;


import java.time.Instant;

/**
 * 잘못된 상속 설계의 예
 * 생성자에서 재정의 가능 메서드를 호출하는 문제를 보여줌
 */
class BadSuperClass {
    public BadSuperClass() {
        System.out.println("부모 생성자 호출");
        overridableMethod();  // 문제가 되는 부분
    }

    public void overridableMethod() {
        System.out.println("부모 메서드 호출");
    }
}

class BadChildClass extends BadSuperClass {
    private final Instant instant;

    public BadChildClass() {
        instant = Instant.now();  // 생성자에서 초기화
    }

    @Override
    public void overridableMethod() {
        System.out.println("자식 메서드 호출 (초기화되지 않은 상태)");
        if (instant != null) {
            System.out.println(instant);
        } else {
            System.out.println("NPE 발생 가능성 있음");
        }
    }
}