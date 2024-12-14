# `equals`와 `hashCode` 메서드 재정의 규약

## `equals`를 재정의하려면 `hashCode`도 재정의하라

`equals` 메서드를 재정의할 때 `hashCode` 메서드를 함께 재정의해야 하는 이유는 `hashCode`가 `equals`의 동작에 영향을 미치기 때문입니다. `hashCode`와 `equals` 메서드는 항상 일관성 있는 관계를 유지해야 합니다.

### 1. `hashCode`는 객체가 변경되지 않는 한 일관된 값을 반환해야 한다.
- `equals` 비교에 사용되는 정보가 변경되지 않는 한, 애플리케이션 실행 중에는 동일한 객체에 대해 `hashCode`는 항상 일관된 값을 반환해야 합니다. (단, 애플리케이션이 재시작될 때마다 달라져도 상관없음)

### 2. `equals`가 두 객체를 같다고 판단하면 두 객체의 `hashCode`는 동일해야 한다.
- `equals` 메서드가 두 객체를 같다고 판단할 경우, 이 두 객체의 `hashCode` 값은 반드시 같아야 합니다.
- 예를 들어, 두 객체가 동일한 값을 가지고 있고 `equals` 메서드가 `true`를 반환한다면, `hashCode`도 반드시 같은 값을 반환해야 합니다.

### 3. `equals`가 두 객체를 다르다고 판단해도 다른 `hashCode`를 반환할 필요는 없다.
- 하지만 다른 객체에 대해서는 다른 `hashCode` 값을 반환하는 것이 좋습니다. 이렇게 하면 해시 테이블(HashMap 등)의 성능이 더 좋아집니다.

## `hashCode`와 `equals`에 관련된 문제 예시

### 예시 코드:

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber((short) 707, (short) 867, (short) 5309), "제니");
//hashCode 를 재정의 하지 않아 다른 객체로 판단
System.out.println(m.get(new PhoneNumber((short) 707, (short) 867, (short) 5309)));
```
## 전형적인 hashCode 작성 예시

```java
@Override
public int hashCode() {
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}

```

### hashCode 작성시 참고할 점
- 가독성 + 명확하고 일관된 해시 코드 생성을 위해 박싱 클래스의 `hashCode()` 사용
- 참조형일 경우 `null`이면 0을 사용해서 계산하고, 계산이 복잡해질 것 같으면 필드의 표준형을 만들어 사용하는 것이 좋습니다.
- **표준형**:
    - `Hello`와 `hello` 대소문자 구별 없이 동일하게 처리하거나, 배열의 경우 `[a, c, b]` -> `[a, b, c]`로 정렬된 기준을 따릅니다.
- 필드가 배열일 경우 핵심 원소 각각을 별도의 필드처럼 다루며, 배열에 핵심 원소가 없을 경우 상수 `0`을 처리하는 방식이 권장됩니다.
- 배열에 핵심 원소가 단 하나도 존재하지 않는다면 `Arrays.hashCode`를 사용합니다.
- `Objects` 클래스에서 제공하는 `hashCode` 메서드는 성능이 그렇게 좋지 않으므로 성능에 민감하지 않을 때만 사용하는 것이 좋습니다.


## 해시코드의 지연 초기화
지연 초기화는 계산이 필요한 시점까지 계산을 지연시켰다가 계산후 이후에 계산된 값을 재사용 가능함
하지만 불면 객체가 아닐 경우 멀티 스레드 환경에서 문제가 발생할 수 있음.

```java
@Override
public int hashCode() {
    int result = hashCode;
    if (result == 0) {
        result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        hashCode = result;
    }
    return result;
}

```
