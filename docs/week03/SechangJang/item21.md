# Item 21: 인터페이스는 구현하는 쪽을 고려해 설계하라

## 디폴트 메서드의 이해

### 자바 8 이전과 이후
- Java 8 이전: 인터페이스에 메서드 추가 시 모든 구현체를 수정해야 함
- Java 8 이후: 디폴트 메서드를 통해 기존 인터페이스에 메서드 추가 가능

```java
public interface List<E> {
    // Java 8에서 추가된 디폴트 메서드
    default void sort(Comparator<? super E> c) {
        Collections.sort(this, c);
    }
}
```

## 디폴트 메서드의 위험성

### Collection.removeIf 예제
```java
// Collection 인터페이스의 디폴트 메서드
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean result = false;
    for (Iterator<E> it = iterator(); it.hasNext();) {
        if (filter.test(it.next())) {
            it.remove();
            result = true;
        }
    }
    return result;
}
```

### 발생할 수 있는 문제점
1. **동기화 문제**
   - SynchronizedCollection에서 removeIf 호출 시 동기화 보장 못함
   - ConcurrentModificationException 발생 가능

2. **기존 구현체와의 호환성**
   ```java
   // Apache Commons Collections의 synchronized 컬렉션
   @Override
   public boolean removeIf(final Predicate<? super E> filter) {
       synchronized (lock) {  // 4.4 버전 이후 수정된 구현
           return decorated().removeIf(filter);
       }
   }
   ```

## 인터페이스 설계 지침

### 1. 테스트 철저히 수행
- 최소 3가지 이상의 구현체 작성
- 다양한 클라이언트 코드 작성
- 실제 사용 사례 시뮬레이션

### 2. 디폴트 메서드 제약사항 인식
```java
// 피해야 할 예
public interface MessageSender {
    default void send(String message) {
        // 기본 구현이 모든 구현체에 적합하지 않을 수 있음
        System.out.println("Sending: " + message);
    }
}
```

### 3. 구현체 영향 고려
- 기존 구현체들의 동작 방식 검토
- 새로운 메서드가 기존 가정을 깨뜨리지 않는지 확인

## 실제 적용 예시

### 좋은 디폴트 메서드 설계
```java
public interface Printable {
    void print();
    
    // 명확하고 일반적인 기능을 제공하는 디폴트 메서드
    default void printTwice() {
        print();
        print();
    }
}
```

### 피해야 할 디폴트 메서드 설계
```java
public interface DataProcessor {
    // 구현체별로 다양한 처리 방식이 필요할 수 있는 메서드
    default void process(Data data) {
        // 특정 구현에 종속적인 로직
        // 이런 경우 디폴트 메서드 사용을 피해야 함
    }
}
```

## 핵심 정리

1. **신중한 디폴트 메서드 사용**
   - 기존 인터페이스에 디폴트 메서드 추가는 신중히
   - 새 인터페이스 설계 시에만 적극적으로 활용

2. **테스트 중요성**
   - 릴리스 전 철저한 테스트
   - 다양한 구현체와 사용 사례 검증

3. **하위 호환성 고려**
   - 기존 구현체들과의 호환성 검증
   - 예상치 못한 동작 방지

4. **문서화**
   - 디폴트 메서드의 의도와 제약사항 명확히 문서화
   - 구현 시 주의사항 명시



