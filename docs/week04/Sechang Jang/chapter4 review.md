# Item 15: 클래스와 멤버의 접근 권한을 최소화하라

## 1. 기본 원칙

### 1.1 정보 은닉의 중요성
프로그램 요소의 접근성을 가능한 한 최소한으로 해야 하는 이유는 **잘 설계된 컴포넌트**와 직접적인 연관이 있습니다. 여기서 잘 설계된 컴포넌트란:
- 클래스 내부 데이터와 구현 정보를 외부 컴포넌트로부터 얼마나 잘 숨겼는가로 평가됩니다.
- 오직 API를 통해서만 다른 컴포넌트와 소통하며 서로의 내부 동작 방식에는 전혀 개의치 않습니다.

이러한 정보 은닉의 장점은 다음과 같습니다:

1. **시스템 개발 속도 향상**
   - 여러 컴포넌트를 병렬로 개발할 수 있기 때문입니다.

2. **시스템 관리 비용 감소**
   - 각 컴포넌트를 더 빨리 파악할 수 있습니다.
   - 디버깅이 용이합니다.
   - 다른 컴포넌트로 교체하는 부담도 적습니다.

3. **성능 최적화에 도움**
   - 완성된 시스템을 프로파일링해 최적화할 컴포넌트를 정확히 찾을 수 있습니다.
   - 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 최적화할 수 있습니다.

4. **소프트웨어 재사용성 증가**
   - 의존성이 낮은 컴포넌트는 그 자체로 독립적인 가치가 있습니다.
   - 낯선 환경에서도 유용하게 쓰일 가능성이 큽니다.

5. **큰 시스템 제작 난이도 감소**
   - 시스템 전체가 완성되지 않은 상태에서도 개별 컴포넌트의 동작을 검증할 수 있습니다.

## 2. 접근 제한자의 올바른 사용

### 2.1 기본 원칙
모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 합니다. 달리 말하면:
- 소프트웨어가 올바로 동작하는 한 가장 낮은 접근 수준을 부여해야 합니다.

### 2.2 톱레벨 클래스와 인터페이스
가능한 두 가지 접근 수준:
1. **package-private**: 패키지 내에서만 사용
2. **public**: 공개 API로 사용

주의사항:
- public 클래스는 그 패키지의 API인 반면, package-private 클래스는 내부 구현에 속합니다.
- 한 클래스에서만 사용하는 package-private 클래스는 해당 클래스의 private static 중첩 클래스로 구현을 고려해야 합니다.

### 2.3 클래스의 멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)
가능한 네 가지 접근 수준:

1. **private**: 멤버를 선언한 톱레벨 클래스에서만 접근 가능
2. **package-private**: 멤버가 소속된 패키지 안의 모든 클래스에서 접근 가능
3. **protected**: package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근 가능
4. **public**: 모든 곳에서 접근 가능

### 2.4 멤버 접근성 제한 원칙

1. **public 클래스의 인스턴스 필드는 되도록 public이 아니어야 합니다.**
   - 이유: 필드가 가변 객체를 참조하거나, final이 아닌 인스턴스 필드를 public으로 선언하면 그 필드에 담을 수 있는 값을 제한할 수 없습니다.
   - 결과: 스레드 안전성을 보장할 수 없습니다.

2. **예외: public static final 필드**
   - 조건: 해당 클래스가 표현하는 추상 개념을 완성하는 데 꼭 필요한 구성요소로써의 상수인 경우
   - 주의: 반드시 기본 타입이나 불변 객체를 참조해야 합니다.

3. **주의: 길이가 0이 아닌 배열은 모두 변경 가능**
   ```java
   // 보안 허점이 존재하는 코드
   public static final Thing[] VALUES = { ... };
   ```

   해결 방법:
   ```java
   // 방법 1: private 배열 + public 불변 리스트
   private static final Thing[] PRIVATE_VALUES = { ... };
   public static final List<Thing> VALUES = 
       Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

   // 방법 2: private 배열 + 복사본 반환
   private static final Thing[] PRIVATE_VALUES = { ... };
   public static final Thing[] values() {
       return PRIVATE_VALUES.clone();
   }
   ```

## 3. 모듈 시스템 (Java 9+)

Java 9에서 추가된 모듈 시스템으로 두 가지 암묵적 접근 수준이 추가되었습니다:
- 패키지가 클래스들의 묶음이듯, 모듈은 패키지들의 묶음입니다.
- 모듈은 자신이 속하는 패키지 중 공개(export)할 것들을 선언할 수 있습니다.
- 그러나 비모듈 방식이 여전히 주로 사용되고 있습니다.

