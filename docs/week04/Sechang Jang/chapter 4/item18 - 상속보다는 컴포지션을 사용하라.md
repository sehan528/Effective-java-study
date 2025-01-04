# Item 18: 상속보다는 컴포지션을 사용하라

## 핵심 개념 (Main Ideas)

### 1. 상속의 위험성
- **정의**: 클래스가 다른 클래스를 확장하는 구현 상속의 문제점
- **목적**: 안전하고 유연한 코드 재사용 방법 이해
- **해결책**: 컴포지션(합성)을 통한 기능 확장

## 세부 내용 (Details)

### 1. 상속의 구체적인 문제점

#### 잘못된 상속 사용의 예
```java
// 문제가 있는 코드
public class InstrumentedHashSet<E> extends HashSet<E> {
    // 추가된 원소의 수를 세는 카운터
    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();  // 여기서 문제 발생
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}

// 문제 발생 코드
public class Main {
    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("틱", "탁탁", "펑"));  // List.of는 Java 9부터 지원
        System.out.println(s.getAddCount());  // 예상: 3, 실제: 6
    }
}
```

**문제점 설명**:
1. **중복 카운트 발생**
   - addAll() 메서드가 내부적으로 add()를 호출
   - 원소 하나당 두 번 카운트됨 (addAll에서 한 번, 내부 add에서 한 번)
   
2. **자기사용(self-use) 문제**
   - 상위 클래스의 메서드가 다른 메서드를 호출하는 내부 동작 방식에 의존
   - 이는 캡슐화를 깨뜨림

### 2. 컴포지션을 통한 해결

#### 래퍼 클래스 구현
```java
// 재사용 가능한 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    // 기존 Set을 감싸고 있음
    private final Set<E> s;
    
    public ForwardingSet(Set<E> s) { 
        this.s = s; 
    }

    // Set 인터페이스의 메서드를 전달
    public void clear()               { s.clear(); }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty(); }
    public int size()                { return s.size(); }
    public Iterator<E> iterator()    { return s.iterator(); }
    public boolean add(E e)          { return s.add(e); }
    public boolean remove(Object o)  { return s.remove(o); }
    public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
    public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
    public Object[] toArray()        { return s.toArray(); }
    public <T> T[] toArray(T[] a)   { return s.toArray(a); }
    @Override 
    public boolean equals(Object o)  { return s.equals(o); }
    @Override 
    public int hashCode()           { return s.hashCode(); }
    @Override
    public String toString()        { return s.toString(); }
}

// 래퍼 클래스 - 컴포지션 활용
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

**컴포지션의 장점 설명**:
1. **유연성**
   ```java
   // 다양한 Set 구현체와 함께 사용 가능
   Set<String> hashSet = new InstrumentedSet<>(new HashSet<>());
   Set<String> treeSet = new InstrumentedSet<>(new TreeSet<>());
   Set<String> linkedHashSet = new InstrumentedSet<>(new LinkedHashSet<>());
   ```

2. **기능 조합**
   ```java
   // 여러 기능을 조합하여 사용 가능
   Set<String> synchronizedInstrumentedSet = 
       new InstrumentedSet<>(Collections.synchronizedSet(new HashSet<>()));
   ```

### 3. 데코레이터 패턴 이해

#### 데코레이터 패턴 예시
```java
// 기본 기능을 가진 클래스
public interface Coffee {
    double getCost();
    String getDescription();
}

public class SimpleCoffee implements Coffee {
    @Override
    public double getCost() { return 1.0; }
    
    @Override
    public String getDescription() { return "Simple Coffee"; }
}

// 데코레이터 베이스 클래스
public abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public double getCost() { return decoratedCoffee.getCost(); }
    
    @Override
    public String getDescription() { return decoratedCoffee.getDescription(); }
}

// 구체적인 데코레이터
public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 0.5;
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", with milk";
    }
}

// 사용 예시
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
System.out.println(coffee.getDescription());  // "Simple Coffee, with milk"
System.out.println(coffee.getCost());         // 1.5
```

**데코레이터 패턴 설명**:
1. **동작 원리**
   - 기본 객체를 감싸는 래퍼 객체 생성
   - 기능을 덧씌우는 방식으로 확장
   - 런타임에 유연하게 기능 추가 가능

2. **장점**
   - 상속 없이 기능 확장 가능
   - 각 기능을 독립적으로 구현
   - 기능 조합이 자유로움

### 4. 상속이 적절한 경우

#### IS-A 관계의 예시
```java
// 적절한 상속의 예
public abstract class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    public abstract void makeSound();
}

public class Dog extends Animal {  // Dog IS-A Animal
    public Dog(String name) {
        super(name);
    }
    
    @Override
    public void makeSound() {
        System.out.println("멍멍!");
    }
}

