# Item 20: 추상 클래스보다는 인터페이스를 우선하라

## 핵심 개념 (Main Ideas)

### 1. 인터페이스와 추상 클래스의 차이점
- **정의**: 다중 구현 메커니즘에서 인터페이스는 타입을 정의하고, 추상 클래스는 타입과 구현을 함께 제공
- **목적**: 유연하고 확장 가능한 타입 계층 구조 설계
- **효과**: 기존 클래스에도 쉽게 새로운 인터페이스를 구현할 수 있음

### 2. 인터페이스의 장점
- **원칙**: 추상 클래스보다 인터페이스를 우선적으로 사용
- **이유**: 다중 구현이 가능하며, 기존 클래스에도 쉽게 추가 가능
- **방법**: 디폴트 메서드와 골격 구현 클래스를 함께 제공

## 세부 내용 (Details)

### 1. 인터페이스의 유연성 활용

#### 믹스인(Mixin) 인터페이스 구현
```java
// 기본 능력을 정의하는 인터페이스
public interface Singer {
    void sing(String song);
}

// 추가 능력을 정의하는 믹스인 인터페이스
public interface Songwriter {
    Song compose(String title);
}

// 여러 인터페이스를 자유롭게 조합
public class PopStar implements Singer, Songwriter {
    @Override
    public void sing(String song) {
        System.out.println("Singing: " + song);
    }
    
    @Override
    public Song compose(String title) {
        return new Song(title);
    }
}
```

**이 코드가 설명하려는 것**:
- Singer와 Songwriter는 서로 독립적인 능력을 정의
- PopStar 클래스는 두 능력을 자유롭게 조합
- 추상 클래스였다면 이런 유연한 조합이 불가능

#### 계층 구조 없는 타입 프레임워크
```java
public interface Animal {
    void eat();
}

public interface Flying {
    void fly();
}

public interface Swimming {
    void swim();
}

// 다양한 조합 가능
public class Duck implements Animal, Flying, Swimming {
    @Override
    public void eat() { /* 구현 */ }
    
    @Override
    public void fly() { /* 구현 */ }
    
    @Override
    public void swim() { /* 구현 */ }
}

public class Penguin implements Animal, Swimming {
    @Override
    public void eat() { /* 구현 */ }
    
    @Override
    public void swim() { /* 구현 */ }
}
```

**상세 설명**:
1. **유연한 확장성**:
   - 필요한 기능만 선택적으로 구현 가능
   - 계층 구조의 제약 없이 기능 조합 가능
   - 새로운 조합을 만들어도 기존 코드에 영향 없음

2. **코드 재사용성**:
   - 공통 기능을 인터페이스로 분리
   - 여러 클래스에서 필요한 기능만 구현
   - 단일 상속의 제약을 우회

### 2. 골격 구현 클래스 활용

#### 템플릿 메서드 패턴 구현
```java
// 인터페이스 정의
public interface List<E> {
    boolean add(E element);
    E get(int index);
    int size();
    // ... 기타 메서드들
}

// 골격 구현 클래스
public abstract class AbstractList<E> implements List<E> {
    // 핵심 기능은 추상 메서드로
    protected abstract void removeRange(int fromIndex, int toIndex);
    
    // 공통 구현 제공
    @Override
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if (o == null) {
            while (it.hasNext()) {
                if (it.next() == null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }
    
    // 편의 메서드 제공
    public void clear() {
        removeRange(0, size());
    }
}
```

**이 골격 구현이 제공하는 이점**:
1. **코드 재사용**:
   - 공통 로직을 골격 구현 클래스에서 제공
   - 구현 클래스는 핵심 기능만 구현하면 됨
   - 중복 코드 감소

2. **유지보수성**:
   - 공통 로직 변경 시 한 곳만 수정
   - 버그 수정이 모든 구현체에 자동 적용
   - 일관된 동작 보장

## 자주 발생하는 질문과 답변

Q: 인터페이스와 추상 클래스 중 어떤 것을 사용해야 할까요?
A: 다음 기준으로 선택할 수 있습니다:
```java
// 인터페이스가 적합한 경우
public interface PaymentProcessor {
    void processPayment(double amount);
    
    // 선택적 기능은 디폴트 메서드로
    default boolean supports(String paymentMethod) {
        return true;  // 기본적으로 모든 방식 지원
    }
}

// 추상 클래스가 적합한 경우
public abstract class DatabaseConnector {
    private final String url;
    private final String username;
    
    protected DatabaseConnector(String url, String username) {
        this.url = url;
        this.username = username;
    }
    
    // 상태를 가지는 공통 메서드
    protected final Connection createConnection() {
        // 연결 생성 로직
    }
}
```

Q: 골격 구현 클래스를 만들 때 주의할 점은 무엇인가요?
A: 다음 사항들을 고려해야 합니다:
```java
// 잘못된 예
public abstract class AbstractShape {
    // 너무 많은 추상 메서드
    protected abstract void draw();
    protected abstract void resize();
    protected abstract void rotate();
    protected abstract void translate();
    // ... 더 많은 추상 메서드들
}

// 좋은 예
public abstract class AbstractShape {
    // 핵심 메서드만 추상으로
    protected abstract void draw();
    
    // 나머지는 기본 구현 제공
    public void resize(double factor) {
        // 기본 크기 조절 구현
    }
    
    public void rotate(double angle) {
        // 기본 회전 구현
    }
}
```

Q: 기존 클래스에 새 인터페이스를 추가하는 것이 항상 가능한가요?
A: 대부분의 경우 가능하지만 주의할 점이 있습니다:
```java
// 기존 클래스
public class LegacyClass {
    public void doSomething() { /* ... */ }
}

// 새 인터페이스
public interface NewFeature {
    void doSomething();  // 기존 메서드와 시그니처가 같음
}

// 안전한 확장
public class EnhancedClass extends LegacyClass implements NewFeature {
    // 이미 doSomething이 구현되어 있으므로
    // 추가 구현 필요 없음
}
```

## 요약 (Summary)

1. **인터페이스 우선 원칙**
   - 추상 클래스보다 인터페이스를 우선 사용
   - 골격 구현으로 추상 클래스의 장점도 취함
   - 다중 상속과 믹스인을 활용한 유연한 설계

2. **확장성과 유지보수성**
   - 기존 클래스에도 인터페이스 추가 가능
   - 독립적인 기능 조합으로 유연성 확보
   - 공통 구현으로 코드 중복 방지

3. **실무 적용 가이드**
   - 인터페이스로 타입 정의
   - 골격 구현으로 편의 제공
   - 디폴트 메서드 적절히 활용