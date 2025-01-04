# Item 13: clone 재정의는 주의해서 진행하라

## 핵심 개념 (Main Ideas)

### 1. Cloneable 인터페이스의 특성
- **정의**: Object의 protected clone 메서드의 동작 방식을 결정하는 믹스인 인터페이스
- **목적**: 객체의 복제 가능성을 명시
- **효과**: 구현 클래스에서 clone 메서드 호출 시 필드 단위 복사 수행

### 2. 복제의 올바른 방법 선택
- **원칙**: 상황에 따라 적절한 복제 방법 선택
- **이유**: clone의 문제점과 제약사항 회피
- **방법**: Copy Constructor나 Copy Factory Method 우선 고려

## 세부 내용 (Details)

### 1. 기본적인 clone 구현

#### 불변 객체의 clone
```java
public final class PhoneNumber implements Cloneable {
    private final short areaCode, prefix, lineNum;

    @Override 
    public PhoneNumber clone() {
        try {
            // Object의 clone 호출
            return (PhoneNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            // 일어날 수 없는 일이므로 런타임 예외로 변환
            throw new AssertionError();
        }
    }
}
```

**이 구현이 설명하는 것**:
1. **기본 복제 메커니즘**
   - super.clone()은 필드 단위 복사 수행
   - 모든 필드가 기본 타입이나 불변 객체를 참조할 때 적합
   - 공변 반환 타이핑(PhoneNumber 반환)으로 캐스팅 불필요

2. **예외 처리**
   - CloneNotSupportedException은 검사 예외이나 실제로는 발생하지 않음
   - Cloneable 구현 클래스에서는 예외가 발생하지 않음을 보장

### 2. 가변 객체의 복제

#### 잘못된 clone 구현
```java
public class Stack implements Cloneable {
    private Object[] elements;
    private int size = 0;

    // 잘못된 clone 구현 - 원본과 복제본이 같은 배열을 공유
    @Override
    public Stack clone() {
        try {
            return (Stack) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

// 문제 발생 예시
Stack original = new Stack();
original.push("원본 데이터");
Stack clone = original.clone();
clone.push("복제본 데이터");  // 원본 스택도 영향받음!
```

**이 코드의 문제점**:
1. **얕은 복사의 한계**
   - elements 배열이 원본과 복제본 간에 공유됨
   - 한 쪽의 수정이 다른 쪽에 영향을 미침
   - 불변식이 깨질 수 있음

#### 올바른 clone 구현 - 깊은 복사
```java
public class Stack implements Cloneable {
    private Object[] elements;
    private int size = 0;

    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            // 배열의 깊은 복사 수행
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    // 동작 검증을 위한 메서드들
    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }
    
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }
}
```

**개선된 구현의 특징**:
1. **깊은 복사 수행**
   - elements 배열도 clone하여 독립적인 복사본 생성
   - 원본과 복제본이 서로 독립적으로 동작
   - 불변식이 보장됨

2. **배열 복제의 특징**
   - 배열의 clone은 런타임 타입과 컴파일타임 타입 모두 보존
   - 배열의 clone은 해당 배열의 clone 메서드가 가장 적절한 방법

### 3. 복잡한 가변 객체의 복제

#### 해시테이블의 깊은 복사 구현
```java
public class HashTable implements Cloneable {
    private Entry[] buckets;
    
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        // 엔트리 깊은 복사 - 반복적 방식
        Entry deepCopy() {
            // 첫 번째 엔트리 생성
            Entry result = new Entry(key, value, next);
            
            // 다음 엔트리들을 순회하며 복사
            Entry p = result;
            while (p.next != null) {
                p.next = new Entry(
                    p.next.key, 
                    p.next.value, 
                    p.next.next);
                p = p.next;
            }
            return result;
        }
    }
    
    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            
            // 각 버킷에 대해 깊은 복사 수행
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null)
                    result.buckets[i] = buckets[i].deepCopy();
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

**구현의 중요 포인트**:
1. **연결 리스트의 복사**
   - 각 버킷의 연결 리스트를 반복적으로 복사
   - 재귀적 방식 대신 반복적 방식 사용
   - 스택 오버플로우 위험 회피

2. **복사 과정의 안전성**
   - 복사 도중 예외가 발생해도 원본은 안전
   - 일관된 상태 유지
   - 메모리 누수 방지

## 자주 발생하는 질문과 답변

Q: clone 대신 복사 생성자나 팩터리를 사용하면 어떤 장점이 있나요?
A: 다음 예시로 살펴보겠습니다:
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    // 복사 생성자
    public PhoneNumber(PhoneNumber original) {
        this.areaCode = original.areaCode;
        this.prefix = original.prefix;
        this.lineNum = original.lineNum;
    }

    // 복사 팩터리
    public static PhoneNumber newInstance(PhoneNumber original) {
        return new PhoneNumber(
            original.areaCode, 
            original.prefix, 
            original.lineNum);
    }
}

// 사용 예시
PhoneNumber original = new PhoneNumber(707, 867, 5309);
// 복사 생성자 사용
PhoneNumber copy1 = new PhoneNumber(original);
// 복사 팩터리 사용
PhoneNumber copy2 = PhoneNumber.newInstance(original);
```

