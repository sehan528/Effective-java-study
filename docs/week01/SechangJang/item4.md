# Item 4: 인스턴스화를 막으려거든 private 생성자를 사용하라

정적(static) 메서드와 정적 필드만을 담은 유틸리티 클래스는 인스턴스로 만들어 쓰기 위한 것이 아닙니다. 하지만 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어주어 의도치 않은 인스턴스화가 가능해집니다.

## 잘못된 유틸리티 클래스 구현

### 1. 생성자 명시하지 않은 경우
```java
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
```
**문제점:**
- 컴파일러가 자동으로 public 기본 생성자를 생성
- 누구나 `new StringUtils()`로 불필요한 인스턴스 생성 가능

### 2. abstract 클래스로 구현한 경우
```java
public abstract class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
}
```
**문제점:**
- 하위 클래스를 만들어 인스턴스화가 가능
- 상속해서 사용하라는 오해를 줄 수 있음

### 3. 잘못된 패턴 유틸리티 예제
```java
public class PatternUtil {
    private static final String PATTERN = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\";
    
    public static boolean isEmailValid(String email) {
        return email.matches(PATTERN);
    }
    
    public String getPattern() {  // 인스턴스 메서드 (잘못된 설계)
        return PATTERN;
    }
}
```
**문제점:**
- 정적 필드에 접근하기 위해 인스턴스 메서드를 추가
- 불필요한 인스턴스화 가능성 열어둠

## 올바른 유틸리티 클래스 구현

### 1. 기본적인 private 생성자 구현
```java
public class StringUtils {
    // 인스턴스화 방지
    private StringUtils() {
        throw new AssertionError();
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}
```

### 2. 패턴 유틸리티 올바른 구현
```java
public class PatternUtil {
    private static final String EMAIL_PATTERN = 
        "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}\\";
    
    private PatternUtil() {
        throw new AssertionError();
    }
    
    public static boolean isEmailValid(String email) {
        return email.matches(EMAIL_PATTERN);
    }
    
    public static String getEmailPattern() {  // 정적 메서드로 제공
        return EMAIL_PATTERN;
    }
}
```

## private 생성자의 효과

1. **인스턴스화 방지**
    - 클래스 외부에서 생성자에 접근 불가
    - 실수로 인한 인스턴스화 방지

2. **상속 방지**
    - private 생성자로 인해 상속 불가능
    - 정적 유틸리티 클래스의 목적에 부합

3. **명시적인 용도 표현**
    - 주석을 통해 클래스의 용도를 명확히 표현 가능
```java
public class DatabaseUtils {
    /**
     * 이 클래스는 유틸리티 클래스이므로 인스턴스화를 금지합니다.
     */
    private DatabaseUtils() {
        throw new AssertionError();
    }
    // ... 정적 메서드들
}
```

## 실제 사용 예시

### 자바 표준 라이브러리의 유틸리티 클래스
- `java.lang.Math`: 수학 연산 유틸리티
- `java.util.Arrays`: 배열 조작 유틸리티
- `java.util.Collections`: 컬렉션 관련 유틸리티

### 흔한 유틸리티 클래스 사례
```java
public class DateUtils {
    private DateUtils() {
        throw new AssertionError();
    }
    
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    public static Date parseDate(String dateStr, String pattern) 
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateStr);
    }
}
```

## 결론

1. 유틸리티 클래스는 인스턴스화를 막아야 함
2. private 생성자와 AssertionError를 통해 확실히 막을 것
3. 상속 방지를 위해서도 private 생성자가 필요
4. 주석을 통해 인스턴스화 금지 의도를 명확히 밝힐 것

### 체크리스트
- [ ] private 생성자가 있는가?
- [ ] AssertionError를 던지도록 구현되었는가?
- [ ] 모든 메서드가 static인가?
- [ ] 상속이 방지되었는가?
- [ ] 용도가 주석으로 명확히 설명되었는가?