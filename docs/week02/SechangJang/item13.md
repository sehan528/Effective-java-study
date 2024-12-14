# Item 13: Clone 재정의는 주의해서 진행하라

## Clone의 이해

Cloneable 인터페이스는 객체의 복제 가능성을 명시하는 용도로 설계되었습니다. 하지만 몇 가지 특이한 점이 있습니다:

- clone 메서드는 Cloneable이 아닌 Object에 protected로 선언되어 있음
- Cloneable 구현만으로는 외부 객체에서 clone 메서드를 호출할 수 없음
- Reflection을 사용하면 가능하나 항상 보장되지 않음

## Clone의 문제점

### 1. Cloneable 인터페이스의 특이성
- 메서드가 하나도 없는 인터페이스
- Object의 protected clone 메서드의 동작 방식을 결정
- Cloneable 구현 클래스의 clone 호출 → 필드 단위 복사
- 미구현 클래스의 clone 호출 → CloneNotSupportedException 발생

### 2. 이례적인 인터페이스 사용
- 일반적인 인터페이스: 해당 클래스가 인터페이스의 기능을 제공한다고 선언
- Cloneable: 상위 클래스의 protected 메서드 동작 방식을 변경
- 실무에서는 public clone 메서드 제공 기대
- 모든 상위 클래스가 프로토콜을 지켜야 하는 제약

### 3. Object 명세의 clone 규약
```java
// 다음 표현식들은 참이어야 함
x.clone() != x
x.clone().getClass() == x.getClass()

// 다음은 일반적으로 참이지만, 필수는 아님
x.clone().equals(x)
```

## Clone을 통한 객체 복사 방법

### 1. 기본 타입 필드만 갖는 클래스의 clone
가장 단순한 clone 구현 방식으로, 모든 필드가 기본 타입이거나 불변 객체를 참조할 때 사용합니다.

```java
@Override 
public PhoneNumber clone() {
    try {
        // 1. super.clone()으로 객체의 모든 필드를 복사
        return (PhoneNumber) super.clone();
    } catch (CloneNotSupportedException e) {
        // 2. CloneNotSupportedException은 검사 예외이지만, 
        // Cloneable을 구현한 클래스에서는 발생할 수 없음
        throw new AssertionError();
    }
}
```

**주요 포인트:**
- super.clone()은 Object의 native 메서드를 호출하여 필드 단위 복사를 수행
- PhoneNumber로의 타입 캐스팅은 항상 성공함 (clone()의 리턴 타입이 Object이기 때문에 필요)
- 공변 반환 타이핑(covariant return typing)을 사용하여 클라이언트가 형변환이 필요 없도록 함

### 2. 가변 객체를 참조하는 클래스의 clone
Stack 클래스 예제를 통해 가변 객체를 포함하는 클래스의 clone을 살펴봅시다.

먼저 잘못된 clone 구현의 예:
```java
// 잘못된 clone 구현 - 가변 객체를 공유하게 됨
@Override
public Stack clone() {
    try {
        return (Stack) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```

**문제점:**
- elements 배열이 원본과 복제본 간에 공유됨
- 한쪽의 수정이 다른쪽에 영향을 미침
- 불변식이 깨질 수 있음

올바른 구현:
```java
@Override
public Stack clone() {
    try {
        Stack result = (Stack) super.clone(); // 1. 기본 필드 복사
        // 2. 배열도 복제
        result.elements = elements.clone();    
        return result;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

**개선된 점:**
- elements 배열을 clone하여 독립적인 복사본 생성
- 배열의 clone은 런타임 타입과 컴파일타임 타입이 모두 원본과 같음
- 복제본의 수정이 원본에 영향을 미치지 않음

### 3. 해시테이블의 깊은 복사
```java
@Override
public HashTable clone() {
    try {
        HashTable result = (HashTable) super.clone();
        result.buckets = new Entry[buckets.length];
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                result.buckets[i] = buckets[i].deepCopy();
            }
        }
        return result;
    } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
    }
}
```

### 4. 대안적인 객체 복사 방법

#### 복사 생성자 (Copy Constructor)
```java
public class Yum {
    private int value;
    private List<String> ingredients;
    
    // 복사 생성자
    public Yum(Yum original) {
        this.value = original.value;
        // 가변 객체는 깊은 복사 수행
        this.ingredients = new ArrayList<>(original.ingredients);
    }
}
```

**장점:**
- 생성자를 사용하므로 불필요한 체크 예외를 던지지 않음
- 형변환이 필요 없음
- final 필드 설정 가능
- 해당 클래스가 구현한 인터페이스 타입의 인스턴스도 인수로 받을 수 있음

#### 복사 팩터리 (Copy Factory)
```java
public class Yum {
    private int value;
    private List<String> ingredients;
    
    // 복사 팩터리
    public static Yum newInstance(Yum original) {
        Yum copy = new Yum();
        copy.value = original.value;
        copy.ingredients = new ArrayList<>(original.ingredients);
        return copy;
    }
}
```

**장점:**
- 복사 생성자의 장점을 모두 가짐
- 더 유연한 명명 가능
- 캐싱 등의 부가 기능 추가 가능


연결 리스트와 같은 복잡한 자료구조를 포함하는 경우의 예시입니다.

```java
public class HashTable implements Cloneable {
    private Entry[] buckets = ...;
    
    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone(); // 1. 기본 복사
            // 2. 버킷 배열 새로 생성
            result.buckets = new Entry[buckets.length];
            
            // 3. 각 버킷의 연결 리스트를 깊은 복사
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class Entry {
        final Object key;
        Object value;
        Entry next;
        
        Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        // 재귀적 복사 방식
        Entry deepCopy() {
            // 다음 엔트리가 있다면 재귀적으로 복사
            return new Entry(key, value,
                           next == null ? null : next.deepCopy());
        }
        
        // 반복적 복사 방식 (권장)
        Entry deepCopy() {
            Entry result = new Entry(key, value, next);
            // 현재 엔트리부터 시작
            Entry p = result;
            while (p.next != null) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
                p = p.next;
            }
            return result;
        }
    }
}
```

**구현 시 주의점:**
1. 단순 clone으로는 버킷 배열만 복사되고 내부 연결 리스트는 공유됨
2. 재귀적 복사는 스택 오버플로우 위험이 있음
3. 반복적 방식의 deepCopy가 더 안전한 방법




## Key Point
- Clone은 Primitive type의 배열이 아니면 사용을 피하자
- Copy Constructor나 Copy Factory method를 활용하라
- Cloneable을 확장하지 말자


