# Item 7: 다 쓴 객체 참조를 해제하라

## 핵심 개념 (Main Ideas)

### 1. 메모리 누수의 이해
- **정의**: 더 이상 필요하지 않은 객체가 GC의 대상이 되지 않고 계속 메모리를 점유하는 현상
- **목적**: 효율적인 메모리 관리와 성능 유지
- **효과**: 메모리 사용량 감소와 애플리케이션 성능 향상

### 2. 객체 참조 해제의 중요성
- **원칙**: 더 이상 필요하지 않은 객체는 참조를 명시적으로 해제
- **이유**: GC가 있더라도 개발자의 메모리 관리 노력 필요
- **방법**: null 처리, 약한 참조 사용, 명시적 해제 메서드 제공

## 세부 내용 (Details)

### 1. 자기 메모리 관리 클래스의 메모리 누수

#### 메모리 누수가 있는 스택 구현
```java
public class Stack<T> {
    private T[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
    }
    
    public void push(T element) {
        ensureCapacity();
        elements[size++] = element;
    }
    
    // 메모리 누수가 있는 pop 메서드
    public T pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];  // 객체 참조가 남아있음
    }
}
```

**코드 분석**:
1. **메모리 누수 발생 지점**
   - pop() 메서드에서 size만 감소
   - 배열에는 여전히 객체 참조가 남아있음
   - GC가 해당 객체를 회수할 수 없음

2. **문제점**
   - 스택에서 꺼낸 객체들이 계속 메모리를 차지
   - 장시간 실행 시 메모리 사용량 증가
   - OutOfMemoryError 발생 가능

#### 개선된 스택 구현
```java
public class Stack<T> {
    private T[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;
    
    @SuppressWarnings("unchecked")
    public Stack() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
    }
    
    public void push(T element) {
        ensureCapacity();
        elements[size++] = element;
    }
    
    // 개선된 pop 메서드
    public T pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        
        // 명시적으로 객체 참조를 null로 설정
        T element = elements[--size];
        elements[size] = null;  // 다 쓴 참조 해제
        
        // 배열 크기 축소 검토
        if (size > 0 && size == elements.length / 4) {
            resize(elements.length / 2);
        }
        
        return element;
    }
    
    private void resize(int newCapacity) {
        @SuppressWarnings("unchecked")
        T[] newElements = (T[]) new Object[newCapacity];
        System.arraycopy(elements, 0, newElements, 0, size);
        elements = newElements;
    }
}
```

**개선 사항 분석**:
1. **참조 해제**
   - pop() 시 명시적으로 null 처리
   - 더 이상 필요 없는 객체를 GC 대상으로 만듦
   - 메모리 누수 방지

2. **추가 개선사항**
   - 배열 크기 동적 조정
   - 메모리 사용 효율성 향상
   - 불필요한 메모리 점유 방지

### 2. 캐시 관련 메모리 누수

#### WeakHashMap을 사용한 캐시 구현
```java
public class ImageCache {
    // 캐시에 저장된 이미지가 더 이상 참조되지 않으면 자동으로 제거됨
    private final Map<String, WeakReference<BufferedImage>> cache = 
        new WeakHashMap<>();
        
    // 캐시 만료 시간 관리를 위한 부가 정보
    private final Map<String, Long> lastAccessTime = new HashMap<>();
    private static final long EXPIRATION_TIME = 1000 * 60 * 10; // 10분
    
    public BufferedImage getImage(String path) {
        cleanExpiredEntries();  // 만료된 항목 정리
        
        WeakReference<BufferedImage> ref = cache.get(path);
        BufferedImage image = (ref != null) ? ref.get() : null;
        
        if (image == null) {
            // 이미지 새로 로드
            image = loadImage(path);
            cache.put(path, new WeakReference<>(image));
            lastAccessTime.put(path, System.currentTimeMillis());
        } else {
            // 마지막 접근 시간 갱신
            lastAccessTime.put(path, System.currentTimeMillis());
        }
        
        return image;
    }
    
    private void cleanExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = 
            lastAccessTime.entrySet().iterator();
            
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > EXPIRATION_TIME) {
                cache.remove(entry.getKey());
                iterator.remove();
            }
        }
    }
    
    private BufferedImage loadImage(String path) {
        // 실제 이미지 로딩 로직
        return null;  // 예시를 위한 더미 반환
    }
}
```

**구현 분석**:
1. **WeakReference 활용**
   - 메모리 압박 시 자동으로 캐시 항목 제거
   - 명시적인 참조가 없는 이미지는 GC 대상이 됨
   - 메모리 누수 방지

