# Item 7: 다 쓴 객체 참조를 해제하라

가비지 컬렉터(GC)가 있다고 해서 메모리 관리를 신경 쓰지 않아도 되는 것은 아닙니다. 의도치 않은 객체 참조를 해제하지 않으면 메모리 누수가 발생할 수 있습니다.

## 메모리 누수의 주요 원인과 해결책

### 1. 자기 메모리를 직접 관리하는 클래스

#### 메모리 누수가 발생하는 스택 구현
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];  // 메모리 누수 발생 지점
    }
}
```

#### 개선된 스택 구현
```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;  // 다 쓴 참조 해제
        return result;
    }
}
```

- 스택이 줄어들 때 제거된 객체의 참조를 해제하지 않으면 메모리 누수 발생
- pop()된 객체들이 GC 대상이 되지 않음
- 명시적으로 null 처리하여 참조 해제 필요

### 2. 캐시

#### 메모리 누수가 발생하는 캐시 구현
```java
public class Cache {
    private final Map<String, Object> cache = new HashMap<>();
    
    public void add(String key, Object value) {
        cache.put(key, value);  // 참조가 영구적으로 남음
    }
}
```

#### WeakHashMap을 사용한 개선된 캐시
```java
public class Cache {
    private final Map<String, Object> cache = new WeakHashMap<>();
    
    public void add(String key, Object value) {
        cache.put(key, value);  // 외부 참조가 없어지면 자동으로 제거됨
    }
}
```

- `WeakHashMap`은 키에 대한 참조가 더 이상 없을 때 해당 엔트리를 자동으로 제거
- 캐시 외부에서 키를 참조하는 동안만 엔트리가 살아있음
- 시간 기반 캐시 만료를 위해서는 `ScheduledThreadPoolExecutor` 사용 가능

### 3. 리스너와 콜백

#### 메모리 누수가 발생하는 리스너 등록
```java
public class EventManager {
    private final List<EventListener> listeners = new ArrayList<>();
    
    public void addListener(EventListener listener) {
        listeners.add(listener);  // 리스너가 계속 쌓임
    }
}
```

#### WeakReference를 사용한 개선된 리스너 관리
```java
public class EventManager {
    private final Map<EventListener, Object> listeners = new WeakHashMap<>();
    
    public void addListener(EventListener listener) {
        listeners.put(listener, Boolean.TRUE);  // 참조가 없어지면 자동으로 제거됨
    }
}
```

## JVM 메모리 구조와 GC

### 메모리 영역
1. **Heap 영역**
    - 객체의 실제 데이터가 저장
    - GC의 대상이 되는 영역

2. **Stack 영역**
    - 메서드 호출과 지역 변수 저장
    - 스코프를 벗어나면 자동으로 정리

3. **Method 영역**
    - 클래스 정보와 static 변수 저장

### 객체 참조 상태
```java
public class ReferenceExample {
    public void example() {
        Object reachable = new Object();     // Reachable 객체
        Object unreachable = new Object();
        unreachable = null;                  // Unreachable 객체
    }
}
```

## 메모리 누수 방지를 위한 체크리스트

1. **자기 메모리 관리 클래스**
    - [ ] 배열 또는 컬렉션의 활성 영역 관리
    - [ ] 더 이상 필요 없는 객체는 null 처리

2. **캐시**
    - [ ] WeakHashMap 사용 검토
    - [ ] 시간 기반 만료 정책 필요성 검토
    - [ ] 주기적인 청소 메커니즘 구현

3. **리스너/콜백**
    - [ ] WeakReference 사용 검토
    - [ ] 명시적인 제거 메서드 제공
    - [ ] 자동 정리 메커니즘 구현

## 디버깅 도구
- 힙 프로파일러
- Memory Analyzer (MAT)
- JConsole
- VisualVM

## 결론

1. 메모리 누수는 겉으로 드러나지 않아 발견하기 어려움
2. 자기 메모리를 관리하는 클래스를 작성할 때는 메모리 누수에 주의
3. 캐시와 리스너/콜백에서 메모리 누수가 자주 발생
4. WeakHashMap과 WeakReference를 적절히 활용
5. 메모리 누수는 예방이 가장 중요