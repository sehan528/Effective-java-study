# Item 14: Comparable을 구현할지 고려하라

## Comparable 인터페이스 소개

`Comparable` 인터페이스는 객체의 순서를 정의하는 메서드인 `compareTo`를 포함합니다. 이는 `Object`의 메서드가 아닌, `Comparable` 인터페이스의 유일한 메서드입니다.

+ Comparable은 객체의 순서를 정의하는 인터페이스
+ 구현하면 정렬, 검색, 극단값 계산, 자동 정렬 컬렉션 사용이 가능
+ equals와 비슷하지만 순서 비교가 가능하고 제네릭함

#### 기본 구조
```java
public interface Comparable<T> {
    int compareTo(T t);
}
```


## Comparable 특징

### Object.equals와의 차이점
1. **순서 비교**: compareTo는 동치성 비교뿐만 아니라 순서도 비교 가능
2. **제네릭**: 타입 별로 다양하게 활용 가능
3. **자연적 순서**: 해당 클래스의 인스턴스들에 대한 자연적인 순서(natural order) 제공

### 활용 예시
```java
public class WordList {
    public static void main(String[] args) {
        Set<String> s = new TreeSet<>();
        Collections.addAll(s, args);
        System.out.println(s);
    }
}
```
- 위 예제는 중복을 제거하고 알파벳 순으로 정렬
- `String`이 `Comparable`을 구현했기 때문에 가능

## compareTo 메서드 규약

### 기본 규약
```java
// x.compareTo(y)의 결과:
-1 (또는 음수) → x가 y보다 작다
 0           → x와 y가 같다
+1 (또는 양수) → x가 y보다 크다
```
- 비교 불가능한 타입이면 `ClassCastException` 발생

### 세부 규약

1. **대칭성**
```java
sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
```

2. **추이성**
```java
(x.compareTo(y) > 0 && y.compareTo(z) > 0) implies x.compareTo(z) > 0
```

3. **반사성**
```java
x.compareTo(y) == 0 implies sgn(x.compareTo(z)) == sgn(y.compareTo(z))
```

4. **equals 일관성** (권장)
```java
(x.compareTo(y) == 0) == (x.equals(y))
```

## equals와 compareTo의 차이

### BigDecimal 예제
```java
BigDecimal bd1 = new BigDecimal("1.0");
BigDecimal bd2 = new BigDecimal("1.00");

HashSet<BigDecimal> hashSet = new HashSet<>();
hashSet.add(bd1);
hashSet.add(bd2);
System.out.println(hashSet.size());  // 2 출력

TreeSet<BigDecimal> treeSet = new TreeSet<>();
treeSet.add(bd1);
treeSet.add(bd2);
System.out.println(treeSet.size());  // 1 출력
```

- `HashSet`: equals 기반 비교 → 다른 객체로 인식
- `TreeSet`: compareTo 기반 비교 → 같은 객체로 인식

## compareTo 메서드 구현 방법

### 기본 구현 방식
```java
public int compareTo(PhoneNumber pn) {
    int result = Short.compare(areaCode, pn.areaCode);
    // 필드들을 직접 비교하고 있는것을 볼 수 있다.
    if (result == 0) {
        result = Short.compare(prefix, pn.prefix);
        if (result == 0) {
            result = Short.compare(lineNum, pn.lineNum);
        }
    }
    return result;
}
```

### Comparator 활용
```java
private static final Comparator<PhoneNumber> COMPARATOR = 
    // method를 활용하여 더 간단하고 가독성 좋음.
    Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
            .thenComparingInt(pn -> pn.prefix)
            .thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn) {
    return COMPARATOR.compare(this, pn);
}
```

## Comparator 활용 및 유의할 점

### equals와 일관성
```java
// BigDecimal 예시로 보는 일관성 문제
BigDecimal bd1 = new BigDecimal("1.0");
BigDecimal bd2 = new BigDecimal("1.00");

// 주의: 같은 값이지만 다르게 동작
bd1.equals(bd2)        // false
bd1.compareTo(bd2) == 0  // true

// 이로 인한 실제 문제
HashSet<BigDecimal> hashSet = new HashSet<>();  // equals 사용
hashSet.add(bd1); hashSet.add(bd2);
System.out.println(hashSet.size());  // 2

TreeSet<BigDecimal> treeSet = new TreeSet<>();  // compareTo 사용
treeSet.add(bd1); treeSet.add(bd2);
System.out.println(treeSet.size());  // 1
```

### hashcode order

```java
// 잘못된 구현: 정수 오버플로우 위험
public int compareTo(Object o) {
    return this.hashCode() - o.hashCode(); // point
}

// 올바른 구현
public int compareTo(Object o) {
    return Integer.compare(this.hashCode(), o.hashCode());
}
```

## 핵심 정리
1. 순서를 고려해야 하는 값 클래스를 작성할 때는 `Comparable` 인터페이스를 구현하자
2. `compareTo` 메서드에서 필드 값 비교시 `<`, `>` 연산자는 피하자
3. 박싱된 기본 타입 클래스가 제공하는 정적 `compare` 메서드나 `Comparator` 인터페이스가 제공하는 비교자 생성 메서드를 사용하자

---
---
---

## 학습 포인트 요약

1. **구현이 필요한 상황**
   - 객체에 자연적인 순서가 있는 경우
   - 정렬이나 검색이 필요한 경우
   - TreeMap이나 TreeSet의 원소로 사용할 경우

2. **구현 방법 선택**
   - 간단한 비교: 직접 비교 방식
   - 다중 필드 비교: Comparator 활용
   - 특별한 정렬 규칙: 커스텀 Comparator 사용

3. **실무 적용 시 고려사항**
   - 불변 클래스의 경우 상수 Comparator 사용 권장
   - null 관련 예외 처리 필요
   - 제네릭 타입 적절히 사용

## 실무 활용 팁
1. 코드 리뷰 시 체크리스트
   - compareTo 규약 준수 여부
   - equals와의 일관성
   - null 처리
   
2. 테스트 케이스 작성 포인트
   - 기본 비교 테스트
   - 경계값 테스트
   - 동치성 테스트

3. 성능 고려사항
   - Comparator 체이닝은 약간의 성능 저하 있음
   - 성능이 중요한 경우 직접 비교 방식 고려

## 예상 질문
1. **Q**: `Comparable`과 `Comparator`의 차이는?
   - **A**: `Comparable`은 클래스의 자연적 순서를 구현할 때, `Comparator`는 다양한 정렬 기준을 구현할 때 사용

2. **Q**: 정렬 컬렉션에서 `equals`와 `compareTo`가 다르면?
   - **A**: `TreeSet`, `TreeMap`은 `compareTo`를 사용하므로 예상과 다른 결과가 나올 수 있음