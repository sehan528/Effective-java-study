제네릭을 사용하면 타입 안전성을 확보하고, 불필요한 형변환을 줄일 수 있다. 그러나 로(raw) 타입을 사용하면 제네릭이 제공하는 타입 안정성과 표현력을 모두 잃게 된다. 로 타입을 사용하면 컴파일러가 타입 검사를 수행할 수 없고, 런타임에서 ClassCastException이 발생할 위험이 커진다. 이번 아이템에서는 로 타입이 무엇인지, 왜 사용하면 안 되는지, 그리고 예외적으로 사용할 수 있는 경우를 살펴본다. 또한, 비한정적 와일드카드 타입(?)을 활용해 타입 안전성을 유지하면서도 유연한 코드를 작성하는 방법을 알아본다. 

---

[[이펙티브 자바] 아이템26 | 로 타입은 사용하지 말라](https://velog.io/@alkwen0996/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-%EC%95%84%EC%9D%B4%ED%85%9C26-%EB%A1%9C-%ED%83%80%EC%9E%85%EC%9D%80-%EC%82%AC%EC%9A%A9%ED%95%98%EC%A7%80-%EB%A7%90%EB%9D%BC)

## 제네릭 클래스와 제네릭 인터페이스

> 클래스와 인터페이스 선언에 타입 매개변수 (**E**)가 쓰이면, 이를 제네릭 클래스 혹은 
제네릭 인터페이스라고 한다.
> 

`List` 인터페이스는 원소의 타입을 나타내는 **타입 매개변수** `E`를 받고 이를 `List<E>`라고 표현한다.

제네릭 클래스와 제네릭 인터페이스를 통틀어 **제네릭 타입** 이라고 한다.

## 제네릭 타입과 매개변수화 타입

제네릭 타입은 일련의 **매개변수화 타입**을 정의한다. 먼저 클래스 혹은 인터페이스 이름이 나오고, 이어서 꺽쇄괄호 안에 실제 타입 매개변수들을 나열한다.

`List<String>`은 원소의 타입이 `String`인 리스트를 뜻하는 **매개변수화 타입**이다. 여기서 `String`이 정규 **타입 매개변수** `E`에 해당하는 **실제 타입 매개변수**이다.

## 제네릭 타입과 로(raw) 타입

제네릭 타입을 하나 정의하면 그에 딸린 로 타입(raw type)도 함께 정의된다.

로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다.

`List<E>`의 로 타입은 `List`이다. **이런 로 타입은 제네릭 타입 정보가 전부 지워진 것처럼 동작한다. 이는 제네릭이 개발되기 이전 코드와 호환되도록 하기 위한 방법이다.**

## 로 타입의 단점

로 타입은 다른 타입의 데이터를 넣어도 컴파일되고 실행된다.

**[컬렉션의 로타입]**

```java
private final Collection stamps = ...;
...
stamps.add(new Coin(...)); // 실수로 동전을 넣는다.
```

위 코드처럼 다른 타입을 컬렉션에 넣어도 컬렉션에서 이 동전을 꺼낼때까지 오류를 알아채지 못한다.

따라서 오류가 발생하고 한참 뒤인 런타임에서야 오류를 알아챌 수 있는데 이렇게 되면 원인을 제공한 코드와 런타임에 문제가 발생한코드가 떨어져있어 에러를 잡기위해 코드 전체를 훑어봐야 할 수도 있다.

이런 문제를 해결하기 위해 매개변수화된 컬렉션 타입으로 **타입 안정성**을 확보해야한다.

## 매개변수화된 컬렉션 타입

**[매개변수화된 컬렉션 타입 - 타입 안정성 확보]**

```java
private final Collection<Stamp> stamps = ...;
```

이렇게 선언하면 컴파일러가 stamps 컬렉션에 **Stamp 인스턴스만 넣어야함을 인지**하여 의도대로 동작함을 보장해준다.

만약, 다른 타입의 인스턴스를 넣으려하면 컴파일 오류가 발생하며 문제를 알려준다.

컴파일러는 컬렉션에서 원소를 꺼내는 모든곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.

## List와 List`<Object>`

로 타입을 쓰면 제네릭이 안겨주는 안정성과 표현성을 모두 잃게된다.

그럼에도 불구하고 이런 로 타입을 남겨놓은 이유는 자바에 제네릭을 받아들이기 이전 코드와의 호환성 때문이다.

비록 `List`와 같은 로 타입은 사용해서는 안되나, `List<Object>`처럼 임의 객체를 허용하는 매개변수화 타입은 괜찮다.

제네릭 타입이 아닌 `List`와 달리 `List<Object>`는 모든 타입을 허용한다는 의사를 컴파일러에 전달한 것이다.

## 제네릭 하위 타입 규칙

`List`를 받는 메서드에 `List<String>`을 넘길 수 있지만, `List<Object>`를 받는 메서드에는 넘길 수 없다.

이는 제네릭 하위 타입 규칙때문이다. `List<String>`은 `List`의 하위 타입이지만, `List<Object>`는 `List`의 하위타입이 아니다.

**그 결과, `List<Object>` 같은 매개변수화 타입을 사용할 때와 달리 `List` 같은 타입을 사용하면 타입 안정성을 잃게된다.**

**[로 타입 사용으로 런타임 실패]**

```java
public static void main(String[] args) {
        final List<String> strings = new ArrayList<>(); 
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
}

private static void unsafeAdd(final List list, final Integer valueOf) {
        list.add(0);
}
```

이 코드는 컴파일은 되지만 
`Unchecked call to 'add(E)' as a member of raw type 'java.util.List` 라는 경고가 발생한다.

경고를 무시하고 프로그램을 실행하면 `ClassCastException` 이 발생한다.

**[`List <Object>` 타입 사용 - 컴파일시 오류 확인]**

```java
public static void main(String[] args) {
        final List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
}

private static void unsafeAdd(final List<Object> list, final Integer valueOf) {
        list.add(0);
}
```

`List`를 `List<Object>`로 변경하면 오류 메시지가 출력되며 컴파일조차 되지 않는다.

## 비한정적 와일드카드 타입

제네릭 타입을 사용하고싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표(`?`)를 사용하자.

제네릭 타입인 `Set<E>`의 비한정적 와일드카드 타입은 `Set<?>`이다.

`<?>`는 어떤 타입이라도 담을 수 있는 **가장 범용적인 매개변수화 타입**이다.

**[비한정적 와일드 카드 타입 - 타입 안전하며, 유연]**

```java
static int numElementsInCommon(Set<?> s1, Set<?> s2){...}
```

`로 타입`과 `비한정적 와일드 카드` 타입의 `차이`는 로 타입에는 아무 원소나 넣을 수 없으니 타입 불변식을 훼손하기 쉬운 반면, 비한정적 와일드카드 타입을 사용한 경우 (null외에는) 어떤 원소도 넣을 수 없다.

만약 다른 원소를 넣으려 하면 오류메시지가 발생한다.

**[컬렉션을 이용해 각 타입 비교]**

```java
public class TypeTest {
    public static void main(String[] args) {
        List raw = new ArrayList<>();
        List<?> wildCard = new ArrayList<>();
        List<Object> generic = new ArrayList<>();

        raw.add("Hello"); // 로 타입 (오류발생x)
        wildCard.add("Hello"); // 컴파일 오류 발생
        generic.add("Hello"); // <Object> 타입 (오류발생x)
    }
}
```

## 로 타입을 사용해야하는 경우

### class 리터럴에는 로타입을 사용해야한다.

```java
List.class, String[].class, int.class
```

자바 명세에는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다.

### instanceof 연산자 사용시 로 타입을 사용하자.

```java
if (o instanceof Set) { // 로 타입
	Set<?> s = (Set<?>) o; // 와일드카드 타입
}
```

런타임에는 제네릭 타입 정보가 지워지므로 `instanceof` 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.

그리고 `instanceof` 연산자는 로 타입과 비한정적 와일드카드 타입 모두 똑같이 동작한다.

> 위 코드에서 O 타입이 Set임을 확인하고 Set<?>로 형변환 해야 한다.(로 타입인 Set이 아니다.) 이는 검사 형변환 이므로 컴파일러 경고가 발생하지 않는다.
> 

### 정리

로 타입은 호환성을 위해 남아있는것일 뿐이다. 이를 사용하면 런타임에 예외가 발생할 수 있으니 사용을 지양하자. 단, `class` 리터럴과 `instanceof`연산자에는 로 타입을 사용하자