# `Comparble`을 구현할지 고민하라

## 객체 참조 필드가 하나인 경우의 예시
- 두 객체 동일하면 : 0
- 첫 번째 객체 < 두 번째 객체 : 음수
- 첫 번째 객체 > 두 번째 객체 : 양수
- `compareTo`는 대소문자 구분 없이 문자열 비교

```java
public final class CaseInsensitiveString implements Comparable<CaseInsensitiveString>{
    private final String s;
    public CaseInsensitiveString(String s) {
        this.s = s;
    }
    

    @Override
    public int compareTo(CaseInsensitiveString cis) {
        return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
    }
}
```

## 기본 타입 필드가 여러개일 경우
- 비교할 필드가 여러개일 경우 중요한 필드 부터 비교하면서 내려가기

```java
    public int compareTo(PhoneNumber pn) {
        int result = Short.compare(areaCode, pn.areaCode);
        if (result == 0) {
            result = Short.compare(prefix, pn.prefix);
            if (result == 0) {
                result = Short.compare(lineNum, pn.lineNum);
            }
        }
        return result;
    }
```

## 비교자 생성 메서드를 활용한 경우
- static import 를 활용해 더 간단하게 표시
```java
private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
```

## 추이성을 위배한 예시
- 이 방식은 사용금지 --> 정수 오버플로우 또는 부동소수점 계산 방식에 따른 오류 발생 할 수 있음
- 오버플로우 발생이유
 int x = Integer.MAX_VALUE; // 2,147,483,647
 int y = Integer.MIN_VALUE; // -2,147,483,648
 int diff = x - y; // 4,294,967,295 (이 값은 int 범위를 초과)
```java
    static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.hashCode() - o2.hashCode();
        }
    };
```

## 추이성 위배한 예시의 대안
1. `정적 compare 메서드`를 활용
- Integer.compare()는 정수의 차이를 비교하는것이 아닌 직접 크기만 비교 따라서 오버플로우 발생 x
```java
    static Comparator<Object> hashCodeOrder2 = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
    };
```

2. `비교자 생성 메서드 활용`
- Integer.compare()과 같은 방식으로 비교 + 람다라 가독성 좋음
```java
    static Comparator<Object> hashCodeOrder3 =
            Comparator.comparing(o -> o.hashCode());
```




