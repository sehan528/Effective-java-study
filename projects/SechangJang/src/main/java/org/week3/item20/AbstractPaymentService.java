package org.week3.item20;

/**
 * 결제 서비스의 골격 구현을 제공하는 추상 클래스
 */
public abstract class AbstractPaymentService implements PaymentService {
    @Override
    public void processPayment(double amount) {
        // 템플릿 메서드 패턴 활용
        validatePayment(amount);
        doProcessPayment(amount);
        notifyComplete();
    }

    // 하위 클래스가 구현해야 하는 실제 결제 처리 메서드
    protected abstract void doProcessPayment(double amount);

    // 공통으로 사용할 수 있는 유틸리티 메서드
    protected String formatAmount(double amount) {
        return String.format("$%.2f", amount);
    }
}