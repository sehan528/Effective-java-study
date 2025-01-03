

# Item 16: public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 1. 잘못된 캡슐화의 예시와 문제점

### 1.1 안티패턴: public 필드 노출
```java
// 이런 클래스는 캡슐화의 이점을 제공하지 못한다!
public class Point {
    public double x;
    public double y;
}
```

이러한 클래스가 가지는 문제점:
1. **내부 표현 변경의 어려움**
   - API를 수정하지 않고는 내부 표현을 바꿀 수 없음
   - 한번 공개된 필드는 계속 그 형태를 유지해야 함

2. **불변식 보장 불가**
   - 외부에서 직접 필드 수정 가능
   - 유효성 검증이나 제약조건 적용 불가능

3. **부수 작업 수행 불가**
   - 필드 접근 시 다른 작업을 수행할 수 없음
   - 예: 접근 통계 수집, 로깅, 동기화 등

## 2. 올바른 캡슐화 방법

### 2.1 접근자 메서드를 통한 캡슐화
```java
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
```

이 방식의 장점:
1. **캡슐화의 이점**
   - 내부 표현 방식을 언제든 바꿀 수 있음
   - 불변식 보장 가능
   - 필드 접근 시 부가 동작 수행 가능

2. **유연성**
   - 나중에 내부 구현을 변경하더라도 API는 그대로 유지 가능
   ```java
   public class Point {
       private double radius;
       private double angle;
       
       public double getX() { return radius * Math.cos(angle); }
       public double getY() { return radius * Math.sin(angle); }
   }
   ```

## 3. package-private 클래스 또는 private 중첩 클래스에서의 예외

### 3.1 package-private 클래스의 경우
```java
// 패키지 내부에서만 사용하는 클래스
class PackagePoint {
    // 필드를 노출해도 문제 없음
    double x;
    double y;
}
```

이 경우 필드를 노출해도 되는 이유:
1. **제한된 접근 범위**
   - 같은 패키지 내부에서만 사용
   - 클라이언트 코드가 이미 패키지 내부로 한정됨

2. **코드의 간결성**
   - 불필요한 접근자 메서드를 줄일 수 있음
   - 패키지 내부에서는 어차피 구현을 알고 있음

### 3.2 private 중첩 클래스의 경우
```java
public class OuterClass {
    private static class PrivatePoint {
        // 필드를 노출해도 문제 없음
        double x;
        double y;
    }
}
```

이 경우의 특징:
1. **더욱 제한된 접근 범위**
   - 바깥 클래스까지로 접근이 제한됨
   - 수정 범위가 매우 좁음

2. **구현 편의성**
   - 간단한 데이터 홀더 역할에 적합
   - 불필요한 캡슐화 비용 감소

## 4. 실수 사례: java.awt.Package

### 4.1 잘못된 설계의 예
```java
// java.awt.Point 클래스 - 잘못된 설계의 예시
public class Point {
    public double x;
    public double y;
}

// java.awt.Dimension 클래스 - 마찬가지로 잘못된 설계
public class Dimension {
    public double width;
    public double height;
}
```

이러한 설계가 문제되는 이유:
1. **호환성 때문에 수정 불가**
   - 이미 수많은 코드에서 사용 중
   - 내부 구현을 변경할 수 없음

2. **성능 이슈**
   - 캡슐화의 이점을 포기한 대가가 성능 향상으로 이어지지도 않음

## 5. 정리

1. **public 클래스의 필드는 절대로 직접 노출하지 말 것**
   - 불변 필드라도 직접 노출은 좋지 않음
   - 캡슐화의 이점을 살리려면 접근자 메서드 사용

2. **package-private 클래스나 private 중첩 클래스는 필드 노출이 나을 수 있음**
   - 클래스의 추상 개념을 더 잘 표현할 수 있다면 필드 노출도 괜찮음
   - 코드가 더 간결해짐

3. **접근자 메서드의 이점**
   - 내부 표현 방식을 자유롭게 변경 가능
   - 불변식 보장 가능
   - 부수 작업 수행 가능