**장점 설명**:
1. **명확성과 안전성**
   - 언어 모순적인 메커니즘(clone)을 사용하지 않음
   - 예외 처리가 필요 없음
   - 형변환이 필요 없음

2. **유연성**
   - final 필드도 정상적으로 복제 가능
   - 인터페이스 타입의 인스턴스도 복제 가능

Q: 가변 상태를 갖는 클래스의 복사는 어떻게 해야 할까요?
A: 깊은 복사를 수행하는 복사 생성자를 사용하세요:
```java
public class Stack {
    private Object[] elements;
    private int size;

    // 복사 생성자
    public Stack(Stack original) {
        // 방어적 복사를 통한 깊은 복사
        this.elements = original.elements.clone();
        this.size = original.size;
    }
    
    // 더 유연한 복사 생성자 - 인터페이스 타입 받기
    public Stack(Collection<? extends E> elements) {
        this.elements = elements.toArray();
        this.size = elements.size();
    }
}

// 사용 예시
Stack original = new Stack();
original.push("테스트");
// 복사 생성자로 안전한 복제
Stack copy = new Stack(original);
```

Q: clone을 재정의할 때 주의해야 할 가장 중요한 점은 무엇인가요?
A: 다음 사례를 통해 알아보겠습니다:
```java
public class Employee implements Cloneable {
    private String name;
    private Date hireDate;  // 가변 객체

    // 잘못된 clone 구현
    @Override
    public Employee clone() {
        try {
            Employee result = (Employee) super.clone();
            // hireDate가 가변객체인데 얕은 복사만 수행
            return result;  // 위험!
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    // 올바른 clone 구현
    @Override
    public Employee clone() {
        try {
            Employee result = (Employee) super.clone();
            // 가변 객체는 깊은 복사 수행
            result.hireDate = (Date) hireDate.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

### 4. 상속 관련 고려사항

#### 상속용 클래스의 clone 처리
```java
public class AbstractFoo implements Cloneable {
    private int x, y;  // 가변 상태
    private State state;  // 가변 객체 참조

    // 상속용 clone 메서드
    @Override
    protected AbstractFoo clone() 
            throws CloneNotSupportedException {
        AbstractFoo result = (AbstractFoo) super.clone();
        // 가변 객체의 깊은 복사
        result.state = state.clone();
        return result;
    }

    // 하위 클래스에서 clone 재정의 시 호출할 메서드
    protected void initialize() {
        this.state = new State();
    }
}

// 구체 하위 클래스
public class Foo extends AbstractFoo {
    private final int data;  // 추가 상태

    @Override
    public Foo clone() {
        // 공변 반환 타입 사용
        Foo result = (Foo) super.clone();
        // 추가 상태 처리
        result.initialize();
        return result;
    }
}
```

**상속 관련 주의사항**:
1. **clone 메서드 선언**
   - protected로 선언하여 하위 클래스에서 접근 가능
   - 필요한 경우 public으로 오버라이드
   - CloneNotSupportedException 선언 필요

2. **하위 클래스 고려사항**
   - 상위 클래스의 clone 호출
   - 필요한 경우 추가 초기화 수행
   - 불변식 보장

## 요약 (Summary)

1. **clone 메서드 구현 지침**
   - 가능하면 복사 생성자나 팩터리 메서드 사용
   - 배열 복사 외에는 clone 사용 지양
   - Cloneable 구현 시 모든 가변 객체 깊은 복사

2. **복제 방식 선택**
   - 단순 객체: 복사 생성자/팩터리
   - 배열: clone 메서드
   - 가변 객체: 깊은 복사 수행

3. **실무 적용 가이드**
   - 새로운 클래스는 Cloneable 확장 지양
   - 복사 생성자 패턴 우선 사용
   - 상속을 고려한 clone 설계 필요시 신중히 구현