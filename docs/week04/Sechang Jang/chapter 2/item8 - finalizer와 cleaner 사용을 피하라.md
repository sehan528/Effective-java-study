# Item 8: finalizer와 cleaner 사용을 피하라

## 핵심 개념 (Main Ideas)

### 1. 자원 정리의 위험성
- **정의**: finalizer와 cleaner는 Java의 객체 소멸자 메커니즘
- **목적**: 더 이상 사용하지 않는 자원의 정리
- **효과**: 예측할 수 없고 위험하며 대부분 불필요한 기능

### 2. 명시적 자원 해제의 중요성
- **원칙**: AutoCloseable 인터페이스를 구현하여 명시적으로 자원 해제
- **이유**: 자원 해제의 시점과 방식을 명확하게 제어할 수 있음
- **방법**: try-with-resources 구문 활용

## 세부 내용 (Details)

### 1. finalizer의 문제점과 위험성

#### 잘못된 finalizer 사용 예시
```java
public class DatabaseConnection {
    private final Connection connection;
    private boolean closed = false;
    
    public DatabaseConnection(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (!closed) {
            connection.close();  // finalize에서 자원 해제 시도
        }
    }
    
    public void query(String sql) throws SQLException {
        if (closed) {
            throw new IllegalStateException("Connection is closed");
        }
        // DB 쿼리 실행
        connection.createStatement().execute(sql);
    }
}
```

**코드 분석**:
1. **실행 시점 불확실성**
   - finalize 메서드는 GC가 실행될 때 호출
   - 언제 실행될지 예측 불가능
   - DB 연결과 같은 중요 자원이 즉시 해제되지 않음

2. **예외 처리 문제**
   - finalize에서 발생한 예외는 무시됨
   - DB 연결이 제대로 닫히지 않을 수 있음
   - 리소스 누수 발생 가능

### 2. Cleaner의 한계와 고려사항

#### Cleaner를 사용한 구현
```java
public class FileResource implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();
    
    private final State state;
    private final Cleaner.Cleanable cleanable;
    private File file;
    
    // 정리할 자원을 가진 static 내부 클래스
    private static class State implements Runnable {
        private File file;
        
        State(File file) {
            this.file = file;
        }
        
        @Override
        public void run() {
            if (file != null && file.exists()) {
                file.delete();
                System.out.println("File deleted by cleaner");
            }
        }
    }
    
    public FileResource(String path) {
        this.file = new File(path);
        this.state = new State(file);
        this.cleanable = cleaner.register(this, state);
    }
    
    @Override
    public void close() {
        cleanable.clean();
    }
}
```

**코드 분석**:
1. **State 클래스의 역할**
   - static 클래스로 선언하여 순환 참조 방지
   - 실제 정리 작업을 수행하는 로직 포함
   - 외부 객체에 대한 참조를 최소화

2. **Cleaner 사용의 한계**
   - 자원 정리 시점이 불확실
   - 성능 오버헤드 발생
   - 안전망으로만 사용해야 함

### 3. 올바른 자원 해제 패턴

#### AutoCloseable을 사용한 구현
```java
public class DatabaseResource implements AutoCloseable {
    private final Connection connection;
    private boolean closed = false;
    
    public DatabaseResource(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }
    
    @Override
    public void close() throws SQLException {
        if (closed) {
            return;  // 이미 닫힌 경우 중복 호출 방지
        }
        
        closed = true;
        connection.close();  // 실제 자원 해제
    }
    
    public void executeQuery(String sql) throws SQLException {
        if (closed) {
            throw new IllegalStateException("Resource is already closed");
        }
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    // try-with-resources를 사용한 클라이언트 코드 예시
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydb";
        
        try (DatabaseResource resource = new DatabaseResource(url)) {
            resource.executeQuery("SELECT * FROM users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

**구현 분석**:
1. **명시적 자원 관리**
   - AutoCloseable 인터페이스 구현
   - 상태 플래그로 자원 상태 추적
   - 중복 close 호출 처리

2. **안전한 자원 해제**
   - try-with-resources로 자동 자원 해제
   - 예외 발생시에도 자원 해제 보장
   - 명확한 에러 메시지 제공

## 자주 발생하는 질문과 답변

Q: cleaner나 finalizer가 필요한 경우는 언제인가요?
A: 다음과 같은 제한적인 경우에만 사용을 고려하세요:

```java
public class NativeResource implements AutoCloseable {
    private long nativeHandle;  // 네이티브 메모리 주소
    private static final Cleaner cleaner = Cleaner.create();
    
    private static class NativeResourceCleaner implements Runnable {
        private final long handle;
        
        NativeResourceCleaner(long handle) {
            this.handle = handle;
        }
        
        @Override
        public void run() {
            // 네이티브 메모리 해제
            freeNativeMemory(handle);
        }
    }
    
    public NativeResource() {
        this.nativeHandle = allocateNativeMemory();
        // 안전망으로 cleaner 등록
        cleaner.register(this, new NativeResourceCleaner(nativeHandle));
    }
    
    @Override
    public void close() {
        freeNativeMemory(nativeHandle);
        nativeHandle = 0;
    }
    
    private static native long allocateNativeMemory();
    private static native void freeNativeMemory(long handle);
}
```

**코드 분석**:
1. **네이티브 리소스 관리**
   - JVM 외부의 네이티브 메모리 관리
   - 명시적 close 메서드 제공
   - cleaner를 보조적 안전망으로 활용

Q: try-with-resources를 사용할 수 없는 경우는 어떻게 처리해야 하나요?
A: try-finally 블록을 사용하되, 다음과 같이 신중하게 구현하세요:

```java
public class ResourceManager {
    private Resource resource;
    
    public void useResource() {
        Resource local = null;
        try {
            local = new Resource();
            // 리소스 사용
            local.process();
        } finally {
            if (local != null) {
                try {
                    local.close();
                } catch (Exception e) {
                    // 로그 기록 but 다른 예외를 삼키지 않음
                    logger.error("Error closing resource", e);
                }
            }
        }
    }
    
    private static class Resource implements AutoCloseable {
        @Override
        public void close() {
            // 리소스 정리
        }
        
        public void process() {
            // 처리 로직
        }
    }
}
```

**코드 분석**:
1. **안전한 예외 처리**
   - close 실패를 로그로 기록
   - 주요 예외를 삼키지 않음
   - null 체크로 NPE 방지

## 요약 (Summary)

1. **자원 관리 원칙**
   - finalizer와 cleaner 사용 지양
   - AutoCloseable 구현 권장
   - try-with-resources 활용

2. **실무 적용 가이드**
   - 명시적 자원 해제 메커니즘 구현
   - 상태 플래그로 자원 상태 추적
   - 중복 해제 처리 구현

3. **주의사항**
   - finalizer 사용 금지
   - cleaner는 안전망으로만 사용
   - 크리티컬한 자원은 명시적 해제