# Item 1: 생성자 대신 정적 팩터리 메서드를 고려하라

## 핵심 개념 (Main Ideas)

### 1. 정적 팩터리 메서드의 개념
- **정의**: 객체의 생성을 전담하는 클래스의 static 메서드
- **목적**: 객체 생성의 유연성 확보와 가독성 향상
- **효과**: 생성자보다 더 명확한 이름 사용, 객체 생성 과정의 캡슐화, 반환 객체의 유연한 선택 가능

### 2. 일반 생성자와의 차이
- **원칙**: 객체 생성에 대한 의도와 방식을 더 명확하게 표현
- **이유**: 생성자는 클래스명으로 고정되어 의미 전달이 제한적
- **방법**: 목적에 맞는 이름의 static 메서드를 통해 객체 생성

## 세부 내용 (Details)

### 1. 명확한 이름을 통한 객체 생성

#### 기본 예시
```java
public class User {
    private String id;
    private String email;
    private String nickname;

    // 기존의 생성자 방식
    public User(String id, String email) {  // 생성자는 용도 파악이 어려움
        this.id = id;
        this.email = email;
    }
    
    // 정적 팩터리 메서드 방식
    public static User withEmail(String id, String email) {  // 이메일로 생성함을 명확히 표현
        User user = new User();
        user.id = id;
        user.email = validateEmail(email);  // 이메일 유효성 검증 포함
        return user;
    }
    
    public static User withSocialId(String socialId) {  // 소셜 로그인용 생성임을 명시
        User user = new User();
        user.id = "SOCIAL_" + socialId;
        return user;
    }

    private static String validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email;
    }
}
```

**이 코드가 설명하는 것**:
1. **명명의 명확성**
   - 생성자는 `new User(id, email)`로만 표현 가능
   - 정적 팩터리는 `withEmail`, `withSocialId`처럼 의도를 명확히 표현
   - 메서드 이름으로 객체 생성 목적을 명시적으로 전달

2. **부가 로직 포함**
   - 생성자는 단순 필드 초기화만 수행
   - 정적 팩터리는 이메일 검증 같은 부가 로직 포함 가능
   - 객체 생성 전에 유효성 검증이나 데이터 변환 수행 가능

### 2. 객체 캐싱과 재사용

#### 싱글톤과 캐싱 예시
```java
public class DatabaseConnector {
    private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    private static final Map<String, DatabaseConnector> CACHE = new HashMap<>();
    
    private final String connectionString;
    
    private DatabaseConnector() {  // private 생성자로 외부 생성 제한
        this.connectionString = "default";
    }
    
    private DatabaseConnector(String connectionString) {
        this.connectionString = connectionString;
    }
    
    // 싱글톤 인스턴스 반환
    public static DatabaseConnector getInstance() {
        return INSTANCE;  // 항상 같은 인스턴스 반환
    }
    
    // 커넥션별 인스턴스 캐싱
    public static DatabaseConnector getConnection(String connectionString) {
        return CACHE.computeIfAbsent(connectionString, 
            k -> new DatabaseConnector(k));  // 캐시에 없으면 새로 생성
    }
}
```

**중요 포인트**:
1. **인스턴스 제어**
   - private 생성자로 무분별한 객체 생성 방지
   - 정적 팩터리로만 인스턴스 획득 가능
   - 메모리와 리소스 사용 최적화

2. **캐싱 메커니즘**
   - 이미 생성된 인스턴스 재사용
   - 불필요한 객체 생성 방지
   - 데이터베이스 연결 같은 비용이 큰 객체에 효과적

### 3. 하위 타입 객체 반환

#### 인터페이스 기반 팩터리 예시
```java
public interface PaymentProcessor {
    boolean processPayment(BigDecimal amount);
}

public class PaymentFactory {
    public static PaymentProcessor getProcessor(String type) {
        return switch (type) {
            case "CREDIT" -> new CreditCardProcessor();
            case "DEBIT" -> new DebitCardProcessor();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }
    
    private static class CreditCardProcessor implements PaymentProcessor {
        @Override
        public boolean processPayment(BigDecimal amount) {
            // 신용카드 결제 처리 로직
            return true;
        }
    }
    
    private static class DebitCardProcessor implements PaymentProcessor {
        @Override
        public boolean processPayment(BigDecimal amount) {
            // 직불카드 결제 처리 로직
            return true;
        }
    }
}
```