## 4. 정리

프로그램 요소의 접근성은 가능한 한 최소한으로 하되:
1. 각 요소의 접근성을 최대한 좁힐 것
2. 단, 공개 API는 신중히 설계한 후 그 외의 모든 멤버는 private으로 만들 것
3. private 멤버를 package-private까지 풀어주는 일은 자주 있을 수 있으나, 그 이상은 안됨
4. 권한을 풀어주는 일이 잦다면 컴포넌트를 더 분해해야 하는 신호일 수 있음
5. public 클래스는 상수용 public static final 필드 외에는 어떠한 public 필드도 가져서는 안됨


# Item 16: public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 1. 잘못된 캡슐화의 예시와 문제점

### 1.1 안티패턴: public 필드 노출
```java
// 이런 클래스는 캡슐화의 이점을 제공하지 못한다!
public class Point {
    public double x;
    public double y;
}
```

이러한 클래스가 가지는 문제점:
1. **내부 표현 변경의 어려움**
   - API를 수정하지 않고는 내부 표현을 바꿀 수 없음
   - 한번 공개된 필드는 계속 그 형태를 유지해야 함

2. **불변식 보장 불가**
   - 외부에서 직접 필드 수정 가능
   - 유효성 검증이나 제약조건 적용 불가능

3. **부수 작업 수행 불가**
   - 필드 접근 시 다른 작업을 수행할 수 없음
   - 예: 접근 통계 수집, 로깅, 동기화 등

## 2. 올바른 캡슐화 방법

### 2.1 접근자 메서드를 통한 캡슐화
```java
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
```

이 방식의 장점:
1. **캡슐화의 이점**
   - 내부 표현 방식을 언제든 바꿀 수 있음
   - 불변식 보장 가능
   - 필드 접근 시 부가 동작 수행 가능

2. **유연성**
   - 나중에 내부 구현을 변경하더라도 API는 그대로 유지 가능
   ```java
   public class Point {
       private double radius;
       private double angle;
       
       public double getX() { return radius * Math.cos(angle); }
       public double getY() { return radius * Math.sin(angle); }
   }
   ```

## 3. package-private 클래스 또는 private 중첩 클래스에서의 예외

### 3.1 package-private 클래스의 경우
```java
// 패키지 내부에서만 사용하는 클래스
class PackagePoint {
    // 필드를 노출해도 문제 없음
    double x;
    double y;
}
```

이 경우 필드를 노출해도 되는 이유:
1. **제한된 접근 범위**
   - 같은 패키지 내부에서만 사용
   - 클라이언트 코드가 이미 패키지 내부로 한정됨

2. **코드의 간결성**
   - 불필요한 접근자 메서드를 줄일 수 있음
   - 패키지 내부에서는 어차피 구현을 알고 있음

### 3.2 private 중첩 클래스의 경우
```java
public class OuterClass {
    private static class PrivatePoint {
        // 필드를 노출해도 문제 없음
        double x;
        double y;
    }
}
```

이 경우의 특징:
1. **더욱 제한된 접근 범위**
   - 바깥 클래스까지로 접근이 제한됨
   - 수정 범위가 매우 좁음

2. **구현 편의성**
   - 간단한 데이터 홀더 역할에 적합
   - 불필요한 캡슐화 비용 감소

## 4. 실수 사례: java.awt.Package

### 4.1 잘못된 설계의 예
```java
// java.awt.Point 클래스 - 잘못된 설계의 예시
public class Point {
    public double x;
    public double y;
}

// java.awt.Dimension 클래스 - 마찬가지로 잘못된 설계
public class Dimension {
    public double width;
    public double height;
}
```

이러한 설계가 문제되는 이유:
1. **호환성 때문에 수정 불가**
   - 이미 수많은 코드에서 사용 중
   - 내부 구현을 변경할 수 없음

2. **성능 이슈**
   - 캡슐화의 이점을 포기한 대가가 성능 향상으로 이어지지도 않음

## 5. 정리

1. **public 클래스의 필드는 절대로 직접 노출하지 말 것**
   - 불변 필드라도 직접 노출은 좋지 않음
   - 캡슐화의 이점을 살리려면 접근자 메서드 사용

2. **package-private 클래스나 private 중첩 클래스는 필드 노출이 나을 수 있음**
   - 클래스의 추상 개념을 더 잘 표현할 수 있다면 필드 노출도 괜찮음
   - 코드가 더 간결해짐

