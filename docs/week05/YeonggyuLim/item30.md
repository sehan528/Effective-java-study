# 이왕이면 제네릭 메서드로 만들라

## 로타입을 사용한 예시
- 컴파일은 가능 하지만 타입 안전하지 않다는 경고가 생김
- 로타입은 Set<Object> 처럼 동작하지만 Set<Object> 도 아니고 Set<?> 도 아님
- 요소를 넣을때 어떤 타입이든 넣을 수 있고 요소를 가져올땐 그냥 Object 로 반환됨
- 당연히 로타입이기에 타입체크를 안함
```java
public class ex1 {
    public static Set union(Set s1, Set s2) {
        Set result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        set.add("ss");
        set2.add(3);

        Set union = union(set, set2);
        for (Object o : union) {
            System.out.println(o);
        }
    }
}
```

## 로타입을 제네릭으로 바꾼 예시

```java
public class ex2 {
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet(s1);
        result.addAll(s2);
        return result;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();

        set.add("ss");
        set2.add(3);

        //다른 타입의 Set 이기 때문에 컴파일 에러
//        Set union = union(set, set2);
//        for (Object o : union) {
//            System.out.println(o);
    }
}
```

## 제네릭 싱글톤 패턴
```java
public class GenericSingletonEx {
    //입력값을 받아 같은 타입의 값을 반환하는 단항 연산자 UnaryOperator
    //상수화 시켜서 싱글톤으로 사용
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    //UnaryOperator<Object>로 선언됐지만 어쩌피 T 타입으로 캐스팅 돼서 반환 되니까 타입 안전
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    public static void main(String[] args) {
        String[] strings = {"삼베", "대마", "나일론"};
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings) {
            System.out.println(sameString.apply(s));
        }

        Number[] numbers = {1, 2.0, 3L};
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number n : numbers) {
            System.out.println(sameNumber.apply(n));
        }
    }
}
```

## 재귀적 타입 한정을 이용한 컬렉션
- E가 Comparable<E> 를 구현해야함
```java
public class ex3 {
    //빈 컬렉션을 던져서 예외처리하게 할바에 그냥 Optional 쓰는게 더 좋긴 함

    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}
```