# 로 타입은 사용하지 말라

## 로타입 사용시 어떤 타입이 들어가던지 꺼내기 전까지 알 수 없음
```java
public class Stamps {
    //로타입
    public static Collection stamps = new ArrayList();

    public static void main(String[] args) {
        stamps.add(new Coins());

        for (Iterator i = stamps.iterator(); i.hasNext();) {
            Stamps stamp = (Stamps) i.next();
        }

    }
    private static class Coins {}
}
```

## 로타입 리스트를 받는 메서드 unsafeAdd --> 꺼내보면 의도한 String 타입이 아니어서 ClassCastException 터짐
```java
public class UnsafeAddEx {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();

        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
    }
    //로타입
    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }
}
```
## 로타입 vs 와일드 카드
```java
public class WildCardEx {

    //raw 타입을 쓴 예시
    static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1))
                result++;
        }
        return result;
    }
    
    //raw 타입 대신 와일드카드 사용한 예시
    static int numElementsInCommonV2(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1))
                result++;
        }
        return result;
    }
}
```

- raw, 와일드카드, Object 3가지의 차이점
1. 로 타입만 타입 안정성이 없음 (컴파일러가 타입 체크를 하지 않음 → 런타임 오류 위험)
2. Object 는 모든 타입의 요소 추가 가능, 반면 와일드카드는 읽기 전용으로 요소 추가 불가능
3. 와일드카드는 읽기만 가능(add 등 불가, 단 null은 추가가능), 따라서 Collection 의 불변성을 보장 --> 와일드카드는 의도적으로 데이터를 수정하지 않는 경우에 적합

## Raw Type, Wildcard, Object의 차이점

| 구분        | Raw Type (`List`)               | Object (`List<Object>`)         | Wildcard (`List<?>`)           |
|------------|--------------------------------|--------------------------------|--------------------------------|
| **타입 안정성** | ❌ 타입 안정성 없음 (컴파일러가 타입 체크를 하지 않음) | ✅ 타입 안정성 보장 (모든 타입을 허용하지만 타입 체크 가능) | ✅ 타입 안정성 보장 (읽기 전용으로 사용 가능) |
| **요소 추가 가능 여부** | ✅ (하지만 잘못된 타입의 요소 추가 가능) | ✅ (모든 타입 추가 가능) | ❌ (읽기 전용, `add()` 불가능) |
| **요소 읽기 가능 여부** | ✅ 가능 (`Object`로 반환) | ✅ 가능 (`Object`로 반환) | ✅ 가능 (`Object`로 반환) |
| **불변성 유지** | ❌ (잘못된 요소 추가 가능) | ❌ (타입이 섞여도 문제 없음) | ✅ (요소 추가가 불가능하므로 불변성 유지) |
| **사용 용도** | 과거 코드, 호환성 유지 | 모든 타입의 요소를 받아야 할 때 | 데이터를 수정하지 않고 읽기만 할 때 |


## 로타입을 사용해도 되는경우
1. 클래스 리터럴
2. instanceof 연산자

- 두 경우 모두 런타임과 관련이 있음
- 런타임시 제네릭 타입 정보가 지워짐 따라서 런타임시 List<String>, List 는 동일함 
- 이처럼 클래스 리터럴 또한 제네릭 타입정보가 소거됨 따라서 로타입을 사용
- instanceof 연산자 또한 런타임시 제네릭 타입정보가 소거됨 즉 o instanceof Set, o instanceof Set<?> 이 경우가 똑같이 동작 즉 제네릭타입의 검사까지 불가능
- 코드가 지저분해지므로 그냥 로타입을 사용
```java
public class RawTypeExceptionEx {

    // 1. 클래스 리터럴에는 로타입을 사용해야 한다.
    // 런타임시 제네릭 타입 정보가 지워 지므로 그냥 로타입 사용
    Class<?> rawClass = Set.class;
//    Class<?> rawClass2 = Set<String>.class;
    // 컴파일 에러


    // 2. 런타임시 제네릭 타입의 정보가 지워짐 또한 로 타입이든 비한정적 와일드 카드 타입이든 instanceof는 완전히 똑같이 동작
    // 따라서 그냥 로타입을 쓰는게 남

    // 컴파일 후 에는 Set<String> Set<Integer> 둘다 그냥 똑같은 Set 취급
    // 따라서 instanceOf Set<String> 같은 검사가 불가능함 그래서 로타입을 사용해야함
    public static void main(String[] args) {
        Object o = new RawTypeExceptionEx();
        if (o instanceof Set) {
            //Set 인지 확인후 Set<?> 로 형변환
            Set<?> s = (Set<?>) o;
        }
    }
}

```