3. **접근자 메서드의 이점**
   - 내부 표현 방식을 자유롭게 변경 가능
   - 불변식 보장 가능
   - 부수 작업 수행 가능

# Item 17: 변경 가능성을 최소화하라

## 1. 불변 클래스의 이해

### 1.1 불변 클래스란?
불변 클래스란 인스턴스의 내부 값을 수정할 수 없는 클래스입니다. 객체가 파괴되는 순간까지 동일한 상태를 유지합니다.

**대표적인 불변 클래스:**
- String
- 기본 타입의 박싱된 클래스들 (Integer, Long 등)
- BigInteger
- BigDecimal

## 2. 불변 클래스를 만드는 규칙

### 2.1 다섯 가지 핵심 규칙
1. **객체의 상태를 변경하는 메서드(변경자)를 제공하지 않는다**
2. **클래스를 확장할 수 없도록 한다**
3. **모든 필드를 final로 선언한다**
4. **모든 필드를 private으로 선언한다**
5. **자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다**

### 2.2 구체적인 예시: Complex 클래스
```java
public final class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double realPart()      { return re; }
    public double imaginaryPart() { return im; }

    // 함수형 프로그래밍 방식의 메서드들
    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }
}
```

**함수형 프로그래밍의 특징:**
- 피연산자에 함수를 적용해 결과를 반환
- 피연산자 자체는 변경되지 않음
- 새로운 객체를 반환

## 3. 불변 클래스의 장점

### 3.1 스레드 안전성
```java
// 불변 객체는 원자성을 보장함
public class ImmutablePoint {
    private final int x;
    private final int y;
    
    // 한번 생성되면 변경 불가능
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
```

### 3.2 객체 공유와 재사용
```java
// BigInteger에서의 내부 공유 예시
public BigInteger negate() {
    // magnitude(절대값)는 그대로 공유하고 부호만 바꿈
    return new BigInteger(this.mag, -this.signum);
}
```

### 3.3 실패 원자성 제공
- 메서드가 실패하더라도 객체는 여전히 유효한 상태 유지
- 불일치 상태에 빠질 가능성이 없음

## 4. 불변 클래스 설계 전략

### 4.1 정적 팩터리를 통한 유연성 확보
```java
// private 생성자와 정적 팩터리 메서드 활용
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

### 4.2 가변 동반 클래스 제공
```java
// String과 StringBuilder의 관계처럼
public final class String { ... }  // 불변
public class StringBuilder { ... } // 가변 동반 클래스
```

## 5. 주의사항과 실제 사례

### 5.1 BigInteger와 BigDecimal 사용 시 주의점
```java
// 방어적 복사를 통한 안전한 사용
public static BigInteger safeInstance(BigInteger val) {
    return val.getClass() == BigInteger.class ?
        val : new BigInteger(val.toByteArray());
}
```

### 5.2 성능 최적화 전략
```java
public final class LazyInitialization {
    private final byte[] data;
    // 계산 비용이 큰 값은 지연 초기화
    private volatile int hashCode;

    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Arrays.hashCode(data);
            hashCode = result;
        }
        return result;
    }
}
```

## 6. 실무 적용 가이드

### 6.1 기본 원칙
1. **클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다**
2. **모든 필드는 private final이어야 한다**
3. **생성자는 불변식 설정이 완료된 상태의 객체를 생성해야 한다**

### 6.2 getter 메서드와 관련하여
- getter가 있다고 해서 무조건 setter가 필요한 것은 아님
- 클래스의 특성을 고려하여 불변성이 필요한지 판단

## 7. 불변성의 장단점 정리

### 장점
1. 설계, 구현, 사용이 용이
2. 스레드 안전성 보장
3. 객체 공유 가능
4. 메모리 사용량 감소
5. 실패 원자성 제공

### 단점
1. 값이 다르면 새로운 객체 생성 필요
2. 큰 객체를 자주 생성해야 하는 경우 성능 저하 가능

### 해결 방안
1. 다단계 연산을 예측하여 기본 기능으로 제공
2. 가변 동반 클래스 제공
3. 지연 초기화 활용

# Item 18: 상속보다는 컴포지션을 사용하라

## 1. 상속의 문제점

### 1.1 캡슐화 위반
상속은 캡슐화를 깨뜨립니다. 하위 클래스가 상위 클래스의 구현에 종속되어 상위 클래스가 변경될 때 하위 클래스가 오동작할 수 있습니다.

### 1.2 실제 오류 사례 분석
```java
// 잘못된 상속 사용 예시
public class InstrumentedHashSet<E> extends HashSet<E> {
    // 추가된 원소의 수
    private int addCount = 0;

