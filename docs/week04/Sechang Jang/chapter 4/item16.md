# Item 16: public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 핵심 개념 (Main Ideas)

### 1. 캡슐화의 중요성
- **정의**: 클래스 내부의 구현을 외부로부터 숨기고 접근자 메서드를 통해서만 접근 가능하게 하는 것
- **목적**: 클래스 내부 표현 방식을 언제든 변경할 수 있는 유연성 확보
- **효과**: 유지보수성 향상, API 안정성 보장, 불변식 보장 가능

### 2. public 클래스의 필드 노출 문제
- **원칙**: public 클래스의 필드는 절대 직접 노출하지 말 것
- **이유**: API 변경의 어려움, 캡슐화 이점 상실, 불변식 보장 불가
- **예외**: package-private 클래스나 private 중첩 클래스

## 세부 내용 (Details)

### 1. 잘못된 캡슐화와 올바른 해결 방법

#### 잘못된 캡슐화의 예
```java
// 이런 클래스는 캡슐화의 이점을 제공하지 못함
public class Point {
    public double x;  // 외부에서 직접 접근 가능한 필드
    public double y;  // 캡슐화 없음
}

// 사용 예시 (문제가 있는 코드)
Point point = new Point();
point.x = 10;  // 직접 필드 수정
point.y = 20;  // 유효성 검사나 부수 작업 수행 불가
```

**문제점 설명**:
1. **캡슐화 위배**
   - 내부 구현이 완전히 노출됨
   - 클래스 사용자가 필드에 직접 의존

2. **유지보수의 어려움**
   - 필드 표현 방식 변경 시 API 변경 필요
   - 모든 클라이언트 코드 수정 필요

3. **불변식 보장 불가**
   - 필드값 검증 불가능
   - 잘못된 값 설정 방지 불가

#### 올바른 캡슐화 방식
```java
public class Point {
    private double x;  // private 필드
    private double y;

    public Point(double x, double y) {
        // 생성자에서 유효성 검사 가능
        if (Double.isNaN(x) || Double.isNaN(y)) {
            throw new IllegalArgumentException("좌표값으로 NaN은 허용하지 않습니다.");
        }
        this.x = x;
        this.y = y;
    }

    // 접근자 메서드
    public double getX() { 
        return x; 
    }

    public double getY() { 
        return y; 
    }

    // 설정자 메서드
    public void setX(double x) {
        // 유효성 검사 수행
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("x 좌표값으로 NaN은 허용하지 않습니다.");
        }
        this.x = x;
        // 필요한 부수 작업 수행 가능
        notifyPositionChange();
    }

    public void setY(double y) {
        if (Double.isNaN(y)) {
            throw new IllegalArgumentException("y 좌표값으로 NaN은 허용하지 않습니다.");
        }
        this.y = y;
        notifyPositionChange();
    }

    private void notifyPositionChange() {
        // 위치 변경에 따른 부수 작업 수행
        // 예: 이벤트 발생, 로깅 등
    }
}
```

**개선된 설계의 장점**:
1. **유효성 검사**
   - 생성자와 설정자에서 값 검증 가능
   - 잘못된 값 설정 방지
   - 객체의 일관성 유지

2. **부수 작업 수행**
   - 값 변경 시 필요한 추가 작업 수행 가능
   - 로깅, 이벤트 발생 등 가능
   - 동기화 등의 부가 기능 추가 용이

3. **내부 구현 변경 유연성**
```java
// 내부 구현을 극좌표계로 변경해도 API는 그대로 유지 가능
public class Point {
    private double r;      // 반지름
    private double theta;  // 각도

    public double getX() {
        return r * Math.cos(theta);  // 계산된 x값 반환
    }

    public double getY() {
        return r * Math.sin(theta);  // 계산된 y값 반환
    }

    public void setX(double x) {
        // 극좌표계로 변환하여 저장
        r = Math.sqrt(x * x + getY() * getY());
        theta = Math.atan2(getY(), x);
    }

    public void setY(double y) {
        r = Math.sqrt(getX() * getX() + y * y);
        theta = Math.atan2(y, getX());
    }
}
```

### 2. package-private 클래스와 private 중첩 클래스의 특별 케이스

#### package-private 클래스의 필드 노출
```java
// 같은 패키지 내에서만 사용되는 클래스
class PackagePrivatePoint {
    // 필드를 노출해도 큰 문제 없음
    double x;
    double y;
}

// 사용 예시 (같은 패키지 내에서)
class PointUser {
    void usePoint() {
        PackagePrivatePoint point = new PackagePrivatePoint();
        point.x = 10;  // 패키지 내부에서는 직접 접근 허용
        point.y = 20;
    }
}
```

**이 방식이 허용되는 이유**:
1. **제한된 사용 범위**
   - 같은 패키지 내부로 사용이 제한됨
   - 클라이언트 코드가 이미 내부 구현을 알고 있음
   - 패키지 전체가 하나의 모듈처럼 동작

2. **유지보수성**
   - 변경이 필요할 때 패키지 내부 코드만 수정
   - 외부 API에 영향을 주지 않음

#### private 중첩 클래스의 필드 노출
```java
public class OuterClass {
    // private 중첩 클래스
    private static class InnerPoint {
        // 필드를 노출해도 안전
        double x;
        double y;
    }

    // 중첩 클래스 사용 예시
    private List<InnerPoint> points = new ArrayList<>();

    public void addPoint(double x, double y) {
        InnerPoint point = new InnerPoint();
        point.x = x;  // 직접 접근이 더 자연스러움
        point.y = y;
        points.add(point);
    }
}
```

**이 방식의 장점**:
1. **캡슐화 유지**
   - 중첩 클래스는 외부에서 접근 불가
   - 클래스의 구현 세부사항으로 완벽히 감춰짐

2. **코드 단순화**
   - 불필요한 접근자 메서드 제거
   - 코드의 가독성 향상

### 3. 실제 사례 분석

#### java.awt.package의 Point와 Dimension
```java
// java.awt.Point - 잘못된 설계의 예
public class Point {
    public int x;
    public int y;
}

// java.awt.Dimension - 마찬가지로 잘못된 설계
public class Dimension {
    public int width;
    public int height;
}
```

**이러한 설계의 문제점**:
1. **API 변경 불가능**
   - 이미 널리 사용되고 있어 변경 불가
   - 하위 호환성 때문에 계속 유지해야 함

2. **성능상의 이점 없음**
   - 직접 필드 접근이 메서드 호출보다 빠르지 않음
   - JVM의 최적화로 인해 성능 차이 미미

## 요약 (Summary)

1. **public 클래스의 필드는 절대로 직접 노출하지 말 것**
   - 캡슐화의 이점 활용
   - 불변식 보장
   - API 안정성 확보

2. **package-private 클래스나 private 중첩 클래스는 예외가 될 수 있음**
   - 제한된 영역에서만 사용
   - 코드 단순화가 더 중요한 경우
   - 패키지 내부 구현에 해당

3. **접근자 메서드를 통한 캡슐화의 이점**
   - 내부 표현 방식 자유로운 변경
   - 불변식 보장
   - 부수 작업 수행 가능