# Item 5: 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

## 핵심 개념 (Main Ideas)

### 1. 의존 객체 주입의 본질
- **정의**: 의존하는 자원을 외부에서 주입받는 설계 패턴
- **목적**: 클래스의 재사용성과 테스트 용이성 향상
- **효과**: 결합도를 낮추고 유연성을 높임

### 2. 의존성 관리의 중요성
- **원칙**: 객체는 자신의 의존성을 직접 생성하지 않아야 함
- **이유**: 특정 구현에 종속되는 것을 방지하고 테스트 용이성 확보
- **방법**: 생성자 주입을 통한 의존성 관리

## 세부 내용 (Details)

### 1. 부적절한 의존성 관리 방식

#### 정적 유틸리티를 잘못 사용한 예
```java
public class PaymentProcessor {
    // 구체 클래스에 직접 의존 - 좋지 않은 방식
    private static final PaymentGateway gateway = new KakaoPayGateway();
    
    private PaymentProcessor() {} // 인스턴스화 방지
    
    public static boolean processPayment(String orderId, int amount) {
        // 특정 결제 게이트웨이에 종속된 구현
        return gateway.pay(orderId, amount);
    }
    
    public static PaymentResult getPaymentStatus(String orderId) {
        return gateway.getStatus(orderId);
    }
}

// 이 클래스를 사용하는 주문 처리 코드
public class OrderService {
    public void completeOrder(Order order) {
        // 다른 결제 방식을 사용하고 싶어도 변경 불가능
        boolean paymentSuccess = PaymentProcessor.processPayment(
            order.getId(), 
            order.getTotalAmount()
        );
        
        if (paymentSuccess) {
            order.setStatus(OrderStatus.COMPLETED);
        }
    }
}
```

**이 코드의 상세 분석**:

1. **PaymentGateway 정적 필드**
   ```java
   private static final PaymentGateway gateway = new KakaoPayGateway();
   ```
   - 구체 클래스(KakaoPayGateway)를 직접 생성하여 결합도가 높음
   - static final로 선언되어 한번 초기화되면 변경 불가능
   - 다른 결제 게이트웨이로 교체하려면 코드 수정이 필요

2. **인스턴스화 방지 생성자**
   ```java
   private PaymentProcessor() {}
   ```
   - 유틸리티 클래스처럼 설계되어 상태 관리나 다형성 활용이 불가능
   - 객체 지향적 설계의 장점을 활용할 수 없음

3. **정적 메서드들**
   ```java
   public static boolean processPayment(String orderId, int amount) {
       return gateway.pay(orderId, amount);
   }
   ```
   - 정적 메서드는 오버라이딩이 불가능하여 확장성이 떨어짐
   - 테스트 시 게이트웨이를 모의 객체로 대체할 수 없음
   - 메서드가 항상 동일한 구현체에 의존

4. **OrderService에서의 사용**
   ```java
   public void completeOrder(Order order) {
       boolean paymentSuccess = PaymentProcessor.processPayment(
           order.getId(), 
           order.getTotalAmount()
       );
   }
   ```
   - PaymentProcessor가 제공하는 방식으로만 결제 처리 가능
   - 테스트나 다른 결제 방식 적용이 어려움
   - 시스템 전체가 특정 결제 방식에 종속됨

### 2. 의존 객체 주입을 통한 개선

#### 생성자 주입을 사용한 올바른 설계
```java
// 결제 게이트웨이 인터페이스
public interface PaymentGateway {
    boolean pay(String orderId, int amount);
    PaymentResult getStatus(String orderId);
}

// 구체적인 결제 게이트웨이 구현
public class KakaoPayGateway implements PaymentGateway {
    @Override
    public boolean pay(String orderId, int amount) {
        // 카카오페이 결제 구현
        return true;
    }
    
    @Override
    public PaymentResult getStatus(String orderId) {
        // 결제 상태 조회 구현
        return new PaymentResult(orderId, PaymentStatus.COMPLETED);
    }
}

// 의존 객체 주입을 사용한 결제 처리기
public class PaymentProcessor {
    private final PaymentGateway gateway;
    
    // 생성자를 통한 의존성 주입
    public PaymentProcessor(PaymentGateway gateway) {
        this.gateway = Objects.requireNonNull(gateway);
    }
    
    public boolean processPayment(String orderId, int amount) {
        return gateway.pay(orderId, amount);
    }
    
    public PaymentResult getPaymentStatus(String orderId) {
        return gateway.getStatus(orderId);
    }
}

// 개선된 주문 서비스
public class OrderService {
    private final PaymentProcessor paymentProcessor;
    
    public OrderService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }
    
    public void completeOrder(Order order) {
        boolean paymentSuccess = paymentProcessor.processPayment(
            order.getId(), 
            order.getTotalAmount()
        );
        
        if (paymentSuccess) {
            order.setStatus(OrderStatus.COMPLETED);
        }
    }
}
```