    public InstrumentedHashSet() {}

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();  // 여기서 카운트 증가
        return super.addAll(c);  // 내부적으로 add() 호출
    }

    public int getAddCount() {
        return addCount;
    }
}

// 문제가 되는 사용 예시
InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
s.addAll(List.of("가", "나", "다"));  // addCount는 예상과 다른 6이 됨
```

**문제점 분석:**
1. `addAll` 메서드에서 원소 개수를 더함 (+3)
2. `HashSet`의 `addAll`이 내부적으로 `add`를 호출
3. 재정의된 `add`가 호출되어 각 원소당 카운트가 다시 증가 (+3)
4. 최종적으로 중복 카운트 발생 (3 + 3 = 6)

## 2. 컴포지션 방식

### 2.1 컴포지션과 전달 방식
```java
// 래퍼 클래스 - 컴포지션 방식
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

// 재사용 가능한 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;  // 컴포지션
    public ForwardingSet(Set<E> s) { this.s = s; }

    // Set 인터페이스의 메서드들을 전달
    public void clear()               { s.clear(); }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty(); }
    // ... 나머지 메서드들도 같은 방식으로 구현
}
```

**컴포지션의 장점:**
1. 기존 클래스의 내부 구현에 영향 받지 않음
2. 기존 클래스의 새로운 메서드가 추가되어도 안전
3. 한번 구현해두면 어떤 Set 구현체라도 계측 가능

### 2.2 데코레이터 패턴
```java
// 데코레이터 패턴 활용 예시
public static void processDogs(Set<Dog> dogs) {
    // 기존 Set을 감싸서 새로운 기능 추가
    InstrumentedSet<Dog> instrumentedDogs = new InstrumentedSet<>(dogs);
    // 작업 수행
    // ...
}
```

## 3. 상속과 컴포지션의 선택 기준

### 3.1 상속 사용이 적절한 경우
클래스 B가 클래스 A의 "진짜" 하위 타입인 경우에만 상속 사용
```java
// 적절한 상속의 예
public class Rectangle {
    protected int width, height;
    // ... 사각형 관련 메서드들
}

// Square는 Rectangle의 진짜 하위 타입
public class Square extends Rectangle {
    // ... 정사각형 관련 메서드들
}
```

### 3.2 잘못된 상속 사용 사례
```java
// 잘못된 상속의 예 - java.util.Properties와 Hashtable
public class Properties extends Hashtable<Object,Object> {
    // Properties는 Hashtable이 아님에도 상속받음
    
    // 이로 인한 문제점
    Properties p = new Properties();
    p.setProperty("key", "value");  // Properties의 메서드
    Object value = p.get("key");    // Hashtable의 메서드
    // 두 메서드가 다른 결과를 반환할 수 있음!
}
```

## 4. 컴포지션 사용 시 주의점

### 4.1 콜백 프레임워크에서의 문제
```java
// SELF 문제 예시
public class Wrapper {
    private Object delegate;  // 내부 객체

    public void doCallback() {
        // 내부 객체는 래퍼의 존재를 모름
        delegate.callback(this);  // 의도한 대로 동작하지 않을 수 있음
    }
}
```

### 4.2 상속 대신 컴포지션을 사용할 때의 자가진단
1. 확장하려는 클래스의 API에 결함이 없는가?
2. 결함이 있다면, 그 결함이 내 클래스의 API까지 전파되어도 괜찮은가?

## 5. 실무 적용 가이드

### 5.1 컴포지션 사용 체크리스트
- [ ] 상속 관계가 is-a 관계인가?
- [ ] 확장하려는 클래스의 API가 안정적인가?
- [ ] 상위 클래스의 변경이 하위 클래스에 영향을 주지 않는가?
- [ ] 메서드 재정의로 인한 부작용은 없는가?

### 5.2 리팩토링 예시
```java
// 상속 기반 코드
public class CustomList extends ArrayList<String> {
    // 위험한 구현
}

// 컴포지션 기반으로 리팩토링
public class CustomList {
    private final List<String> list = new ArrayList<>();
    
    // 필요한 메서드만 위임
    public void add(String item) {
        list.add(item);
    }
    
    // 추가 기능 구현
    public void addWithValidation(String item) {
        // 커스텀 로직
    }
}
```

