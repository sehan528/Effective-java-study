# Item 22: 인터페이스는 타입을 정의하는 용도로만 사용하라

## 핵심 개념 (Main Ideas)

### 1. 인터페이스의 본질적 목적
- **정의**: 인터페이스는 클래스가 구현해야 할 동작을 명시하는 타입
- **목적**: 클라이언트에게 클래스가 제공하는 기능을 명확히 전달
- **효과**: 구현 클래스들이 공통 동작을 보장받을 수 있음

### 2. 상수 인터페이스 안티패턴
- **원칙**: 상수를 정의하는 용도로 인터페이스를 사용하지 말 것
- **이유**: 내부 구현을 클래스의 API로 노출시키는 실수를 방지
- **방법**: 상수는 적절한 클래스나 열거 타입에 정의

## 세부 내용 (Details)

### 1. 올바른 인터페이스 설계와 잘못된 예시

#### 올바른 인터페이스 사용
```java
/**
 * 결제 처리를 위한 인터페이스
 * 클래스가 '무엇을 할 수 있는지' 명확히 정의
 */
public interface PaymentProcessor {
    void processPayment(Payment payment);
    PaymentStatus checkStatus(String paymentId);
    void refund(String paymentId);
}

// 구현 클래스는 명확한 계약을 따름
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) {
        // 신용카드 결제 처리 로직
    }
    
    @Override
    public PaymentStatus checkStatus(String paymentId) {
        // 결제 상태 확인 로직
        return status;
    }
    
    @Override
    public void refund(String paymentId) {
        // 환불 처리 로직
    }
}
```

**이 코드가 설명하려는 것**:
1. **타입으로서의 인터페이스**
   - 결제 처리기가 반드시 제공해야 할 기능을 정의
   - 구현 클래스의 행위를 명확히 규정
   - 클라이언트에게 사용 방법을 명확히 안내

2. **확장성과 유지보수성**
   - 새로운 결제 방식 추가가 용이
   - 기존 코드 수정 없이 기능 확장 가능
   - 인터페이스가 변경되면 모든 구현체가 영향을 받음

#### 잘못된 상수 인터페이스 사용
```java
// 안티패턴: 상수 인터페이스
public interface PhysicalConstants {
    // 인터페이스가 상수 저장소로 잘못 사용됨
    double AVOGADROS_NUMBER = 6.022_140_857e23;
    double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    double ELECTRON_MASS = 9.109_383_56e-31;
}

// 잘못된 사용 예
public class ThermodynamicsCalculator implements PhysicalConstants {
    // 이 클래스는 실제로 '물리 상수'가 아님!
    public double calculateKineticEnergy(double mass, double velocity) {
        return mass * velocity * velocity / 2.0;
    }
}
```

**문제점 분석**:
1. **인터페이스 오용**
   - 상수는 구현 세부사항인데 인터페이스로 노출됨
   - IS-A 관계가 성립하지 않음
   - 클라이언트 코드가 내부 구현에 종속됨

2. **유지보수 문제**
   - 상수를 제거하거나 변경하기 어려움
   - 바이너리 호환성 때문에 불필요한 상수도 계속 유지해야 함
   - 이름 충돌 가능성 있음

### 2. 올바른 상수 처리 방법

#### 유틸리티 클래스 사용
```java
/**
 * 물리 상수를 위한 유틸리티 클래스
 * 인스턴스화 방지와 함께 상수들을 논리적으로 그룹화
 */
public final class PhysicalConstants {
    private PhysicalConstants() {
        throw new AssertionError("No instances!");
    }
    
    // 상수에 대한 설명을 javadoc으로 제공
    /**
     * 아보가드로 수 (1/mol)
     */
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    
    /**
     * 볼츠만 상수 (J/K)
     */
    public static final double BOLTZMANN_CONST = 1.380_648_52e-23;
    
    // 단위 변환 유틸리티 메서드도 제공 가능
    public static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32;
    }
}
```

**이 접근방식의 장점**:
1. **명확한 의도**
   - 상수들의 논리적 그룹화
   - 관련 유틸리티 메서드 포함 가능
   - 문서화와 단위 정보 제공 용이

2. **유지보수성**
   - 상수 추가/제거가 다른 코드에 영향을 주지 않음
   - 필요한 상수만 선택적으로 임포트 가능
   - 상수 값 변경이 지역적으로 관리됨

#### 열거 타입 사용
```java
/**
 * 관련된 상수들을 위한 열거 타입
 * 상수와 관련 동작을 함께 캡슐화
 */
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6);
    
    private final double mass;   // kg
    private final double radius; // meters
    
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
    
    // 상수와 관련된 동작을 함께 제공
    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }
}
```

**열거 타입의 이점**:
1. **타입 안전성**
   - 컴파일타임 타입 검사
   - 잘못된 값 사용 방지
   - 관련 동작을 캡슐화

2. **유지보수성**
   - 상수 추가/제거가 쉬움
   - 관련 동작을 한 곳에서 관리
   - 문서화와 버전 관리가 용이

## 자주 발생하는 질문과 답변

Q: 여러 클래스에서 사용하는 상수들은 어떻게 관리해야 하나요?
A: 다음과 같은 방법을 고려하세요:
```java
// 1. 상수 그룹별로 별도의 유틸리티 클래스
public final class DatabaseConstants {
    private DatabaseConstants() {}
    public static final int MAX_CONNECTIONS = 100;
    public static final int TIMEOUT_SECONDS = 30;
}

// 2. 관련 인터페이스나 클래스에 포함
public interface DatabaseOperations {
    // 인터페이스의 동작과 관련된 상수만 포함
    int DEFAULT_TIMEOUT = 30;  // 이 상수는 메서드와 직접 관련됨
    void connect(String url, int timeout);
}
```

Q: 정적 임포트는 언제 사용하는 것이 좋나요?
A: 다음 기준을 참고하세요:
```java
// 권장: 명확성이 유지되는 경우
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

// 비권장: 모호성이 생길 수 있는 경우
import static com.myapp.Constants.*;  // 너무 많은 상수를 가져옴
```

## 요약 (Summary)

1. **인터페이스 설계 원칙**
   - 인터페이스는 타입 정의용으로만 사용
   - 상수 정의는 다른 적절한 방법 사용
   - 구현 세부사항 노출 금지

2. **상수 관리 베스트 프랙티스**
   - 유틸리티 클래스 사용
   - 열거 타입 활용
   - 관련 클래스/인터페이스에 포함

3. **실무 적용 가이드**
   - 상수의 범위와 용도에 따라 적절한 위치 선정
   - 문서화와 유지보수성 고려
   - 타입 안전성 확보