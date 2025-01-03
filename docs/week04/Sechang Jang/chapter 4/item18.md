# Item 18: 상속보다는 컴포지션을 사용하라

## 1. 상속의 문제점

### 1.1 배경
상속은 코드를 재사용하는 강력한 방법이지만, 잘못 사용하면 오히려 프로그램을 취약하게 만듭니다. 상속은 캡슐화를 깨뜨리는 문제가 있습니다.

### 1.2 구체적인 문제점
1. **상위 클래스의 구현이 하위 클래스에 노출**
   - 하위 클래스가 상위 클래스의 구현 세부사항에 의존
   - 상위 클래스 변경 시 하위 클래스 오동작 가능성

2. **메서드 재정의의 위험성**
```java
// 잘못된 상속의 예
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;  // 추가된 원소 수

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();  // 여기서 중복 카운트 발생
        return super.addAll(c);
    }
}

// 문제 발생 예시
InstrumentedHashSet<String> set = new InstrumentedHashSet<>();
set.addAll(Arrays.asList("a", "b", "c"));  // addCount는 6이 됨
```

### 1.3 캡슐화 위반으로 인한 영향
1. **유지보수성 저하**
   - 상위 클래스 수정 시 하위 클래스 전체 검토 필요
   - 상위 클래스 작성자와 하위 클래스 작성자가 다를 경우 문제 심화

2. **확장성 제한**
   - 상위 클래스의 제약을 그대로 물려받음
   - 유연한 설계 변경이 어려움

## 2. 컴포지션을 통한 해결

### 2.1 컴포지션이란?
기존 클래스를 확장하는 대신 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하는 방식입니다.

### 2.2 컴포지션 구현 방법
```java
// 컴포지션 사용 예시
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;
    private final Set<E> s;

    public InstrumentedSet(Set<E> s) {
        this.s = s;
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return s.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return s.addAll(c);
    }
}
```

### 2.3 전달 클래스(forwarding class)
```java
// 재사용 가능한 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) { this.s = s; }

    // Set 인터페이스의 메서드를 전달
    public void clear()               { s.clear(); }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty()          { return s.isEmpty(); }
    // ... 나머지 메서드들도 같은 방식으로 구현
}
```

## 3. 컴포지션의 장점

### 3.1 유연성
1. **새로운 메서드 추가 용이**
2. **기존 클래스 내부 구현과 독립적**
3. **다양한 상위 클래스 기능 활용 가능**

### 3.2 데코레이터 패턴 활용
```java
// 기존 Set에 기능을 덧씌우는 예시
Set<Date> dates = new InstrumentedSet<>(new HashSet<>());
Set<Date> syncDates = new InstrumentedSet<>(
    Collections.synchronizedSet(new HashSet<>())
);
```

## 4. 상속 사용이 적절한 경우

### 4.1 IS-A 관계가 성립할 때
```java
// 적절한 상속의 예
public class Rectangle {
    private int width, height;
    // ... 
}

// Square IS-A Rectangle
public class Square extends Rectangle {
    // ...
}
```

### 4.2 확실한 하위 타입인 경우
1. **상위 클래스의 API에 결함이 없는지 확인**
2. **하위 클래스의 의미가 상위 클래스의 의미와 일치**

## 5. 실무 적용 가이드

### 5.1 상속 vs 컴포지션 선택 기준
1. **상속 선택 시 확인사항**
   - IS-A 관계가 확실한가?
   - API에 결함이 없는가?
   - 결함이 있다면 하위 클래스까지 전파되어도 괜찮은가?

2. **컴포지션 선택 시 이점**
   - 내부 구현 변경에 독립적
   - 유연한 기능 확장 가능
   - 다중 상속의 효과를 안전하게 달성

### 5.2 주의사항
1. **콜백 프레임워크에서의 주의점**
   - 자기 참조 전달 시 래퍼 객체가 아닌 내부 객체 전달 가능
   - SELF 문제 발생 가능성

2. **래퍼 클래스 주의사항**
   - 성능 저하가 있을 수 있음
   - 디버깅이 어려울 수 있음