// 부적절한 상속의 예
public class Stack extends Vector {  // Stack IS-NOT-A Vector
    // Vector의 모든 메서드가 노출됨
    // add(int index, element) 같은 메서드로 스택의 불변식이 깨질 수 있음
}
```

**IS-A 관계 판단 기준**:
1. **의미적 관계**
   - 하위 클래스가 상위 클래스의 진정한 하위 타입인가?
   - 상위 클래스의 모든 메서드가 하위 클래스에서 의미가 있는가?

2. **대체 가능성**
   - 리스코프 치환 원칙 만족 여부
   - 상위 클래스 대신 하위 클래스를 사용해도 문제가 없는가?

## 자주 발생하는 질문과 답변

Q: 상속과 컴포지션을 결정할 때 가장 중요한 판단 기준은 무엇인가요?
A: IS-A 관계의 성립 여부가 가장 중요합니다:
1. **IS-A 관계 확인**
   ```java
   // IS-A 관계가 성립하는 예
   public class Bird extends Animal { } // Bird는 Animal이다.
   
   // IS-A 관계가 성립하지 않는 예
   public class TextFile extends ArrayList<String> { } // TextFile은 ArrayList가 아니다.
   ```
2. **구현 세부사항 vs 진정한 하위 타입**
   - 상속은 하위 클래스가 상위 클래스의 진정한 하위 타입인 경우에만 사용
   - 단순히 코드를 재사용하기 위한 경우라면 컴포지션 사용

Q: SELF 문제(자기 참조 문제)가 무엇이고 어떻게 해결하나요?
A: SELF 문제는 콜백 프레임워크에서 발생하는 문제입니다:
```java
// SELF 문제 예시
public class Window {
    public void addCallback(WindowCallback callback) {
        // callback은 나중에 호출될 것임
    }
}

public class WindowWrapper {
    private final Window window;
    
    public WindowWrapper(Window window) {
        this.window = window;
        // 문제: window는 wrapper가 아닌 내부 객체를 호출
        window.addCallback(this);  // 콜백에서 문제 발생
    }
}
```
해결방법:
1. 래퍼 클래스가 콜백 인터페이스를 직접 구현하지 않음
2. 컴포지션 대신 다른 패턴(예: 프록시 패턴) 사용 고려

Q: 상속 대신 컴포지션을 사용할 때의 단점은 없나요?
A: 컴포지션에도 몇 가지 단점이 있습니다:
1. **메서드 호출 부가 비용**
   ```java
   // 컴포지션 사용 시 메서드 호출 체인 발생
   public class Wrapper {
       private Wrapped wrapped;
       
       public void operation() {
           // 추가 메서드 호출 발생
           wrapped.operation();
       }
   }
   ```
2. **디버깅 어려움**
   - 호출 스택이 더 깊어짐
   - 여러 래퍼 클래스가 중첩될 경우 복잡도 증가

3. **해결방안**
   - 성능 문제가 실제로 중요한 경우에만 고려
   - 대부분의 경우 유연성과 안전성이 더 중요

Q: 기존 상속을 컴포지션으로 리팩토링할 때의 전략은?
A: 다음과 같은 단계적 접근이 필요합니다:
```java
// 1. 기존 상속 기반 코드
public class LegacyChild extends Parent {
    @Override
    public void operation() {
        // 기존 구현
    }
}

// 2. 컴포지션으로 리팩토링
public class CompositionBased {
    private final Parent wrapped;
    
    public CompositionBased(Parent wrapped) {
        this.wrapped = wrapped;
    }
    
    public void operation() {
        // 필요한 추가 로직
        wrapped.operation();
    }
    
    // 3. 점진적으로 다른 메서드들도 이전
}
```
리팩토링 전략:
1. 테스트 코드 작성
2. 점진적인 변경
3. 기존 코드와의 호환성 유지

Q: 인터페이스 상속과 구현 상속의 차이는 무엇인가요?
A: 두 가지는 매우 다른 개념입니다:
```java
// 인터페이스 상속 (타입 정의)
public interface Drawable {
    void draw();
}

public interface ColoredDrawable extends Drawable {
    void setColor(Color c);
}

// 구현 상속 (코드 재사용)
public class Parent {
    protected void helperMethod() {
        // 구현 세부사항
    }
}

public class Child extends Parent {
    public void someOperation() {
        // 부모의 구현을 재사용
        helperMethod();
    }
}
```
주요 차이점:
1. **인터페이스 상속**
   - 타입 정의에 중점
   - 구현 제약 없음
   - 다중 상속 가능

2. **구현 상속**
   - 코드 재사용에 중점
   - 구현에 종속
   - 단일 상속만 가능

## 요약 (Summary)

1. **상속 사용 시 주의사항**
   - IS-A 관계가 확실한 경우에만 사용
   - 상위 클래스의 API 결함이 없는지 확인
   - 캡슐화 위반 가능성 고려

2. **컴포지션 권장 이유**
   - 유연한 기능 확장 가능
   - 내부 구현 의존성 제거
   - 캡슐화 유지

3. **실무 적용 포인트**
   - 상속보다 컴포지션을 먼저 고려
   - 래퍼 클래스와 데코레이터 패턴 활용
   - 불필요한 API 노출 방지