# Item 17: 변경 가능성을 최소화하라

## 1. 불변 클래스의 이해

### 1.1 배경과 정의
불변 클래스란 인스턴스의 내부 값을 수정할 수 없는 클래스를 말합니다. 불변 인스턴스에 간직된 정보는 객체가 파괴되는 순간까지 절대 달라지지 않습니다.

### 1.2 대표적인 불변 클래스
- String
- 기본 타입의 박싱된 클래스들 (Integer, Double 등)
- BigInteger
- BigDecimal

### 1.3 불변성이 중요한 이유
1. **설계와 구현의 용이성**
   - 가변 클래스보다 설계하기 쉬움
   - 오류가 생길 여지가 적음
2. **스레드 안전성**
   - 동기화 없이도 안전하게 공유 가능
3. **재사용성**
   - 자유롭게 공유할 수 있고 방어적 복사도 필요 없음

## 2. 불변 클래스 만들기 위한 5가지 규칙

### 2.1 핵심 규칙
1. **객체의 상태를 변경하는 메서드(변경자) 제공 금지**
   ```java
   // 잘못된 예
   public class MutablePoint {
       private int x, y;
       public void setX(int x) { this.x = x; }  // 변경자 메서드
   }
   
   // 올바른 예
   public final class Point {
       private final int x, y;
       public Point(int x, int y) {
           this.x = x;
           this.y = y;
       }
   }
   ```

2. **클래스를 확장할 수 없도록 설계**
   - final 클래스로 선언
   - 또는 모든 생성자를 private/package-private으로 만들고 정적 팩터리 제공

3. **모든 필드를 final로 선언**
   - 시스템이 강제하는 수단으로 설계자의 의도를 명확히 표현
   - 다중 스레드 환경에서 안전성 보장

4. **모든 필드를 private으로 선언**
   - 필드가 참조하는 가변 객체를 클라이언트에서 직접 접근하여 수정하는 일을 방지
   - public final로도 불변이 되지만 내부 표현을 바꾸지 못하므로 지양

5. **자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 통제**
   ```java
   public final class ImmutableHolder {
       private final Date date;
       
       public ImmutableHolder(Date date) {
           // 생성자에서 방어적 복사
           this.date = new Date(date.getTime());
       }
       
       public Date getDate() {
           // 접근자에서도 방어적 복사
           return new Date(date.getTime());
       }
   }
   ```

## 3. 불변 클래스의 장단점

### 3.1 장점
1. **스레드 안전성**
   - 여러 스레드가 동시에 사용해도 훼손되지 않음
   - 동기화가 필요 없음

2. **객체 공유와 재사용**
   ```java
   // BigInteger에서의 내부 공유 예시
   public class BigInteger {
       private final int signum;
       private final int[] mag;
       
       public BigInteger negate() {
           // 부호만 반대로 바꾸고 mag 배열은 공유
           return new BigInteger(-signum, mag);
       }
   }
   ```

3. **실패 원자성 제공**
   - 메서드에서 예외가 발생해도 객체는 여전히 유효한 상태 유지

### 3.2 단점과 해결책
1. **값이 다르면 반드시 별도 객체 생성**
   - 값의 가짓수가 많으면 비용 증가
   
2. **해결방안: 다단계 연산을 예측하여 기본 기능으로 제공**
   ```java
   // 가변 동반 클래스를 통한 성능 최적화
   public class String {  // 불변 클래스
       // ... String의 불변 구현
   }
   
   public class StringBuilder {  // 가변 동반 클래스
       // ... 가변 구현으로 성능 최적화
   }
   ```

## 4. 불변 클래스 설계 전략

### 4.1 정적 팩터리를 통한 유연성 확보
```java
// private 생성자와 정적 팩터리
public class Complex {
    private final double re;
    private final double im;
    
    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }
}
```

### 4.2 재활용을 통한 성능 최적화
```java
public final class Integer {
    // 자주 사용되는 값들을 캐싱
    private static class IntegerCache {
        static final Integer[] cache = new Integer[-(-128) + 127 + 1];
        static {
            for (int i = 0; i < cache.length; i++)
                cache[i] = new Integer(i - 128);
        }
    }
}
```

## 5. 실무 적용 가이드

### 5.1 불변 클래스 설계 시 고려사항
1. getter가 있다고 무조건 setter를 만들지 않음
2. 필요한 경우가 아니면 클래스는 불변으로 설계
3. 불변으로 만들 수 없는 클래스라도 변경 가능성을 최소화

### 5.2 성능 최적화 전략
1. 다단계 연산을 기본 기능으로 제공
2. 가변 동반 클래스 제공 검토
3. 자주 사용되는 값들은 캐싱 고려

### 5.3 주의사항
1. 상속을 허용하지 않음
2. 모든 필드를 final과 private으로 선언
3. 방어적 복사를 통해 내부 가변 객체 보호