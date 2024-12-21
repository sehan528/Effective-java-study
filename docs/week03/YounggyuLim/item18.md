# 상속 보다는 컴포지션을 사용하라

- 메서드 호출과 달리 상속은 캡슐화를 깨뜨린다.
```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;
    public InstrumentedHashSet() {
    }

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
        addCount += c.size();
        return super.addAll(c);
    }
    public int getAddCount() {
        return addCount;
    }

    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("틱", "틱틱", "펑"));
        //addAll 내부에서 add를 호출하기 때문에 addCount++ 가 두번씩 호출됨
        System.out.println(s.getAddCount());
        for (String string : s) {
            System.out.println(string);
        }
    }
}

```

- 이러한 상속시의 문제점을 해결할 방법
## 컴포지션 : 기존 클래스가 새로운 클래스의 구성요소로 사용되는것
- 컴포지션을 활용하여 동적으로 기능을 추가하는 설계 패턴을 데코레이터 패턴이라함
- 아래 예시는 데코레이터 패턴으로 Set의 기능을 확장하면서 기존 구현에 의존하지 않음
```java
//ForwardingSet 에 상속이 아닌 위임 즉 HashSet의 메서드만 호출해서 중복호출 할 일 없음
public class InstrumentedSet<E> extends ForwardingSet<E>{
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

    public static void main(String[] args) {
        Set<String> hashSet = new HashSet<>();
        InstrumentedSet<String> instrumentedSet = new InstrumentedSet<>(hashSet);

        //위임 받았기 떄문에 add, addAll 이 독립적으로 작동
        instrumentedSet.addAll(List.of("1", "2", "3"));
        System.out.println(instrumentedSet.getAddCount());
        for (String s : instrumentedSet) {
            System.out.println(s);
        }
    }
}

```

## 요약
- 상속은 캡슐화를 해친다는 문제 점이 있기 때문에 상위 클래스와 하위클래스가 순수한 is-a 관계일 때만 사용 해야 한다.
- is-a 관계더라도 하위 클래스의 패키지가 상위 클래스와 다르고 상위 클래스가 확장을 고려하지 않고 설계 됐을시 문제 될 수 있음
- 상속 대신 컴포지션, forwarding(위임) 을 사용하자 래퍼 클래스로 구현할 인터페이스가 존재 할경우 강력히 추천