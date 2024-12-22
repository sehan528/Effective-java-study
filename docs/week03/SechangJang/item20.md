# Item 20: 추상 클래스보다는 인터페이스를 우선하라

## 인터페이스 vs 추상 클래스

### 주요 차이점
1. **다중 구현**
   ```java
   // 클래스는 하나만 확장 가능
   public class Sub extends Super { }
   
   // 인터페이스는 여러 개 구현 가능
   public class Sub implements Interface1, Interface2 { }
   ```

2. **유연성**
   - 추상 클래스: 단일 상속만 가능하여 제약이 큼
   - 인터페이스: 어떤 클래스든 손쉽게 구현 가능

3. **기존 클래스 확장**
   - 추상 클래스: 기존 클래스에 끼워넣기 어려움
   - 인터페이스: 기존 클래스에도 쉽게 구현 가능

## 인터페이스의 강점

### 1. 믹스인 정의에 적합
```java
// Comparable은 믹스인 인터페이스의 좋은 예
public class StringBuffer implements Comparable<StringBuffer> {
    @Override
    public int compareTo(StringBuffer other) {
        return toString().compareTo(other.toString());
    }
}
```

### 2. 계층구조가 없는 타입 프레임워크 가능
```java
public interface Singer {
    void sing(String song);
}

public interface Songwriter {
    String compose();
}

// 두 인터페이스를 모두 구현 가능
public class SingerSongwriter implements Singer, Songwriter {
    @Override
    public void sing(String song) { ... }
    
    @Override
    public String compose() { ... }
}
```

### 3. 디폴트 메서드 지원 (Java 8+)
```java
public interface Deliverable {
    // 기본 구현을 제공하는 디폴트 메서드
    default void deliver() {
        System.out.println("배달을 시작합니다.");
    }
}
```

## 골격 구현 패턴 (Template Method Pattern)

### 구조
1. 인터페이스: API 정의
2. 추상 골격 구현 클래스: 공통 로직 구현
3. 구체 클래스: 나머지 메서드 구현

```java
// 1. 인터페이스
public interface Vehicle {
    void start();
    void stop();
    void accelerate();
    void brake();
}

// 2. 골격 구현
public abstract class AbstractVehicle implements Vehicle {
    @Override
    public void start() {
        System.out.println("시동을 겁니다.");
    }
    
    @Override
    public void stop() {
        System.out.println("시동을 끕니다.");
    }
}

// 3. 구체 클래스
public class Car extends AbstractVehicle {
    @Override
    public void accelerate() {
        System.out.println("가속합니다.");
    }
    
    @Override
    public void brake() {
        System.out.println("제동합니다.");
    }
}
```

### 주의사항
1. Object 메서드는 디폴트 메서드로 제공 불가
   - equals, hashCode, toString 등은 골격 구현 클래스에서 구현
2. 인터페이스는 public 이외의 접근 제어자를 가질 수 없음
3. 필드와 상태는 골격 구현 클래스에서 관리

## 실무적 조언

### 1. 인터페이스 선호 이유
- 기존 클래스에 새로운 기능 추가 용이
- 다중 상속 지원으로 유연한 설계 가능
- 계층 구조가 없는 타입 프레임워크 구축 가능

### 2. 추상 클래스 사용 시점
- 공통 기능이 많은 경우
- 상태나 protected 메서드가 필요한 경우
- 다형성보다 코드 재사용이 중요한 경우

## 핵심 정리
1. 인터페이스를 우선적으로 고려
2. 복잡한 구현이 필요한 경우 골격 구현과 함께 사용
3. 추상 클래스는 공통 기능 구현이 필요할 때만 사용
4. 디폴트 메서드는 신중히 사용