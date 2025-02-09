제네릭을 사용하면 컴파일러가 타입을 체크하여 타입 안전성을 보장할 수 있다. 하지만 일반적인 제네릭 컬렉션은 하나의 컨테이너가 한 가지 타입만 저장할 수 있도록 제한된다. 그렇다면 여러 타입의 객체를 타입 안전하게 저장하고 검색하려면 어떻게 해야 할까?

이 문제를 해결하는 방법이 바로 **타입 안전 이종 컨테이너 패턴**이다. 이 패턴에서는 Map 자료구조를 응용하여 컨테이너 자체가 아닌 각 컨테이너의 **키(key)를 제네릭 타입으로 만들어** 다양한 타입의 객체를 저장할 수 있도록 설계한다. 이를 통해 타입 안정성을 유지하면서도 유연하게 여러 타입의 데이터를 다룰 수 있다.

이번 아이템에서는 타입 안전 이종 컨테이너 패턴의 개념을 이해하고 구현하는 방법을 살펴본다. 또한, 런타임 타입 안전성을 확보하는 방법과 패턴의 한계를 분석하며, 이를 실전에서 어떻게 활용할 수 있는지 알아본다.

---

## 1. 타입 안전 이종 컨테이너 패턴

제네릭은 Collection (Set, Map) , ThreadLocal 등 단일 원소 컨테이너에서도 자주 사용된다. 하지만 여기서 매개변수 되는 대상은 “컨테이너 자신” 이다. 이는 하나의 컨테이너 매개변수화할 수 있는 타입의 수가 제한됨을 의미한다. 만약 다중의 컨테이너들을 매개변수로 가질려면 어떻게 해야할까? 또 그런 상황은 어떤 경우가 있을까? 저자는 다음과 같은 예를 들었다.

> DB의 하나의 record 별로 여러 column을 가질 수 있다. 하지만 전화번호, 이름 등.. 타입이 다양할 것이다. 이 다양한 정보들을 모두 ‘타입 안전’ 하게 가져올려면 어떤 방법이 있을까?
> 

이에 대한 해답으론 컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하면 제네릭 타입 시스템이 값의 타입이 키와 같음을 보장하는 방법을 제안했다.

이러한 설계 방식을 **타입 안전 이종 컨테이너 패턴**이라 한다.

우선 예시로 들 수 있는 Fav 인터페이스를 살펴보자. 

```java

public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance); // key가 매개변수화 되어있음
    public <T> T getFavorite(Class<T> type);
}
```

클라이언트에서 Fav를 사용하는 예시이다. 

```java
public static void main(String[] args) {
    Favorites favorites = new Favorites();
    favorites.putFavorite(String.class,"morning");
    favorites.putFavorite(Integer.class, 0xcafebabe);
    favorites.putFavorite(Class.class, Favorites.class);

    String favoriteString = favorites.getFavorite(String.class);
    Integer favoriteInteger = favorites.getFavorite(Integer.class);
    Class<?> favoriteClass = favorites.getFavorite(Class.class);

    System.out.printf("%s %x %s", favoriteString, favoriteInteger, favoriteClass.getName());
}
```

위 코드들을 기반으로 이제 타입 안전 이종 컨테이너의 “구현부” 를 작성할 것 이다. 
그 전에 Type Token에 대해 알아보고 진행하도록 하자.

## 2. 타입 토큰 (Type Token)

타입 토큰(Type Token)은 런타임에 타입 정보를 표현하는 Class 객체를 의미합니다.

> 컴파일 타임시, 타입정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴을 타입토큰(Type Token) 이라고 한다.
> 

**1. 타입 토큰의 기본 개념**

```java
// String.class는 String 타입을 나타내는 타입 토큰
Class<String> stringToken = String.class;

// Integer.class는 Integer 타입을 나타내는 타입 토큰
Class<Integer> integerToken = Integer.class;
```

타입 토큰의 특징으로는 다음과 같다.