**설계의 장점**:
1. **구현 은닉**
   - 실제 구현 클래스를 클라이언트로부터 숨김
   - 인터페이스 기반 프로그래밍 촉진
   - 구현 변경에 유연하게 대응 가능

2. **조건부 객체 생성**
   - 상황에 따라 적절한 구현체 선택
   - 시스템 설정이나 실행 환경에 따른 유연한 객체 생성
   - 새로운 구현체 추가가 용이

## 자주 발생하는 질문과 답변

Q: 정적 팩터리 메서드의 명명 컨벤션은 어떻게 되나요?
A: 일반적으로 다음과 같은 네이밍 패턴을 사용합니다:

```java
public class Order {
    private String customer;
    private List<Item> items;
    private LocalDate orderDate;

    // from: 단일 매개변수로 인스턴스 생성
    public static Order from(OrderRequest request) {
        Order order = new Order();
        order.customer = request.getCustomer();
        order.items = request.getItems();
        return order;
    }

    // of: 여러 매개변수로 인스턴스 생성
    public static Order of(String customer, List<Item> items) {
        Order order = new Order();
        order.customer = customer;
        order.items = new ArrayList<>(items);
        return order;
    }

    // valueOf: 자세한 버전의 변환 메서드
    public static Order valueOf(Map<String, Object> orderData) {
        Order order = new Order();
        order.customer = (String) orderData.get("customer");
        order.items = (List<Item>) orderData.get("items");
        order.orderDate = LocalDate.parse((String) orderData.get("orderDate"));
        return order;
    }

    // getInstance: 싱글톤 또는 캐시된 인스턴스 반환
    private static final Order EMPTY_ORDER = new Order();
    public static Order getInstance() {
        return EMPTY_ORDER;  // 같은 빈 주문 인스턴스 재사용
    }

    // create: 새로운 인스턴스를 생성함을 명확히 함
    public static Order createEmptyOrder() {
        return new Order();  // 항상 새로운 인스턴스 생성
    }
}
```

각 명명 패턴의 특징과 용도는 다음과 같습니다:
- `from`: API 응답이나 DTO처럼 다른 타입의 단일 매개변수를 변환할 때 사용
- `of`: 여러 매개변수를 받아 인스턴스를 생성할 때 사용
- `valueOf`: 문자열이나 맵 등 상세한 변환 과정이 필요할 때 사용
- `getInstance`: 싱글톤이나 캐시된 인스턴스를 재사용할 때 사용
- `create` 또는 `newInstance`: 매번 새로운 인스턴스 생성이 필요할 때 사용
+ 이는 암시적인 약속 표현이며 기계적으로 지키는것이 아님.

Q: 정적 팩터리 메서드의 단점은 무엇인가요?
A: 주요 단점은 다음과 같습니다:
1. private 생성자로 인한 상속 불가능
2. 다른 static 메서드와 구분이 어려울 수 있음
3. Javadoc에서 생성자처럼 특별 취급되지 않음

Q: 생성자 대신 정적 팩터리를 써야 할 때는 언제인가요?
A: 다음 상황에서 정적 팩터리가 유용합니다:
1. 생성 의도를 명확히 표현해야 할 때
2. 인스턴스 통제가 필요할 때
3. 하위 타입 객체 반환이 필요할 때
4. 매개변수에 따라 다른 클래스의 객체를 반환해야 할 때

## 요약 (Summary)

1. **장점**
   - 이름으로 객체 생성 의도를 명확히 표현
   - 호출될 때마다 새로운 객체를 생성할 필요가 없음
   - 반환 타입의 하위 타입 객체를 반환할 수 있음
   - 입력 매개변수에 따라 다른 클래스의 객체를 반환할 수 있음

2. **주의사항**
   - 상속을 위한 public/protected 생성자가 필요한 경우 사용 불가
   - 정적 팩터리 메서드만 제공하면 javadoc에서 인스턴스화 방법을 찾기 어려움
   - 프로그래머가 찾기 어려울 수 있으므로 명명 규칙을 잘 따라야 함

3. **실무 적용 포인트**
   - 생성자와 정적 팩터리 메서드의 장단점을 고려하여 선택
   - 객체 생성의 유연성이 필요한 경우 정적 팩터리 메서드 선호
   - 명확한 명명 규칙을 따라 가독성 확보