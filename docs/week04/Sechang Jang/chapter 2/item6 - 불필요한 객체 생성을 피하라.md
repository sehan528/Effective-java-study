# Item 6: 불필요한 객체 생성을 피하라

## 핵심 개념 (Main Ideas)

### 1. 객체 재사용의 중요성
- **정의**: 동일한 기능의 객체를 매번 생성하지 않고 재사용하는 것
- **목적**: 메모리 사용량 감소와 성능 향상
- **효과**: 특히 불변 객체는 안전하게 재사용 가능

### 2. 객체 생성 비용 최적화
- **원칙**: 비용이 큰 객체는 캐싱하여 재사용
- **이유**: 불필요한 객체 생성은 성능 저하의 원인
- **방법**: 정적 필드나 캐시를 활용한 재사용

## 세부 내용 (Details)

### 1. 문자열 객체의 생성과 재사용

#### 문자열 생성의 올바른 방법
```java
// 잘못된 방식 - 불필요한 객체 생성
public class StringExample {
    public String greeting() {
        String hello = new String("Hello");  // 매번 새로운 객체 생성
        return hello + " World";
    }
}

// 올바른 방식 - 문자열 리터럴 사용
public class StringExample {
    public String greeting() {
        String hello = "Hello";  // String Pool에서 재사용
        return hello + " World";
    }
}
```

**코드 분석**:
1. **잘못된 방식의 문제점**
   - new String()은 항상 새로운 객체를 힙 메모리에 생성
   - 동일한 문자열에 대해 불필요한 객체 중복 생성
   - 가비지 컬렉션 부담 증가

2. **올바른 방식의 장점**
   - JVM의 String Pool을 활용한 재사용
   - 메모리 사용량 감소
   - 문자열 비교 시 성능 향상

### 2. 비용이 큰 객체의 재사용

#### 정규식 패턴 객체 재사용
```java
// 비효율적인 구현
public class PatternExample {
    // 메서드 호출마다 Pattern 객체 생성
    public boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    // 여러 이메일 검증 - 성능 문제 발생
    public List<String> filterValidEmails(List<String> emails) {
        return emails.stream()
            .filter(this::isValidEmail)  // 매번 Pattern 객체 생성
            .collect(Collectors.toList());
    }
}

// 효율적인 구현
public class PatternExample {
    // Pattern 객체를 정적 필드로 캐싱
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public List<String> filterValidEmails(List<String> emails) {
        return emails.stream()
            .filter(this::isValidEmail)  // 캐싱된 Pattern 재사용
            .collect(Collectors.toList());
    }
}
```

**코드 분석**:
1. **비효율적인 구현의 문제점**
   - matches() 메서드 내부에서 매번 Pattern 객체 생성
   - 정규식 컴파일 비용이 반복적으로 발생
   - 대량의 데이터 처리 시 성능 저하 심각

2. **효율적인 구현의 장점**
   - Pattern 객체를 한 번만 생성하고 재사용
   - 정규식 컴파일 비용 최소화
   - 스레드 안전한 구현 가능

### 3. 오토박싱과 언박싱 주의

#### 기본 타입과 래퍼 타입의 선택
```java
// 성능이 저하되는 구현
public class AutoboxingExample {
    // 불필요한 래퍼 타입 사용
    private static Long sum = 0L;  // 래퍼 클래스
    
    public static void addNumbers() {
        for (int i = 0; i < 1000000; i++) {
            sum += i;  // 매번 오토박싱 발생
        }
    }
}

// 최적화된 구현
public class AutoboxingExample {
    // 기본 타입 사용
    private static long sum = 0L;  // primitive 타입
    
    public static void addNumbers() {
        for (int i = 0; i < 1000000; i++) {
            sum += i;  // 오토박싱 없음
        }
    }
    
    // 래퍼 타입이 필요한 경우
    public static List<Long> getEvenNumbers(int max) {
        return IntStream.rangeClosed(1, max)
            .filter(i -> i % 2 == 0)
            .mapToObj(Long::valueOf)  // 명시적 박싱
            .collect(Collectors.toList());
    }
}
```

**코드 분석**:
1. **성능 저하 요인**
   - 불필요한 래퍼 객체 생성
   - 반복문에서 지속적인 박싱/언박싱
   - 메모리 사용량 증가

2. **최적화 포인트**
   - 가능한 기본 타입 사용
   - 컬렉션이나 제네릭에서만 래퍼 타입 사용
   - 명시적 박싱으로 의도 표현

## 자주 발생하는 질문과 답변

Q: 객체 재사용과 방어적 복사는 어떻게 구분해야 하나요?
A: 다음 예시로 설명드리겠습니다:

```java
// 재사용이 안전한 경우 - 불변 객체
public class DateFormatter {
    // SimpleDateFormat은 스레드 안전하지 않아 ThreadLocal 사용
    private static final ThreadLocal<SimpleDateFormat> formatter =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public static String formatDate(Date date) {
        return formatter.get().format(date);
    }
}

// 방어적 복사가 필요한 경우 - 가변 객체
public class Period {
    private final Date start;
    private final Date end;
    
    public Period(Date start, Date end) {
        // 방어적 복사로 불변성 보장
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        
        // 유효성 검사는 복사본으로 진행
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException();
        }
    }
    
    // 접근자도 방어적 복사 제공
    public Date getStart() {
        return new Date(start.getTime());
    }
}
```

**코드 분석**:
1. **재사용이 안전한 경우**
   - 불변 객체나 스레드 안전한 객체
   - 상태 변경이 없는 유틸리티성 객체
   - ThreadLocal을 통한 스레드 안전성 확보

2. **방어적 복사가 필요한 경우**
   - 가변 객체를 클래스의 필드로 사용
   - 외부로부터 객체의 불변성을 보호해야 할 때
   - getter에서도 방어적 복사 필요

Q: 캐시 사용 시 메모리 관리는 어떻게 해야 하나요?
A: WeakHashMap을 사용한 예시로 설명드리겠습니다:

```java
public class ImageProcessor {
    // WeakHashMap을 사용한 캐시
    private static final Map<String, WeakReference<BufferedImage>> imageCache = 
        new WeakHashMap<>();
    
    public static BufferedImage getImage(String path) {
        // 캐시된 이미지가 있다면 반환
        WeakReference<BufferedImage> ref = imageCache.get(path);
        BufferedImage image = (ref != null) ? ref.get() : null;
        
        if (image == null) {
            // 이미지 로드 및 캐시
            image = loadImage(path);
            imageCache.put(path, new WeakReference<>(image));
        }
        
        return image;
    }
    
    private static BufferedImage loadImage(String path) {
        // 실제 이미지 로딩 로직
        return null;  // 예시를 위한 반환
    }
}
```

**코드 분석**:
1. **메모리 관리 전략**
   - WeakReference 사용으로 메모리 압박 시 자동 해제
   - 필요한 객체만 캐시에 유지
   - 명시적인 캐시 크기 제한 없음

2. **구현 포인트**
   - 캐시 키의 적절한 선택
   - 참조 객체의 생명주기 관리
   - 메모리 누수 방지

## 요약 (Summary)

1. **객체 생성 최적화 원칙**
   - 비용이 큰 객체는 재사용
   - 불변 객체는 안전하게 공유
   - 가변 객체는 방어적 복사 고려

2. **실무 적용 가이드**
   - 성능 측정을 통한 최적화 결정
   - 코드 복잡도와 유지보수성 고려
   - 적절한 캐싱 전략 선택

3. **주의사항**
   - 과도한 최적화 지양
   - 코드 명확성 유지
   - 스레드 안전성 고려