**개선된 설계의 상세 분석**:

1. **인터페이스 정의**
   ```java
   public interface PaymentGateway {
       boolean pay(String orderId, int amount);
       PaymentResult getStatus(String orderId);
   }
   ```
   - 결제 게이트웨이의 핵심 기능을 추상화
   - 구현체들이 반드시 구현해야 할 계약 정의
   - 시스템이 구체적인 결제 방식에 의존하지 않도록 함

2. **구현체 클래스**
   ```java
   public class KakaoPayGateway implements PaymentGateway {
       @Override
       public boolean pay(String orderId, int amount) {
           // 카카오페이 결제 구현
           return true;
       }
   }
   ```
   - 각 결제 방식별로 독립적인 구현 가능
   - 인터페이스 구현으로 타입 안전성 보장
   - 새로운 결제 방식 추가가 기존 코드 수정 없이 가능

3. **의존성 주입을 사용한 프로세서**
   ```java
   public class PaymentProcessor {
       private final PaymentGateway gateway;
       
       public PaymentProcessor(PaymentGateway gateway) {
           this.gateway = Objects.requireNonNull(gateway);
       }
   }
   ```
   - 생성자를 통해 의존성을 외부에서 주입받음
   - null 체크로 안전성 확보
   - final 키워드로 불변성 보장

4. **개선된 주문 서비스**
   ```java
   public class OrderService {
       private final PaymentProcessor paymentProcessor;
       
       public OrderService(PaymentProcessor paymentProcessor) {
           this.paymentProcessor = paymentProcessor;
       }
   }
   ```
   - 결제 처리 책임을 PaymentProcessor에 위임
   - 의존성 주입으로 다양한 결제 방식 사용 가능
   - 단위 테스트 작성이 용이
```java
// 테스트를 위한 모의 게이트웨이
public class TestPaymentGateway implements PaymentGateway {
    private final boolean shouldSucceed;
    
    public TestPaymentGateway(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }
    
    @Override
    public boolean pay(String orderId, int amount) {
        return shouldSucceed;
    }
    
    @Override
    public PaymentResult getStatus(String orderId) {
        return new PaymentResult(orderId, 
            shouldSucceed ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
    }
}

// 테스트 코드
@Test
void paymentProcessorTest() {
    // 성공 케이스 테스트
    PaymentProcessor successProcessor = 
        new PaymentProcessor(new TestPaymentGateway(true));
    assertTrue(successProcessor.processPayment("order1", 1000));
    
    // 실패 케이스 테스트
    PaymentProcessor failProcessor = 
        new PaymentProcessor(new TestPaymentGateway(false));
    assertFalse(failProcessor.processPayment("order2", 1000));
}
```

### 3. 팩터리 메서드 패턴과의 결합

#### 복잡한 의존성 주입 처리
```java
public class PaymentProcessorFactory {
    private final Map<String, Supplier<PaymentGateway>> gatewayFactories;
    
    public PaymentProcessorFactory() {
        gatewayFactories = new HashMap<>();
        gatewayFactories.put("KAKAO", KakaoPayGateway::new);
        gatewayFactories.put("NAVER", NaverPayGateway::new);
        gatewayFactories.put("CARD", CreditCardGateway::new);
    }
    
    public PaymentProcessor createProcessor(String paymentMethod) {
        Supplier<PaymentGateway> factory = gatewayFactories.get(paymentMethod);
        if (factory == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 방식: " + paymentMethod);
        }
        return new PaymentProcessor(factory.get());
    }
}

// 사용 예시
public class PaymentService {
    private final PaymentProcessorFactory factory;
    
    public PaymentService(PaymentProcessorFactory factory) {
        this.factory = factory;
    }
    
    public void processUserPayment(String orderId, int amount, String method) {
        PaymentProcessor processor = factory.createProcessor(method);
        processor.processPayment(orderId, amount);
    }
}
```


