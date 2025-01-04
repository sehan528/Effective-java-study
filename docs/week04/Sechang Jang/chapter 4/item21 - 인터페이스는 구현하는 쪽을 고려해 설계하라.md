# Item 21: 인터페이스는 구현하는 쪽을 고려해 설계하라

## 핵심 개념 (Main Ideas)

### 1. 디폴트 메서드의 영향력
- **정의**: 자바 8부터 도입된 기존 인터페이스에 메서드를 추가하는 방법
- **목적**: 인터페이스의 진화를 용이하게 하고 기존 구현체와의 호환성 유지
- **효과**: 기존 구현체들이 깨지지 않고도 인터페이스에 새로운 메서드 추가 가능

### 2. 디폴트 메서드의 제약사항
- **원칙**: 기존 구현체들과의 충돌 가능성을 항상 고려해야 함
- **이유**: 디폴트 메서드는 기존 구현체가 모르는 상태에서 추가될 수 있음
- **방법**: 철저한 테스트와 문서화를 통해 안전한 디폴트 메서드 설계

## 세부 내용 (Details)

### 1. 디폴트 메서드의 위험성

#### Collection.removeIf 예제
```java
// Collection 인터페이스의 removeIf 디폴트 메서드
public interface Collection<E> {
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if (filter.test(it.next())) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }
}

// 동기화를 고려하지 않은 구현체의 예
public class SynchronizedCollection<E> implements Collection<E> {
    private final Collection<E> delegate;
    private final Object mutex;
    
    public boolean remove(Object o) {
        synchronized (mutex) {
            return delegate.remove(o);
        }
    }
    
    // removeIf는 재정의하지 않음 - 동기화 깨짐!
    // 디폴트 구현이 동기화 없이 실행됨
}
```

**이 코드가 설명하려는 것**:
1. **동기화 문제**:
   - 디폴트 메서드는 동기화를 고려하지 않음
   - 기존의 동기화된 컬렉션이 동기화를 잃을 수 있음
   - 멀티스레드 환경에서 데이터 훼손 가능성

2. **해결 방안**:
```java
// 올바른 구현
public class SafeSynchronizedCollection<E> implements Collection<E> {
    private final Collection<E> delegate;
    private final Object mutex;
    
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        synchronized (mutex) {
            return delegate.removeIf(filter);
        }
    }
}
```

### 2. 인터페이스 설계 시 고려사항

#### 디폴트 메서드 추가 시 주의점
```java
// 잘못된 디폴트 메서드 설계
public interface PaymentProcessor {
    void process(Payment payment);
    
    // 문제가 될 수 있는 디폴트 메서드
    default void processInBatch(List<Payment> payments) {
        for (Payment payment : payments) {
            process(payment);  // 성능 문제 발생 가능
        }
    }
}

// 최적화가 필요한 구현체
public class BulkPaymentProcessor implements PaymentProcessor {
    @Override
    public void process(Payment payment) {
        // 단일 결제 처리
    }
    
    // 배치 처리를 위한 최적화 필요
    @Override
    public void processInBatch(List<Payment> payments) {
        // 데이터베이스 배치 처리 등 최적화된 구현 필요
        // 하지만 디폴트 구현이 이미 존재
    }
}
```

**상세 설명**:
1. **성능 영향**:
   - 디폴트 구현이 성능을 고려하지 않을 수 있음
   - 구현체가 최적화할 기회를 제한할 수 있음
   - 특히 배치 처리 같은 경우 문제가 됨

2. **해결 방안**:
```java
public interface PaymentProcessor {
    void process(Payment payment);
    
    // 더 나은 디폴트 메서드 설계
    default void processInBatch(List<Payment> payments) {
        // 최적화된 구현이 있는지 확인
        if (this instanceof OptimizedBatchProcessor) {
            ((OptimizedBatchProcessor) this).processBatchOptimized(payments);
            return;
        }
        // 기본 구현
        for (Payment payment : payments) {
            process(payment);
        }
    }
}

// 최적화를 위한 보조 인터페이스
interface OptimizedBatchProcessor {
    void processBatchOptimized(List<Payment> payments);
}
```

## 자주 발생하는 질문과 답변

Q: 디폴트 메서드와 추상 메서드 중 어떤 것을 선택해야 하나요?
A: 다음 기준을 고려하세요:
```java
// 디폴트 메서드가 적절한 경우
public interface DataProcessor {
    void process(Data data);
    
    // 단순한 유틸리티 기능
    default boolean isValid(Data data) {
        return data != null && data.size() > 0;
    }
}

// 추상 메서드가 적절한 경우
public interface ComplexProcessor {
    void process(Data data);
    
    // 구현체별로 다양한 방식이 필요한 경우
    void validateAndProcess(Data data);
}
```

Q: 기존 인터페이스에 디폴트 메서드를 추가할 때 호환성은 어떻게 보장하나요?
A: 다음과 같은 방식으로 접근하세요:
```java
// 1. 보수적인 접근
public interface CautiosInterface {
    // 기존 메서드
    void existingMethod();
    
    // 새로운 디폴트 메서드
    default void newMethod() {
        // 가장 안전하고 기본적인 구현만 제공
        throw new UnsupportedOperationException(
            "Optional operation");
    }
}

// 2. 점진적인 도입
public interface ProgressiveInterface {
    void existingMethod();
    
    // 우선 추상 메서드로 추가
    void newMethod();  // 1단계
    
    // 나중에 디폴트 구현 제공  // 2단계
    //default void newMethod() { ... }
}
```

Q: 디폴트 메서드의 테스트는 어떻게 해야 하나요?
A: 다음과 같이 다양한 시나리오를 테스트하세요:
```java
// 테스트를 위한 구현체들
class BasicImpl implements TestInterface {
    // 기본 구현
}

class CustomImpl implements TestInterface {
    @Override
    public void defaultMethod() {
        // 커스텀 구현
    }
}

class BrokenImpl implements TestInterface {
    @Override
    public void defaultMethod() {
        throw new RuntimeException("Broken");
    }
}

@Test
public void testDefaultMethod() {
    // 1. 기본 구현 테스트
    // 2. 재정의한 경우 테스트
    // 3. 예외 상황 테스트
}
```

## 요약 (Summary)

1. **디폴트 메서드 설계 원칙**
   - 기존 구현체들과의 충돌 가능성 고려
   - 문서화를 통한 명확한 사용 지침 제공
   - 최소한의 기능만 기본 구현으로 제공

2. **호환성 보장 방법**
   - 디폴트 메서드 추가 전 영향 분석
   - 충분한 테스트 케이스 작성
   - 점진적인 도입 전략 수립

3. **실무 적용 가이드**
   - 기존 코드에 미치는 영향 최소화
   - 성능과 동기화 이슈 고려
   - 구현체의 유연성 보장