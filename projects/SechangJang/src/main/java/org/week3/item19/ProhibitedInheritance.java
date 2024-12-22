package org.week3.item19;


/**
 * 상속을 금지하는 두 가지 방법을 보여주는 예제
 */
// 방법 1: final 클래스
final class FinalClass {
    public void doSomething() {
        System.out.println("final 클래스 인스턴스 생성");
    }
}

// 방법 2: private 생성자와 정적 팩터리
class FactoryClass {
    private FactoryClass() {}  // 상속 불가능

    public static FactoryClass newInstance() {
        System.out.println("정적 팩터리로 생성된 인스턴스");
        return new FactoryClass();
    }
}