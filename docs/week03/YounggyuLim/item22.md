# 인터페이스는 타입을 정의하는 용도로만 사용하라

- 상수 인터페이스를 잘못 사용한 예
- 상수를 공개할 목적이면 차라리 그 클래스나 인터페이스 자체에 추가하기
- 또는 인스턴스화 할 수 없는 유틸리티 클래스에 담아 공개하기
```java
public interface PhysicalConstants {
static final double AVOGADROS_NUMBER = 6.022;
static final double BOLTZMANN_CONSTANT = 1.380;
static final double ELECTRON_MASS = 9.109;
}
```

- 상수를 공개할 유틸리티 클래스 예시
```java
public class PhysicalConstants {
//상수 유틸리티 클래스
private PhysicalConstants() {} //인스턴스화 방지
public static final double AVOGADROS_NUMBER = 6.022;
public static final double BOLTZMANN_CONSTANT = 1.380;
public static final double ELECTRON_MASS = 9.109;
}

```
