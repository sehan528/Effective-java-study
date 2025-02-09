**서로 다른 `Type1`과 `Type2`가 있을 때 `List<Type1>`과 `List<Type2>`는 하위 타입도 상위 타입의 관계도 아니다**. 예를들어, `List<String>`은 `List<Object>`의 하위 타입이 아니라는 의미다. `List<String>`에는 문자열만 넣을 수 있지만 `List<Object>`는 어떤 객체든 넣을 수 있다. 

Java 제네릭 타입은 기본적으로 **“불공변”**이다.  타입 안정성을 위해 다음과 같이 설계 되었지만 동시에 이런 점이 API의 유연성을 제한하는 요인이 되버렸다. 이런 유연성을 극복하고자 저자는 **한정적 와일드카드 타입 (PECS)**이라는 방법론을 제안했고 이번 아이템에선 PECS에 대해 알아볼 것 이다.

---

## 펙스(PECS) - 와일드카드 타입 사용 공식

Joshua Bloch는 PECS를 "Producer Extends, Consumer Super"의 약자로, 제네릭 프로그래밍에서 와일드카드 타입을 사용할 때 적용되는 중요한 원칙이 될 것 이라 했다.

1. 매개변수화 타입 `T`가 생산자(**producer**)라면 `<? extends T>`를 사용한자.
    1. ⇒ **생산자(Producer)**: 메서드나 구조체에 ***데이터를 제공하는 역할*** 을 합니다.
2. 매개변수화 타입 `T`가 소비자(**consumer**)라면 `<? super T>`를 사용하자.
    1. ⇒ **소비자(Consumer)**: 메서드나 구조체로부터 ***데이터를 받아 사용하는 역할*** 을 합니다.

### 예시

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) // PECS 사용하지 않은 경우
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) // PECS 적용
```

- `s1`과 `s2`는 생산자이니 PECS 공식에 따라 생산자 와일드카드 방식에 따라 사용해야 한다.

### 클라이언트

```java
Set<Integer> integers = Set.of(1, 3, 5);
Set<Double> doubles = Set.of(2.0, 4.0, 6.0);
Set<Number> numbers = union(integers, doubles);

// ---

