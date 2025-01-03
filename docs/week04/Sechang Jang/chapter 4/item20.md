# Item 20: 추상 클래스보다는 인터페이스를 우선하라

## 1. 다중 구현 메커니즘의 이해

### 1.1 배경
자바에서는 다중 구현 메커니즘으로 인터페이스와 추상 클래스를 제공합니다. 둘 다 인스턴스 메서드를 구현 형태로 제공할 수 있지만, 본질적인 차이가 있습니다.

### 1.2 인터페이스와 추상 클래스의 핵심 차이
1. **구현 클래스의 제약**
   - 추상 클래스: 반드시 추상 클래스의 하위 클래스가 되어야 함
   - 인터페이스: 어떤 클래스를 상속했든 정의된 메서드만 구현하면 됨

2. **유연성 측면**
   - 추상 클래스: 단일 상속만 가능하여 새로운 타입 확장이 어려움
   - 인터페이스: 기존 클래스에도 쉽게 새로운 인터페이스 구현 가능

### 1.3 실제 적용 사례
자바 플랫폼 라이브러리의 예시:
- Comparable
- Iterable
- AutoCloseable

이러한 인터페이스들은 기존 클래스들에 쉽게 구현되어 추가될 수 있었습니다.

## 2. 인터페이스의 강점

### 2.1 믹스인(Mixin) 정의
1. **개념**
   - 믹스인: 클래스의 주된 기능에 선택적 기능을 '혼합'할 수 있게 하는 메커니즘
   - 추상 클래스로는 불가능한 기능 (단일 상속의 한계)

2. **실제 예시**
   ```java
   public interface Comparable<T> {
       int compareTo(T o);
   }
   
   // 주된 기능은 유지하면서 비교 기능 추가
   public class File implements Readable, Comparable<File> {
       private long size;
       
       @Override
       public int compareTo(File other) {
           return Long.compare(size, other.size);
       }
   }
   ```

### 2.2 계층 구조가 없는 타입 프레임워크
1. **장점**
   - 타입을 계층적으로 제한하지 않음
   - 조합의 유연성 제공

2. **현실 세계의 예시**
   ```java
   public interface Singer { void sing(); }
   public interface Songwriter { void compose(); }
   
   // 다양한 조합 가능
   public class RockStar implements Singer, Songwriter { ... }
   public class BackupSinger implements Singer { ... }
   ```

## 3. 골격 구현(Skeletal Implementation)

### 3.1 개념과 필요성
1. **정의**
   - 인터페이스와 추상 클래스의 장점을 모두 취하는 방식
   - 인터페이스로 타입을 정의하고, 골격 구현 클래스로 공통 기능 제공

2. **이름 규칙**
   - 관례상 'Abstract{Interface}' 형태로 명명
   - 예: AbstractList, AbstractSet, AbstractMap

### 3.2 구현 방법
```java
// 1. 인터페이스 정의
public interface Collection<E> {
    boolean add(E e);
    boolean remove(Object o);
    int size();
    // ... 기타 메서드들
}

// 2. 골격 구현 제공
public abstract class AbstractCollection<E> implements Collection<E> {
    // 기본 기능 구현
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }
    
    // 공통 로직 제공
    public boolean isEmpty() {
        return size() == 0;
    }
}
```

### 3.3 시뮬레이트한 다중 상속
1. **개념**
   - private 내부 클래스로 골격 구현을 확장
   - 기능을 외부에 위임

2. **장점**
   - 다중 상속의 효과를 안전하게 달성
   - 내부 구현 세부사항 숨김

## 4. 주의사항과 제약

### 4.1 디폴트 메서드 제약
1. Object의 equals, hashCode 등은 디폴트 메서드로 제공 불가
2. 인터페이스는 인스턴스 필드를 가질 수 없음
3. public이 아닌 정적 멤버도 가질 수 없음 (private 정적 메서드 제외)

### 4.2 골격 구현 작성 지침
1. 기반 메서드들을 선정하여 추상 메서드로 선언
2. 기반 메서드들을 이용해 직접 구현 가능한 메서드는 디폴트 메서드로 제공
3. 공통 기능은 골격 구현 클래스에 구현

## 5. 실무 적용 가이드

### 5.1 인터페이스 선택 시점
- 새로운 타입을 정의할 때
- 기존 클래스에 새로운 기능을 추가할 때
- 계층 구조 없이 타입을 만들어야 할 때

### 5.2 추상 클래스 선택 시점
- 공통 기능 구현이 필요한 경우
- 상태나 비public 메서드가 필요한 경우
- 단일 상속이 문제되지 않는 경우