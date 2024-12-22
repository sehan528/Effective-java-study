package org.week3.item22;

/**
 * 상수 처리 방법을 테스트하는 실행 클래스
 */
public class ConstantTest {
    public static void main(String[] args) {
        // 잘못된 상수 인터페이스 사용 예시
        System.out.println("=== 잘못된 상수 인터페이스 사용 예시 ===");
        System.out.println("게임 최대 점수: " + GameConstants.MAX_SCORE);
        System.out.println("경고: 상수 인터페이스는 안티패턴입니다!");

        // 올바른 상수 처리 방법
        System.out.println("\n=== 올바른 상수 처리 방법 ===");
        System.out.println("물리 상수 π: " + PhysicalConstants.PI);
        System.out.println("계산 결과: " + PhysicalConstants.calculateCircleArea(10));

        // 열거 타입 사용 예시
        System.out.println("\n=== 열거 타입 사용 예시 ===");
        PaymentType paymentType = PaymentType.CREDIT_CARD;
        double amount = 100.0;
        System.out.println("결제 수수료율: " + paymentType.calculateFee(amount) + "%");
        System.out.printf("수수료 계산: $%.2f%n", paymentType.calculateFee(amount));

        // 클래스 내 상수 사용 예시
        System.out.println("\n=== 클래스 내 상수 사용 예시 ===");
        Calculator calculator = new Calculator();
        System.out.println("원의 넓이: " + PhysicalConstants.calculateCircleArea(5));
    }
}