# 타입 안전 이종 컨테이너를 고려하라

## 타입 안전 이종 컨테이너 패턴
- class 리터럴(Class<T>)을 타입 토큰이라 함
- 일반적인 Map<K, V> 는 키의 타입이 동일해야 하지만
- 이 패턴을 사용시 다양한 타입의 값을 안전하게 저장하고 검색할 수 있음
- 즉 키(Class<T>)에 따라 값이 달라지는 유연한 컨테이너를 만들 수 있음

```java
public class Favorite{
    public <T> void putFavorite(Class<T> type, T instance);
    public <T> T getFavorite(Class<T> type);
    }
```

## 타입 안전 이종 컨테이너 - 클라이언트
```java
public static void main(String[] args) {
        Favorites f = new Favorites();
        
        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebabe);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        Integer favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);
        System.out.printf("%s %x %s%n", favoriteString,
                favoriteInteger, favoriteClass.getName());
    }
```

## 타입 안전 이종 컨테이너 - 구현
- 키가 와일드 타입
- cast 메서드를 이용해 이 객체의 참조를 Class 객체가 가르키는 타입으로 동적 형변환
- cast 메서드가 단순히 인수를 그대로 반환하는데 왜 사용하는것인가?
- cast 메서드의 시그니처가 Class<T>의 클래스가 제네릭이라는 이점을 완벽히 활용하기 때문
- 일반적인 강제 캐스팅과 달리 잘못된 형변환시 ClassCastException 을 발생 시킴
- 즉 타입 안전한 형변환 가능

```java
public class Favorites {
    private final Map<Class<?>, Object> favorites = new HashMap<>();

        public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }
    
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

## Favorites 클래스의 두가지 제약
### 1. 악의적인 클라이언트가 Class 객체를 로타입으로 넘길시 Favorites 인스턴스의 타입 안정성이 쉽게 깨진다
- 아래 코드 실행시 ClassCastException 이 터지지 않음
- String.class --> Class<String> 제네릭 타입임
- Class<String>은 String.class 와 같지만
- Class rawType 은 제네릭 타입이 생략된 그냥 Class 로 선언돼서 로타입임
- 로타입이기 때문에 타입 검사가 생략돼 타입 불일치가 발생해도 컴파일러가 검출하지 못함
- 따라서 putFavorite 호출 시 타입 불일치가 발생해도 ClassCastException 이 터지지 않음

```java
public static void main(String[] args) {
    Favorites f = new Favorites();
    
    Class rawType = String.class;
    f.putFavorite(rawType, 123);


    f.putFavorite(String.class, "Java");
    f.putFavorite(Integer.class, 0xcafebabe);
    f.putFavorite(Class.class, Favorites.class);

    String favoriteString = f.getFavorite(String.class);
    Integer favoriteInteger = f.getFavorite(Integer.class);
    Class favoriteClass = f.getFavorite(Class.class);
    System.out.printf("%s %x %s%n", favoriteString,
            favoriteInteger, favoriteClass.getName());
}
```

- cast 메서드를 이용해 안전하게 putFavorite 을 사용할 경우 (동적 형변환으로 런타임 타입 안정성 확보)
```java
public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
```
- 위의 코드를 동일하게 실행하면 ClassCastException 터짐
- 강제 형변환이 이뤄지지 않고 타입이 불일치해서 오류가 터짐

### 2. 실체화 불가한 코드에는 사용 불가
- String 이나, String[]은 저장할 수 있어도 List<String>은 저장할 수 없음
- List<String>.class 는 문법 오류가 나는데 그 이유는 List<Integer>, List<String>은 둘다 List.class 라는 같은 클래스 객체를 공유함
- 따라서 Class<List<String>>를 만들 수 없음 그렇기 때문에 타입토큰(Class<T>)을 사용하는 Favorites 는 List<String> 같은 실체화 불가 타입을 저장할 수 없음
- 이 두번째 제약에 대한 완벽한 우회는 없음

## Favorites 은 어떤 클래스 객체든 받아들이지만 메서드가 허용하는 타입을 제한하고 싶을 때
- 한정적 타입 토큰을 활용
- 한정적 타입 토큰 : 한정적 타입 매개변수나 한정적 와일드카드를 사용하여 표현 가능한 타입을 제한하는 타입 토큰

### 한정적 타입 토큰을 사용한 예시 애너테이션 API
- AnnotatedElement 인터페이스에 선언된 대상 요소에 달려 있는 애너테이션을 런타임에 읽어 오는 기능을 읽어오는 메서드
- annotationType : 한정적 타입 토큰 역할 
- 어노테이션이 있으면 어노테이션 반환 없으면 null 반환함
- 즉 어노테이션된 요소는 그 키가 어노테이션 타입인 타입 안전 이종 컨테이너

```java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```

- asSubClass 메서드를 사용해 한정적 타입 토큰을 안전하게 형변환 함
```java
public class AnnotationElementEx {

    static Annotation getAnnotation(AnnotatedElement element,
                                    String annotationTypeName) {
        Class<?> annotationType = null;
        //어노테이션 타입 로드
        //어노테이션 타입 이름을 동적으로 클래스 객체로 변환
        try {
            annotationType = Class.forName(annotationTypeName);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
        //어노테이션 반환
        //클래스가 어노테이션의 서브타입인지 확인
        return element.getAnnotation(
                annotationType.asSubclass(Annotation.class));
    }

    @Deprecated
    static class MyClass {
    }

    public static void main(String[] args) {
        AnnotatedElement element = MyClass.class;

        String annotationTypeName = "java.lang.Deprecated";
        Annotation annotation = AnnotationElementEx.getAnnotation(element, annotationTypeName);
        if (annotation != null) {
            System.out.println("어노테이션 : " + annotation);
        } else {
            System.out.println("어노테이션 x");
        }
    }
}

```