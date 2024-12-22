package org.week3.item20;

/**
 * 결제 서비스를 정의하는 기본 인터페이스
 */
public interface PaymentService {
    void processPayment(double amount);
    
    // 디폴트 메서드로 기본 구현 제공
    default void validatePayment(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        System.out.println("기본 보안 검사 수행...");
    }
    
    default void notifyComplete() {
        System.out.println("결제 완료 알림 전송");
    }
}