# Item 19: 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라.

## 1. 상속을 위한 설계와 문서화의 필요성

### 1.1 배경
클래스를 상속용으로 설계하려면 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 protected 메서드 형태로 공개해야 합니다. 이는 단순히 기능을 제공하는 것을 넘어서, 하위 클래스가 그 기능을 확장할 수 있도록 허용하는 것을 의미합니다.

### 1.2 문제점
상속용으로 설계되지 않은 클래스를 상속하면:
1. 상위 클래스의 변경이 하위 클래스를 망가뜨릴 수 있음
2. 내부 동작 방식을 예측할 수 없음
3. 보안상의 취약점이 발생할 수 있음

### 1.3 현실적 영향
```java
// 상속을 고려하지 않은 설계의 예
public class HashMap<K,V> {
    // 내부 구현이 변경될 수 있으며, 이는 하위 클래스에 영향을 줄 수 있음
    void addEntry(int hash, K key, V value, int bucketIndex) {
        // ...구현 상세...
    }
}
```

## 2. 상속용 클래스의 문서화

### 2.1 내부 동작 방식 문서화
1. **재정의 가능 메서드 호출 시점**
   ```java
   /**
    * 이 메서드는 리스트에 원소를 추가할 때마다 호출된다.
    * clear 메서드는 이 메서드를 사용해 구현되어 있다.
    * 
    * @implSpec
    * 이 구현은 원소를 리스트 끝에 추가하고, size를 1 증가시킨다.
    */
   protected void onAdd(E element) {
       // 구현
   }
   ```

2. **각 메서드별 호출 순서**
   - 상위 클래스의 메서드가 어떤 순서로 다른 메서드들을 호출하는지
   - 각 호출이 끼치는 영향

### 2.2 Hook 제공
```java
public abstract class AbstractList<E> {
    /**
     * 지정된 범위의 원소들을 이 리스트에서 제거한다.
     * clear 메서드는 이 메서드를 호출한다.
     * 
     * @implSpec
     * 이 메서드는 fromIndex부터 시작하여 (toIndex - fromIndex) 개의
     * 원소를 제거한다.
     */
    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }
}
```

## 3. 상속 설계의 제약사항

### 3.1 생성자 제약
1. **문제점**
```java
public class Super {
    // 잘못된 예 - 생성자가 재정의 가능 메서드 호출
    public Super() {
        overrideMe();
    }
    
    public void overrideMe() {}
}

public class Sub extends Super {
    private final String field;
    
    Sub() {
        field = "초기값";
    }
    
    @Override
    public void overrideMe() {
        System.out.println(field); // NPE 발생!
    }
}
```

2. **원인**
   - 상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행
   - 하위 클래스의 필드가 초기화되기 전에 재정의된 메서드 호출

3. **해결책**
   - 생성자에서 재정의 가능 메서드를 호출하지 않음
   - private, final, static 메서드만 호출

### 3.2 Cloneable/Serializable 인터페이스
1. **주의사항**
   - clone과 readObject 메서드는 생성자와 비슷한 효과
   - 이들 메서드에서도 재정의 가능 메서드 호출 금지

2. **구체적 지침**
   ```java
   // Serializable을 구현한 상속용 클래스의 예
   public class Parent implements Serializable {
       // 잘못된 예 - private으로 선언
       private Object writeReplace() { ... }
       
       // 올바른 예 - protected로 선언
       protected Object writeReplace() { ... }
   }
   ```

## 4. 상속 허용 여부 결정

### 4.1 상속을 금지하는 방법
1. **클래스를 final로 선언**
   ```java
   public final class StringConverter {
       // 상속 불가능한 클래스
   }
   ```

2. **생성자를 private이나 package-private으로 선언하고 정적 팩터리 제공**
   ```java
   public class StringConverter {
       private StringConverter() { } // 상속 불가능
       
       public static StringConverter newInstance() {
           return new StringConverter();
       }
   }
   ```

### 4.2 상속용 클래스 설계 테스트
1. **필수 검증사항**
   - [ ] 재정의 가능 메서드의 문서화 여부
   - [ ] 상속용 메서드의 protected 접근 수준 적절성
   - [ ] 생성자의 재정의 가능 메서드 호출 여부
   - [ ] 하위 클래스 동작 검증

## 5. 실무 적용 가이드

### 5.1 상속 허용 시 고려사항
1. 재정의 가능한 메서드의 내부 동작 방식을 문서로 남김
2. 내부 동작 과정 중 끼어들 수 있는 hook을 protected로 제공
3. 상속용 클래스는 배포 전 하위 클래스를 만들어 검증

### 5.2 문서화 핵심 포인트
1. "@implSpec" 태그를 사용하여 구현 상세 기술
2. protected 메서드의 용도와 사용 시점 명시
3. 재정의 가능 메서드의 호출 순서와 영향 설명