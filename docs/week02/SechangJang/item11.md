# Item 11: equals를 재정의하려거든 hashCode도 재정의하라

## hashCode 재정의가 필요한 이유

equals를 재정의한 클래스에서 hashCode도 재정의하지 않으면 다음과 같은 Object 명세의 일반 규약을 어기게 됩니다:

1. equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 같은 값을 반환해야 합니다.
2. **equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 합니다.**
3. equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없습니다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아집니다.

## 해시 충돌 문제의 예시

다음과 같은 전화번호부 예제를 통해 hashCode를 재정의하지 않았을 때의 문제점을 살펴보겠습니다:

```java
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(707, 867, 5309), "제니");
m.get(new PhoneNumber(707, 867, 5309)); // null 반환
```

이 코드에서 get 메서드가 null을 반환하는 이유는 PhoneNumber 클래스가 hashCode를 재정의하지 않았기 때문입니다.

## 좋은 해시 함수 작성 방법

좋은 해시 함수는 서로 다른 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 합니다.

### 해시 코드 계산 단계

1. int 변수 result를 선언하고 첫 번째 핵심 필드의 해시코드로 초기화
2. 나머지 핵심 필드들에 대해 다음을 수행:
   - 해당 필드의 해시코드 c 계산
   - result = 31 * result + c

### 필드별 해시코드 계산 방법

- 기본 타입: Type.hashCode(f) 사용
- 참조 타입: 해당 필드의 hashCode 호출
- 배열: Arrays.hashCode 사용
- null 참조: 0 사용

## 주의사항

1. equals에서 사용하지 않는 필드는 hashCode 계산에서 제외
2. 성능향상을 위해 핵심 필드를 생략하면 안 됨
3. hashCode 생성 규칙을 API 사용자에게 자세히 공표하지 말 것
   - 클라이언트가 이 값에 의지하지 않게 하고 추후에 계산 방식을 바꿀 수 있도록 함

## 지연 초기화(Lazy Initialization)

해시코드를 계산하는 비용이 크고 사용 빈도가 낮다면 지연 초기화 전략을 고려해볼 수 있습니다:

```java
private int hashCode; // 자동으로 0으로 초기화

@Override public int hashCode() {
    if (hashCode == 0) {
        hashCode = compute(); // 실제 해시코드 계산
    }
    return hashCode;
}
```

단, 이 경우 스레드 안전성을 고려해야 합니다.