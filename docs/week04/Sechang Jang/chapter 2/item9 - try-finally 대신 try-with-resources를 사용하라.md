# Item 9: try-finally 대신 try-with-resources를 사용하라

## 핵심 개념 (Main Ideas)

### 1. try-with-resources의 필요성
- **정의**: AutoCloseable을 구현한 자원의 자동 해제를 보장하는 구문
- **목적**: 자원 해제의 안전성과 코드 가독성 향상
- **효과**: 예외 처리의 정확성과 자원 관리의 신뢰성 보장

### 2. try-finally의 한계
- **원칙**: 자원 해제를 보장하기 위해 명시적 finally 블록 필요
- **이유**: 복잡한 중첩 구조와 예외 처리의 불완전성
- **방법**: try-with-resources로 대체하여 문제 해결

## 세부 내용 (Details)

### 1. try-finally의 문제점

#### 단일 자원 사용의 문제
```java
public class FileProcessor {
    public String processFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        try {
            // 파일 처리 로직
            return reader.readLine();
        } finally {
            reader.close(); // 여기서 예외가 발생하면 try 블록의 예외가 무시됨
        }
    }
}
```

**코드 분석**:
1. **예외 처리의 불완전성**
   - finally 블록의 예외가 try 블록의 예외를 덮어씀
   - 원인이 되는 첫 번째 예외가 손실됨
   - 디버깅이 어려워짐

2. **가독성 문제**
   - 코드 구조가 복잡
   - 자원 해제 로직이 비즈니스 로직과 섞임
   - 유지보수가 어려움

### 2. try-with-resources 해결책

#### 개선된 자원 관리
```java
public class ImprovedFileProcessor {
    public String processFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            return reader.readLine();  // 자원 자동 해제
        }
    }
    
    // 여러 자원을 사용하는 경우
    public void copyFile(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
```

**코드 분석**:
1. **자동 자원 해제**
   - AutoCloseable 구현체는 자동으로 close() 호출
   - 자원 해제 순서는 선언의 역순
   - 예외가 발생해도 자원 해제 보장

2. **예외 처리 개선**
   - 원래 발생한 예외가 보존됨
   - 자원 해제 시의 예외는 억제됨(suppressed)
   - 모든 예외 정보에 접근 가능

### 3. 커스텀 리소스 구현

#### AutoCloseable 구현 예시
```java
public class DatabaseConnection implements AutoCloseable {
    private final Connection connection;
    private boolean closed = false;
    
    public DatabaseConnection(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }
    
    public void executeQuery(String sql) throws SQLException {
        if (closed) {
            throw new IllegalStateException("Connection is already closed");
        }
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    @Override
    public void close() throws SQLException {
        if (!closed) {
            closed = true;
            connection.close();
        }
    }
    
    // 사용 예시
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        
        try (DatabaseConnection db = new DatabaseConnection(url)) {
            db.executeQuery("SELECT * FROM users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

**구현 분석**:
1. **상태 관리**
   - closed 플래그로 연결 상태 추적
   - 중복 close 방지
   - 명확한 에러 메시지 제공

2. **안전한 자원 관리**
   - 내부 Statement도 try-with-resources 사용
   - 계층적 자원 해제
   - 예외 처리 체계화

### 4. 복잡한 시나리오 처리

#### 조건부 자원 해제
```java
public class ResourceManager {
    public void processResources(boolean condition) {
        // 첫 번째 리소스는 항상 필요
        try (Resource1 r1 = new Resource1()) {
            r1.process();
            
            // 조건부로 두 번째 리소스 사용
            if (condition) {
                try (Resource2 r2 = new Resource2()) {
                    r2.process();
                }
            }
        } catch (Exception e) {
            // 예외 처리
            handleException(e);
            
            // 억제된 예외 처리
            for (Throwable suppressed : e.getSuppressed()) {
                handleSuppressedException(suppressed);
            }
        }
    }
    
    private void handleException(Exception e) {
        // 주 예외 처리 로직
        System.err.println("Main exception: " + e.getMessage());
    }
    
    private void handleSuppressedException(Throwable t) {
        // 억제된 예외 처리 로직
        System.err.println("Suppressed: " + t.getMessage());
    }
}
```

**구현 분석**:
1. **조건부 리소스 관리**
   - 필요한 경우에만 리소스 할당
   - 중첩된 try-with-resources 사용
   - 각 리소스의 스코프 최소화

2. **포괄적 예외 처리**
   - 주 예외와 억제된 예외 모두 처리
   - 세부적인 예외 처리 로직 분리
   - 디버깅 정보 보존

## 자주 발생하는 질문과 답변

Q: try-with-resources와 try-finally를 함께 사용해야 할 때는 언제인가요?
A: 다음과 같은 경우에 고려할 수 있습니다:

```java
public class HybridResourceManager {
    public void processResource() {
        try (Resource resource = new Resource()) {
            resource.process();
        } finally {
            // 자원 해제와 무관한 필수 정리 작업
            cleanupRequiredState();
        }
    }
    
    private void cleanupRequiredState() {
        // 예: 임시 파일 삭제, 상태 초기화 등
        try {
            Files.deleteIfExists(Paths.get("temp.dat"));
        } catch (IOException e) {
            // 로깅만 하고 다시 throw하지 않음
            logger.warn("Failed to delete temp file", e);
        }
    }
}
```

**코드 분석**:
1. **리소스 관리와 상태 정리 분리**
   - 자원은 try-with-resources로 관리
   - 부가적인 정리는 finally에서 처리
   - 각각의 책임이 명확함

Q: AutoCloseable과 Closeable의 차이는 무엇인가요?
A: 다음 예시로 설명드리겠습니다:

```java
// Closeable 구현 - IOException만 throw 가능
public class FileResource implements Closeable {
    private FileInputStream fis;
    
    @Override
    public void close() throws IOException {
        if (fis != null) {
            fis.close();
        }
    }
}

// AutoCloseable 구현 - 모든 Exception throw 가능
public class DatabaseResource implements AutoCloseable {
    private Connection conn;
    
    @Override
    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
```

## 요약 (Summary)

1. **기본 원칙**
   - try-with-resources 사용 우선
   - AutoCloseable 인터페이스 구현
   - 자원 해제의 자동화 추구

2. **실무 적용 가이드**
   - 여러 자원의 선언 순서 고려
   - 명확한 예외 처리 전략 수립
   - 자원 상태 추적 구현

3. **주의사항**
   - close 메서드의 예외 처리
   - 자원 해제 순서 고려
   - 중첩 리소스 관리 주의