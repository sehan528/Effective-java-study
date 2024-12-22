package org.week3.item19;


/**
 * 상속 설계 예제들을 테스트하는 실행 클래스
 */
public class InheritanceTest {
    public static void main(String[] args) {
        // 올바른 상속 설계 테스트
        System.out.println("=== 올바른 상속 설계 테스트 ===");
        Shape rectangle = new Rectangle(10, 20);
        rectangle.draw("빨간색");
        rectangle.drawBorder(2);

        // 상속 금지 테스트
        System.out.println("\n=== 상속 금지 테스트 ===");
        FinalClass finalClass = new FinalClass();
        finalClass.doSomething();
        
        FactoryClass factoryClass = FactoryClass.newInstance();

        // 잘못된 상속 설계 테스트
        System.out.println("\n=== 잘못된 상속 설계 테스트 ===");
        BadChildClass badChild = new BadChildClass();
        badChild.overridableMethod();
    }
}