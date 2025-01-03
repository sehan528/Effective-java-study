# Item 23: 태그 달린 클래스보다는 클래스 계층구조를 활용하라

## 핵심 개념 (Main Ideas)

### 1. 태그 달린 클래스의 문제점
- **정의**: 하나의 클래스가 여러 가지 타입을 표현하며, 현재 타입을 태그 값으로 알려주는 방식
- **목적**: 하나의 클래스로 여러 타입의 객체를 표현하려는 시도
- **효과**: 하나의 클래스로 여러 타입을 표현할 수 있지만, 다양한 문제를 야기함

### 2. 클래스 계층구조의 우수성
- **원칙**: 각 타입을 독립된 클래스로 표현하고 상속을 통해 관계 정의
- **이유**: 타입 안전성 보장, 코드 명확성 향상, 유지보수성 개선
- **방법**: 공통 부분은 상위 클래스로, 특화된 부분은 하위 클래스로 분리

## 세부 내용 (Details)

### 1. 태그 달린 클래스의 단점

#### 나쁜 예: 태그 달린 클래스
```java
class Shape {
    // 태그 필드 - 현재 도형의 종류를 나타냄
    enum Type { RECTANGLE, CIRCLE }
    final Type type;

    // 직사각형용 필드
    double length;
    double width;

    // 원용 필드
    double radius;

    // 직사각형용 생성자
    Shape(double length, double width) {
        this.type = Type.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    // 원용 생성자
    Shape(double radius) {
        this.type = Type.CIRCLE;
        this.radius = radius;
    }

    // 태그에 따라 다른 계산 로직
    double area() {
        switch (type) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * radius * radius;
            default:
                throw new AssertionError("Unknown shape: " + type);
        }
    }
}
```

**이 코드의 문제점**:
1. **불필요한 복잡성**
   - 여러 구현이 한 클래스에 혼재
   - 태그 처리를 위한 부가 코드 필요
   - switch 문의 중복 발생 가능성

2. **메모리 낭비**
   - 모든 인스턴스가 모든 필드를 포함
   - 사용하지 않는 필드도 메모리 차지
   - 불필요한 초기화 코드 존재

### 2. 클래스 계층구조로의 전환

#### 계층구조를 사용한 개선된 설계
```java
// 추상 기반 클래스
abstract class Shape {
    abstract double area();  // 추상 메서드로 정의
}

// 직사각형 구체 클래스
class Rectangle extends Shape {
    private final double length;
    private final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override
    double area() {
        return length * width;
    }
}

// 원 구체 클래스
class Circle extends Shape {
    private final double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * radius * radius;
    }
}
```

**개선된 설계의 장점**:
1. **타입 안전성**
   - 컴파일타임에 타입 검사 가능
   - 잘못된 필드 접근 방지
   - 각 타입에 필요한 필드만 포함

2. **코드 명확성**
   - 각 타입의 동작이 명확히 분리
   - 불필요한 코드가 제거됨
   - 의도가 명확히 드러남

### 3. 실제 활용 예시

#### 복잡한 비즈니스 로직에서의 활용
```java
// 잘못된 방식: 태그 달린 클래스
class Payment {
    enum Type { CREDIT_CARD, BANK_TRANSFER, CRYPTOCURRENCY }
    final Type type;
    
    // 신용카드 필드
    String cardNumber;
    String expiryDate;
    
    // 계좌이체 필드
    String bankAccount;
    String bankCode;
    
    // 암호화폐 필드
    String walletAddress;
    String cryptoType;
    
    // 태그에 따른 처리 로직
    boolean process() {
        switch (type) {
            case CREDIT_CARD:
                return processCreditCard();
            case BANK_TRANSFER:
                return processBankTransfer();
            case CRYPTOCURRENCY:
                return processCrypto();
            default:
                throw new IllegalStateException();
        }
    }
    
    private boolean processCreditCard() { /* ... */ }
    private boolean processBankTransfer() { /* ... */ }
    private boolean processCrypto() { /* ... */ }
}
```

```java
// 개선된 방식: 클래스 계층구조
abstract class Payment {
    abstract boolean process();
}

class CreditCardPayment extends Payment {
    private final String cardNumber;
    private final String expiryDate;
    
    @Override
    boolean process() {
        // 신용카드 처리 로직
        return true;
    }
}

class BankTransferPayment extends Payment {
    private final String bankAccount;
    private final String bankCode;
    
    @Override
    boolean process() {
        // 계좌이체 처리 로직
        return true;
    }
}
```


**실제 활용의 이점**:
1. **확장성**
   - 새로운 결제 방식 추가가 용이
   - 기존 코드 수정 없이 확장 가능
   - 각 결제 방식의 독립성 보장

2. **유지보수성**
   - 각 결제 방식별 로직이 분리됨
   - 버그 수정이 다른 타입에 영향을 주지 않음
   - 테스트가 용이해짐

## 자주 발생하는 질문과 답변

Q: 태그 필드와 클래스 계층구조 중 언제 무엇을 선택해야 하나요?
A: 다음 기준으로 판단하세요:
```java
// 태그 필드가 불가피한 경우 (매우 드묾)
class TemporaryDocument {
    private enum State { DRAFT, PUBLISHED }
    private State state;
    
    // 상태 전이가 빈번하고 임시적인 경우
}

// 대부분의 경우 계층구조가 더 나음
abstract class Document {
    abstract void process();
}

class DraftDocument extends Document {
    @Override
    void process() { /* 초안 처리 */ }
}

class PublishedDocument extends Document {
    @Override
    void process() { /* 발행된 문서 처리 */ }
}
```

Q: 필드가 많은 클래스를 계층구조로 전환할 때 좋은 방법은?
A: 단계적 접근이 필요합니다:
```java
// 1단계: 공통 필드와 메서드 식별
abstract class BaseProduct {
    protected String name;
    protected double price;
}

// 2단계: 특화된 필드와 메서드를 하위 클래스로 이동
class DigitalProduct extends BaseProduct {
    private String downloadUrl;
}

class PhysicalProduct extends BaseProduct {
    private double weight;
    private String dimensions;
}
```

## 요약 (Summary)

1. **태그 달린 클래스의 문제점**
   - 불필요한 코드와 메모리 낭비
   - 가독성과 유지보수성 저하
   - 오류 가능성 증가

2. **클래스 계층구조의 장점**
   - 타입 안전성 보장
   - 코드 명확성과 재사용성 향상
   - 유지보수와 확장이 용이

3. **실무 적용 가이드**
   - 태그 필드 발견 시 계층구조로 전환 고려
   - 새로운 설계 시 처음부터 계층구조 사용
   - 각 타입의 특성을 살린 설계 지향