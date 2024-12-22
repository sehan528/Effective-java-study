# Item 22: 인터페이스는 타입을 정의하는 용도로만 사용하라

## 인터페이스의 본질

### 올바른 인터페이스의 역할
- 타입을 정의하는 용도
- 클래스가 무엇을 할 수 있는지 명시
- 클라이언트에게 제공하는 기능 명세

```java
// 올바른 인터페이스 사용 예
public interface PaymentProcessor {
    void processPayment(double amount);
    PaymentStatus getStatus();
    void cancelPayment();
}
```

## 안티패턴: 상수 인터페이스

### 잘못된 상수 인터페이스 예시
```java
// 이렇게 하지 마세요!
public interface PhysicalConstants {
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

### 상수 인터페이스의 문제점
1. 내부 구현이 API로 노출됨
2. 클라이언트 코드가 내부 구현에 종속됨
3. 바이너리 호환성을 위해 계속 유지해야 함
4. 네임스페이스 오염

## 올바른 상수 처리 방법

### 1. 특정 클래스나 인터페이스에 추가
```java
public final class Integer {
    public static final int MIN_VALUE = 0x80000000;
    public static final int MAX_VALUE = 0x7fffffff;
    // ... 나머지 코드
}
```

### 2. 열거 타입 사용
```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6);
    
    private final double mass;
    private final double radius;
    
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
}
```

### 3. 유틸리티 클래스 사용
```java
// 권장되는 상수 처리 방법
public final class PhysicalConstants {
    private PhysicalConstants() {}  // 인스턴스화 방지
    
    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    public static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

### 정적 임포트 사용
```java
// 정적 임포트 사용 시
import static com.example.PhysicalConstants.*;

public class PhysicsCalculator {
    double getKineticEnergy(double mass, double velocity) {
        return mass * velocity * velocity / 2.0;
    }
}
```

## 실무 적용 가이드

### 1. 상수 위치 선정
- 특정 클래스와 연관된 상수 → 해당 클래스에 포함
- 여러 클래스에서 사용하는 상수 → 유틸리티 클래스
- 연관된 상수 그룹 → 열거 타입

### 2. 정적 임포트 사용 지침
```java
// 권장: 클래스 이름을 포함한 사용
PhysicalConstants.AVOGADROS_NUMBER

// 비권장: 정적 임포트를 통한 직접 사용
AVOGADROS_NUMBER  // 출처가 불분명할 수 있음
```

## 핵심 정리

1. 인터페이스 사용 원칙
   - 타입 정의 용도로만 사용
   - 상수 정의 용도로 사용하지 않음

2. 상수 처리 방법
   - 관련 클래스에 포함
   - 열거 타입 사용
   - 유틸리티 클래스 사용

3. 권장 사항
   - 상수의 범위와 목적을 고려한 위치 선정
   - 명확한 네이밍과 문서화
   - 적절한 접근 제어자 사용

4. 주의 사항
   - 인터페이스를 상수 저장소로 사용하지 않음
   - 무분별한 정적 임포트 지양
   - 상수의 가시성과 범위 신중히 고려