- 컴파일타임의 타입 정보를 런타임까지 전달할 수 있음
- 각 타입당 단 하나의 Class 객체만 존재 (싱글톤)
- 제네릭과 함께 사용하여 타입 안전성 보장 가능

**2. 타입 토큰의 활용 예시**

```java
public class TypeSafeMap {
    private Map<Class<?>, Object> map = new HashMap<>();
    
    // 타입 토큰을 사용한 안전한 저장
    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }
    
    // 타입 토큰을 사용한 안전한 조회
    public <T> T get(Class<T> type) {
        return type.cast(map.get(type));
    }
}

// 사용 예시
TypeSafeMap map = new TypeSafeMap();
map.put(String.class, "Hello");  // String.class가 타입 토큰
map.put(Integer.class, 42);      // Integer.class가 타입 토큰

String str = map.get(String.class);   // "Hello"
Integer num = map.get(Integer.class); // 42
```

만약 타입 토큰이 없다면 클라이언트는 사용하는 해당 클래스가 무슨 타입의 객체를 저장하고 있는지 런타임에 알 수 없고, 타입 안전하게 객체를 꺼내올 수도 없을 것이다. 이러한 타입 토큰의 개념은 특히 리플렉션 API, 직렬화/역직렬화, DI 컨테이너 등에서 광범위하게 활용된다.

## 3. 타입 안전 이종 컨테이너 구현

**[타입 안전 이종 컨테이너 - 인스턴스 저장, 검색기능의 Favorites 클래스]**

```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();
	
    public <T> void putFavorite(Class<T> type, T instance){
        favorites.put(Objects.requireNonNull(type), instance);
    } // 클래스의 리터럴 타입은 Class가 아니라 Class<T>이다.

    public <T> T getFavorite(Class<T> type){
        return type.cast(favorites.get(type));
        // Object 타입의 객체(favorites.get(type)를 꺼내 T로 바꿔 반환해야 한다.
        // cast메서드로 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환 한다.
    }

}
...
public static void main(String[] args) {
    Favorites favorites = new Favorites();
    favorites.putFavorite(String.class,"morning");
    // String의 클래스 타입은 Class<String>이다.
    favorites.putFavorite(Integer.class, 0xcafebabe);
    // Integer의 클래스 타입은 Class<Integer>이다.
    favorites.putFavorite(Class.class, Favorites.class);

    String favoriteString = favorites.getFavorite(String.class);
    Integer favoriteInteger = favorites.getFavorite(Integer.class);
    Class<?> favoriteClass = favorites.getFavorite(Class.class);

    System.out.printf("%s %x %s", favoriteString, favoriteInteger, favoriteClass.getName());
}
...
// 실행결과: morning cafebabe Favorites
```

구현부로는 다음과 같은 코드를 작성할 수 있다.

```java
private Map<Class<?>, Object> favorites = new HashMap<>();
```

이에 대해 다음과 같이 볼 수 있다.

1. `Map<Class<?>, Object>` 의 key로 비한정적 와일드카드 타입이 들어감.
    
    키를 **비한정적인 와일드카드 타입**으로 선언하였기 때문에, 이를 통해서 **다양한 매개변수화 타입의 키**를 허용할 수 있게 되었다. 만약 `Map<Class<T>, Object>` 였다면 오직 한가지 타입의 키만 담을 수 있었을 것이다.
    
2.  `Favorites` 맵의 값 타입 (value)는 Object 이다.
이는 fav 맵 값 타입은 키와 값 사이의 타입 관계를 보증하지 않는다는 의미다. 즉, 모든 값이 키로 명시한 타입임을 보증하지 않는다.

또한 클라이언트의 `Favorites` 인스턴스는 타입 안전하다. `String` 타입을 요청했는데 `Integer`를 반환하는일은 절대 없기 때문이다. 모든 키의 타입이 제각각이라, 일반적인 맵과 달리 여러가지 타입의 원소를 담을 수 있다. 여기서 구현부와 맞아떨어지는 부분을 볼 수 있다.

