# Item 4: 인스턴스화를 막으려거든 private 생성자를 사용하라

## 핵심 개념 (Main Ideas)

### 1. 유틸리티 클래스의 본질
- **정의**: 정적 메서드와 정적 필드만을 담은 클래스
- **목적**: 관련 기능을 그룹화하고 네임스페이스 역할 수행
- **효과**: 특정 도메인의 공통 기능을 중복 없이 제공

### 2. 인스턴스화 방지의 중요성
- **원칙**: 유틸리티 클래스는 인스턴스 생성을 막아야 함
- **이유**: 불필요한 객체 생성 방지와 의도 명확화
- **방법**: private 생성자와 예외 throw를 조합

## 세부 내용 (Details)

### 1. 잘못된 유틸리티 클래스 설계의 문제점

#### 암묵적 public 생성자 문제
```java
public class MathUtils {
    // 생성자를 명시하지 않음 - 컴파일러가 public 기본 생성자 생성
    
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static int multiply(int a, int b) {
        return a * b;
    }
}

// 문제가 되는 사용 예시
public class Calculator {
    public void calculate() {
        // 불필요한 인스턴스 생성 가능
        MathUtils utils = new MathUtils();  // 컴파일 오류 없음
        int result = utils.add(5, 3);       // 정적 메서드를 인스턴스처럼 사용
    }
}
```

**이 코드의 문제점**:
1. **의도하지 않은 인스턴스화**
   - 컴파일러가 자동으로 public 생성자 생성
   - 아무나 인스턴스 생성 가능
   - 정적 메서드임에도 인스턴스로 호출하는 실수 유발

2. **자원 낭비**
   - 불필요한 객체 생성으로 메모리 낭비
   - 가비지 컬렉션 부하 증가
   - 성능 저하 가능성

### 2. 올바른 유틸리티 클래스 설계

#### private 생성자를 이용한 인스턴스화 방지
```java
public class StringUtils {
    /**
     * 이 클래스는 인스턴스화를 위한 것이 아닙니다.
     * 문자열 처리를 위한 정적 유틸리티 메서드만 제공합니다.
     */
    private StringUtils() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static String reverse(String str) {
        if (str == null) {
            throw new IllegalArgumentException("입력 문자열이 null입니다.");
        }
        return new StringBuilder(str).reverse().toString();
    }
    
    public static String toSnakeCase(String str) {
        if (str == null) {
            throw new IllegalArgumentException("입력 문자열이 null입니다.");
        }
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}

// 올바른 사용 예시
public class TextProcessor {
    public void process() {
        String input = "helloWorld";
        // 정적 메서드로 직접 호출
        if (!StringUtils.isEmpty(input)) {
            String result = StringUtils.toSnakeCase(input);
            System.out.println(result);  // "hello_world" 출력
        }
    }
}
```

**올바른 설계의 특징**:
1. **명시적 제한**
   - private 생성자로 인스턴스화 명시적 차단
   - AssertionError로 의도 명확히 전달
   - 상속도 자동으로 방지됨

2. **가독성과 유지보수성**
   - 문서화 주석으로 클래스의 용도 명확히 설명
   - 각 메서드의 책임이 명확함
   - 입력 검증과 예외 처리가 포함됨

### 3. 실전 유틸리티 클래스 예시

#### 파일 처리 유틸리티
```java
public class FileUtils {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    
    // 인스턴스화 방지
    private FileUtils() {
        throw new AssertionError("인스턴스화 할 수 없습니다.");
    }
    
    /**
     * 파일의 확장자를 반환합니다.
     * @param filename 파일명
     * @return 확장자 (점 제외)
     * @throws IllegalArgumentException 파일명이 null이거나 비어있는 경우
     */
    public static String getExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }
        
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
    
    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환합니다.
     * @param bytes 파일 크기 (바이트)
     * @return 변환된 크기 문자열 (예: "1.5 MB")
     */
    public static String humanReadableByteCount(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("바이트 수는 음수일 수 없습니다.");
        }
        
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}

// 사용 예시
public class FileManager {
    public void processFile(String filename, long fileSize) {
        String extension = FileUtils.getExtension(filename);
        String readableSize = FileUtils.humanReadableByteCount(fileSize);
        System.out.printf("파일 형식: %s, 크기: %s%n", extension, readableSize);
    }
}
```

## 자주 발생하는 질문과 답변

Q: 왜 abstract 클래스로 인스턴스화를 막으면 안되나요?
A: 다음 예시를 통해 설명드리겠습니다:
```java
// 잘못된 방법: abstract 클래스 사용
public abstract class DateUtils {
    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}

// 문제점 예시: 하위 클래스로 인스턴스화 가능
public class CustomDateUtils extends DateUtils {
    // 하위 클래스를 만들어 인스턴스화할 수 있음
    public void someMethod() {
        // 불필요한 인스턴스 메서드
    }
}

// 올바른 방법: private 생성자 사용
public class DateUtils {
    private DateUtils() {
        throw new AssertionError();
    }
    
    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
```

Q: 유틸리티 클래스와 싱글톤의 차이는 무엇인가요?
A: 다음 예시로 차이점을 설명드리겠습니다:
```java
// 싱글톤 예시 - 상태를 가질 수 있음
public class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private Connection connection;  // 상태를 가짐
    
    private DatabaseConnection() {
        // 초기화 로직
    }
    
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }
}

// 유틸리티 클래스 예시 - 상태가 없음
public class DatabaseUtils {
    private DatabaseUtils() {
        throw new AssertionError();
    }
    
    public static Connection createConnection(String url) {
        // 매번 새로운 연결 생성
        return DriverManager.getConnection(url);
    }
}
```

## 요약 (Summary)

1. **유틸리티 클래스 설계 원칙**
   - private 생성자로 인스턴스화 방지
   - 상속 방지를 위한 명시적 제한
   - 명확한 문서화와 예외 처리

2. **실무 적용 포인트**
   - 관련 기능끼리 그룹화하여 유틸리티 클래스 구성
   - 각 메서드는 독립적이고 순수 함수적으로 구현
   - 적절한 예외 처리와 널 체크 포함

3. **주의사항**
   - abstract 클래스 사용 지양
   - 불필요한 인스턴스 메서드 포함하지 않기
   - 상태를 가지지 않도록 설계