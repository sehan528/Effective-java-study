# Item 19: 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

## 핵심 개념 (Main Ideas)

### 1. 상속 설계의 기본 원칙
- **정의**: 클래스의 내부 동작 과정을 문서화하고, 하위 클래스가 안전하게 확장할 수 있도록 설계
- **목적**: 상속을 통한 재사용성 확보와 동시에 캡슐화 유지
- **효과**: 안전한 상속 계층 구조 형성, 유지보수성 향상

### 2. 상속 문서화의 중요성
- **원칙**: 재정의 가능 메서드의 내부 동작을 명확히 기술
- **이유**: 하위 클래스가 상위 클래스의 동작을 정확히 이해하고 활용할 수 있도록 함
- **방법**: @implSpec 태그를 활용한 구현 설명 제공

## 세부 내용 (Details)

### 1. 메서드 재정의 규칙과 문서화

#### 상속용 클래스의 API 문서화
```java
public abstract class AbstractCollection<E> {
    /**
     * 지정된 원소가 이 컬렉션에 포함되어 있다면 제거합니다.
     * 
     * @implSpec
     * 이 구현은 컬렉션을 순회하며 주어진 원소를 찾습니다.
     * 원소를 찾으면 반복자의 remove 메서드를 호출해 제거합니다.
     * iterator 메서드를 재정의하면 이 메서드의 동작에 영향을 미칩니다.
     *
     * @param o 이 컬렉션에서 제거할 원소
     * @return 이 컬렉션이 특정 원소를 포함하고 있었다면 true
     */
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (o.equals(it.next())) {
                it.remove();
                return true;
            }
        }
        return false;
    }
}
```

**문서화 요소 설명**:
1. **메서드 동작 방식**
   - 컬렉션 순회 방법
   - 원소 제거 방식
   - iterator 메서드와의 연관성

2. **재정의 영향**
   - iterator 메서드 재정의가 remove 동작에 미치는 영향
   - 성능과 동작 방식의 변경 가능성

#### Hook 메서드 제공 예시
```java
public abstract class AbstractList<E> {
    /**
     * 지정된 범위의 원소들을 이 리스트에서 제거합니다.
     * 
     * @implSpec
     * 이 메서드는 clear 메서드에서 호출되며, 
     * 하위 클래스에서 부분리스트의 clear 작업을 
     * 효율적으로 구현할 수 있도록 제공됩니다.
     * 
     * @param fromIndex 제거할 첫 원소의 인덱스
     * @param toIndex 제거할 마지막 원소의 다음 인덱스
     */
    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
            it.next();
            it.remove();
        }
    }
}
```

**Hook 메서드의 중요성**:
1. **성능 최적화**
   ```java
   // removeRange를 활용한 효율적인 clear 구현
   public class CustomList<E> extends AbstractList<E> {
       @Override
       public void clear() {
           if (size() > 0) {
               // 단일 작업으로 모든 원소 제거
               removeRange(0, size());
           }
       }
   }
   ```

2. **확장성 제공**
   - 하위 클래스가 기본 동작을 커스터마이즈할 수 있는 진입점 제공
   - 핵심 알고리즘은 유지하면서 세부 동작 변경 가능

### 2. 생성자 작성 규칙

#### 잘못된 생성자 사용의 예
```java
public class Parent {
    public Parent() {
        // 생성자에서 재정의 가능 메서드 호출 - 잘못된 예
        initialize();  // 재정의 가능 메서드
    }
    
    protected void initialize() {
        // 초기화 로직
    }
}

public class Child extends Parent {
    private final String importantField;
    
    public Child() {
        importantField = "초기값";  // 부모 생성자 호출 후 초기화
    }
    
    @Override
    protected void initialize() {
        // NPE 발생! importantField가 아직 초기화되지 않음
        System.out.println("초기화: " + importantField.toLowerCase());
    }
}

// 실행 시
Child child = new Child();  // NullPointerException 발생
```

**문제점 분석**:
1. **초기화 순서**
   - 상위 클래스 생성자가 먼저 실행됨
   - 하위 클래스의 필드가 초기화되기 전에 재정의된 메서드 호출
   - null 참조나 초기화되지 않은 상태 접근 위험

2. **해결 방안**
```java
public class SafeParent {
    public SafeParent() {
        // 생성자에서는 final 메서드만 호출
        finalMethod();
    }
    
    // 재정의할 수 없는 메서드
    private final void finalMethod() {
        // 안전한 초기화 로직
    }
}
```

## 특수한 상속 상황 처리

### 1. Cloneable 구현 클래스의 상속