Set<Number> numbers = Union.<Number>union(integers,doubles);
//자바 7까지는 명시적 타입 인수를 사용해야 했다.
```

와일드카드가 제대로 사용된다면 사용자는 와일드 카드가 사용된지도 모른다. 만약 사용자가 와일드카드 타입을 신경써야 한다면 그 API는 문제가 있을 수 있다. 참고로, 만약 자바 버전이 7버전이라면 위 클라이언트 코드에서 명시적 타입 인수를 사용해주어야 한다. 자바 8이전까지는 타입 추론 능력이 충분하지 못해 반환타입을 명시해야 했다.

우선 생산자, 소비자 별로 사용했을 때와 사용하지 않았을 때 자세한 코드 비교를 통해 어떻게 작성하고 어떤 이점을 가져오는지 파악하자. 

---

## 생산자

**[와일드카드를 사용하지 않은 pushAll 메서드]**

```java
public void pushAll(Iterable<E> src) {
    for (E e : src) {
        push(E);
    }
}
...
public static void main(String[] args) {
    Stack<Number> numberStack = new Stack<>();
    Iterable<Integer> iterable = ...;

    numberStack.pushAll(iterable); // Collision
}
```

- **문제점**:
    - `Stack<Number>`에 `Iterable<Integer>`를 전달하려고 하면, `Integer`는 `Number`의 하위 타입이지만, `Iterable<Integer>`는 `Iterable<Number>`의 하위 타입이 아니므로 컴파일 오류가 발생한다.

`*src` 매개변수* 는 `Stack`이 사용할 `E` instance를 생산하므로 **생산자** 라고 볼 수 있다. 그리고 `Integer`는 `Number`의 하위 타입이므로 논리적으로 잘 동작해야 할 것 같지만 타입 변경할 수 없다는 에러가 발생한다. 이런 상황에 **한정적 와일드카드 타입**이 유용하게 사용될 수 있다.

**[E 생산자(producer) 매개변수에 와일드카드 타입 적용]**

```java
...
public void pushAll(Iterable<? extends E> src) {
    for (E e : src) {
        push(E);
    }
}
...
```

`Iterable <? extends E>`는 다음과 같이 해석될 수 있다.

> `E` 는`Iterable`의 `E` 가 아니라 **`E`의 하위 타입의 `Iterable`**이어야 한다는 의미를 갖는다.
> 

말장난 같이 느껴질 수 있기에 이를 풀어보자면 다음과 같다.

- **`Iterable<E>`**는 정확히 E 타입의 요소만을 허용합니다.
- **`Iterable<? extends E>`**는 E 타입 뿐만 아니라 E의 모든 하위 타입도 허용합니다.

예시로 돌아가 **`Stack<Number>`**의 **`pushAll`** 메서드는 이제 **`Iterable<Number>`**, **`Iterable<Integer>`**, 
**`Iterable<Double>`** 등을 모두 인자로 받을 수 있음을 의미한다. 이는 Number의 모든 하위 타입(Integer, Double, Float 등)이 허용됨을 의미합니다. 

한정적 와일드카드를 통해 컴파일러는 **`src`**에서 꺼내는 모든 요소가 최소한 E 타입임을 보장하며 결론적으로 **`push(E)`** 메서드를 안전하게 호출할 수 있다. 

---

## 소비자

**와일드카드를 사용하지 않은 popAll 메서드**

```java
...
public void popAll(Collection<E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
...
public static void main(String[] args) {
    Stack<Number> numberStack = new Stack<>();
    Collection<Object> objects = ...;

    numberStack.popAll(objects);
}
...
```

- **문제점**:
    - `Stack<Number>`에서 `Collection<Object>`로 원소를 옮기려고 하면, `Collection<Object>`는 `Collection<Number>`의 하위 타입이 아니므로 컴파일 오류가 발생합니다.

**`dst`** 매개변수는 **`Stack`**으로부터 **`E`** 인스턴스를 소비하므로 **소비자**라고 볼 수 있다. 그리고 **`Object`**는 **`Number`**의 상위 타입이므로 논리적으로 잘 동작해야 할 것 같지만 타입 변경할 수 없다는 에러가 발생한다. 

이런 상황에 **한정적 와일드카드 타입**이 유용하게 사용될 수 있다. 즉, `Collection<Object>`는 `Collections<Number>`의 하위 타입이 아니기 때문에 오류가 발생한다. 이번 상황역시 와일드카드 타입으로 해결이 가능하다.

**E 소비자 매개변수에 와일드카드 타입 적용**

```java
...
public void popAll(Collection<? super E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
...
```

`Collection<? super E>` 는 다음과 같이 해석될 수 있다.

> E는 Collection의 E가 아니라 **E의 상위 타입의 Collection** 이어야 한다는 의미를 갖는다.
> 

이를 풀어보자면 다음과 같다.

- **`Collection<E>`**는 정확히 `E` 타입의 요소만을 허용합니다.
- **`Collection<? super E>`**는 `E` 타입 뿐만 아니라 `E`의 모든 상위 타입도 허용합니다.

예시로 돌아가 **`Stack<Number>`**의 **`popAll`** 메서드는 이제 **`Collection<Number>`**, **`Collection<Object>`** 
등을 모두 인자로 받을 수 있음을 의미한다. 이는 Number의 모든 상위 타입(Object 등)이 허용됨을 의미합니다. 한정적 와일드카드를 통해 컴파일러는 **`dst`**에 추가되는 모든 요소가 최소한 E 타입임을 보장하며 결론적으로 **`dst.add(pop())`** 메서드를 안전하게 호출할 수 있다.

---

## 매개변수와 인수의 차이

> 매개변수는 메서드 선언에 정의한 변수이고, 인수는 메서드 호출 시 넘기는 실젯값이다.
> 

**[매개변수와 인수 예시]**

```java
void add(int value) {...}
add(10);
```

위 코드에서 value는 매개변수이고 10은 인수다.

**[제네릭 매개변수와 인수 예시]**

```java
class Set<T> {...}
Set<Integer> = {...}
```

여기서 T는 타입 매개변수가 되고, Integer는 타입 인수가 된다.

---

## 타입 매개변수와 와일드카드 모두 사용해도 괜찮을 경우

> 기본 규칙: 메서드 선언에 타입 매개변수가 한번만 나오면 와일드 카드로 대체하라
> 

**[swap 메서드의 두 가지 선언 - 비한정적 타입 매개변수, 비한정적 와일드 카드]**

```java
public static <E> void swap(<List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```

이때, 한정적 타입 매개변수라면 한정적 와일드카드로 비한정적 타입 매개변수라면 비한정적 와일드카드로 변경하면 된다.

**[직관적으로 구현한 코드 - 문제발생]**

```java
public static void swap(List<?> list, int i, int j){
	list.set(i, list.set(j, list.get(i));
}
```

이 코드는 `list.get(i)`로 꺼낸 코드를 다시 리스트에 넣을 수 없다는 오류를 발생시킨다. 이 오류는 제네릭 메서드인 `private` 도우미 메서드를 작성함으로 해결할 수 있다.

```java
public static void swap(List<?> list, int i, int j){
	swapHelper(list, i, j);
}

// 와일드카드 타입을 실제 타입으로 바꿔주는 private 도우미 메서드
private static <E> void swapHelper(List<E> list, int i, int j){
	list.set(i, list.set(j, list.get(i));
}
```

`swapHelper` 메서드는 `List<E>`에서 꺼낸 타입이 항상 `E`이고 `E`타입의 값은 해당 `List`에 다시 넣어도 안전함을 알고 있다.

## 정리

유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하자. 생산자(producer)는 `extends`를 소비자(consumer)는 `super`를 사용한다.

`Comparable`과 `Comparator`는 소비자라는 사실도 기억하자.