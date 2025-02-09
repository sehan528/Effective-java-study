타입을 안전하게 관리하는 것은 변수뿐만 아니라 메서드에서도 매우 중요한 요소이다. 이전 Item 29: 이왕이면 제네릭 타입으로 만들라에서는 클래스와 인터페이스 수준에서 제네릭을 적용하는 방법을 다뤘다면, 이번 아이템을 통해 제네릭 메서드의 필요성과 구현 방법들을 익혀 불필요한 객체 생성을 방지하면서도 다양한 타입을 처리할 수 있는 효과적인 방법을 알아본다.

---

## 제네릭 메서드 작성방법

- 매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다.
    - `Collections`의 `binarySearch`, `sort` 등 알고리즘 메서드는 모두 제네릭이다.
- 제네릭 메서드 작성법은 제네릭 타입 작성법과 비슷하다.
- 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 온다.

**[제네릭 메서드]**

```java
public static <E> Set<E> union (Set<E> s1, Set<E> s2){
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

- **설명**:
    - 이 메서드는 두 개의 `Set`을 입력받아 합집합을 반환합니다.
    - **타입 매개변수 `<E>`**: 메서드의 제한자(`public static`)와 반환 타입(`Set<E>`) 사이에 위치합니다.
    - **타입 안전성**: `s1`과 `s2`는 동일한 타입 `E`의 `Set`이어야 합니다. 따라서 반환된 `Set<E>`도 타입 안전합니다.
    - **한계**: 세 개의 `Set`이 모두 같은 타입이어야 합니다. 이를 더 유연하게 만들기 위해 **한정적 와일드카드 타입**을 사용할 수 있습니다.

위 코드는 세개의 Set 집합이 타입이 모두 같아야 한다. 이를 한정적 와일드 카드 타입을 이용하면 더 유연하게 개선이 가능하다.

## 제네릭 싱글턴 팩터리

불변 객체를 여러 타입으로 활용할 때가 있다. 제네릭은 런타임시 타입 정보가 소거 되므로 하나의 객체를 어떤 타입으로든 매개변수화 할 수 있다. 객체를 매개변수화하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 **정적 팩터리**가 필요하다.

이 정적 팩터리를 **제네릭 싱글턴 팩터리**라고 한다.

**[제네릭 싱글턴 팩터리 패턴 - 항등함수]**

```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarinings("unchecked")
public static <T> UnaryOperator<T> identityFunction(){
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

- **설명**:
    - **싱글턴 팩터리**: 불변 객체를 *여러 타입으로 활용할 수 있도록* 하는 정적 팩터리 메서드입니다.
    - **항등 함수**: 입력 값을 그대로 반환하는 함수입니다. (`f(x) = x`)
    - **타입 안전성**:
        - `IDENTITY_FN`은 `UnaryOperator<Object>` 타입이지만, 제네릭 메서드 `identityFunction`은 이를 `UnaryOperator<T>`로 형변환합니다.
        - 항등 함수는 타입에 관계없이 안전하므로, `@SuppressWarnings("unchecked")`를 사용하여 경고를 숨깁니다.
    - **활용**: 이 메서드는 어떤 타입 `T`에도 안전하게 사용할 수 있습니다.

항등함수는 입력 값을 수정 없이 그대로 반환하는 특별한 함수이므로 `T`가 어떤 타입이든 `UnaryOperator<T>`를 사용해도 타입 안전하다.  따라서 비검사 형변환 경고는 숨겨도 된다.

## 재귀적 한정적 타입

**재귀적 타입 한정은** 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정하는 개념이다.

이런 재귀적 타입 한정은 주로 타입의 자연적 순서를 지정해주는 `Comparable`과 함께 사용된다.

```java
public interface Comparable<T>{
	int compareTo(T o);
}
```

타입 매개변수 `T`는 `Comparable<T>`를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다.

실제 거의 모든 타입은 자기 자신과 같은 타입만 비교가 가능하다. `String`은 `Comparable<String>`을 구현하고 `Integer`는 `Comparable<Integer>`를 구현한다.

`Comparable`을 구현한 원소의 컬렉션을 입력받는 메서드들은 정렬, 검색등 기능을 수행하는데 이런 기능을 수행하기 위해 컬렉션에 담긴 모든 원소가 상호 비교되어야 한다.

**[재귀적 타입 한정을 이용해 상호 비교 할 수 있음을 표현]**

```java
public static <E extends Comparable<E>> E max(Collection<E> c);
```

- **설명**:
    - **재귀적 타입 한정**: 타입 매개변수가 자신을 포함하는 표현식으로 한정되는 것을 말합니다.
    - **`<E extends Comparable<E>>`**:
        - `E`는 `Comparable<E>`를 구현한 타입이어야 합니다.
        - 즉, `E`는 자기 자신과 비교할 수 있는 타입이어야 합니다.
    - **동작**:
        - 이 메서드는 컬렉션 `c`에서 최댓값을 찾아 반환합니다.
        - `compareTo` 메서드를 사용하여 원소들을 비교합니다.
    - **타입 안전성**:
        - `E`는 `Comparable<E>`를 구현한 타입이므로, `compareTo` 메서드를 안전하게 사용할 수 있습니다.
        - 컴파일 시점에 타입 안전성이 보장됩니다.

`<E extends Comparable<E>>`가 모든 타입 `E`는 자신과 비교할 수 있다는 의미를 갖는다.

아래는 재귀적 타입 한정을 이용한 메서드를 구현했다. 컴파일오류나 경고는 발생하지 않으며 컬렉션에 담긴 원소의 자연적 순서를 기준으로 최댓값을 계산한다.

**[재귀적 타입 한정을 이용한 최댓값 계산 메서드]**

```java
public static <E extends Comparable<E>> E max(Collection<E> c){
    if(c.isEmpty()){
       throw new IllegalArgumentException("컬렉션이 비었습니다.");
    }

    E result = null;
    for (E e : c){
        if(result == null || e.compareTo(result) > 0){
            result = Objects.requireNonNull(e);
        }
    }

    return result;
}
```

## 정리

- **제네릭 메서드**는 타입 안전성을 보장하면서도 유연한 코드를 작성할 수 있게 해줍니다.
- **제네릭 싱글턴 팩터리**는 불변 객체를 여러 타입으로 활용할 수 있도록 합니다.
- **재귀적 타입 한정**은 타입 매개변수가 자신과 비교할 수 있음을 표현할 때 사용됩니다.
- 제네릭 메서드를 사용하면 형변환 없이 타입 안전한 코드를 작성할 수 있습니다.