#### clone() 메서드 작성 시 주의사항
```java
public class Stack implements Cloneable {
    private Object[] elements;
    private int size;
    
    // 잘못된 clone 메서드 구현 - 재정의 가능 메서드를 호출
    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            // 위험: resize는 재정의될 수 있는 메서드
            result.resize(size);  
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    protected void resize(int newSize) {
        // 크기 조정 로직
    }
}

// 문제가 발생할 수 있는 하위 클래스
public class SpecialStack extends Stack {
    private Object specialField;
    
    @Override
    protected void resize(int newSize) {
        // specialField가 초기화되기 전에 호출될 수 있음
        specialField.toString();  // NPE 발생 가능
    }
}
```

**안전한 구현 방법**:
```java
public class SafeStack implements Cloneable {
    private Object[] elements;
    private int size;
    
    // 안전한 clone 구현 - final 메서드만 사용
    @Override
    public SafeStack clone() {
        try {
            SafeStack result = (SafeStack) super.clone();
            // 배열의 복사본 생성
            result.elements = elements.clone();
            // 재정의 불가능한 private 도우미 메서드 사용
            initializeState(result);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    // 재정의 불가능한 private 도우미 메서드
    private void initializeState(SafeStack stack) {
        // 안전한 초기화 로직
    }
}
```

### 2. Serializable 구현 클래스의 상속

#### 직렬화 관련 메서드 처리
```java
public class Parent implements Serializable {
    // 잘못된 예 - private으로 선언된 writeReplace
    private Object writeReplace() {
        // 직렬화 로직
        return new SerializationProxy(this);
    }
}

public class Child extends Parent {
    // 부모의 writeReplace가 private이라 무시됨
    // 의도하지 않은 직렬화 형태가 사용될 수 있음
}

// 올바른 구현
public class SafeParent implements Serializable {
    // protected로 선언하여 하위 클래스가 재정의할 수 있게 함
    protected Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    // readObject도 protected로 선언
    protected Object readObject() throws InvalidObjectException {
        throw new InvalidObjectException("프록시가 필요합니다");
    }
}
```

### 3. 상속을 금지하는 설계 방법

#### final 클래스 사용
```java
// 상속을 명시적으로 금지
public final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // 나머지 메서드들...
}
```

#### private 생성자와 정적 팩터리 메서드
```java
// 상속을 우회적으로 금지
public class UtilityClass {
    // private 생성자로 상속 방지
    private UtilityClass() {
        throw new AssertionError("인스턴스화 할 수 없습니다");
    }
    
    // 정적 팩터리 메서드
    public static UtilityClass newInstance() {
        return new UtilityClass();
    }
}
```

### 4. 실제 사례 분석: AbstractList의 removeRange

```java
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    /**
     * clear 작업을 효율적으로 구현하기 위한 hook 메서드
     * 
     * @implSpec
     * 이 메서드는 fromIndex부터 toIndex까지의 모든 원소를 제거합니다.
     * clear 메서드는 이 메서드를 호출하여 모든 원소를 제거합니다.
     * 이 구현은 리스트 반복자를 사용하여 지정된 범위의 원소를 제거합니다.
     * 
     * Note: 이 메서드를 재정의하면 clear 메서드의 성능을 크게 향상시킬 수 있습니다.
     */
    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }

    /**
     * 리스트의 모든 원소를 제거합니다.
     */
    public void clear() {
        removeRange(0, size());
    }
}
```

**removeRange의 중요성 설명**:
1. **성능 최적화**
   - 일반적인 구현: O(n) 시간 복잡도
   - 최적화된 구현: 더 나은 성능 제공 가능

2. **실제 활용 예**:
```java
public class ArrayList<E> extends AbstractList<E> {
    private Object[] elementData;
    private int size;
    
    // removeRange 최적화 구현
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        // 배열 복사를 통한 효율적인 범위 제거
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, 
                        elementData, fromIndex, numMoved);
        
        // 참조 해제로 GC 지원
        int newSize = size - (toIndex - fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }
}
```

## 실무 적용 가이드

### 1. 상속용 클래스의 설계와 검증

