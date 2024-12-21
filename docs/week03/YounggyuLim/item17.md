# 변경 가능성을 최소화 하라 (불변 클래스)

- 불변 클래스의 규칙
1. 객체의 상태를 변경하는 메서드를 제공하지 않는다. ex) setter
2. 클래스를 확장할 수 없도록 한다.
3. 모든 필드를 `final`로 선언한다.
4. 모든 필드를 `private`으로 선언한다.
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.

- 불변클래스의 장점
1. 불변 객체는 스레드에 안전하고 불변이기에 값의 변화가 없어 값을 재사용해 메모리를 아낄 수 있음
```java
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
}
```
2. 불변 객체는 자유롭게 공유가 가능하며, 불변 객체끼리는 내부 데이터를 공유할 수 있다.
3. 객체를 만들 때 다른 불변 객체들을 구성요소로 사용해 불변식을 유지하기 쉬워짐
4. 불변 객체는 그 자체로 실패 원자성을 제공하므로 잠깐이라도 불일치 상태에 빠질 가능성이 없음

- 불변클래스의 단점
- 값이 다르면 반드시 독립된 객체로 만들어야 한다.

## 신뢰할 수 없는 클라이언트로부터 `BigInteger` or `BigDecimal`을 인수로 받는다면 주의 해야함
```java
//불변 클래스의 필드가 가변객체를 참조 할 시 불변성이 깨질 수 있어 BigInteger 또는 BigDecimal 인수 검사
    //방어적 복사를 통해 외부의 변경으로부터 내부를 보호함
    public static BigInteger safeInstance(BigInteger val) {
        return val.getClass() == BigInteger.class ?
                val : new BigInteger(val.toByteArray());
    }
```

## 불변 클래스의 직렬화할때 주의점 
- 불변 클래스가 가변 객체를 참조하는 필드를 갖는다면 직렬화시  `readObject` 나 `readResolve`
또는 `ObjectOutputStream.writeUnshared` 와 `ObjectInputStream.readUnshared` 메서드를 반드시 제공해야함 그렇지 않으면 이 클래스로부터 가변 인스턴스를 만들어 낼 수 있다.

