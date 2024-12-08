# Item 6: 불필요한 객체 생성을 피하라

동일한 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 것이 대부분의 상황에서 적절합니다. 특히 불변 객체는 언제든 재사용이 가능합니다.

## 객체 생성 비용 최적화 방법

### 1. 문자열 객체 생성
```java
// 나쁜 예
String s1 = new String("hello");  // 매번 새로운 인스턴스 생성

// 좋은 예
String s2 = "hello";  // String Pool에서 재사용
```

- `new String()`은 항상 새로운 객체를 생성
- 문자열 리터럴은 String Pool을 통해 재사용
- 같은 가상 머신 내에서 동일한 문자열 리터럴은 같은 객체를 참조

### 2. 비싼 객체의 캐싱

#### 잘못된 정규표현식 사용
```java
// 나쁜 예
public class EmailValidator {
    static boolean isEmailValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");  // 매번 Pattern 객체 생성
    }
}
```

#### 개선된 정규표현식 사용
```java
// 좋은 예
public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");  // 한 번만 생성하고 재사용
    
    static boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
```

- Pattern 객체는 생성 비용이 높음
- 정적 final 필드로 캐싱하여 재사용
- 반복적인 정규표현식 매칭 시 성능 대폭 향상

### 3. 오토박싱 주의

#### 성능이 저하되는 코드
```java
// 나쁜 예
public class SumCalculator {
    public static long sum() {
        Long sum = 0L;  // 래퍼 클래스 사용
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            sum += i;   // 매번 오토박싱 발생
        }
        return sum;
    }
}
```

#### 개선된 코드
```java
// 좋은 예
public class SumCalculator {
    public static long sum() {
        long sum = 0L;  // 기본 타입 사용
        for (long i = 0; i <= Integer.MAX_VALUE; i++) {
            sum += i;   // 오토박싱 없음
        }
        return sum;
    }
}
```

- 래퍼 클래스 대신 기본 타입 사용
- 불필요한 오토박싱 회피
- 성능 대폭 향상

### 4. 적절한 타입 선택

#### 상황에 따른 타입 선택
```java
public class Product {
    // 가격이 없을 수 있는 경우
    private Integer price;  // null 가능

    // 가격이 반드시 있어야 하는 경우
    private int price;     // 0이상의 값
}
```

- null 표현이 필요한 경우 래퍼 클래스 사용
- 기본값이 의미있는 경우 기본 타입 사용
- 상황에 맞는 적절한 타입 선택 중요

### 5. 객체 재사용 vs 방어적 복사

#### 재사용이 적절한 경우
```java
public class DateFormatter {
    private static final SimpleDateFormat formatter = 
        new SimpleDateFormat("yyyy-MM-dd");  // 스레드 안전성 고려 필요
    
    public static String format(Date date) {
        return formatter.format(date);
    }
}
```

#### 방어적 복사가 필요한 경우
```java
public final class Period {
    private final Date start;
    private final Date end;
    
    public Period(Date start, Date end) {
        // 방어적 복사
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException();
        }
    }
}
```

## 객체 생성 비용 최적화 시 고려사항

1. **성능 측정**
    - 실제 성능 저하가 있는지 확인
    - 과도한 최적화 지양

2. **코드 가독성**
    - 최적화로 인해 코드가 복잡해지지 않도록 주의
    - 명확성과 유지보수성 고려

3. **상황에 따른 판단**
    - 객체 생성 비용이 실제로 문제가 되는지 확인
    - 재사용과 방어적 복사 중 적절한 방식 선택

## 결론

1. 동일한 기능의 객체는 재사용하라
2. 비용이 비싼 객체는 캐싱하여 재사용하라
3. 기본 타입과 래퍼 타입을 적절히 사용하라
4. 방어적 복사가 필요한 상황인지 검토하라
5. 불필요한 객체 생성을 피하되, 과도한 최적화는 피하라

### 체크리스트
- [ ] 문자열 생성 시 new String() 사용을 피했는가?
- [ ] 비싼 객체는 정적 final 필드로 캐싱했는가?
- [ ] 불필요한 오토박싱이 없는가?
- [ ] 상황에 맞는 적절한 타입을 선택했는가?
- [ ] 객체 재사용과 방어적 복사 중 적절한 방식을 선택했는가?