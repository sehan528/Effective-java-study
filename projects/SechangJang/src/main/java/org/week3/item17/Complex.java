package org.week3.item17;

/**
 * 불변 복소수 클래스 예제
 * - 불변 클래스의 기본 원칙을 모두 준수
 * - 객체 캐싱을 통한 성능 최적화
 */
public final class Complex {
    private final double re;
    private final double im;

    // 자주 사용되는 값은 캐싱
    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);
    public static final Complex I = new Complex(0, 1);

    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex valueOf(double re, double im) {
        if (re == 0 && im == 0) return ZERO;
        if (re == 1 && im == 0) return ONE;
        if (re == 0 && im == 1) return I;
        return new Complex(re, im);
    }

    // 새로운 Complex 인스턴스를 반환하는 불변성 보장
    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    @Override
    public String toString() {
        return re + " + " + im + "i";
    }
}