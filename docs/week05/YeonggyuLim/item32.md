# 제네릭과 가변인수를 함께 쓸 때는 신중하라

## 제네릭과 가변인수를 혼용한 예시
- List<String>[] 배열이 Object[] 로 형변환 돼서 위험
```java
public class VarargsEx {
    static void dangerous(List<String>... stringLists) {
        List<Integer> intList = List.of(42);
        Object[] objects = stringLists; // 힙오염 발생함 (배열이 형변환 되니까)
        objects[0] = stringLists[0].get(0); //ClassCastException
    }
}
```

## 자신의 제네릭 매개변수 배열의 참조를 노출하는 예시

- 참조가 노출돼 외부에서 이 배열을 수정할 수 있어 타입 안정성이 깨진다
- T... 가변인수는 Object[]로 변환됨
- 배열은 공변적이므로 Object[]로 변호나되면 타입 안정성이 깨짐
```java
public class ex2 {
    //제네릭 매개변수 배열의 참조를 노출
    static <T> T[]toArray(T... args) {
        return args; //args 배열의 참조를 외부로 직접 노출
    }

    public static void main(String[] args) {
        String[] strings = toArray("A", "B", "C");
        Object[] objects = strings; //공변성 문제
        objects[0] = 42;
        String s = strings[0];
    }
}
```

## 자신의 제네릭 매개변수 배열의 참조를 노출하는 예시2
- 컴파일러 상에서 String 으로 추론하지만 런타임시 제네릭 타입이 소거 돼서 어떤 타입인지 모름
- 따라서 내부적으로 Object 타입을 생성 따라서 ClassCastException 발생

```java
public class ex3 {

    static <T> T[]toArray(T... args) {
        return args;
    }

    static <T> T[] pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
        }
        throw new AssertionError();
    }

    public static void main(String[] args) {
        String[] attributes = pickTwo("좋은", "빠른", "저렴한");
    }
}
```

## 제네릭 varargs 매개변수를 안전하게 사용하는 메서드(배열)

#### @SafeVarargs 가 안전하게 사용될 수 있는 조건
- @SafeVarargs 어노테이션 된 또 다른 varargs 메서드에 넘기는것은 안전
- 이 배열 내용의 일부 함수(varargs 를 받지 않는)를 호출만 하는 일반 메서드에 넘기는 것도 안전

#### @SafeVarargs 사용 할때의 규칙
1. varargs 매개변수 배열에 아무것도 저장하지 않는다.
2. 그 배열(복사본 포함)을 신뢰할 수 없는 코드에 노출하지 않는다.

```java
public class VarargsSafeEx {
    @SafeVarargs
    //List<? extends T>... --> List<? extends T>[] 배열 형태로 전달됨
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list: lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void main(String[] args) {
        //List.of 활용하면 여러 인수를 넘길 수 있는 이유 --> 이미 @SafeVarargs 어노테이션이 있음
        List<String> audience = flatten(List.of("friend", "romans", "countrymen"));
    }
}
```

## 제네릭 varargs 매개변수를 List 로 대체한 예시

- toArray 처럼 varargs 메서드를 안전하게 작성하는게 불가능한 상황에서 List.of 사용 가능 (@SafeVarargs 어노테이션이 이미 있으니까)
- toArray 는 런타임시 제네릭 타입 소거로 인해 T[]가 Object 배열로 변환됨 ClassCastException 발생할 수 있음
- List.of 는 List<T> 를 반환해 제네릭 타입 유지해서 더 안전
- 또한 List.of 는 불변 리스트를 제공해 내부 요소를 변경할 수 없음 즉 참조를 외부에 노출하지 않는다는 뜻

```java
public class PickTwoRefactor {
    static <T> List<T> pickTwo(T a, T b, T c) {
        switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0: return List.of(a, b);
            case 1: return List.of(a, c);
            case 2: return List.of(b, c);
        }
        throw new AssertionError();
    }

    public static void main(String[] args) {
        List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
    }
}
```
