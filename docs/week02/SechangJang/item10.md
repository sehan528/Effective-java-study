# Item 10: equals는 일반 규약을 지켜 재정의하라

> equals 메서드는 재정의하기 쉬워 보이지만 함정이 많다. 문제를 회피하는 가장 쉬운 방법은 아예 재정의하지 않는 것이다.

## equals를 재정의하지 않아도 되는 경우

다음 상황 중 하나에 해당한다면 equals를 재정의하지 않는 것이 최선이다:

1. **각 인스턴스가 본질적으로 고유한 경우**
   - 값을 표현하는 것이 아닌 동작하는 개체를 표현하는 클래스 (예: Thread)

2. **논리적 동치성을 검사할 필요가 없는 경우**
   - java.util.regex.Pattern은 equals를 재정의하지 않았다
   
3. **상위 클래스에서 재정의한 equals가 하위 클래스에도 적합한 경우**
   - 대부분의 Set 구현체는 AbstractSet이 구현한 equals를 상속
   - List 구현체들은 AbstractList로부터, Map 구현체들은 AbstractMap으로부터 상속

4. **클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없는 경우**
   ```java
   @Override
   public boolean equals(Object o) {
       throw new AssertionError(); // equals 호출 금지
   }
   ```

## equals를 재정의해야 하는 경우

**객체 식별성(object identity)이 아닌 논리적 동치성을 확인해야 하는 경우**
- 주로 값 클래스들이 해당됨 (Integer, String 등)
- 값이 같은지 판단할 필요가 있는 클래스
- Enum의 경우 논리적 동치성과 객체 식별성이 같으므로 equals를 재정의할 필요 없음

## equals 메서드 재정의 시 따라야 할 일반 규약

equals 메서드는 동치관계(equivalence relation)를 구현하며, 다음을 만족해야 한다:

### 1. 반사성(reflexivity)
- `x.equals(x) == true`
- 객체는 자기 자신과 같아야 함

### 2. 대칭성(symmetry)
- `x.equals(y) == true`이면 `y.equals(x) == true`
- 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 함

예시: 대칭성을 위반하는 케이스
```java
public final class CaseInsensitiveString {
    private final String s;
    
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String)  // 한 방향으로만 작동!
            return s.equalsIgnoreCase((String) o);
        return false;
    }
}
```

### 3. 추이성(transitivity)
- `x.equals(y) == true`이고 `y.equals(z) == true`이면 `x.equals(z) == true`
- 첫 번째 객체와 두 번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면, 첫 번째 객체와 세 번째 객체도 같아야 함

추이성을 지키면서 값을 추가하는 방법: 상속 대신 컴포지션 사용
```java
public class ColorPoint {
    private final Point point;
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    // 뷰 메서드
    public Point asPoint() {
        return point;
    }

    @Override 
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}
```

### 4. 일관성(consistency)
- 두 객체가 같다면 수정되지 않는 한 앞으로도 영원히 같아야 함
- equals의 판단에 신뢰할 수 없는 자원이 끼어들어서는 안 됨
- 예: java.net.URL의 equals는 주어진 URL과 매핑된 호스트의 IP 주소를 이용해 비교하는데, 이는 네트워크 상태에 따라 달라질 수 있음

### 5. null-아님
- `x.equals(null) == false`
- 모든 객체가 null과 같지 않아야 함

## equals 메서드 구현 방법

1. == 연산자를 사용해 자기 자신의 참조인지 확인
2. instanceof 연산자로 입력이 올바른 타입인지 확인
3. 입력을 올바른 타입으로 형변환
4. 핵심 필드들이 모두 일치하는지 검사

### 필드 비교 시 주의사항

- 기본 타입: == 연산자 사용
- 참조 타입: equals 메서드로 비교
- float, double: Float.compare(float, float), Double.compare(double, double) 사용
- 배열: Arrays.equals 사용
- null 값 가능성: Objects.equals(object, object) 사용

## 주의사항

1. equals를 재정의할 때는 hashCode도 반드시 재정의할 것
2. 너무 복잡하게 해결하지 말 것
3. Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말 것
   ```java
   // 잘못된 예
   public boolean equals(MyClass o)
   ```

## 정리

equals 메서드를 재정의해야 할 때는 일반 규약을 확실히 지켜가며 비교해야 한다. 웬만하면 재정의하지 않는 것이 더 안전하며, 재정의해야 한다면 핵심 필드들의 동치성만 검사하도록 구현해야 한다.