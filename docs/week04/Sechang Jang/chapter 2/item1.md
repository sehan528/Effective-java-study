# Item 1: 생성자 대신 정적 팩터리 메서드를 고려하라

## 핵심 개념 (Main Ideas)

### 1. 정적 팩터리 메서드의 개념
- **정의**: 객체 생성을 담당하는 클래스의 정적(static) 메서드
- **목적**: 생성자보다 더 명확하고 유연한 객체 생성 방법 제공
- **효과**: 객체 생성 과정을 더 잘 통제하고 가독성을 높임

### 2. 기존 생성자와의 비교
```java
// 기존의 생성자 방식
public class User {
    private String name;
    private String email;
    
    // 생성자로는 의도를 명확히 표현하기 어려움
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

// 정적 팩터리 메서드 방식
public class User {
    private String name;
    private String email;
    
    // private 생성자 - 외부에서 직접 객체 생성을 제한
    private User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // 정적 팩터리 메서드 - 생성 의도를 이름으로 표현
    public static User createWithEmail(String name, String email) {
        // 이메일 유효성 검증 등의 로직을 포함할 수 있음
        validateEmail(email);
        return new User(name, email);
    }
}
```

**코드 설명**:
1. **생성자의 한계**
   - 이름이 클래스명으로 고정되어 의도 전달이 어려움
   - 동일한 매개변수를 갖는 생성자를 여러 개 만들 수 없음
   - 객체 생성 과정을 제어할 수 없음

2. **정적 팩터리 메서드의 장점**
   - 메서드 이름으로 객체 생성 의도를 명확히 표현
   - 객체 생성 전에 유효성 검증 등의 로직 수행 가능
   - private 생성자와 함께 사용하여 객체 생성을 제어 가능

## 세부 내용 (Details)

### 1. 인스턴스 통제와 캐싱

#### 싱글턴 패턴 구현
```java
public class DatabaseConnection {
    // 싱글턴(Singleton): 
    // 전체 시스템에서 단 하나의 인스턴스만 존재하도록 보장하는 패턴
    private static final DatabaseConnection INSTANCE = 
        new DatabaseConnection();
        
    // 데이터베이스 설정 정보
    private final String url;
    private final Properties properties;
    
    // private 생성자로 외부에서의 인스턴스 생성 방지
    private DatabaseConnection() {
        // 설정 파일에서 데이터베이스 정보 로드
        this.url = loadUrl();
        this.properties = loadProperties();
    }
    
    // 정적 팩터리 메서드로 인스턴스 제공
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }
    
    // 실제 데이터베이스 작업 메서드
    public void executeQuery(String sql) {
        // 쿼리 실행 로직
    }
}

// 사용 예시
public class OrderService {
    public void processOrder(Order order) {
        // 항상 같은 데이터베이스 연결 인스턴스를 사용
        DatabaseConnection db = DatabaseConnection.getInstance();
        db.executeQuery("INSERT INTO orders ...");
    }
}
```

**이 코드가 설명하는 개념들**:
1. **싱글턴 패턴**
   - 정의: 특정 클래스의 인스턴스가 시스템에서 단 하나만 존재하도록 보장하는 디자인 패턴
   - 사용 이유: 데이터베이스 연결, 설정 관리 등 공유 리소스 관리에 적합
   - 구현 방법: private 생성자와 정적 팩터리 메서드 조합

2. **인스턴스 통제의 이점**
   - 메모리 사용 최적화: 불필요한 객체 생성 방지
   - 일관성 보장: 동일한 설정과 상태 유지
   - 리소스 관리: 공유 리소스에 대한 접근 제어

### 2. 객체 캐싱을 통한 성능 최적화

