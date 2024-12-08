# Item 8: finalizer와 cleaner 사용을 피하라

Java는 finalizer와 cleaner라는 두 가지 객체 소멸자를 제공합니다. 하지만 이들은 예측불가능하고 위험하며 대부분의 경우 불필요합니다.

## finalizer와 cleaner의 문제점

### 1. 실행 시점 불확실성
```java
public class BankAccount {
    private boolean closed;
    
    protected void finalize() {
        // 잔액을 전송하는 코드
        if (!closed) {
            transferRemainingBalance();  // 언제 실행될지 보장 없음
        }
    }
}
```
- `closed` 필드로 계좌 종료 상태를 추적
- `finalize()`에서 미처리된 잔액 이체를 시도하지만, 실행 시점이 불확실

**문제점:**
- GC 실행 시점에 의존하므로 즉각적인 실행 보장 없음
- 중요한 작업을 처리하기에 부적합
- 시스템 리소스 반환이 지연될 수 있음

### 2. 예외 처리의 문제
```java
public class ResourceHandler {
    protected void finalize() {
        try {
            // 리소스 정리 작업
        } catch (Exception e) {
            // 예외가 무시됨
        }
    }
}
```

**문제점:**
- finalizer에서 발생한 예외는 무시됨
- 해당 객체는 마무리되지 않은 상태로 남을 수 있음
- -> 다른 스레드가 손상된 객체에 접근할 수 있음
- -> 이후 해당 리소스를 사용하는 코드에서 예상치 못한 오류 발생 가능
- -> 예외가 발생하더라도 스택 트레이스나 경고 없이 종료

### 3. 성능 문제
```java
// 나쁜 예
public class SlowObject {
    @Override
    protected void finalize() {
        // 아무 작업이 없어도 성능 저하 발생
    }
}

// 좋은 예
public class FastObject implements AutoCloseable {
    @Override
    public void close() {
        // 명시적인 리소스 정리
    }
}
```
- `SlowObject`는 finalize() 메서드 존재만으로도 가비지 컬렉션 성능 저하
- `FastObject`는 AutoCloseable을 구현하여 명시적인 자원 해제
- close()는 즉시 실행되며 성능 저하 없음
- 실제 테스트 시 finalize()가 있는 객체는 생성과 소멸이 약 50배 더 느림


**문제점:**
- 가비지 컬렉터의 효율을 크게 떨어뜨림
- 객체 생성/제거 시간이 크게 증가
- 심각한 성능 저하 발생

### 4. 보안 문제
```java
public class Vulnerable {
    private final Object resource;
    
    public Vulnerable(Object resource) {
        if (resource == null) {
            throw new IllegalArgumentException();
        }
        this.resource = resource;
    }
    
    protected void finalize() {
        // 악의적인 하위 클래스가 재정의할 수 있음
    }
}
```

## 올바른 자원 해제 방법

### 1. AutoCloseable 구현
```java
public class Resource implements AutoCloseable {
    private boolean closed;
    
    @Override
    public void close() {
        if (closed) {
            throw new IllegalStateException("이미 닫힘");
        }
        closed = true;
        // 실제 리소스 정리 작업
    }
    
    public void use() {
        if (closed) {
            throw new IllegalStateException("닫힌 리소스");
        }
        // 리소스 사용
    }
}
```
- `closed` 필드로 연결 상태를 추적
- 이미 닫힌 연결에 대한 재사용 시도를 명확히 방지
- 모든 메서드에서 연결 상태 확인
- 예외 발생 시 명확한 에러 메시지 제공
- 리소스 누수 방지를 위한 철저한 상태 관리

### 2. try-with-resources 사용
```java
public class ResourceUser {
    public static void main(String[] args) {
        try (Resource resource = new Resource()) {
            resource.use();
        } catch (Exception e) {
            // 예외 처리
        }
    }
}
```
- try-with-resources로 자동 리소스 해제 보장
- 명시적인 close() 호출 불필요
- 예외가 발생해도 리소스가 항상 해제됨
- 코드가 간결하고 안전



### 3. 안전망으로서의 Cleaner 사용
```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();
    
    private final State state;
    private final Cleaner.Cleanable cleanable;
    
    private static class State implements Runnable {
        @Override
        public void run() {
            // 리소스 정리 로직
        }
    }
    
    public Room() {
        this.state = new State();
        this.cleanable = cleaner.register(this, state);
    }
    
    @Override
    public void close() {
        cleanable.clean();
    }
}
```
- Cleaner를 안전망으로 사용하는 표준적인 방법
- State를 static 클래스로 선언하여 순환 참조 방지
- 명시적 close()와 함께 보조적인 안전망 제공
- Cleaner는 가비지 컬렉터가 객체를 수거할 때 동작
- 클라이언트가 close()를 호출하지 않았을 때의 마지막 보험


## cleaner/finalizer의 적절한 사용 사례

### 1. 안전망으로서의 역할
```java
public class SafetyNetResource implements AutoCloseable {
    private boolean cleaned = false;
    
    @Override
    public void close() {
        if (!cleaned) {
            clean();
        }
    }
    
    protected void finalize() {
        if (!cleaned) {
            clean();  // 사용자가 close()를 호출하지 않았을 때의 안전망
        }
    }
    
    private void clean() {
        cleaned = true;
        // 실제 정리 작업
    }
}
```

### 2. 네이티브 피어 정리
```java
public class NativeResource implements AutoCloseable {
    private long nativeHandle;  // 네이티브 리소스 핸들
    
    private static final Cleaner cleaner = Cleaner.create();
    
    private static class CleaningAction implements Runnable {
        private final long handle;
        
        CleaningAction(long handle) {
            this.handle = handle;
        }
        
        @Override
        public void run() {
            freeNativeHandle(handle);
        }
    }
    
    public NativeResource() {
        this.nativeHandle = createNativeHandle();
        cleaner.register(this, new CleaningAction(nativeHandle));
    }
    
    @Override
    public void close() {
        freeNativeHandle(nativeHandle);
    }
    
    private native long createNativeHandle();
    private static native void freeNativeHandle(long handle);
}
```

## 결론

1. finalizer와 cleaner는 사용하지 말 것
2. 자원 해제는 AutoCloseable 구현으로 해결
3. try-with-resources 사용으로 안전한 자원 해제 보장
4. cleaner는 안전망 역할이나 네이티브 리소스 정리용으로만 사용
5. critical한 리소스 해제는 절대 finalizer나 cleaner에 의존하지 말 것

### 체크리스트
- [ ] AutoCloseable 인터페이스 구현
- [ ] 명시적인 close() 메서드 제공
- [ ] try-with-resources 사용
- [ ] 상태 추적을 위한 필드 관리
- [ ] 중복 close 체크