따라서 `Favorites`는 타입 안전 이종 컨테이너라 할 만하다. 또한 `Favorite` 클래스에서 타입 안전을 보장하는 비결은 `cast` 메서드에 있다.

### cast 메서드

> 형변환 연산자의 동적 버전으로, 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지 검사한 다음, 맞다면 반환하고 아니면 `ClassCastException` 을 던진다. 이를 활용하면, `T` 로 비검사 형변환을 하지 않아도 된다.
> 

그 이유는 `cast` 메서드의 반환 타입은 `Class` 객체의 타입 매개변수와 같다. 즉, `cast` 메서드는 `Class` 클래스가 제네릭이라는 이점을 잘 활용한다.

```java
public class Class<T> {
	T cast(Object object);
}
```

이 기능은 `getFavorite` 메서드에 필요한 기능으로 `T`로 비검사 형변환하는 과정 없이도 `Favorites`를 타입 안전하게 만들어준다.

## 4. 동적 형변환으로 타입 안정성 확보

만약, 클라이언트가 Class 객체를 (제네릭이 아닌) **로(Raw) 타입으로 넘기면** **타입안정성**이 깨지게 된다. 하지만, 이렇게 로 타입을 넘길경우 컴파일시 비검사 경고가 뜰 것이다.

만약, 타입 안전성을 확보하고 싶다면 값(value) 인수로 주어진 타입이 키(key)로 명시한 타입과 같은지 확인하면 된다.

**[동적 형변환으로 타입 안전성 확보]**

```java
public <T> void putFavorite(Class<T> type, T instance){
    favorites.put(Objects.requireNonNull(type), type.cast(instance));
}
```

- **설명**:
    - `putFavorite` 메서드에서 `type.cast(instance)`를 사용하여 동적 형변환을 수행합니다.
    - 이로 인해, 저장되는 값이 타입 토큰과 일치하는지 확인할 수 있습니다.
    - 타입 안전성을 보장하기 위해, 값이 타입 토큰과 일치하지 않으면 `ClassCastException`이 발생합니다.

`java.util.Collections`의 `checkedSet`, `checkedList`, `checkedMap` 등이 있는데 이들은 모두 제네릭이라 `Class` 객체와 컬렉션의 컴파일타임 타입이 같음을 보장하고 이 래퍼틀은 내부 컬렉션들을 실체화한다.

즉, 이 wraper 들은 *제네릭과 로 타입을 섞어서 사용하는 애플리케이션* 에서 클라이언트 코드가 컬렉션에 잘못된 타입의 원소를 넣지 못하게 추적하는데 도움을 준다.

## 5. 실체화 불가 타입에는 사용할 수 없다.

***Reminder***

> 실체화 불가 타입(Non-Reifiable Type)은 **런타임에 타입 정보가 소거(Erased)되는 타입**을 말합니다. 이는 제네릭 타입 시스템의 핵심 개념 중 하나로, **컴파일 시점에는 타입 정보가 존재**하지만 **런타임**에는 타입 정보가 사라지는 특징을 가진다.
> 
1. **실체화 가능 타입(Reifiable Type)**
    - **정의**: 런타임에 타입 정보가 완전히 유지되는 타입입니다.
    - **예시**:
    - **특징**:
2. **실체화 불가 타입(Non-Reifiable Type)**
    - **정의**: 런타임에 타입 정보가 소거(Erased)되는 타입입니다.
    - **예시**:
    - **특징**:

`String`이나 `String[]`은 사용할 수 있지만 `List<String>`은 사용할 수 없다.

`List<String>`을 사용하려는 코드는 컴파일 되지 않는다. ***그 이유는 `List<String>`용 `Class` 객체를 얻을 수 없기 때문이다.***

