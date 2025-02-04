# Item 17: 변경 가능성을 최소화하라  

## Why? (왜 이렇게 해야 할까?)  

1. **안전성**: 값이 안 바뀌니 에러나 버그 걱정 줄어듦.  
2. **스레드 안전**: 불변 객체는 동시 접근에 안전.  
3. **가독성**: 한 번 만들어진 객체는 변하지 않으니 코드 이해가 쉬움.  
4. **성능**: 캐싱 활용 가능, 방어적 복사 불필요.  

**요약**: “변경 가능성을 최소화하면 코드가 덜 골치 아파지고, 유지보수가 쉬워집니다!”  


## Please Do (이렇게 하세요!)  

### 1. **불변 객체 만들기: 5가지 규칙**  
- **setter 메서드 금지**: 값을 바꿀 수 없게 만드세요.  
- **상속 차단**: 클래스에 `final`을 붙이세요.  
- **모든 필드는 `final`로**: 값 변경 불가!  
- **모든 필드는 `private`로**: 외부 접근 차단!  
- **가변 객체는 복사 후 저장**: 안전장치 마련.  

#### 예시: 기본 불변 클래스  
```java
public final class ImmutablePoint {
    private final int x;
    private final int y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public ImmutablePoint move(int dx, int dy) {
        return new ImmutablePoint(x + dx, y + dy);
    }
}
```  

- **Point** 객체는 값이 한 번 설정되면 끝.  
- `move` 메서드는 기존 객체를 수정하지 않고 새로운 객체를 반환.  


### 2. **불변성 + 함수형 프로그래밍 스타일**  
- 메서드가 객체 상태를 안 바꿈.  
- 항상 새로운 객체를 반환.  

#### 예시: 복소수 클래스  
```java
public final class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex add(Complex other) {
        return new Complex(re + other.re, im + other.im);
    }

    public Complex subtract(Complex other) {
        return new Complex(re - other.re, im - other.im);
    }
}
```  

- **이점**:  
  - 안전한 멀티스레드 연산.  
  - 값 변경 걱정 없이 연산 로직 작성 가능.  


### 3. **캐싱으로 성능 최적화**  
#### 예시: 정적 팩토리 메서드 + 캐싱  
```java
public final class Color {
    private static final Map<String, Color> CACHE = new HashMap<>();
    private final String name;
    private final int rgb;

    private Color(String name, int rgb) {
        this.name = name;
        this.rgb = rgb;
    }

    public static Color valueOf(String name, int rgb) {
        return CACHE.computeIfAbsent(name, k -> new Color(name, rgb));
    }
}
```  

- **캐싱 이점**:  
  - 객체 재활용 → 메모리 절약.  
  - 객체 생성 비용 감소.  


### 4. **가변 동반 클래스 사용**  
- 성능이 중요한 경우, 중간 단계 작업에 가변 클래스를 사용.  
- 최종적으로 불변 객체로 변환.  

#### 예시: String과 StringBuilder  
```java
// 불변 클래스
public final class String {
    private final char[] value;
}

// 가변 동반 클래스
public final class StringBuilder {
    private char[] value;

    public StringBuilder append(String str) {
        // 내부 버퍼에 문자열 추가
        return this;
    }

    public String toString() {
        return new String(value);
    }
}
```  

- **이점**:  
  - 성능 최적화: 중간 객체 생성을 최소화.  
  - 최종 데이터는 여전히 불변.  


### 5. **방어적 복사로 안전성 확보**  
#### 예시: 내부 배열 보호  
```java
public final class SafeArrayHolder {
    private final int[] array;

    public SafeArrayHolder(int[] array) {
        this.array = array.clone();  // 방어적 복사
    }

    public int[] getArray() {
        return array.clone();  // 또 방어적 복사
    }
}
```  

- **필수 이유**:  
  - 배열은 기본적으로 가변 객체.  
  - 외부에서 원본 데이터 변경 방지.  


## Please Don't (이렇게는 하지 마세요!)  

### 1. **setter로 필드 변경 허용**  
```java
public class BadExample {
    private int value;

    public void setValue(int value) {
        this.value = value;
    }
}
```  
- **문제점**: 값을 아무렇게나 바꿀 수 있음.  

### 2. **public 필드 노출**  
```java
public class AnotherBadExample {
    public int value;  // 외부에서 바로 접근 가능
}
```  
- **문제점**: API 안정성 없음, 코드 혼란.  


## 요약 (Cheat Sheet)  

- **Do**: 클래스는 불변으로 설계.  
- **Do**: 필드는 `private final`로 선언.  
- **Do**: 가변 객체는 방어적 복사.  
- **Don’t**: setter로 필드를 변경 가능하게 만들지 말 것.  
- **Don’t**: public 필드를 그대로 노출하지 말 것.