#### 테스트 클래스 작성 예시
```java
// 상속용으로 설계된 클래스
public class AbstractUserService {
    /**
     * 사용자 정보를 처리합니다.
     * 
     * @implSpec
     * 이 구현은 다음 순서로 동작합니다:
     * 1. validateUser 호출하여 사용자 유효성 검증
     * 2. processUserData 호출하여 데이터 처리
     * 3. notifyUserProcessed 호출하여 처리 완료 알림
     */
    public final void handleUser(User user) {
        if (validateUser(user)) {
            processUserData(user);
            notifyUserProcessed(user);
        }
    }

    /**
     * 사용자 유효성을 검증합니다.
     * 하위 클래스에서 재정의하여 추가 검증을 구현할 수 있습니다.
     */
    protected boolean validateUser(User user) {
        return user != null && user.getId() != null;
    }

    /**
     * 사용자 데이터를 처리합니다.
     * 하위 클래스는 반드시 이 메서드를 구현해야 합니다.
     */
    protected abstract void processUserData(User user);

    /**
     * 사용자 처리 완료를 알립니다.
     * 기본 구현은 로깅만 수행합니다.
     */
    protected void notifyUserProcessed(User user) {
        Logger.info("User " + user.getId() + " processed");
    }
}

// 테스트를 위한 구체적인 구현
class TestUserService extends AbstractUserService {
    private final List<User> processedUsers = new ArrayList<>();

    @Override
    protected void processUserData(User user) {
        processedUsers.add(user);
    }

    @Override
    protected boolean validateUser(User user) {
        // 상위 클래스의 검증에 추가 검증 수행
        return super.validateUser(user) && user.getEmail() != null;
    }

    // 테스트를 위한 보조 메서드
    public List<User> getProcessedUsers() {
        return new ArrayList<>(processedUsers);
    }
}
```

#### 설계 검증을 위한 테스트 코드
```java
public class UserServiceTest {
    @Test
    public void validateInheritanceContract() {
        TestUserService service = new TestUserService();
        
        // 1. null 사용자 테스트
        assertFalse(service.validateUser(null));
        
        // 2. 유효하지 않은 사용자 테스트
        User invalidUser = new User(null, "test@example.com");
        assertFalse(service.validateUser(invalidUser));
        
        // 3. 유효한 사용자 처리 테스트
        User validUser = new User("id1", "test@example.com");
        service.handleUser(validUser);
        assertEquals(1, service.getProcessedUsers().size());
        
        // 4. protected 메서드 동작 검증
        assertTrue(service.getProcessedUsers().contains(validUser));
    }
}
```

### 2. 실전에서의 상속 설계 결정

#### 상속 허용 여부 결정 트리
```java
// 1. 상속을 금지해야 하는 경우
public final class StringUtils {
    private StringUtils() { }  // 인스턴스화 방지
    
    public static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }
}

// 2. 제한된 상속을 허용하는 경우
public abstract class AbstractDao {
    private final DataSource dataSource;
    
    protected AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    // 하위 클래스에서 사용할 수 있는 유틸리티 메서드
    protected final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    // 하위 클래스가 구현해야 하는 추상 메서드
    protected abstract void executeQuery(Connection conn) throws SQLException;
}

// 3. 완전한 상속 지원을 위한 경우
public class HashMap<K,V> extends AbstractMap<K,V> {
    // 잘 문서화된 protected 메서드들
    protected void reinitialize() { ... }
    protected Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) { ... }
}
```

### 3. 문서화 가이드라인

#### @implSpec 주석 작성 예시
```java
/**
 * 이 컬렉션에 주어진 원소가 있는지 확인합니다.
 *
 * @implSpec
 * 이 구현은 컬렉션을 순회하면서 equals 메서드를 사용해 원소를 비교합니다.
 * iterator 메서드를 재정의하면 이 메서드의 동작에 영향을 미칩니다.
 * 순회 중에 컬렉션이 수정되면 이 메서드의 동작은 보장되지 않습니다.
 *
 * @param o 검색할 원소
 * @return 이 컬렉션이 주어진 원소를 포함하면 true, 아니면 false
 * @throws NullPointerException o가 null이고, 이 컬렉션이 null을 허용하지 않는 경우
 */
public boolean contains(Object o) {
    // 구현 내용
}
```

## 핵심 요약

1. **상속 설계시 고려사항**
   - 문서화: 재정의 가능 메서드의 자기사용 패턴 명시
   - 테스트: 실제 하위 클래스를 만들어 검증
   - 제약: 생성자에서 재정의 가능 메서드 호출 금지

2. **실무적 조언**
   - protected 메서드는 신중히 선별
   - 상속용 클래스는 배포 전 충분한 테스트
   - 클래스 확장성과 캡슐화 사이의 균형 유지

3. **코딩 관례**
   - @implSpec 태그 활용
   - protected 멤버는 영원한 API가 됨을 인식
   - 하위 클래스를 만들어 테스트 필수

이러한 지침들을 따르면 안전하고 유용한 상속 가능 클래스를 만들 수 있습니다. 상속을 허용하지 않을 경우에는 명시적으로 금지하는 것이 좋습니다.