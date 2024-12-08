# Item 3: private 생성자나 열거 타입으로 싱글턴임을 보증하라

싱글턴(Singleton)은 인스턴스를 오직 하나만 생성할 수 있는 클래스입니다. 주로 무상태(stateless) 객체나 설계상 유일해야 하는 시스템 컴포넌트에서 사용됩니다.

## 싱글턴 구현 방식

### 1. public static final 필드 방식
가장 간단한 구현 방식입니다.

```java
public class Speaker {
    public static final Speaker INSTANCE = new Speaker();
    private Speaker() {
        // 리플렉션 공격 방지
        if (INSTANCE != null) {
            throw new RuntimeException("싱글턴 인스턴스는 이미 존재합니다.");
        }
    }
}
```

**장점:**
- 해당 클래스가 싱글턴임이 API에 명확히 드러남
- 구현이 간단하고 직관적
- final 키워드로 인해 다른 객체 참조 불가능

**단점:**
- 리플렉션을 통한 private 생성자 호출 가능성 존재
- 직렬화/역직렬화시 새 인스턴스 생성 가능성

### 2. 정적 팩터리 메서드 방식
은닉된 static 필드와 접근 메서드를 제공하는 방식입니다.

```java
public class Speaker {
    private static final Speaker INSTANCE = new Speaker();
    private Speaker() {}

    public static Speaker getInstance() {
        return INSTANCE;
    }
}
```

**장점:**
- API 변경 없이 싱글턴이 아니게 변경 가능
- 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있음
- 정적 팩터리 메서드 참조를 공급자(Supplier)로 사용 가능

```java
// Supplier로 사용 예시
Supplier<Speaker> speakerSupplier = Speaker::getInstance;
Speaker speaker = speakerSupplier.get();
```

### 3. 지연 초기화(Lazy Initialization) 방식
필요한 시점에 인스턴스를 생성하는 방식입니다.

```java
public class Speaker {
    private static Speaker instance;
    private Speaker() {}
    
    public static synchronized Speaker getInstance() {
        if (instance == null) {
            instance = new Speaker();
        }
        return instance;
    }
}
```

**사용 시나리오:**
- 인스턴스 생성 비용이 큰 경우
- 인스턴스가 필요한지 확실하지 않은 경우
- DB 연결과 같이 자원 관리가 필요한 경우

### 4. Enum 싱글턴
가장 간단하고 안전한 방식입니다.

```java
public enum Speaker {
    INSTANCE;
    
    private String message;
    
    public Speaker getInstance() {
        return INSTANCE;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
```

**사용 예시:**
```java
Speaker speaker1 = Speaker.INSTANCE.getInstance();
speaker1.setMessage("안내 방송 중입니다.");
Speaker speaker2 = Speaker.INSTANCE;
System.out.println(speaker1.getMessage()); // "안내 방송 중입니다."
System.out.println(speaker2.getMessage()); // "안내 방송 중입니다."
```

**장점:**
- 직렬화/역직렬화 시에도 인스턴스 하나임을 보장
- 리플렉션 공격에도 안전
- 구현이 간단

**단점:**
- 상속이 불가능
- 유연성이 떨어짐

## 직렬화 문제 해결
싱글턴 클래스를 직렬화할 때는 추가적인 처리가 필요합니다.

```java
public class Speaker implements Serializable {
    private static final Speaker INSTANCE = new Speaker();
    
    private Speaker() {}
    
    public static Speaker getInstance() { return INSTANCE; }
    
    // 역직렬화시 새 인스턴스 생성 방지
    private Object readResolve() {
        return INSTANCE;
    }
}
```

## 싱글턴의 단점과 한계

1. **테스트의 어려움**
    - 싱글턴을 사용하는 클라이언트 코드의 테스트가 어려움
    - Mock 객체로 대체하기 힘듦

2. **상속 불가능**
    - private 생성자로 인해 상속이 불가능
    - 객체지향의 장점인 다형성 활용이 제한됨

3. **서버 환경에서의 문제**
    - 여러 JVM에 분산 배치되는 경우 인스턴스 유일성 보장이 어려움
    - 클래스 로더 구성에 따라 여러 인스턴스 생성 가능

4. **전역 상태**
    - 전역 상태를 만들 수 있어 객체지향 원칙에 위배될 수 있음

## 결론

1. 싱글턴이 필요한 상황이라면 다음 순서로 구현 방식을 고려하라:
    - Enum 방식 (가장 권장)
    - public static final 필드 방식
    - 정적 팩터리 메서드 방식

2. 상황별 선택:
    - 상속이 필요한 경우: 정적 팩터리 메서드 방식
    - 완벽한 직렬화가 필요한 경우: Enum 방식
    - 지연 초기화가 필요한 경우: synchronized 정적 팩터리 메서드 방식