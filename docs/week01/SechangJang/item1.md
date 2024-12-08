# Item 1: 생성자 대신 정적 팩터리 메서드를 고려하라

객체 지향 프로그래밍에서 인스턴스를 생성하는 방법은 크게 두 가지가 있습니다
1. public 생성자를 통한 생성
2. 정적 팩터리 메서드(static factory method)를 통한 생성

## 정적 팩터리 메서드란?
객체 생성의 역할을 하는 클래스의 정적 메서드입니다. 기존 생성자 방식과 비교하면 다음과 같습니다:

```java
// 전통적인 생성자 방식
public class Laptop {
    private String model;
    private String company;
    
    public Laptop(String modelName, String company) {
        this.model = modelName;
        this.company = company;
    }
}

// 정적 팩터리 메서드 방식
public class Laptop {
    private String model;
    private String company;
    
    // private 생성자
    private Laptop() {}
    
    public static Laptop ofModelNameAndCompany(String modelName, String company) {
        Laptop laptop = new Laptop();
        laptop.model = modelName;
        laptop.company = company;
        return laptop;
    }
}
```
정적 팩터리 메서드는 단순히 정적 메서드를 통해 객체를 생성하는 것을 넘어서, API 설계 시 클라이언트 코드의 가독성과 유지보수성을 크게 향상시킬 수 있는 기법입니다.


## 장점과 실제 활용

### 1. 의미 있는 이름을 가질 수 있다
생성자는 클래스와 동일한 이름을 가져야 하는 제약이 있지만, 정적 팩터리 메서드는 의도를 담은 이름을 가질 수 있습니다.

#### 예시: API 요청 처리
```java
public class LaptopForm {
    private String name;
    private String corp; // company의 다른 표현
}

public class Laptop {
    private Long id;
    private String modelName;
    private String company;
    
    // Form 데이터로부터 변환 - 의도가 명확한 이름
    public static Laptop from(LaptopForm form) {
        Laptop laptop = new Laptop();
        laptop.modelName = form.getName();
        laptop.company = form.getCorp();
        return laptop;
    }
}
```

- API로부터 받은 데이터(`LaptopForm`)를 실제 도메인 객체(`Laptop`)로 변환하는 과정을 명확히 표현
- `from`이라는 이름을 통해 "외부 데이터로부터의 변환"이라는 의도를 명확히 전달
- 필드명이 다른 경우(name→modelName, corp→company)의 매핑을 자연스럽게 처리


### 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다
불변 클래스로 만들어 인스턴스를 캐싱하여 재활용할 수 있으며, 이는 성능을 향상시킬 수 있습니다.

#### 예시: 싱글톤 패턴
```java
public class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    
    private DatabaseConnection() {}
    
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }
}
```
- 데이터베이스 연결과 같이 비용이 큰 객체를 한 번만 생성하여 재사용
- 생성자를 private으로 선언하여 외부에서의 인스턴스 생성을 방지
- `getInstance()`를 통해 항상 동일한 인스턴스를 반환하여 자원 관리 용이


#### 예시: 객체 캐싱
```java
public class Color {
    private static final Map<String, Color> CACHE = new HashMap<>();
    private final String colorCode;
    
    private Color(String colorCode) {
        this.colorCode = colorCode;
    }
    
    public static Color valueOf(String colorCode) {
        return CACHE.computeIfAbsent(colorCode, code -> new Color(code));
    }
}
```
- 자주 사용되는 색상 코드에 대한 객체를 캐싱하여 메모리 사용 최적화
- `computeIfAbsent`를 사용하여 스레드 안전하게 캐시 관리
- 동일한 colorCode에 대해 항상 같은 인스턴스를 반환하여 메모리 절약


### 3. 반환 타입의 하위 타입 객체를 반환할 수 있다
인터페이스를 반환 타입으로 사용하여 구현 클래스를 숨길 수 있습니다.

#### 예시: 인터페이스 기반 팩터리
```java
public interface PaymentProcessor {
    void processPayment(BigDecimal amount);
}

public class PaymentFactory {
    public static PaymentProcessor getProcessor(String type) {
        return switch (type) {
            case "CREDIT" -> new CreditCardProcessor();
            case "DEBIT" -> new DebitCardProcessor();
            default -> throw new IllegalArgumentException("Unknown payment type");
        };
    }
}
```
- 클라이언트 코드는 `PaymentProcessor` 인터페이스만 알면 됨
- 실제 구현체(`CreditCardProcessor`, `DebitCardProcessor`)는 클라이언트로부터 숨겨짐
- 새로운 결제 방식 추가 시 기존 코드 수정 없이 확장 가능

### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다
상황에 따라 적절한 클래스의 객체를 반환할 수 있습니다.

#### 예시: 조건에 따른 객체 생성
```java
public interface Logger {
    void log(String message);
}

public class LoggerFactory {
    public static Logger getLogger(String environment, String category) {
        if ("PRODUCTION".equals(environment)) {
            return new ProductionLogger(category);
        } else if ("DEVELOPMENT".equals(environment)) {
            return new DevelopmentLogger(category);
        } else {
            return new ConsoleLogger();
        }
    }
}
```
- 환경(production/development)에 따라 적절한 로깅 구현체를 반환
- 카테고리별로 다른 로깅 설정을 적용 가능
- 클라이언트는 로거의 구체적인 구현을 알 필요 없이 인터페이스만으로 사용 가능

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다
이는 서비스 제공자 프레임워크를 만드는 근간이 됩니다.

#### 예시: JDBC 드라이버 로딩
```java
public class JdbcConnectionFactory {
    public static Connection createConnection(String dbType, String url) {
        // 실행 시점에 드라이버 클래스가 동적으로 로드됨
        return switch (dbType) {
            case "MYSQL" -> DriverManager.getConnection(url); // MySQL 드라이버
            case "POSTGRESQL" -> DriverManager.getConnection(url); // PostgreSQL 드라이버
            default -> throw new IllegalArgumentException("Unknown database type");
        };
    }
}
```
- 컴파일 시점에는 실제 데이터베이스 드라이버가 존재하지 않아도 됨
- 런타임에 필요한 드라이버가 동적으로 로드됨
- 새로운 데이터베이스 추가 시 코드 수정 없이 드라이버만 추가하면 됨


#### from vs of 사용 기준
- `from`: 객체를 파라미터로 받아 변환하는 경우 (예: Form → Entity)
- `of`: 여러 개별 파라미터로 새로운 객체를 생성하는 경우

## 단점과 주의사항

1. **상속의 제한**
    - 정적 팩터리 메서드만 있고 public/protected 생성자가 없으면 상속이 불가능합니다.
    - 하지만 이는 컴포지션을 사용하도록 유도하므로 장점이 될 수도 있습니다.

2. **가시성 문제**
    - 생성자와 달리 API 문서에서 찾기 어려울 수 있습니다.
    - 이는 명확한 명명 규칙을 따르는 것으로 완화할 수 있습니다.

## 결론

정적 팩터리 메서드는 다음과 같은 상황에서 특히 유용합니다:
1. API 설계 시 메서드 명으로 생성 의도를 명확히 전달하고 싶을 때
2. DTO ↔ Entity 변환 등 객체 변환 로직이 필요할 때
3. 인스턴스 생성을 통제하고 싶을 때

생성자를 무조건 배제할 필요는 없지만, 정적 팩터리 메서드가 제공하는 이점을 고려하여 적절히 활용하는 것이 좋습니다.