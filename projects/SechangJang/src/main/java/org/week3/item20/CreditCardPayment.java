package org.week3.item20;

/**
 * 신용카드 결제 구현 클래스
 * 여러 인터페이스를 구현하여 기능 확장
 */
public class CreditCardPayment extends AbstractPaymentService 
    implements PaymentService, Logging, Secured {
    
    @Override
    protected void doProcessPayment(double amount) {
        secure();  // 보안 기능 사용
        log("신용카드 결제 시작: " + formatAmount(amount));
        System.out.println("신용카드 결제 진행: " + formatAmount(amount));
    }

    // 추가적인 비즈니스 로직
    public void processRefund(double amount) {
        log("환불 처리: " + formatAmount(amount));
        System.out.println("환불 처리 완료");
    }
}