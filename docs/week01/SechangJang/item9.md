# Item 9: try-finally 대신 try-with-resources를 사용하라

자원을 올바르게 해제하는 것은 프로그램의 안정성과 성능에 중요합니다. Java 7에서 도입된 try-with-resources는 자원 해제를 더 안전하고 명확하게 처리할 수 있게 해줍니다.

## try-finally의 문제점

### 1. 단일 자원 사용 시
```java
public class ResourceHandler {
    public static String readFirstLine(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();  // 여기서 예외가 발생하면 try 블록의 예외가 무시됨
        }
    }
}
```

- 기본적인 자원 관리는 가능하나 예외 처리가 불완전
- finally 블록의 예외가 try 블록의 예외를 덮어씀
- 디버깅이 어려워짐
- 실제 문제의 원인을 파악하기 힘듦

### 2. 다중 자원 사용 시
```java
public class FileCopier {
    static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[1024];
                int n;
                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
```

- 중첩된 try-finally 블록으로 인해 코드가 복잡
- 가독성이 떨어짐
- 여러 예외가 발생할 경우 처리가 어려움
- 유지보수가 어려워짐

## try-with-resources 해결책

### 1. 단일 자원 사용
```java
public class BetterResourceHandler {
    public static String readFirstLine(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }
}
```

- 코드가 간결하고 명확
- 자원 해제가 자동으로 처리
- 예외 처리가 더 정확하고 유용
- 실제 발생한 예외가 보존됨

### 2. 다중 자원 사용
```java
public class BetterFileCopier {
    static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        }
    }
}
```

- 여러 자원을 한 번에 관리 가능
- 자원 해제 순서가 자동으로 관리됨
- 코드가 간결하고 이해하기 쉬움
- 모든 예외가 적절히 처리됨

### 3. 예외 처리 개선 예시
```java
public class ResourceWithException implements AutoCloseable {
    public void doWork() throws WorkException {
        throw new WorkException("Work failed");
    }
    
    @Override
    public void close() throws CloseException {
        throw new CloseException("Close failed");
    }
}

public class ResourceUser {
    public void useResource() {
        try (ResourceWithException resource = new ResourceWithException()) {
            resource.doWork();
        } catch (Exception e) {
            e.printStackTrace();
            // 원래 발생한 WorkException이 주 예외로 전달됨
            // CloseException은 억제된 예외로 첨부됨
            Throwable[] suppressed = e.getSuppressed();
            // 억제된 예외 처리
        }
    }
}
```

- 주 예외가 보존됨
- 자원 해제 시 발생한 예외는 억제된 예외로 첨부
- `getSuppressed()`로 모든 예외 정보 접근 가능
- 디버깅이 용이

## AutoCloseable 구현
```java
public class CustomResource implements AutoCloseable {
    private boolean closed = false;
    
    public void doWork() throws WorkException {
        if (closed) {
            throw new IllegalStateException("Resource is closed");
        }
        // 작업 수행
    }
    
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            // 자원 해제 로직
        }
    }
}
```

- `AutoCloseable` 인터페이스 구현
- 자원 상태 추적
- 중복 close 방지
- 안전한 자원 관리 보장

## 결론

1. try-with-resources의 장점:
    - 코드 간결성
    - 자동 자원 해제
    - 정확한 예외 처리
    - 실제 예외 원인 파악 용이

2. 사용 시 주의사항:
    - `AutoCloseable` 구현 필요
    - 자원 해제 순서 고려
    - 예외 처리 전략 수립

### 체크리스트
- [ ] 자원이 AutoCloseable 구현
- [ ] try-with-resources 사용
- [ ] 다중 자원 처리 시 선언 순서 고려
- [ ] 예외 처리 로직 구현
- [ ] 억제된 예외 처리 방안 마련