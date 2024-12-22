# Item 18: 상속보다는 컴포지션을 사용하라

## 상속의 문제점

### 캡슐화 위반
- 상속은 상위 클래스의 구현에 의존적
- 상위 클래스의 변경이 하위 클래스에 영향을 미침
- 캡슐화가 깨져 결합도가 높아짐

### 예시: HashSet 확장 시의 문제
```java
// 상위 클래스 (원본 HashSet의 일부)
public class HashSet<E> {
    public boolean add(E e) { ... }
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);  // addAll이 add를 호출
        return true;
    }
}

// 문제가 있는 하위 클래스
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int count = 0;
    
    @Override
    public boolean add(E e) {
        count++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        count += c.size();  // 여기서 카운트 증가
        return super.addAll(c);  // 여기서 다시 add가 호출되어 중복 카운트
    }
}
```

**문제점**
- `addAll` 호출 시 `count`가 중복으로 증가
- 상위 클래스의 구현 세부사항에 종속적
- 상위 클래스 변경 시 하위 클래스가 오동작할 가능성

## 컴포지션 (Composition)

### 정의
- 기존 클래스를 확장하는 대신 새로운 클래스의 private 필드로 기존 클래스의 인스턴스를 참조
- 새 클래스는 기존 클래스의 메서드를 호출해 결과를 반환 (전달, forwarding)

### 예시: 컴포지션을 사용한 래퍼 클래스
```java
// 컴포지션을 사용한 래퍼 클래스
public class InstrumentedSet<E> {
    private final Set<E> set;  // 기존 Set을 감싸는 래퍼
    private int count = 0;

    public InstrumentedSet(Set<E> set) {
        this.set = set;
    }

    public boolean add(E e) {
        count++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        count += c.size();
        return set.addAll(c);
    }
}
```

### 컴포지션의 장점
1. 기존 클래스의 내부 구현에 영향받지 않음
2. 단순한 메서드 전달로 캡슐화 유지
3. 구현 시점에 의존 객체를 선택할 수 있는 유연성

## 상속 사용이 적절한 경우

1. **진정한 is-a 관계인 경우**
   - 하위 클래스가 상위 클래스의 진정한 하위 타입인 경우
   - 예: "자동차는 교통수단이다"

2. **패키지 내부에서의 상속**
   - 같은 프로그래머가 통제하는 패키지 내부
   - 확장을 고려해 설계되고 문서화된 클래스

## 실무적 가이드라인

### 상속 사용 시 확인사항
- 확장하려는 클래스의 API에 결함은 없는가?
- 결함이 있다면 하위 클래스까지 전파되어도 괜찮은가?
- is-a 관계가 확실한가?

### 컴포지션 권장 상황
- 상속 관계가 명확하지 않을 때
- API의 결함이 전파될 우려가 있을 때
- 유연한 설계가 필요할 때

## 핵심 정리
1. 상속은 강력하지만 캡슐화를 해친다
2. 상속은 is-a 관계일 때만 사용한다
3. 상황이 애매하다면 컴포지션을 사용한다
4. 래퍼 클래스는 하위 클래스보다 견고하고 유연하다