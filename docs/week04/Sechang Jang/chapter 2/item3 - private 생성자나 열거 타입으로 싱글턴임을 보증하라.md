# Item 3: private 생성자나 열거 타입으로 싱글턴임을 보증하라

## 핵심 개념 (Main Ideas)

### 1. 싱글턴의 본질
- **정의**: 인스턴스를 오직 하나만 생성할 수 있는 클래스
- **목적**: 시스템 컴포넌트의 유일성 보장과 전역 상태 관리
- **효과**: 메모리 효율성과 상태 일관성 확보

### 2. 싱글턴 구현의 안전성
- **원칙**: 생성자 접근 제한과 인스턴스 접근 제어
- **이유**: 유일한 인스턴스 보장과 외부 공격 방지
- **방법**: private 생성자 또는 열거 타입 사용

## 세부 내용 (Details)

### 1. public static final 필드 방식

#### 기본 구현과 보안 강화
```java
public class DatabaseConnector {
    // 인스턴스가 클래스 로딩 시점에 생성됨
    public static final DatabaseConnector INSTANCE = new DatabaseConnector();
    
    // 생성자 접근 제한 및 리플렉션 공격 방지
    private DatabaseConnector() {
        if (INSTANCE != null) {
            throw new IllegalStateException("이미 인스턴스가 존재합니다.");
        }
        // 데이터베이스 연결 설정
        System.out.println("데이터베이스 연결 초기화");
    }
    
    public void executeQuery(String query) {
        System.out.println("쿼리 실행: " + query);
    }
}

// 사용 예시
public class Application {
    public void process() {
        DatabaseConnector db = DatabaseConnector.INSTANCE;
        db.executeQuery("SELECT * FROM users");
        
        // 다른 곳에서도 같은 인스턴스 사용
        DatabaseConnector sameDb = DatabaseConnector.INSTANCE;
        System.out.println(db == sameDb);  // true
    }
}
```

**이 구현의 특징**:
1. **명확성**
   - public static final 필드로 싱글턴임이 명확히 드러남
   - 클래스 로딩 시점에 인스턴스가 생성되어 안전성 보장
   - 리플렉션 공격에 대한 방어 코드 포함

2. **단순성**
   - 구현이 간단하고 이해하기 쉬움
   - final로 인해 인스턴스 불변성 보장
   - 정적 팩터리 방식보다 성능상 이점 (JVM이 최적화하기 쉬움)

### 2. 정적 팩터리 메서드 방식

#### 제어권과 유연성 확보
```java
public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private Map<String, String> settings = new HashMap<>();
    
    private Configuration() {
        // 기본 설정 로드
        loadDefaultSettings();
    }
    
    public static Configuration getInstance() {
        return INSTANCE;
    }
    
    // 제네릭 싱글턴 팩터리 패턴 예시
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;
    
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }
    
    // 설정 관리 메서드
    public void setSetting(String key, String value) {
        settings.put(key, value);
    }
    
    public String getSetting(String key) {
        return settings.get(key);
    }
    
    private void loadDefaultSettings() {
        settings.put("timeout", "30");
        settings.put("maxConnections", "100");
    }
}

// 사용 예시
public class Application {
    public void configureApp() {
        Configuration config = Configuration.getInstance();
        config.setSetting("env", "production");
        
        // 제네릭 싱글턴 팩터리 사용
        UnaryOperator<String> sameString = Configuration.identityFunction();
        String input = "test";
        assert input == sameString.apply(input);
    }
}
```

**이 방식의 장점**:
1. **API 유연성**
   - 싱글턴이 아니게 변경하기 쉬움
   - 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있음
   - 메서드 참조를 공급자(Supplier)로 사용 가능

### 3. 열거 타입 방식

#### 가장 안전한 구현
```java
public enum LogManager {
    INSTANCE;

    private final Map<String, List<String>> logs = new HashMap<>();
    
    // 열거 타입도 생성자와 메서드를 가질 수 있음
    private LogManager() {
        System.out.println("로그 매니저 초기화");
    }
    
    public void addLog(String category, String message) {
        logs.computeIfAbsent(category, k -> new ArrayList<>())
            .add(message);
    }
    
    public List<String> getLogs(String category) {
        return new ArrayList<>(
            logs.getOrDefault(category, Collections.emptyList())
        );
    }
    
    // 모든 로그 초기화
    public void clearLogs() {
        logs.clear();
    }
}

// 사용 예시
public class Application {
    public void processWithLogging() {
        LogManager logger = LogManager.INSTANCE;
        
        logger.addLog("system", "애플리케이션 시작");
        // 비즈니스 로직 수행
        logger.addLog("system", "처리 완료");
        
        // 다른 클래스에서도 같은 로그 매니저 사용
        List<String> systemLogs = logger.getLogs("system");
        System.out.println(systemLogs);  // 모든 시스템 로그 출력
    }
}
```

**열거 타입의 강점**:
1. **완벽한 싱글턴 보장**
   - 직렬화/역직렬화 자동 처리
   - 리플렉션 공격으로부터 안전
   - 복잡한 직렬화 코드가 필요 없음

## 자주 발생하는 질문과 답변

Q: 싱글턴 패턴은 언제 사용하는 것이 적절한가요?
A: 다음과 같은 상황에서 싱글턴 사용을 고려하세요:
```java
// 적절한 사용 예시: 설정 관리자
public class AppSettings {
    private static final AppSettings INSTANCE = new AppSettings();
    private final Properties properties = new Properties();
    
    private AppSettings() {
        // 설정 파일 로드
        try (InputStream input = getClass().getResourceAsStream("/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("설정 로드 실패", e);
        }
    }
    
    public static AppSettings getInstance() {
        return INSTANCE;
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}

// 부적절한 사용 예시: 상태를 가진 비즈니스 객체
public class UserManager {  // 싱글턴으로 구현하면 안 됨
    private static final UserManager INSTANCE = new UserManager();
    private User currentUser;  // 상태를 가짐 - 멀티 스레드 환경에서 문제 발생
    
    private UserManager() {}
    
    public static UserManager getInstance() {
        return INSTANCE;
    }
}
```

Q: 싱글턴과 정적 클래스의 차이는 무엇인가요?
A: 다음 예시로 차이점을 설명드리겠습니다:
```java
// 정적 클래스
public class MathUtils {
    private MathUtils() {} // 인스턴스화 방지
    
    public static double calculateAverage(List<Double> numbers) {
        return numbers.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}

// 싱글턴
public class DatabasePool {
    private static final DatabasePool INSTANCE = new DatabasePool();
    private final List<Connection> connections = new ArrayList<>();
    
    private DatabasePool() {
        // 커넥션 풀 초기화
    }
    
    public static DatabasePool getInstance() {
        return INSTANCE;
    }
    
    public Connection getConnection() {
        // 커넥션 반환 로직
        return connections.remove(0);
    }
}
```

## 요약 (Summary)

1. **구현 방식 선택 기준**
   - 기본적으로는 열거 타입 방식 권장
   - 상속이 필요한 경우 정적 팩터리 메서드 방식
   - 간단한 경우 public static final 방식

2. **실무적 고려사항**
   - 상태 관리가 필요한 경우 신중하게 검토
   - 테스트 용이성 고려
   - 스레드 안전성 확보

3. **안전한 구현을 위한 체크리스트**
   - 생성자의 접근 제한
   - 직렬화 처리 고려
   - 리플렉션 공격 방어