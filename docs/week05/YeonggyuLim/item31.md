# 한정적 와일드카드를 사용해 API 유연성을 높이라

- Stack<Number>로 선언시 pushAll(intVal) 호출시 intVal 은 Integer 타입
- Number 의 하위타입인 Integer 지만 제네릭은 불공변 즉 타입을 한정시킴 따라서 하위타입이 들어갈 수 없음 
```java
    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }
```

- E의 하위타입이 와일드카드로 들어갈 수 있게 리팩토링 (생산자)
```java
    public void pushAll(Iterable<? extends E> src) {
        for (E e : src) {
            push(e);
        }
    }
```
- Number 를 컬렉션에 담을때 Object 로 변환할 수 없음
- 제네릭은 불공변이므로, Collection<Number>는 Collection<Object>를 받을 수 없음
```java
public void popAll(Collection<E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }

public static void main(String[] args) {
    StackV4<Number> stackV4 = new StackV4<>();

    Iterable<Integer> intVal = Arrays.asList(1, 2, 3);
    Iterable<Double> doubleVal = Arrays.asList(1.1, 2.2, 3.3);

    stackV4.pushAll(intVal);
    stackV4.pushAll(doubleVal);

    Collection<Object> objects = new ArrayList<>();
    //불공변 때문에 제네릭에서 Number 는 Object 의 하위타입으로 간주하지 않음
//        stackV4.popAll(objects);
}
```
- E의 상위타입을 넣을 수 있는 리팩토링 (소비자)
```java
//E의 상위타입을 넣을 수 있음
    public void popAll(Collection<? super E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }

    public static void main(String[] args) {
        StackV5<Number> stackV5 = new StackV5<>();

        Iterable<Integer> intVal = Arrays.asList(1, 2, 3);
        Iterable<Double> doubleVal = Arrays.asList(1.1, 2.2, 3.3);

        stackV5.pushAll(intVal);
        stackV5.pushAll(doubleVal);

        Collection<Object> objects = new ArrayList<>();

        stackV5.popAll(objects);

        for (Object object : objects) {
            System.out.println(object);
        }
    }
```

## PECS 공식
- 생산자는 데이터를 읽기만함 외부로 데이터를 제공하는 역할
- 소비자는 데이터를 추가하거나 사용 ex) 리스트에 값 추가

## PECS 생산자
- 주석처리된 부분을 PECS 공식(생산자)에 맞게 리팩토링 해서 Number 의 하위타입 Integer, Double 등을 전달 가능하게 됨
- 즉 T의 하위 타입도 허용하여 더 유연한 코드

```java
public class ChooserV3<T> {
    private final List<T> choiceList;
    
    //제네릭 리스트는 애초에 컴파일전에 타입이 다를경우 컴파일 에러가 나서
    //제네릭 타입이 소거된 이후에도 내부 타입으로 문제가 생길 일이 없음
//    public ChooserV3(Collection<T> choices) {
//        choiceList = new ArrayList<>(choices);
//    }
    
    public ChooserV3(Collection<? extends T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```

## PECS 소비자
- E를 받아서 상위타입의 콜렉션을 만들어 담아줄 수 있는 소비자
```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
```

## PECS 소비자 + 생산자 예시
- 리스트를 읽어서 (생산자) Comparable 에 추가(소비자)해서 최대값을 비교해서 반환
- 일반적으로 Comparator<E> 보다는 Comparator<? super E>를 사용하는것이 더 낫다
- E가 직접 Comparable<E>를 구현하지 않아도 상위타입이 Comparable 을 구현하면 사용 가능하기 때문
```java
public class MaxEx {
    public static <E extends Comparable<? super E>> E max(List<? extends E> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }

        E result = null;
        for (E e : list) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}
```

## 제네릭 메서드 선언
- public API 라면 swap2가 더낫다
- 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체
- 하지만 두번째 메서드에서 에러가 나는데 와일드 카드는 null 타입만 삽입이 가능하기 때문
```java
public static <E> void swap(List<E> list, int i, int j) {}

//요소의 타입을 알 수 없음, 와일드 카드는 읽기는 허용되지만 쓰기는 허용 되지 않음(null 제외)
//정확히는 와일드 카드는 컴파일러가 어떤 타입인지는 모르지만 특정 타입임은 보장함
    public static void swap2(List<?> list, int i , int j) {
        list.set(i, list.set(j, list.get(i)));
    }
```

- 두번째 메서드의 에러 해결법
- ? 타입을 매개변수로 캡쳐해서 <E> 타입 매개변수로 넘김
- E 타입을 구체화 하면서 타입 안정성을 보장
- 컴파일 시점에 와일드카드 타입을 캡쳐해서 저장후 swap3 호출시 swapHelper 에 넘겨 List<E> 타입으로 변환
- 와일드카드의 읽기 전용(null 제외) 속성을 벗어나 set 에 쓰기가 가능해짐
```java
 public static void swap3(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```