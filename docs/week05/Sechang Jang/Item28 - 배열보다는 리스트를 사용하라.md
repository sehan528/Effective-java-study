배열과 제네릭은 런타임 시 다른 동작을 한다. 배열은 컴파일 시점에서 자료형을 명확히 알아야 하지만, 제네릭은 이를 고려하지 않는다. 따라서 배열 타입에 제네릭을 적용하려 하면 컴파일 에러가 발생한다. 이번 아이템을 통해 배열과 제네릭이 어떻게 다른 방식으로 동작하는지를 살펴보고, 배열을 리스트로 대체하는 것이 왜 더 안전한 방법인지 알아봅니다. 또한, 런타임 타입 안정성을 보장하는 방법과 실체화 불가 타입 개념까지 학습할 것입니다. 이를 통해 배열을 사용할 때 발생할 수 있는 문제를 방지하고, 더욱 안전한 코드를 작성하는 기준을 생각해보도록 하자.

---

## 배열과 리스트 차이점

### 첫 번째 - 배열은 **공변**인 반면 리스트는 **불공변**이다.

> 공변: 함께 변한다
> 
> 
> **불공변:** 함께 변하지 않는다.
> 

배열의 경우 `Sub`가 `Super`의 하위 타입이라면 `Sub[]`는 배열 `Super[]`의 하위 타입이 된다.

