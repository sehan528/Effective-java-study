package chapter4.item17.ex2;

import java.math.BigInteger;

public class Complex {
    private final double re;
    private final double im;

    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    //정적 팩토리 메서드 이용시 객체 재활용(캐싱)이 가능
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }

    //불변 클래스의 필드가 가변객체를 참조 할 시 불변성이 깨질 수 있어 BigInteger 또는 BigDecimal 인수 검사
    //방어적 복사를 통해 외부의 변경으로부터 내부를 보호함
    public static BigInteger safeInstance(BigInteger val) {
        return val.getClass() == BigInteger.class ?
                val : new BigInteger(val.toByteArray());
    }

    //불변 클래스 직렬화시 주의할 점
    //불변 클래스가 가변 객체를 참조하는 필드를 갖는다면 직렬화시
    //readObject 나 readResolve 메서드를 반드시 제공해야함 그렇지 않으면 이 클래스로부터 가변 인스턴스를 만들어 낼 수 있다.
}
