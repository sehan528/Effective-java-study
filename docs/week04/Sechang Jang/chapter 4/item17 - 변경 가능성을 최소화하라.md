# Item 17: 변경 가능성을 최소화하라

## 핵심 개념 (Main Ideas)

### 1. 불변 객체의 본질
- **정의**: 인스턴스 내부 값을 생성 시점부터 파괴 시점까지 절대 수정할 수 없는 클래스
- **목적**: 단순하고 안전한 객체 생성 및 관리
- **대표 예시**: String, 기본 타입의 박싱된 클래스들, BigInteger, BigDecimal

### 2. 불변성 달성을 위한 5가지 핵심 규칙
- **원칙**: 객체의 상태를 변경할 수 없도록 설계
- **이유**: 스레드 안전성, 캐싱 용이성, 방어적 복사 불필요
- **구체적 규칙**:
  1. 객체 상태 변경 메서드(변경자) 제공 금지
  2. 클래스 확장 불가능하게 설계
  3. 모든 필드를 final로 선언
  4. 모든 필드를 private으로 선언
  5. 가변 객체에 대한 접근 통제

## 세부 내용 (Details)

### 1. 불변 클래스 구현의 실제

#### 기본적인 불변 클래스 구현
```java
public final class ImmutablePoint {
    private final int x;    // final로 선언된 필드
    private final int y;    // 모든 필드가 final

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }  // 변경자(setter) 없음
    public int getY() { return y; }

    public ImmutablePoint translate(int dx, int dy) {
        // 새로운 객체를 반환
        return new ImmutablePoint(x + dx, y + dy);
    }
}
```

**구현 설명**:
1. **불변성 보장 방법**
   - `final` 클래스로 선언하여 상속 방지
   - 모든 필드를 `private`과 `final`로 선언
   - setter 메서드를 제공하지 않음
   - 수정이 필요한 경우 새로운 객체 생성

2. **메서드 동작 방식**
   - `translate`는 기존 객체를 수정하지 않음
   - 대신 새로운 객체를 생성하여 반환
   - 원본 객체는 항상 불변 상태 유지

#### 함수형 프로그래밍 스타일 예시
```java
public final class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    // 함수형 스타일의 메서드들
    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex times(Complex c) {
        return new Complex(re * c.re - im * c.im,
                          re * c.im + im * c.re);
    }
}
```

**함수형 접근 방식 설명**:
1. **함수형 프로그래밍의 특징**
   - 메서드가 객체의 상태를 변경하지 않음
   - 연산 결과로 새로운 객체를 반환
   - 원본 객체는 불변으로 유지

2. **이점**
   - 스레드 안전성 보장
   - 메서드 호출의 결과 예측 용이
   - 코드 이해와 디버깅이 쉬움

### 2. 불변 객체의 최적화 전략

#### 객체 캐싱을 통한 성능 최적화
```java
public final class Color {
    private static final Map<String, Color> CACHE = new HashMap<>();
    private final String name;
    private final int rgb;

    private Color(String name, int rgb) {  // private 생성자
        this.name = name;
        this.rgb = rgb;
    }

    public static Color valueOf(String name, int rgb) {
        // 캐시된 객체가 있으면 재사용
        return CACHE.computeIfAbsent(name, 
            k -> new Color(name, rgb));
    }
}
```

**캐싱 최적화 설명**:
1. **구현 방식**
   - private 생성자와 정적 팩터리 메서드 패턴 사용
   - Map을 이용한 객체 캐싱
   - 이미 생성된 객체 재사용

2. **성능상 이점**
   - 객체 생성 비용 감소
   - 메모리 사용량 절감
   - 가비지 컬렉션 부하 감소

#### 가변 동반 클래스를 통한 최적화
```java
// 불변 클래스
public final class String {
    private final char[] value;
    
    // ... 불변 객체 구현
}

// 가변 동반 클래스
public final class StringBuilder {
    private char[] value;
    
    public StringBuilder append(String str) {
        // 내부 버퍼에 문자열 추가
        // 필요시 버퍼 크기 조정
        return this;  // 메서드 체이닝 지원
    }
}

// 사용 예시
StringBuilder builder = new StringBuilder();
for (String item : items) {
    builder.append(item);  // 단일 객체에서 모든 연산 수행
}
String result = builder.toString();  // 최종적으로 불변 객체 생성
```

**가변 동반 클래스 활용 설명**:
1. **목적**
   - 여러 단계의 연산을 효율적으로 처리
   - 중간 객체 생성 최소화
   - 성능 최적화

2. **장점**
   - 메모리 사용량 감소
   - 가비지 컬렉션 부하 감소
   - 전체 연산 효율성 증가

### 3. 방어적 복사와 안전성

#### 불변 객체의 내부 배열 보호
```java
public final class SafeArrayHolder {
    private final int[] array;  // 내부 배열

    public SafeArrayHolder(int[] array) {
        // 생성자에서 방어적 복사
        this.array = array.clone();
    }

    public int[] getArray() {
        // getter에서도 방어적 복사
        return array.clone();
    }
}
```

**방어적 복사 설명**:
1. **필요성**
   - 배열은 가변 객체
   - 외부에서 내부 배열 수정 가능성 차단
   - 불변성 보장을 위해 필수

2. **구현 포인트**
   - 생성자에서 입력 배열 복사
   - getter에서 내부 배열 복사
   - clone() 사용하여 깊은 복사 수행

### 4. 자주 발생하는 질문과 답변

Q: BigInteger와 BigDecimal은 왜 주의가 필요한가요?
A: 하위 호환성 때문에 불변성이 완벽하지 않습니다:
```java
public static BigInteger safeInstance(BigInteger val) {
    // 입력값이 진짜 BigInteger인지 확인하고 방어적 복사
    return val.getClass() == BigInteger.class ? 
           val : new BigInteger(val.toByteArray());
}
```

Q: 불변 객체의 성능은 항상 나쁜가요?
A: 상황에 따라 다릅니다:
```java
// 캐시를 활용한 지연 초기화 예시
public final class LazyInit {
    private final byte[] data;
    private volatile byte[] hash;  // 캐시 필드

    public byte[] getHash() {
        byte[] h = hash;
        if (h == null) {  // 첫 접근시에만 계산
            h = hash = computeHash(data);
        }
        return h.clone();  // 방어적 복사
    }
}
```

## 요약 (Summary)

1. **불변 클래스 설계 원칙**
   - 클래스는 꼭 필요한 경우가 아니면 불변으로 설계
   - 불변으로 만들 수 없는 클래스라도 변경 가능성을 최소화
   - 합당한 이유가 없다면 모든 필드는 private final로 선언

2. **성능 최적화 전략**
   - 다단계 연산은 가변 동반 클래스 제공
   - 자주 사용되는 값은 캐싱 고려
   - 필요한 경우 지연 초기화 활용

3. **안전성 보장 방법**
   - 생성자와 접근자에서 방어적 복사 수행
   - 내부 가변 컴포넌트 보호
   - 하위 클래스 생성 금지