컴파일러가 발생시키는 비검사 경고(unchecked warning)는 단순한 권고사항이 아니다. 이를 무시하면 런타임에서 ClassCastException이 발생하거나, 타입 안정성이 깨지는 치명적인 버그로 이어질 수 있다. 이번 아이템에서는 비검사 경고가 발생하는 원인과 이를 해결하는 방법을 다룬다. 또한, 경고를 제거할 수 없는 경우 @SuppressWarnings("unchecked")를 올바르게 사용하는 방법을 살펴본다. 이를 통해 타입 안정성을 유지하면서도 불필요한 경고를 최소화하는 코드 작성법을 익혀보자.

---

## 할 수 있는 한 모든 비검사 경고를 제거하라.

**[비검사 경고가 발생하는 코드]**

```java
Set<Lark> exaltation = new HashSet();
```

- **설명**:
    - `Set<Lark>`는 `Lark` 타입의 요소만 허용하도록 선언되었습니다.
    - 하지만 `new HashSet()`은 로 타입이므로, 어떤 타입의 요소든 추가할 수 있습니다.
    - 이로 인해 타입 안정성이 깨지고, 런타임에 `ClassCastException`이 발생할 수 있습니다.

위 코드는 비검사 경고가 발생한다. 컴파일러의 안내에 따라 코드를 수정하면 비검사 경고가 사라진다.

하지만, 컴파일러의 안내와 달리 다이아몬드 연산자만으로도 비검사경고를 해결할 수 있다. 그러면 컴파일러가 올바른 실제 타입 매개변수(이 경우 Lark)를 추론해준다.

***Reminder***

비검사 경고 : 컴파일 단계에서 발생하는 경고로, **제네릭 타입 시스템을 우회하거나 타입 안정성을 보장할 수 없는 코드**를 사용할 때 나타납니다.  이 경고는 컴파일러가 타입 안정성을 검사할 수 없는 상황에서 발생하며, 잠재적인 런타임 오류(예: `ClassCastException`)를 유발할 수 있기 때문에 주의가 필요합니다.

**[비검사 경고가 발생해결]**

```java
Set<Lark> exaltation = new HashSet<>();
```

- **설명**:
    - `Set<Lark>`는 `Lark` 타입의 요소만 허용하도록 선언되었습니다.
    - `new HashSet<>()`은 컴파일러가 `Lark` 타입을 추론하여 `HashSet<Lark>`로 처리합니다.
    - 따라서 `exaltation`에는 `Lark` 타입의 요소만 추가할 수 있으며, 타입 안정성이 보장됩니다.

**이렇게 비검사 경고를 해결하면 타입 안정성이 보장된다.**

## @SuppressWarnings("uncheck")를 이용한 비검사 경고 제거

만약, 경고를 제거할 수는 없지만 **타입 안전하다고 확신**할 수 있다면 `@SuppressWarnings("unchecked")`를 이용해 비검사 경고를 숨기자.

만약, 타입 안전하다고 검증된 코드의 검사를 그대로 두면 진짜 문제를 알리는 경고 코드를 구분하기 쉽지 않다.

또한 타입 안전함을 검증하지 않은 채 경고를 숨기면 잘못된 보안인식을 심어주는 꼴이된다.

## @SuppressWarnings("uncheck") 사용범위

`@SuppressWarnings("unchecked")`는 **가능한 좁은 범위**에 적용하자.

`@SuppressWarnings` 애너테이션은 개별 지역변수 선언부터 클래스 전체까지 어떤 선언에도 달 수 있다.

한줄이 넘는 메서드나 생성자에 `@SuppressWarnings`가 달려있다면 지역변수나 아주 짧은 메서드 혹은 생성자로 옮기자.

절대로 클래스 전체에 적용해서는 안된다.

**[기존 ArrayList의 toArray 메서드]**

```java
public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
}
```

- **문제점**:
    - `Arrays.copyOf()`는 `Object[]` 타입의 배열을 반환합니다.
    - 이 배열을 `T[]`로 형변환(`(T[])`)하는 과정에서 **타입 안정성이 보장되지 않습니다**.
    - 컴파일러는 이 형변환이 안전한지 확인할 수 없으므로 **비검사 경고(unchecked warning)**를 발생시킵니다.

위 코드를 컴파일하면 `@copyOf()`@ 부분에서 경고가 발생한다. 이 경고를 제거하려면 지역변수를 추가해야 한다.

`return`문에는 `@SuppressWarnings("unchecked")`를 다는게 불가능하기 때문이다.

**[지역변수를 추가해 @SuppressWarnings의 범위를 좁힌다.]**

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size)
        // 생성한 배열과 매개변수로 받은 배열이 모두 T[]로 같으므로
        // 올바른 형변환이다.
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Arrays.copyOf(elementData, size, a.getClass());
        return result
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

- **변경 사항**:
    1. `Arrays.copyOf()`의 결과를 `T[]`로 형변환하는 부분을 **지역변수 `result`*에 저장합니다.
    2. `@SuppressWarnings("unchecked")`를 지역변수 선언에 적용하여, 경고를 숨기는 범위를 최소화합니다.
    3. `return result`를 통해 결과를 반환합니다.
- **장점**:
    - `@SuppressWarnings("unchecked")`의 범위가 **지역변수 선언 한 줄**로 제한됩니다.
    - 다른 부분에서 발생할 수 있는 경고를 놓치지 않습니다.
    - 코드의 가독성과 유지보수성이 향상됩니다.

이 코드는 깔끔하게 컴파일되고 비검사 경고를 숨기는 범위도 최소로 좁혔다.

**`@SuppressWarnings("unchecked")` 에너테이션을 사용할 때면 그 경고를 무시해도 안전한 이유를 항상 주석으로 남겨야한다.**

다른 사람이 해당 코드를 이해하는데 도움이되며, 그 코드를 잘못 수정해 타입 안정성을 잃는 상황을 줄여주기 때문이다.

## 정리

- **비검사 경고(unchecked warning)**는 타입 안정성을 보장할 수 없는 코드에서 발생합니다.
- `@SuppressWarnings("unchecked")`를 사용할 때는 **가능한 좁은 범위**에 적용해야 합니다.
- 지역변수를 추가하여 경고를 숨기는 범위를 최소화하면, 코드의 안정성과 가독성을 높일 수 있습니다.