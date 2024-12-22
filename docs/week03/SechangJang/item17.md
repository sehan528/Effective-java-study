# Item 17: 변경 가능성을 최소화하라

## 불변 클래스란?
인스턴스의 내부 값을 수정할 수 없는 클래스입니다. 불변 인스턴스는 생성 시점부터 파괴 시점까지 내부 값이 동일하게 유지됩니다.

## 불변 클래스의 대표적인 예
- String
- 기본 타입의 박싱된 클래스들 (Integer, Double 등)
- BigInteger
- BigDecimal

## 불변 클래스를 만드는 다섯 가지 규칙

1. **객체의 상태를 변경하는 메서드를 제공하지 않는다**
   - setter 메서드 제공하지 않음
   - 기타 수정 메서드 제공하지 않음

2. **클래스를 확장할 수 없도록 한다**
   - 클래스를 final로 선언
   - 모든 생성자를 private으로 만들고 정적 팩터리 메서드 제공

3. **모든 필드를 final로 선언한다**
   - 시스템이 강제하는 수단을 통해 설계자의 의도 명확히 전달
   - 멀티스레드 환경에서 동기화 없이 객체 공유 가능

4. **모든 필드를 private으로 선언한다**
   - 필드가 참조하는 가변 객체를 직접 접근하여 수정하는 일을 방지
   - 불변 객체라도 private이 아니면 다음 릴리스에서 변경이 어려움

5. **자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다**
   ```java
   public final class User {
       private final List<AddressInfo> addressInfoList;
       
       // 가변 컴포넌트인 주소 정보를 숨김
       public List<String> getAddressList() {
           return addressInfoList.stream()
               .map(AddressInfo::getAddress)
               .collect(Collectors.toList());
       }
   }
   ```

## 불변 클래스의 장점

### 1. 스레드 안전성
- 여러 스레드가 동시에 사용해도 훼손되지 않음
- 동기화가 필요 없음
- 안전하게 공유 가능

### 2. 실패 원자성 제공
- 메서드에서 예외가 발생해도 유효한 상태 유지
- 불변식을 보장

### 3. 캐싱과 재사용 용이
```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE  = new Complex(1, 0);
public static final Complex I    = new Complex(0, 1);
```

### 4. 방어적 복사가 필요 없음
- 원본과 복사본이 같으므로 복사 자체가 의미 없음
- clone() 메서드나 복사 생성자가 필요 없음

## 불변 클래스의 단점과 해결 방법

### 단점
- 값이 다르면 반드시 별도의 객체 생성 필요
- 원하는 객체를 완성하기까지 여러 단계 필요시 중간 객체가 많이 생성됨

### 해결 방법

1. **가변 동반 클래스 제공**
   ```java
   // 불변 클래스
   public final class String { ... }
   
   // 가변 동반 클래스
   public final class StringBuilder { ... }
   ```

2. **다단계 연산들을 예측하여 기본 기능으로 제공**
   - 흔한 다단계 연산을 기본 기능으로 제공
   - 각 단계마다 객체를 생성하지 않아도 됨

## 불변 클래스 설계 예시 (Value Object)

```java
// 가변 엔티티
@Entity
public class Person {
    @Id
    private Long id;
    private String name;
    private float height;
    // getter, setter
}

// 불변 VO
@Getter
public class PersonVo {
    private final String name;
    private final float height;
    
    private PersonVo(String name, float height) {
        this.name = name;
        this.height = height;
    }
    
    public static PersonVo from(Person p) {
        return new PersonVo(p.getName(), p.getHeight());
    }
}
```

## 핵심 정리
1. 클래스는 꼭 필요한 경우가 아니면 불변이어야 함
2. getter가 있다고 해서 무조건 setter를 만들지 말 것
3. 불변으로 만들 수 없는 클래스라도 변경 가능성을 최소화할 것
4. 특별한 이유가 없다면 모든 필드는 private final로 선언
5. 생성자는 불변식 설정이 모두 완료된 상태의 객체를 생성해야 함