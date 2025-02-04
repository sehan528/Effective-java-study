# 배열보다는 리스트를 사용하라

1. 배열
- 아래 예시에서 배열의 경우 런타임시 타입이 달라 넣을 수 없는걸 알 수 있음
- ArrayStoreException 을 던짐
2. 리스트
- 리스트의 경우 애초에 타입이 호환되지 않음 제네릭 타입에 맞지 않으니까
```java
public class ArrayEx {
    public static void main(String[] args) {
        Object[] objectArray = new Long[1];
        objectArray[0] = "타입이 달라 넣을 수 없음";

//        List<Object> ol = new ArrayList<Long>();
//        ol.add("타입이 달라서 안들어감");
    }
}
```

- 제네릭이 배열 생성을 허용하지 않는 이유

- 아래 예시는 컴파일 에러가 나지만 가능하다고 가정하고 설명
- 배열은 공변이라는 성질을 갖음 하지만 제네릭은 타입 안정성을 위해 불공변이라는 성질을 갖음
- String[] -> Object[]로 캐스팅이 가능함 하지만 이는 제네릭의 설계 의도와 맞지 않음
- 왜냐하면 String 만 넣기로 약속했는데 Object 도 들어가게 됐으니까

```java
public class GenericArrayBadEx {
    public static void main(String[] args) {
        List<String>[] stringList = new List<String>[1];

        List<Integer>[] intList = List.of(42);

        Object[] objects = stringList; //배열은 공변임 따라서 List<String> 에서 Object 로 캐스팅 돼 버림

        objects[0] = intList;

        String s = stringList[0].get(0);
        
    }
}
```

## 제네릭 vs 배열
# 배열 vs 제네릭 비교

| 구분        | 배열 (Array)                      | 제네릭 (Generics)                 |
|------------|----------------------------------|----------------------------------|
| **타입 특성** | 공변 (Covariant)                | 불공변 (Invariant)               |
| **타입 안정성** | 런타임에서 타입 검사 (`ArrayStoreException` 가능) | 컴파일 타임에 타입 검사 |
| **예제** | `Object[] arr = new String[10];` ✅ 가능 | `List<Object> list = new ArrayList<String>();` ❌ 불가능 |


- 제네릭이 필요한 예시
```java
public class ChooserV1 {
    private final Object[] choiceArray;

    public ChooserV1(Collection choices) {
        choiceArray = choices.toArray();
    }

    //choose 호출할때 마다 Object 를 원하는 타입으로 형변환 해줘야함
    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

- 제네릭을 도입했지만 문제가 있는 예시
- 제네릭을 도입했지만 제네릭 타입의 배열을 사용
- 런타임시 제네릭의 타입은 소거됨 --> 따라서 어떤 타입이 들어오는지 알 수 가 없음
```java
public class ChooserV2<T> {
    private final T[] choiceArray;
    public ChooserV2(Collection<T> choices) {
        choiceArray = (T[]) choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

- 위의 문제를 보완한 예시
- 제네릭 배열과 달리 제네릭 리스트는 컴파일시 에 타입을 제한 함 따라서 내부 요소의 타입이 안정적

```java
public class ChooserV3<T> {
    private final List<T> choiceList;
    public ChooserV3(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```