# Item 10: equals는 일반 규약을 지켜 재정의하라

## 핵심 개념 (Main Ideas)

### 1. equals 메서드의 재정의 기준
- **정의**: Object.equals의 동작을 클래스 특성에 맞게 재정의하는 것
- **목적**: 객체의 논리적 동치성을 확인하기 위해
- **효과**: 컬렉션 클래스들을 포함한 수많은 클래스들이 정확히 동작하게 함

### 2. equals 재정의가 필요 없는 경우
- **원칙**: 다음 상황에서는 재정의하지 않는 것이 최선
- **이유**: 기본 equals가 클래스의 특성에 맞는 동작을 이미 구현
- **방법**: Object의 equals를 그대로 사용

## 세부 내용 (Details)

### 1. equals 재정의가 필요하지 않은 상황들

#### 각 인스턴스가 본질적으로 고유한 경우
```java
public class Thread {
    @Override
    public boolean equals(Object obj) {
        return this == obj;  // Object equals 그대로 사용
    }
}
```

**이 상황의 설명**:
1. **본질적 고유성**
   - 쓰레드와 같이 동작하는 개체를 표현
   - 값이 아닌 동작이 중요한 클래스
   - 인스턴스 자체의 고유성이 의미있는 경우

#### 상위 클래스의 equals가 하위 클래스에 적합한 경우
```java
public class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {
    // AbstractCollection의 equals를 상속
    // Set의 특성상 원소들의 순서가 상관없기 때문에 적합
}

public class LinkedHashSet<E> extends HashSet<E> {
    // HashSet의 equals를 상속
    // 순서가 보장되어야 하지만 equals 비교에서는 무관
}
```

**이 상황의 설명**:
1. **상속의 적절성**
   - 상위 클래스의 equals가 하위 클래스의 동치성 개념과 일치
   - 불필요한 코드 중복을 피함
   - Set, List, Map의 구현체들이 대표적

### 2. equals 재정의가 필요한 상황

#### 값 클래스의 경우
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix, 999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    @Override 
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum 
            && pn.prefix == prefix 
            && pn.areaCode == areaCode;
    }

    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }
}
```

**이 구현의 중요 포인트**:
1. **값 비교의 필요성**
   - 객체가 값을 표현하는 경우
   - 두 객체의 내용이 같은지 비교해야 하는 경우
   - 식별성(identity)이 아닌 동치성(equality) 검사가 필요한 경우

2. **구현 세부사항**
   - 성능 최적화를 위한 자기 자신 검사
   - instanceof 연산자로 타입 검사
   - 각 필드의 동치성 검사

### 3. equals 메서드의 일반 규약 준수

#### 대칭성 위반의 예와 해결
```java
// 대칭성을 위반하는 잘못된 코드
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 잘못된 equals 구현 - 대칭성 위반!
    @Override public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString)
            return s.equalsIgnoreCase(
                ((CaseInsensitiveString) o).s);
        if (o instanceof String)  // 한 방향으로만 작동!
            return s.equalsIgnoreCase((String) o);
        return false;
    }
}

// 실제 문제 발생 코드
CaseInsensitiveString cis = new CaseInsensitiveString("Hello");
String s = "hello";
cis.equals(s);    // true
s.equals(cis);    // false  // 대칭성 위반!

// 올바른 구현
public final class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성을 지키는 equals 구현
    @Override public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString &&
            ((CaseInsensitiveString) o).s.equalsIgnoreCase(s);
    }
}
```

**이 코드가 설명하려는 것**:
1. **대칭성의 중요성**
   - equals 관계는 양방향으로 동일해야 함
   - 다른 타입과의 동치성 비교는 주의가 필요
   - 한쪽만 true를 반환하면 심각한 문제 발생

#### 추이성 위반의 예와 해결
```java
// 추이성을 위반할 수 있는 코드
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point)o;
        return p.x == x && p.y == y;
    }
}