반면, 리스트의 경우 서로 다른 타입 `Type1`, `Type2`가 있을 때, `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고 상위 타입도 아니다.

**[문법상 허용 - 런타임 실패]**

```java
Object[] objectArray = new Long[1];
objectArray[0] = "타입이 달라 넣을 수 없다."; // ArrayStoreException 발생
```

- **설명**:
    - `Long`은 `Object`의 하위 타입이므로, `Long[]`는 `Object[]`의 하위 타입으로 간주됩니다.
    - 따라서 `Object[] objectArray = new Long[1];`은 문법상 허용됩니다.
    - 하지만 `objectArray[0] = "타입이 달라 넣을 수 없다.";`에서 
    `String`을 `Long[]`에 추가하려고 하면, 런타임에 `ArrayStoreException`이 발생합니다.

### **배열의 문제점**

- 배열은 공변성이 있기 때문에 **런타임에 타입 안정성을 보장할 수 없습니다**.
- 컴파일 시점에는 문제가 없어 보이지만, 런타임에 오류가 발생할 수 있습니다.

**[문법상 불허용 - 컴파일 실패]**

```java
List<Object> objectList = new ArrayList<>(); // 호환되지 않는 타입이다.
objectList.add("타입이 달라 넣을 수 없다.");
```

- **설명**:
    - `ArrayList`는 `List`interface를 구현한 Class 이다. == ArrayList는 List의 하위 타입이다.
    - 허나 `List<Object>`와 `List<Long>`은 서로 다른 타입입니다.
    - 제네릭은 불공변성이므로, `List<Long>`을 `List<Object>`에 할당할 수 없습니다.
    - 따라서 컴파일 시점에 오류가 발생합니다.

**배열은 이런 오류 발생을 런타임시에 알 수 있지만 리스트를 사용하면 컴파일시에 바로 알 수 있다.**

### 두 번째 - 배열은 실체화된다.

배열은 자신이 담기로 한 원소의 타입을 인지하고 확인한다. 그래서 `Long` 타입 배열에 `String` 타입 데이터를 입력하려고하면 `ArrayStoreException`이 발생한다.

반면, `List`는 **타입 정보**가 런타임에는 소거된다. 원소 타입을 **컴파일시에만 검사**하며 런타임에는 알 수 없다는 말이다.

이런 이유로 배열은 **제네릭 타입(`new List<E>[]`)**, **매개변수화 타입(`new List<String>[]`)**, **타입 매개변수(`new E[]`)** 로 사용할 수 없다. 이런 식으로 코드를 작성하려하면 **제네릭 배열 생성 오류**를 일으킨다.

→ 제네릭과 매개변수화 타입은 컴파일 단계에서 추론할 대상이 있어야 되는데 “명확하지 않은 대상”을 추론 대상으로 삼을 경우 타입의 정체성을 확정지을 수 없다. == 실체화 불가 타입.

**[제네릭 배열 생성을 허용하지 않는 이유 - 컴파일되지 않는다.]**

```java
List<String>[] stringLists = new List<String>[1]; // (1) 허용된다고 가정해보자.
List<Integer> intList = List.of(42);              // (2) 원소가 하나인 List<Integer> 생성
Object[] objects = stringLists;                   // (3) stringLists를 objects에 할당
objects[0] = intList;                             // (4) intList를 objects의 첫번째 원소로 저장한다.
String s = stringLists[0].get(0);                 // (5) stringList[0]에 들어가있는 첫번째 요소는 Integer이므로 형변환 오류 발생.
```

설명

1. **`List<String>[] stringLists = new List<String>[1];`** 
    - **목적**: `List<String>` 타입의 배열을 생성하려고 합니다.
    - **문제점**: 제네릭 배열 생성은 허용되지 않습니다. 컴파일러는 이 코드를 허용하지 않고 **제네릭 배열 생성 오류**를 발생시킵니다.
    - **가정**: 만약 이 코드가 허용된다고 가정해봅시다.
2.  **`List<Integer> intList = List.of(42);`** 
    - **목적**: `List<Integer>` 타입의 리스트를 생성합니다. 이 리스트는 `42`라는 정수 하나를 포함합니다.
    - **동작**: `List.of(42)`는 `List<Integer>` 타입의 불변 리스트를 생성합니다.
3. **`Object[] objects = stringLists;`** 
    - **목적**: `stringLists` 배열을 `Object[]` 타입의 배열에 할당합니다.
    - **배열의 공변성**: 배열은 공변(Covariant)이므로, `List<String>[]`는 `Object[]`의 하위 타입으로 간주됩니다. 따라서 이 할당은 문법상 허용됩니다.

4. **`objects[0] = intList;`** 

- **목적**: `intList`(`List<Integer>`)를 `objects` 배열의 첫 번째 원소로 저장합니다.
- **문제점**: `objects`는 원래 `List<String>[]` 타입이었지만, `Object[]`로 업캐스팅되었기 때문에 `List<Integer>`를 저장할 수 있습니다.
- **결과**: `stringLists[0]`에는 `List<Integer>`가 저장됩니다.

5. **`String s = stringLists[0].get(0);`**

- **목적**: `stringLists[0]`의 첫 번째 원소를 가져와서 `String` 타입으로 형변환합니다.
- **문제점**:
    - `stringLists[0]`은 실제로 `List<Integer>`입니다.
    - 따라서 `stringLists[0].get(0)`은 `Integer` 타입의 값(`42`)을 반환합니다.
    - 하지만 이 값을 `String` 타입으로 형변환하려고 하면, 런타임에 `ClassCastException`이 발생합니다.

제네릭 배열을 만들지 못하게하는 이유는 컴파일러가 자동 생성한 형변환 코드에서 런타임에 `ClassCastException`이 발생할 수 있기 때문에 타입 안전하지 않기 때문이다. 또한 런타임에 `ClassCastException`이 발생하는 것을 막아주겠다는 제네릭 타입 시스템 취지에 어긋나는 일이기도 하다.

따라서 이런일을 방지하기위해 (1)번 과정에서 컴파일오류를 발생시킨다.

## 실체화 불가 타입

`E`, `List<E>`, `List<String>` 같은 타입을 **실체화 불가 타입**이라고 한다.

쉽게 말해, 실체화 되지 않아서 런타임에는 컴파일타임보다 타입 정보를 적게 가지는 타입이다. 소거 매커니즘 때문에 매개변수화 타입 가운데 실체화될 수 있는 타입은 `List<?>`, `Map<?,?>` 같은 비한정적 와일드카드 타입뿐이다.

참고로, 배열은 비한정적 와일드카드로 만들수는 있지만 유용하게 쓰일 일은 거의 없다.

## @SafeVarargs

> @SafeVarargs는 메서드 작성자가 해당 메서드가 타입 안전하다는 것을 보장하는 장치이다.
> 

제네릭 컬렉션에서는 자신의 원소 타입을 담은 배열을 반환하는게 보통은 불가능하다. 또한 제네릭 타입과 가변인수 메서드를 함께 쓰면 해석하기 어려운 경고 메시지를 받게 된다.

가변인수 메서드를 호출할 때마다 가변인수 매개변수를 담을 배열이 하나 만들어지는데 이때 그 배열의 원소가 실체화 불가 타입이라면 경고가 발생한다.

이 때 `@SafeVarargs`를 사용하면 잠재적 오류에 대한 경고를 무시함으로써 해결할 수 있다. 만약, 메서드가 타입 안전하지 않다면 절대 `@SafeVarargs`를 사용해서는 안된다.

**[@SafeVarargs - 잠재적 오류 경고 무시]**

```java
public class SafeVars {
    @SafeVarargs
    public static void print(List... names) {
        for (List<String> name : names) {
            System.out.println(name);
        }
    }

