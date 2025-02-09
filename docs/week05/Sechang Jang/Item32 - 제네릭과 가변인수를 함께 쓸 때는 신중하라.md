제네릭과 가변인자를 함께 사용하면 타입 안정성이 깨질 위험이 있다. 가변인자 메서드는 내부적으로 **배열을 사용**하는데, 제네릭 배열은 타입 안정성을 보장할 수 없기 때문이다. 이러한 문제를 방치하면 **힙 오염(Heap Pollution)**이 발생하고, 런타임에서 `ClassCastException`이 터질 가능성이 높아진다.

이번 아이템에서는 제네릭 가변인자 배열이 왜 위험한지, 그리고 이를 방지하는 방법을 살펴본다. `@SafeVarargs` 애너테이션을 활용하는 방법과, 제네릭 가변인자를 **리스트(List)로 대체하여 해결하는 방법**도 함께 알아볼 것 이다.

---

## 제네릭 가변인자 배열은 타입 안정성을 해칠수 있다.

> 가변인수: 자바 5 때 추가되었고 메서드에 넘기는 인수(매개변수)의 개수를 클라이언트가 동적으로 조절할 수 있게 해주는 기능이다. Javascript 에선 Spread operator와 같은 컨셉을 지니고 있다.
> 

**[제네릭과 가변인사를 혼용하면 타입안정성을 해칠 수 있다.]**

```java
static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;               // 힙 오염 발생
    String s = stringLists[0].get(0);   // ClassCastException
}
```

- **설명**:
    - `stringLists`는 `List<String>[]` 타입의 배열입니다.
    - `objects`는 `Object[]` 타입으로, `stringLists`를 할당받습니다.
    - `objects[0] = integers;`에서 `List<Integer>`를 `objects` 배열에 저장합니다. 이로 인해
    **힙 오염(Heap Pollution)**이 발생합니다.
    - `stringLists[0].get(0)`은 `String` 타입을 기대하지만, 실제로는 `Integer` 타입이 반환되므로 `ClassCastException`이 발생합니다.
- **문제점**:
    - 제네릭과 가변인자를 혼용하면, 타입 안정성이 깨질 수 있습니다.
    - 힙 오염이 발생하면 런타임에 예기치 않은 오류가 발생할 수 있습니다.

가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다. 이렇게 만들어진 배열이 클라이언트에게 노출되는 문제가 발생한다.

```java
warning: [unchecked] Possible heap pollution from
parameterized vararg type List<String>
```

- 매개변수에 제네릭이나 매개변수화 타입이 포함되면서 컴파일 경고가 발생하게 된다. 경고의 원인은 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면서 힙 오염이 발생하기 때문이다.
- 매개변수화 타입이 변수가 타입이 다른 객체를 참조하면 입 오염이 발생한다. 이렇게 다른 객체를 참조하는 상황에서는 컴파일러가 자동생성한 형변환이 실패하여 제네릭 타입 안정성이 보장되지 못한다.

## 제네릭 가변인자 매개변수의 모순점

- 제네릭 배열을 프로그래머가 직접 생성하는건 허용하지 않으면서 제네릭 가변인자 매개변수를 받는 메서드를 선언할 수 있는건 분명 모순적이다.
- 이런 모순을 수용한 이유는 제네릭이나 매개변수화 타입의 가변인자 매개변수를 받는 메서드가 실무에서 유용하기 때문이다.
- 대표적인 예시로 `Arrays.asList(T... a)`, `Collections.addAll(Collection<? super T> c, T... elements)`, `EnumSet.of(E first, E...rest)`가 있다.

## SafeVarargs

> 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치로 자바7에서 추가되었다.
> 
- 매개변수 배열이 호출자로부터 순수하게 인수를 전달하는 일만 한다면 타입 안전하고 말 할수 있다. 하지만, 가변인자 매개변수에 아무것도 저장하지 않고도 타입 안정성을 깨는 경우가 있으니 조심해야 한다.

**[제네릭 가변인자(varargs) 매개변수 배열에 다른 메서드가 접근하면 안전하지 않다 - 힙 오염 발생, 콜스택까지 전이]**

```java
static <T> T[] pickTwo(T a, T b, T c){
    switch (ThreadLocalRandom.current().nextInt(3)){
        case 0: return toArray(a,b);
        case 1: return toArray(b,c);
        case 2: return toArray(a,c);
    }
    throw new AssertionError(); // 도달 못함.
}

static <T> T[] toArray(T... args){
    return args;
}
...
public static void main(String[] args) {
    final String[] attributes = pickTwo("좋은", "빠른", "저렴한"); // ClassCastException 발생
}
```

- **설명**:
    - `pickTwo` 메서드는 가변인자(`T...`)를 사용하여 두 개의 원소를 반환합니다.
    - `toArray` 메서드는 가변인자 배열을 반환합니다.
    - `pickTwo` 메서드에서 반환된 배열은 `Object[]` 타입이지만, 클라이언트 코드에서는 `String[]` 타입으로 형변환하려고 합니다.
    - `Object[]`는 `String[]`의 하위 타입이 아니므로, **`ClassCastException`*이 발생합니다.