## 자주 발생하는 질문과 답변

Q: 의존성 주입과 서비스 로케이터 패턴의 차이는 무엇인가요?
A: 다음 예시로 차이점을 설명드리겠습니다:

```java
// 서비스 로케이터 패턴 - 안티패턴
public class ServiceLocator {
    private static Map<String, Object> services = new HashMap<>();
    
    public static void register(String name, Object service) {
        services.put(name, service);
    }
    
    public static Object getService(String name) {
        return services.get(name);
    }
}

// 서비스 로케이터 사용 예시
public class EmailClient {
    private EmailSender emailSender;
    
    public EmailClient() {
        // 의존성이 숨겨져 있음
        this.emailSender = (EmailSender) ServiceLocator.getService("emailSender");
    }
}

// 의존성 주입 패턴 - 권장
public class EmailClient {
    private final EmailSender emailSender;
    
    // 의존성이 명시적으로 드러남
    public EmailClient(EmailSender emailSender) {
        this.emailSender = Objects.requireNonNull(emailSender);
    }
}
```

**코드 분석**:
1. **서비스 로케이터 패턴의 문제점**
   - 의존성이 코드 내부에 숨겨져 있어 파악이 어려움
   - 타입 안전성이 보장되지 않음 (Object 타입으로 관리)
   - 전역 상태를 사용하여 테스트가 어려움

2. **의존성 주입 패턴의 장점**
   - 의존성이 생성자에 명시적으로 드러남
   - 컴파일 타임에 타입 안전성 보장
   - 테스트와 재사용이 용이

Q: 순환 의존성이 발생할 경우 어떻게 해결하나요?
A: 다음과 같은 방법들로 해결할 수 있습니다:

```java
// 문제가 있는 순환 의존성 코드
public class UserService {
    private final EmailService emailService;
    
    public UserService(EmailService emailService) {
        this.emailService = emailService;
    }
}

public class EmailService {
    private final UserService userService;  // 순환 참조!
    
    public EmailService(UserService userService) {
        this.userService = userService;
    }
}

// 해결 방법 1: 인터페이스 도입
public interface UserNotifier {
    void notifyUser(String userId, String message);
}

public class UserService {
    private final UserNotifier notifier;
    
    public UserService(UserNotifier notifier) {
        this.notifier = notifier;
    }
}

public class EmailService implements UserNotifier {
    @Override
    public void notifyUser(String userId, String message) {
        // 이메일 발송 구현
    }
}

// 해결 방법 2: 제3의 서비스로 책임 분리
public class NotificationService {
    private final EmailSender emailSender;
    private final UserRepository userRepository;
    
    public NotificationService(EmailSender emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }
    
    public void sendNotification(String userId, String message) {
        User user = userRepository.findById(userId);
        emailSender.send(user.getEmail(), message);
    }
}
```

**코드 분석**:
1. **인터페이스 도입 방식**
   - 구체적인 구현체 대신 인터페이스에 의존
   - 관심사를 명확히 분리
   - 단방향 의존성 구조로 변경

2. **책임 분리 방식**
   - 순환 의존성을 가진 책임을 별도 서비스로 분리
   - 각 서비스의 책임이 더 명확해짐
   - 단일 책임 원칙 준수

## 요약 (Summary)

1. **의존성 주입의 핵심 원칙**
   - 클래스는 의존 객체를 직접 생성하지 않음
   - 생성자를 통해 의존성을 주입받음
   - 불변성을 보장하기 위해 final 필드 사용

2. **실무 적용 가이드**
   - 인터페이스 기반 설계로 유연성 확보
   - 팩터리 패턴 활용으로 객체 생성 캡슐화
   - 단위 테스트를 고려한 설계 지향

3. **주의사항**
   - 과도한 의존성 주입 지양
   - 순환 의존성 발생 주의
   - 명확한 의존성 명세 유지