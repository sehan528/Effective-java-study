# Item 24: 멤버 클래스는 되도록 static으로 만들라

## 핵심 개념 (Main Ideas)

### 1. 중첩 클래스의 유형과 특징
- **정의**: 다른 클래스 내부에 정의된 클래스
- **목적**: 논리적 그룹화와 캡슐화 강화
- **효과**: 코드의 가독성과 유지보수성 향상

### 2. static 멤버 클래스의 중요성
- **원칙**: 멤버 클래스에서 바깥 클래스 참조가 필요 없다면 static으로 선언
- **이유**: 메모리 누수 방지와 성능 향상
- **방법**: 클래스 선언에 static 키워드 추가

## 세부 내용 (Details)

### 1. 중첩 클래스의 종류와 특징

#### 정적 멤버 클래스
```java
public class Container {
    private static int staticField = 0;
    private int instanceField = 0;
    
    // 정적 멤버 클래스
    public static class StaticMemberClass {
        public void accessFields() {
            System.out.println(staticField);  // 정적 필드 접근 가능
            // System.out.println(instanceField);  // 컴파일 에러
            // System.out.println(Container.this); // 컴파일 에러
        }
        
        public void doSomething() {
            // 독립적인 작업 수행
        }
    }
}

// 사용 예시
Container.StaticMemberClass member = new Container.StaticMemberClass();
member.doSomething();
```

**이 코드가 설명하려는 것**:
1. **독립성**
   - 바깥 클래스의 인스턴스 없이도 사용 가능
   - 정적 멤버만 접근 가능
   - 메모리 참조가 명확함

2. **메모리 효율**
   - 불필요한 참조를 갖지 않음
   - 메모리 누수 가능성 없음

#### 비정적 멤버 클래스
```java
public class Container {
    private int value = 42;
    
    // 비정적 멤버 클래스
    public class NonStaticMemberClass {
        public void accessOuter() {
            System.out.println(value);  // 외부 클래스 필드 직접 접근
            System.out.println(Container.this.value);  // 명시적 참조
        }
    }
    
    // 잘못된 사용 예 (메모리 누수 가능성)
    public NonStaticMemberClass createMember() {
        return new NonStaticMemberClass();  // 숨은 외부 참조 생성
    }
}

// 인스턴스 생성 방법
Container container = new Container();
Container.NonStaticMemberClass member = container.new NonStaticMemberClass();
```

**이 구조의 문제점**:
1. **숨은 참조**
   - 비정적 멤버 클래스는 암묵적으로 외부 클래스 참조를 가짐
   - 메모리 누수의 원인이 될 수 있음
   - 디버깅이 어려움

### 2. 정적 멤버 클래스의 활용

#### 헬퍼 클래스 구현
```java
public class Calculator {
    // 계산 결과를 담는 정적 멤버 클래스
    public static class Result {
        private final double value;
        private final String unit;
        
        public Result(double value, String unit) {
            this.value = value;
            this.unit = unit;
        }
        
        public double getValue() { return value; }
        public String getUnit() { return unit; }
    }
    
    public static Result calculate(double input) {
        double result = complexCalculation(input);
        return new Result(result, "meters");
    }
    
    private static double complexCalculation(double input) {
        // 복잡한 계산 로직
        return input * 2;
    }
}

// 사용 예시
Calculator.Result result = Calculator.calculate(5.0);
```

**적절한 활용의 예**:
1. **결과 캡슐화**
   - 계산 결과를 의미 있는 객체로 표현
   - 관련 데이터를 논리적으로 그룹화
   - 타입 안전성 제공

### 3. 비정적 멤버 클래스의 적절한 사용

#### 어댑터 패턴 구현
```java
public class ArrayList<E> {
    // ... 다른 필드와 메서드들 ...
    
    // Iterator 어댑터로 적절한 비정적 멤버 클래스
    private class Itr implements Iterator<E> {
        int cursor;       // 다음 반환할 요소의 인덱스
        int lastRet = -1; // 마지막으로 반환한 요소의 인덱스
        
        public boolean hasNext() {
            return cursor != size();
        }
        
        @SuppressWarnings("unchecked")
        public E next() {
            // 외부 클래스의 상태에 접근 필요
            if (cursor >= size())
                throw new NoSuchElementException();
                
            Object[] elementData = ArrayList.this.elementData;
            lastRet = cursor;
            cursor++;
            return (E) elementData[lastRet];
        }
    }
    
    public Iterator<E> iterator() {
        return new Itr();
    }
}
```

**적절한 사용의 이유**:
1. **외부 상태 접근**
   - 반복자가 컬렉션의 상태에 접근해야 함
   - 외부 클래스와 긴밀한 연관 관계
   - 논리적으로 외부 클래스에 종속

## 자주 발생하는 질문과 답변

Q: 언제 정적 멤버 클래스를 사용하고, 언제 비정적 멤버 클래스를 사용해야 하나요?
A: 다음 기준으로 판단하세요:
```java
// 1. 정적 멤버 클래스가 적절한 경우
public class PaymentProcessor {
    // 결제 결과를 나타내는 클래스 - 외부 참조 불필요
    public static class PaymentResult {
        private final boolean success;
        private final String message;
        
        public PaymentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}

// 2. 비정적 멤버 클래스가 적절한 경우
public class BankAccount {
    private double balance;
    
    // 거래 내역을 추적하는 클래스 - 계좌 상태 참조 필요
    public class Transaction {
        public void withdraw(double amount) {
            if (balance >= amount) {
                balance -= amount;  // 외부 상태 수정
            }
        }
    }
}
```

Q: 멤버 클래스를 비정적으로 생성했을 때의 구체적인 문제점은 무엇인가요?
A: 다음과 같은 문제들이 발생할 수 있습니다:
```java
// 메모리 누수 가능성이 있는 코드
public class LeakyClass {
    private static List<NonStaticInner> instances = new ArrayList<>();
    
    // 비정적 멤버 클래스
    public class NonStaticInner {
        // 큰 데이터를 가진 필드
        private byte[] data = new byte[1024 * 1024];
    }
    
    public void createInstances() {
        for (int i = 0; i < 100; i++) {
            // 각 NonStaticInner 인스턴스가 LeakyClass 인스턴스를 참조
            instances.add(new NonStaticInner());
        }
    }
}

// 개선된 버전
public class BetterClass {
    private static List<StaticInner> instances = new ArrayList<>();
    
    // 정적 멤버 클래스
    public static class StaticInner {
        private byte[] data = new byte[1024 * 1024];
    }
    
    public void createInstances() {
        for (int i = 0; i < 100; i++) {
            // 외부 클래스 참조 없음
            instances.add(new StaticInner());
        }
    }
}
```

## 요약 (Summary)

1. **멤버 클래스 설계 원칙**
   - 바깥 클래스 참조가 필요 없다면 무조건 static 선언
   - 필요한 경우에만 비정적으로 생성
   - 불필요한 메모리 참조 최소화

2. **활용 가이드**
   - 독립적인 헬퍼 클래스는 정적으로
   - 어댑터 구현은 비정적으로
   - 메모리 누수 가능성 항상 고려

3. **실무 적용 포인트**
   - 멤버 클래스 생성 시 static 고려가 첫 단계
   - 외부 참조 필요성 신중히 검토
   - 성능과 메모리 영향 분석