2. **시간 기반 만료**
   - 마지막 접근 시간 기록
   - 주기적인 만료 항목 정리
   - 캐시 크기 제한

### 3. 리스너 또는 콜백

#### 콜백 등록 및 해제 구현
```java
public class EventManager {
    // WeakHashMap을 사용하여 콜백 저장
    private final Map<EventListener, Void> listeners = new WeakHashMap<>();
    
    // 이벤트 소스 추적
    private final List<WeakReference<Object>> eventSources = 
        new ArrayList<>();
    
    public void registerListener(EventListener listener, Object source) {
        // 리스너 등록
        listeners.put(listener, null);
        // 이벤트 소스 추적
        eventSources.add(new WeakReference<>(source));
    }
    
    public void unregisterListener(EventListener listener) {
        listeners.remove(listener);
    }
    
    public void fireEvent(Event event) {
        // 죽은 참조 정리
        cleanDeadReferences();
        
        // 이벤트 발생
        for (EventListener listener : listeners.keySet()) {
            listener.onEvent(event);
        }
    }
    
    private void cleanDeadReferences() {
        // 죽은 참조 제거
        eventSources.removeIf(ref -> ref.get() == null);
    }
}

// 사용 예시
public class Application {
    private EventManager eventManager = new EventManager();
    
    public void setupEventHandling() {
        Button button = new Button();
        
        EventListener listener = event -> {
            System.out.println("Button clicked!");
        };
        
        eventManager.registerListener(listener, button);
    }
}
```

## 자주 발생하는 질문과 답변

Q: 어떤 경우에 명시적으로 null 처리가 필요한가요?
A: 다음 예시로 설명드리겠습니다:

```java
public class ResourceHolder {
    private byte[] hugeArray;  // 큰 메모리를 차지하는 리소스
    
    public void processData() {
        try {
            // 리소스 사용
            hugeArray = new byte[100000];
            // 데이터 처리 로직
        } finally {
            // 명시적 null 처리가 필요한 경우
            if (hugeArray != null) {
                hugeArray = null;  // 큰 객체는 빨리 해제하는 것이 좋음
            }
        }
    }
    
    // 명시적 null 처리가 불필요한 경우
    public void processSmallData() {
        byte[] smallArray = new byte[100];
        // 메서드 종료시 자동으로 smallArray는 스코프를 벗어남
    }
}
```

**코드 분석**:
1. **null 처리가 필요한 경우**
   - 클래스의 멤버 변수로 큰 객체를 참조
   - 객체 풀이나 캐시를 직접 관리
   - 네이티브 리소스와 연결된 객체

2. **null 처리가 불필요한 경우**
   - 지역 변수
   - 매개변수
   - 곧바로 스코프를 벗어나는 변수

Q: WeakReference와 SoftReference의 차이는 무엇인가요?
A: 다음 예시를 통해 설명드리겠습니다:

```java
public class ReferenceExample {
    // 약한 참조 예시
    public void weakReferenceExample() {
        WeakReference<LargeObject> weakRef = 
            new WeakReference<>(new LargeObject());
            
        // GC 발생시 즉시 수거 대상
        LargeObject obj = weakRef.get();  // null일 수 있음
    }
    
    // 부드러운 참조 예시
    public void softReferenceExample() {
        SoftReference<LargeObject> softRef = 
            new SoftReference<>(new LargeObject());
            
        // 메모리가 부족할 때만 수거 대상
        LargeObject obj = softRef.get();  // 메모리 여유가 있으면 유지
    }
    
    private static class LargeObject {
        private byte[] data = new byte[1024 * 1024];  // 1MB
    }
}
```

**차이점 분석**:
1. **WeakReference**
   - GC 발생시 즉시 수거 대상
   - 캐시나 콜백 리스너에 적합
   - 메모리 해제 우선순위 높음

2. **SoftReference**
   - 메모리 부족할 때만 수거
   - 성능이 중요한 캐시에 적합
   - 메모리 해제 우선순위 낮음

## 요약 (Summary)

1. **메모리 누수 방지 원칙**
   - 명시적인 null 처리 필요성 검토
   - 약한 참조 활용
   - 캐시와 리스너의 수명 주기 관리

2. **실무 적용 가이드**
   - 자원 관리 코드 검토
   - 주기적인 메모리 모니터링 수행
   - 적절한 참조 타입 선택

3. **주의사항**
   - 불필요한 객체 참조 확인
   - 순환 참조 방지
   - 리소스 해제 패턴 준수