`List<String>.class`라고 쓰면 문법 오류가 발생한다. `List<String>`과 `List<Integer>`는 `List.class`라는 같은 `Class` 객체를 공유하므로 같은 타입의 객체 참조를 반환한다면 객체 내부에서 이들을 구분할 방법이 없어진다. 이 제약에 대한 만족스런 우회로는 없지만 슈퍼 타입 토큰(Super Type Token) 으로 해결하려는 시도가 있었다.

→ 자세한 부분은 Neal Gafter의 super type token을 참고하라는 주석이 있음.

## 6. 한정적 타입 토큰 (추가적인 방법들)

**[Favorites 클래스 - 비한정적인 타입토큰 사용]**

```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance){
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type){
        return type.cast(favorites.get(type));
    }

}
```

`Favorites`가 사용하는 타입 토큰은 비한정적이라 `getFavorite`과 `putFavorite` 어떤 `Class` 객체도 받아들인다. 만약 이 메서드들이 허용하는 타입을 제한하고 싶다면 **한정적 타입 토큰**을 활용하면 된다.

> 한정적 타입 토큰: 단순히 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰이다.
> 

***애너테이션 API*** 는 한정적 타입 토큰을 적극적으로 사용한다.

* 애너테이션 API는 리플렉션 API 중 일부로 `java.lang.relfect` 패키지에 포함되어 있다.

**[AnnotatedElement메서드 - 애너테이션을 런타임에 읽는 기능]**

```java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```

- **설명**:
    - `getAnnotation` 메서드는 한정적 타입 토큰을 사용하여 특정 애너테이션 타입을 검색합니다.
    - `annotationType`은 애너테이션 타입을 나타내는 한정적 타입 토큰입니다.

`annotationType` 메서드는 토큰으로 명시한 타입의 애너테이션이 대상 요소에 달려있다면 그 애너테이션을 반환하고 없다면 `null`을 반환한다. → 감지하면 반환 & 없으면 null. 

즉, 애너테이션된 요소는 그 키가 애너테이션 타입인 타입 안전 이종 컨테이너이다.

**[asSubClass - 한정적 타입토큰을 안전하게 형변환] // `java.lang.Class` 내장.**

```java
static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName){
    Class<?> annotationType = null; // 비한정적 타입 토큰
    try {
        annotationType = Class.forName(annotationTypeName);
    }catch (Exception exception){
        throw new IllegalArgumentException(exception);
    }
    return element.getAnnotation(annotationType.asSubclass(Annotation.class));
}

```

Class 패키지의 asSubclass 메서드의 역할로는 다음과 같습니다. 

- 주어진 클래스가 명시된 superclass의 하위 클래스인지 확인하고 변환합니다.
- 아닌 경우 ClassCastException을 던집니다

(형변환 된다는 것은 이 클래스가 인수로 명시한 클래스의 하위 클래스라는 의미이다.)

**설명**:

- `asSubclass` 메서드를 사용하여 비한정적 타입 토큰을 한정적 타입 토큰으로 안전하게 형변환합니다.
- 이로 인해, 런타임에 타입 안전성을 보장할 수 있습니다.

## 정리

1. 컬렉션 API로 대표되는 일반적인 제네릭형태는 한 컨테이너가 다룰수 있는 매개변수의 수가 고정적이다. 하지만 컨테이너자체가 아닌 키를 타입 매개변수로 바꾸면 이런 제약이 없는 타입 안전 이종 컨테이너를 만들 수 있다.
2. 타입 이종 컨테이너는 `Class`를 키로 사용하며, 이런 식으로 쓰이는 `Class` 객체를 타입 토큰이라 한다.
3. 또한 직접 구현한 키 타입도 쓸 수 있다. 예컨데 DB의 행(컨테이너)을 표현한 `DatabaseRow` 타입에는 제네릭 타입인 `Column<T>`를 사용할 수 있다. 하지만 타입이종 컨테이너를 사용하는데 제약이 있으니 이런 제약들을 주의해서 사용하자.