#### 캐시를 활용한 인스턴스 재사용
```java
public final class Color {
    // 캐시(Cache): 자주 사용되는 객체를 미리 생성하여 보관하는 저장소
    private static final Map<String, Color> CACHE = new ConcurrentHashMap<>();
    
    private final String colorCode;  // 색상 코드 (예: "#FF0000")
    
    private Color(String colorCode) {
        // 색상 코드 유효성 검증
        if (!isValidColorCode(colorCode)) {
            throw new IllegalArgumentException(
                "Invalid color code: " + colorCode);
        }
        this.colorCode = colorCode;
    }
    
    // 정적 팩터리 메서드 - 캐시된 인스턴스 반환
    public static Color valueOf(String colorCode) {
        // computeIfAbsent: 키가 없을 때만 새로운 객체 생성
        // 동시성 문제를 자동으로 처리해줌
        return CACHE.computeIfAbsent(
            colorCode.toUpperCase(),
            k -> new Color(k)
        );
    }
    
    // 자주 사용되는 색상은 상수로 제공
    public static final Color RED = valueOf("#FF0000");
    public static final Color GREEN = valueOf("#00FF00");
    public static final Color BLUE = valueOf("#0000FF");
    
    private static boolean isValidColorCode(String code) {
        // 색상 코드 형식 검증 (#RRGGBB)
        return code.matches("^#[0-9A-Fa-f]{6}$");
    }
}

// 사용 예시와 장점 설명
public class ColorExample {
    public void demonstrateColorReuse() {
        // 같은 색상 코드로 여러 번 호출해도 동일한 인스턴스 반환
        Color red1 = Color.valueOf("#FF0000");
        Color red2 = Color.valueOf("#FF0000");
        
        // 동일한 인스턴스임을 보장 (메모리 절약)
        System.out.println(red1 == red2);  // true
        
        // 상수 사용으로 더 명확한 의미 전달
        Color blue = Color.BLUE;  // #0000FF와 같음
    }
}
```

**이 코드가 설명하는 개념들**:
1. **객체 캐싱(Object Caching)**
   - 정의: 자주 사용되는 객체를 미리 생성하여 재사용하는 기법
   - 장점: 메모리 사용량 감소, 객체 생성 비용 절약
   - 사용 시기: 불변 객체나 생성 비용이 큰 객체에 적합

2. **ConcurrentHashMap**
   - 정의: 여러 스레드가 동시에 접근해도 안전한 Map 구현체
   - 사용 이유: 멀티스레드 환경에서 캐시 관리에 적합
   - 특징: 동시성 처리를 자동으로 해결

### 3. 하위 타입 객체 반환

#### 인터페이스 기반 팩터리
```java
// 지불 처리를 위한 인터페이스
public interface PaymentProcessor {
    void processPayment(BigDecimal amount);
    boolean supports(String paymentMethod);
}

// 구체적인 구현체들
public class PaymentProcessorFactory {
    // private 중첩 클래스: 외부에서 직접 접근 불가능
    private static class CreditCardProcessor implements PaymentProcessor {
        @Override
        public void processPayment(BigDecimal amount) {
            // 신용카드 결제 처리 로직
            System.out.println("신용카드 결제: " + amount);
        }
        
        @Override
        public boolean supports(String paymentMethod) {
            return "CREDIT_CARD".equals(paymentMethod);
        }
    }
    
    private static class BankTransferProcessor implements PaymentProcessor {
        @Override
        public void processPayment(BigDecimal amount) {
            // 계좌이체 처리 로직
            System.out.println("계좌이체: " + amount);
        }
        
        @Override
        public boolean supports(String paymentMethod) {
            return "BANK_TRANSFER".equals(paymentMethod);
        }
    }
    
    // 정적 팩터리 메서드
    public static PaymentProcessor getProcessor(String paymentMethod) {
        return switch (paymentMethod) {
            case "CREDIT_CARD" -> new CreditCardProcessor();
            case "BANK_TRANSFER" -> new BankTransferProcessor();
            default -> throw new IllegalArgumentException(
                "지원하지 않는 결제 방식: " + paymentMethod);
        };
    }
}

// 사용 예시
public class PaymentExample {
    public void processPurchase(BigDecimal amount, String method) {
        // 클라이언트는 구체적인 구현체를 알 필요 없음
        PaymentProcessor processor = 
            PaymentProcessorFactory.getProcessor(method);
            
        // 인터페이스를 통한 결제 처리
        processor.processPayment(amount);
    }
}
```

**이 설계가 제공하는 이점**:
1. **구현 세부사항 은닉**
   - 정의: 클라이언트 코드가 구체적인 구현 클래스를 알 필요가 없게 하는 것
   - 장점: 구현 변경의 유연성 확보, 코드 결합도 감소
   - 활용: 인터페이스 기반 프로그래밍 촉진

2. **팩터리 패턴**
   - 정의: 객체 생성 로직을 캡슐화하는 디자인 패턴
   - 장점: 객체 생성 과정의 유연성, 확장성 향상
   - 사용 시기: 객체 생성 로직이 복잡하거나 변경될 수 있을 때

