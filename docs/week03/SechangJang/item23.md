# Item 23: 태그 달린 클래스보다는 클래스 계층구조를 활용하라

## 태그 달린 클래스의 문제점

### 태그 달린 클래스란?
```java
class Figure {
    enum Shape { RECTANGLE, CIRCLE }
    final Shape shape;  // 태그 필드  - 현재모양을 나타낸다.

    // 사각형일 때만 사용
    double length;
    double width;

    // 원일 때만 사용
    double radius;
    
    // 태그에 따라 다른 동작
    double area() {
        switch (shape) {
            case RECTANGLE: return length * width;
            case CIRCLE: return Math.PI * radius * radius;
            default: throw new AssertionError();
        }
    }
}
```
태그 달린 클래스란 두가지 이상의 의미를 표현할 때 그 중 현재 표현하는 의미를 태그값으로 알려주는 클래스를 말한다.

### 단점
1. **불필요한 코드 증가**
   - 열거 타입 선언
   - 태그 필드
   - switch 문

2. **메모리 낭비**
   - 사용하지 않는 필드도 공간 차지
   - 불필요한 초기화 코드

3. **가독성 저하**
   - 여러 구현이 한 클래스에 혼합
   - 현재 상태 파악이 어려움

4. **오류 가능성 증가**
   - 런타임에 타입 안전성 보장 못함
   - 새로운 타입 추가 시 실수 가능성

## 클래스 계층구조로 변환하기

### 1. 루트 클래스 정의
```java
abstract class Figure {
    abstract double area();
}
```

### 2. 구체 클래스 구현
```java
class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}

class Circle extends Figure {
    final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
```

## 실제 활용 예시: 사용자 타입 시스템

### 태그 달린 클래스 버전 (안티패턴)
```java
class User {
    enum Type { CUSTOMER, RESTAURANT, DELIVERY_PERSON }
    final Type type;
    
    // 공통 필드
    String name;
    
    // 특정 타입만 사용하는 필드들
    String address;      // 고객, 음식점만 사용
    double latitude;     // 배달원만 사용
    double longitude;    // 배달원만 사용
}
```

### 계층 구조 버전 (권장)
```java
abstract class User {
    private final String name;
    
    User(String name) {
        this.name = name;
    }
    
    abstract boolean processOrder(String orderInfo);
}

class Customer extends User {
    private final String address;
    
    @Override
    boolean processOrder(String orderInfo) {
        // 주문 처리 로직
        return true;
    }
}

class DeliveryPerson extends User {
    private double latitude;
    private double longitude;
    
    @Override
    boolean processOrder(String orderInfo) {
        // 배달 처리 로직
        return true;
    }
}
```

## 장점

1. **타입 안전성**
   - 컴파일타임에 타입 검사 가능
   - 잘못된 필드 접근 방지

2. **유지보수성**
   - 새로운 타입 추가가 용이
   - 각 타입별 동작을 명확히 분리

3. **코드 재사용**
   - 공통 기능을 상위 클래스에 구현
   - 중복 코드 제거

## 핵심 정리

1. **태그 달린 클래스를 발견하면 계층구조로 리팩터링 고려**
2. **새로운 클래스 설계 시 태그 필드 사용 지양**
3. **각 의미를 독립된 클래스로 분리하여 타입 안전성 확보**
4. **상속과 다형성을 활용하여 더 유연하고 명확한 설계 추구**