    public static void main(String[] args) {
        SafeVars safeVars = new SafeVars();
        List<String> list = new ArrayList<>();

        list.add("b");
        list.add("c");
        list.add("a");
        print(list);
    }
}
```

- **설명**:
    - `@SafeVarargs`는 메서드 작성자가 해당 메서드가 **타입 안전함을 보장**할 때 사용하는 애너테이션입니다.
    - 가변인수(varargs) 메서드와 제네릭을 함께 사용하면, 컴파일러는 **비검사 경고(unchecked warning)**를 발생시킵니다.
    - 이는 가변인수 메서드가 호출될 때마다 **배열이 생성**되기 때문입니다. 제네릭 타입은 런타임에 타입 정보가 소거되므로, 이 배열의 타입 안정성을 보장할 수 없습니다.
    - `@SafeVarargs`를 사용하면 이 경고를 무시할 수 있습니다. 하지만 메서드가 **타입 안전하지 않다면 절대 사용해서는 안 됩니다**.
    - 위 코드에서 `print` 메서드는 `List<String>` 타입의 가변인수를 받아 출력합니다. `@SafeVarargs`를 사용하여 경고를 무시하고, 타입 안전성을 보장합니다.

## 배열대신 컬렉션을 사용하자.

배열로 형변환할 때 제네릭 배열 생성 오류나 비검사 형변환 경고가 뜨는 경우 대부분 배열 `E[]`대신 `List<E>`를 사용하면 해결된다.

**조금 복잡해지고 성능이 나빠질 수 있지만 타입안정성이 보장되고 상호 운용성이 좋아진다.**

**[Chooser - 제네릭 적용 필요]**

```java
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(final Object[] choiceArray) {
        this.choiceArray = choiceArray;
    }

    public Object choose(){
        Random random = ThreadLocalRandom.current();
        return choiceArray[random.nextInt(choiceArray.length)];
    }
}
```

- **설명**:
    - `Chooser` 클래스는 배열을 사용하여 랜덤하게 원소를 선택하는 기능을 제공합니다.
    - **문제점**:
        - `choose` 메서드는 `Object` 타입을 반환하므로, 사용 시 매번 형변환을 해야 합니다.
        - 만약 배열에 다른 타입의 원소가 들어있다면, 런타임에 `ClassCastException`이 발생할 수 있습니다.
        - 예를 들어, `choiceArray`에 `String`과 `Integer`가 섞여 있다면, `String`으로 형변환할 때 오류가 발생합니다.

위 클래스를 사용하려면 choose 메서드를 호출할 때마다 반환된 `Object`를 원하는 타입으로 형변환해야 한다. 만약 타입이 다른 원소가 들어있으면 런타임시에 형변환 오류가 발생한다.

**[리스트 기반 Chooser - 타입 안정성 확보]**

```java
public class ListChooser {
    private final List<T> choiceList;

    public ListChooser(final Collection<T> choices) {
        this.choiceList = new ArrayList<>(choices);
    }

    public T choose(){
        Random random = ThreadLocalRandom.current();
        return choiceList[random.nextInt(choiceList.size())];
    }
}
```

- **설명**:
    - `ListChooser` 클래스는 제네릭과 리스트를 사용하여 랜덤하게 원소를 선택하는 기능을 제공합니다.
    - **장점**:
        - `List<T>`를 사용하므로, 타입 안정성이 보장됩니다.
        - `choose` 메서드는 `T` 타입을 반환하므로, 사용 시 형변환을 할 필요가 없습니다.
        - 런타임에 `ClassCastException`이 발생할 가능성이 없습니다.
    - **단점**:
        - 배열 대신 리스트를 사용하므로, 약간의 성능 저하가 발생할 수 있습니다.
        - 코드가 조금 더 복잡해질 수 있습니다.
    - **결론**:
        - 리스트를 사용하면 타입 안정성을 보장할 수 있고, 상호 운용성도 좋아집니다.

코드는 조금 길어졌지만 리스트를 사용함으로써 런타임에 ClassCastException을 만날일이 없어졌다.

## 정리

배열은 런타임에는 타입 안전하지만 컴파일시에는 그렇지 않다. 제네릭은 배열과 반대이다. 되도록이면 제네릭을 사용하고 둘을 섞어 사용하다 경고를 만날경우 배열을 리스트로 대체하자.