- **문제점**:
    - 가변인자 배열이 다른 메서드에 노출되면, 타입 안정성을 보장할 수 없습니다.
    - 힙 오염이 발생할 수 있습니다.

`pickTwo` 메서드는 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입의 Object타입의 배열을 반환한다. 컴파일러가 `String` 배열로 형변환하는 코드를 자동으로 생성한다. `String` 배열이 `Object` 배열의 하위 타입이 아니기 때문에 이 형변환은 `ClassCastException`이 발생하며 실패한다.

## @SafeVarargs 사용 규칙

- 제네릭이나 매개변수화 타입의 가변인자(varargs) 매개변수를 받는 모든 메서드에 @SafeVarargs를 달자.
- 단, 타입 안전하지 않은 가변인자(varargs) 메서드에는 절대 사용해서는 안된다.

**[가변인자 매개변수 배열에 다른 메서드가 접근하고도 안전한 예외경우]**

```java
@SafeVarargs
static <T>List<T>  flatten(List<? extends T>... lists){
   final List<T> result = new ArrayList<>();

    for (List<? extends T> list : lists){
        result.addAll(list);
    }

    return result;
}
```

- **설명**:
    - `flatten` 메서드는 여러 리스트를 하나의 리스트로 병합합니다.
    - `@SafeVarargs` 애너테이션을 사용하여 이 메서드가 타입 안전함을 보장합니다.
    - 가변인자 배열(`lists`)에 다른 메서드가 접근하지 않고, 배열의 내용을 수정하지 않으므로 타입 안전합니다.
- **장점**:
    - 가변인자를 사용하면서도 타입 안정성을 보장합니다.
    - `@SafeVarargs`를 사용하여 컴파일러 경고를 숨길 수 있습니다.

`@SafeVarargs` 로 메서드의 타입 안전함을 보장하고 있다.

## @SafeVarargs 사용 판단 기준

- 가변인자(varargs) 매개변수 배열에 메서드에서 아무것도 저장하지 않는다.
- 그 배열(혹은 복제본)을 신뢰할 수 없는 코드(클라이언트)에 노출하지 않는다.

> @SafeVarargs 애너테이션은 재정의할 수 없는 메서드에만 달아야한다. 재정의한 메서드는 타입 안전성을 보장할 수 없기 때문이다. 자바 8에서는 정적 그리고 final 인스턴스 메서드에만 붙일 수 있었고, 자바 9 부터는 private 인스턴스 메서드에도 허용된다.
> 

## 제네릭 가변인자 매개변수의 대안

가변인자(varargs) 매개변수를 List 매개변수로 바꿀수도 있다.

**[제네릭 varargs 매개변수를 List로 대체한 경우 - 타입안전]**

```java
static <T> List<T> pickTwo(T a, T b, T c){
    switch (ThreadLocalRandom.current().nextInt(3)){
        case 0: return List.of(a,b);
        case 1: return List.of(b,c);
        case 2: return List.of(a,c);
    }

    throw new AssertionError(); // 도달 못함.
}
...
public static void main(String[] args) {
    final List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

- **설명**:
    - `pickTwo` 메서드는 가변인자 대신 `List.of`를 사용하여 두 개의 원소를 반환합니다.
    - `List.of`는 불변 리스트를 생성하며, `@SafeVarargs`가 적용되어 타입 안전합니다.
    - 반환된 리스트는 `List<String>` 타입이므로, 클라이언트 코드에서 안전하게 사용할 수 있습니다.
- **장점**:
    - 배열 대신 리스트를 사용하므로, 타입 안정성이 보장됩니다.
    - 힙 오염이 발생하지 않습니다.

`List.of`에도 `@SafeVarargs` 에너테이션이 달려있어 컴파일러가 메서드의 타입안전성을 검증하고 보장한다. 배열없이 제네릭만 사용하므로 타입 안전하다.

## 정리

1. **제네릭과 가변인자를 혼용하면 타입 안정성이 깨질 수 있습니다.**
    - 힙 오염이 발생할 수 있으며, 런타임에 `ClassCastException`이 발생할 수 있습니다.
2. **가변인자 배열이 다른 메서드에 노출되면 안전하지 않습니다.**
    - `pickTwo`와 `toArray` 메서드의 예시에서 볼 수 있듯이, 타입 안정성을 보장할 수 없습니다.
3. **`@SafeVarargs`를 사용하여 타입 안전성을 보장할 수 있습니다.**
    - `flatten` 메서드처럼 가변인자 배열을 안전하게 사용할 수 있습니다.
4. **가변인자 대신 리스트를 사용하면 타입 안전성을 보장할 수 있습니다.**
    - `pickTwo` 메서드에서 `List.of`를 사용하여 타입 안전한 코드를 작성할 수 있습니다.