// 잘못된 상속 구현
public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    // 잘못된 equals 구현 - 추이성 위반!
    @Override public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return o != null && o instanceof Point && 
                super.equals(o);
        ColorPoint cp = (ColorPoint)o;
        return super.equals(o) && cp.color == color;
    }
}

// 올바른 해결책 - 컴포지션 사용
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

    @Override public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        ColorPoint cp = (ColorPoint)o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}
```

**이 설계가 보여주는 것**:
1. **상속의 문제점**
   - 리스코프 치환 원칙 위반 가능성
   - equals의 추이성을 깨뜨릴 수 있음
   - 타입을 확인하는 로직이 복잡해짐

2. **컴포지션의 장점**
   - 추이성 보장
   - 타입 안전성 확보
   - 더 명확한 설계

### 4. 고품질 equals 메서드 구현 방법

#### equals 구현의 단계별 레시피
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    @Override public boolean equals(Object o) {
        // 1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인
        if (o == this)
            return true;

        // 2. instanceof 연산자로 입력이 올바른 타입인지 확인
        if (!(o instanceof PhoneNumber))
            return false;

        // 3. 입력을 올바른 타입으로 형변환
        PhoneNumber pn = (PhoneNumber)o;

        // 4. 핵심 필드들이 모두 일치하는지 하나씩 검사
        return pn.lineNum == lineNum 
            && pn.prefix == prefix 
            && pn.areaCode == areaCode;
    }
}
```

**구현 상세 지침**:
1. **float와 double 필드**
   ```java
   // float와 double은 특별 처리 필요
   public class Coordinate {
       private final double x;
       private final double y;

       @Override public boolean equals(Object o) {
           if (o == this)
               return true;
           if (!(o instanceof Coordinate))
               return false;
           Coordinate c = (Coordinate)o;
           // Float.compare나 Double.compare 사용
           return Double.compare(c.x, x) == 0
               && Double.compare(c.y, y) == 0;
       }
   }
   ```

2. **배열 필드**
   ```java
   public class Matrix {
       private final int[][] matrix;

       @Override public boolean equals(Object o) {
           if (o == this)
               return true;
           if (!(o instanceof Matrix))
               return false;
           Matrix m = (Matrix)o;
           // Arrays.deepEquals 사용
           return Arrays.deepEquals(matrix, m.matrix);
       }
   }
   ```

## 자주 발생하는 질문과 답변

Q: equals를 재정의할 때 hashCode도 반드시 재정의해야 하나요?
A: 네, 반드시 해야 합니다:
```java
public final class Point {
    private final int x;
    private final int y;

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;
        Point p = (Point)o;
        return p.x == x && p.y == y;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y);  // equals와 일관되게 구현
    }
}
```

Q: Object.equals를 재정의하지 않고 다른 equals 메서드를 추가하면 안되나요?
A: 절대 하면 안됩니다:
```java
// 잘못된 구현 - 재정의가 아닌 다중정의
public class Point {
    private final int x;
    private final int y;

    // 다음과 같은 equals 메서드는 작성하지 말 것!
    public boolean equals(Point p) {  // Object.equals를 재정의한 게 아님!
        if (p == null)
            return false;
        return p.x == x && p.y == y;
    }
}
```

## 요약 (Summary)

1. **equals 재정의 규칙**
   - Object.equals의 규약을 준수
   - 일반 규약(반사성, 대칭성, 추이성, 일관성)을 지킬 것
   - hashCode도 반드시 재정의

2. **구현 시 고려사항**
   - null-아님 규칙 준수
   - 각 필드의 특성에 맞는 비교 방법 사용
   - 필요한 경우에만 재정의

3. **실무 적용 가이드**
   - 자신만의 값 타입이 필요할 때만 재정의
   - AutoValue 프레임워크 활용 고려
   - 꼭 필요한 경우가 아니면 재정의하지 말 것