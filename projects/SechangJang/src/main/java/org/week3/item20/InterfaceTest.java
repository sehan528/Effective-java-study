package org.week3.item20;

/**
 * 인터페이스와 골격 구현의 사용을 테스트하는 실행 클래스
 */
public class InterfaceTest {
    public static void main(String[] args) {
        // 기본 결제 테스트
        System.out.println("=== 결제 시스템 테스트 ===");
        PaymentService payment = new CreditCardPayment();
        payment.processPayment(100.0);

        // 다중 구현 테스트
        System.out.println("\n=== 다중 구현 테스트 ===");
        CreditCardPayment creditCard = new CreditCardPayment();
        creditCard.log("로깅 기능 추가됨");
        creditCard.secure();
        creditCard.processPayment(200.0);

        // 확장 기능 테스트
        System.out.println("\n=== 확장 기능 테스트 ===");
        creditCard.processRefund(50.0);
    }
}