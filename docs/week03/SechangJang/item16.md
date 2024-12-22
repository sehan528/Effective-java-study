# Item 16: public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 잘못된 클래스 설계의 예

### 인스턴스 필드를 직접 노출하는 클래스
```java
public class Point {
    public double x;  // 외부에서 직접 접근 가능
    public double y;  // 캡슐화의 이점 없음
}
```

### 직접 노출의 문제점
1. **API 변경의 어려움**
   - 내부 표현을 바꾸려면 API 자체를 수정해야 함
   - getter/setter 없이는 유연한 변경 불가능

2. **불변식 보장 불가**
   - 클라이언트가 직접 데이터 수정 가능
   - 유효성 검증 불가능

3. **부수 작업 제한**
   - 필드 접근 시 부가 작업을 수행할 수 없음

## 올바른 클래스 설계

### 접근자 메서드를 활용한 캡슐화
```java
public class Point {
    private double x;
    private double y;  // private 필드

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

### 캡슐화의 장점
1. **내부 구현 변경 용이**
   - 외부 API를 유지하면서 내부 구현 변경 가능
   - 클라이언트 코드 영향 최소화

2. **불변식 보장**
   - setter에서 유효성 검증 가능
   - 데이터 일관성 유지

3. **부수 작업 가능**
   - 접근자 메서드에서 로깅, 동기화 등 추가 작업 수행 가능

## 특별한 경우: package-private 클래스와 private 중첩 클래스

### 중첩 클래스를 활용한 구현
```java
public class ColorPoint {
    private static class Point {  // private 중첩 클래스
        public double x;
        public double y;
    }

    public Point getPoint() {
        Point point = new Point();
        point.x = 1.2;
        point.y = 5.3;
        return point;
    }
}
```

### 중첩 클래스의 이점
- 외부 클래스에서만 접근 가능
- 코드가 더 간결해짐
- 패키지 내부 구현을 숨김

## 실전 활용 예시: 유연한 내부 구현

```java
public class ItemInfo {
    private final String name;    // 변경 불가능
    private double cost;          // 내부용
    private static final double TAX_RATE = 0.1;

    public String getName() { return name; }
    
    // 외부에는 price로 노출, 내부에서는 cost로 관리
    public double getPrice() { 
        return cost * (1 + TAX_RATE); 
    }
    
    public void setPrice(double price) {
        this.cost = price / (1 + TAX_RATE);
    }
}
```

## 정리

1. **public 클래스의 필드는 절대로 직접 노출하지 말 것**
   - 불변 필드라도 직접 노출은 지양

2. **package-private 클래스나 private 중첩 클래스는 필드 노출이 받아들여질 수 있음**
   - 구현이 더 깔끔해질 수 있는 경우에 한함 (데이터 캡슐화는 내부 구현을 숨기고 유연성을 제공한다)

3. **접근자 메서드를 사용한 캡슐화는 유연성을 제공**
   - (package-private 클래스나 private 중첩 클래스의 경우 필드 노출이 허용될 수 있다)
   - 내부 구현 변경의 자유
   - 불변식 보장
   - 부수 작업 가능

