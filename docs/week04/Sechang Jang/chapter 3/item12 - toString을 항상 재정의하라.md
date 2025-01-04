# Item 12: toString을 항상 재정의하라

## 핵심 개념 (Main Ideas)

### 1. toString 재정의의 중요성
- **정의**: 객체의 주요 정보를 문자열로 제공하는 메서드
- **목적**: 디버깅과 로깅을 용이하게 하고 객체의 상태를 명확히 표현
- **효과**: 클래스 사용자에게 더 나은 사용자 경험 제공

### 2. 기본 toString의 한계
- **원칙**: Object의 기본 toString은 대부분의 경우 충분한 정보를 제공하지 못함
- **이유**: 클래스_이름@16진수해시코드 형태는 실제 사용에 도움이 되지 않음
- **방법**: 객체의 중요 정보를 포함하도록 재정의

## 세부 내용 (Details)

### 1. 잘못된 toString 구현의 예와 문제점

#### 기본 toString 사용 시의 문제
```java
public class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    
    // toString 재정의하지 않음
    // 결과: PhoneNumber@163b91
}

// 사용 예시
Map<PhoneNumber, String> map = new HashMap<>();
map.put(new PhoneNumber(707, 867, 5309), "제니");
System.out.println(map);  
// 출력: {PhoneNumber@163b91=제니}
```

**이 코드의 문제점**:
1. **가독성 부족**
   - 의미 있는 정보를 제공하지 않음
   - 디버깅이 어려움
   - 로그 분석이 힘듦

2. **사용성 저하**
   - 객체의 상태를 파악하기 어려움
   - 데이터 검증이 어려움
   - 실수하기 쉬운 코드가 됨

### 2. 올바른 toString 구현 방법

#### 포맷 명시 버전
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    /**
     * 이 전화번호의 문자열 표현을 반환합니다.
     * 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성됩니다.
     * XXX는 지역 코드, YYY는 프리픽스, ZZZZ는 가입자 번호입니다.
     * 각 필드는 해당 자리 수만큼 0으로 패딩됩니다.
     */
    @Override 
    public String toString() {
        return String.format("%03d-%03d-%04d",
                areaCode, prefix, lineNum);
    }

    // toString이 반환하는 값의 각 필드를 얻을 수 있는 접근자 제공
    public short getAreaCode() { return areaCode; }
    public short getPrefix() { return prefix; }
    public short getLineNum() { return lineNum; }
}
```

**구현의 장점**:
1. **명확한 계약**
   - 포맷이 명시되어 있어 파싱이 가능
   - 문서화가 잘 되어 있음
   - 접근자 메서드 제공

2. **단점과 주의사항**
   - 포맷 변경의 유연성이 떨어짐
   - 클라이언트가 이 포맷에 의존할 수 있음

#### 포맷 미명시 버전
```java
public final class ComplexNumber {
    private final double re;
    private final double im;

    /**
     * 이 복소수의 문자열 표현을 반환합니다.
     * 반환된 문자열은 이 복소수를 파악하기 쉬운 형태이나,
     * 정확한 포맷은 명시되지 않았으며 향후 변경될 수 있습니다.
     */
    @Override 
    public String toString() {
        // 실수부가 0이 아닌 경우
        if (re != 0) {
            return im == 0 ? String.valueOf(re) :
                   im > 0 ? String.format("%.2f+%.2fi", re, im) :
                   String.format("%.2f%.2fi", re, im);
        }
        // 실수부가 0인 경우
        return im == 0 ? "0" : String.format("%.2fi", im);
    }

    // 필요한 접근자 메서드들
    public double realPart() { return re; }
    public double imaginaryPart() { return im; }
}
```

**이 구현의 특징**:
1. **유연성**
   - 포맷 변경이 자유로움
   - 향후 개선의 여지가 있음
   - 클라이언트 의존성 감소

2. **사용성**
   - 읽기 쉬운 형태 제공
   - 불필요한 정보 생략
   - 상황에 따른 최적화된 출력

### 3. 특별한 상황에서의 toString

#### 집합 클래스의 toString
```java
public class PowerSet<E> {
    private final Set<E> elements;
    private final Set<Set<E>> powerSet;

    /**
     * 이 멱집합의 문자열 표현을 반환합니다.
     * 각 부분집합은 중괄호로 둘러싸여 있으며,
     * 전체 멱집합도 중괄호로 둘러싸여 있습니다.
     */
    @Override
    public String toString() {
        if (powerSet.isEmpty()) return "{}";
        
        StringBuilder result = new StringBuilder("{");
        for (Set<E> set : powerSet) {
            result.append(set).append(", ");
        }
        // 마지막 ", " 제거 후 닫는 중괄호 추가
        return result.substring(0, result.length() - 2) + "}";
    }
}
```

**구현 포인트**:
1. **가독성 고려**
   - 구조적인 표현 사용
   - 불필요한 정보 제외
   - 일관된 형식 유지

## 자주 발생하는 질문과 답변

Q: toString이 반환한 값을 파싱해서 사용해도 될까요?
A: 권장하지 않습니다. 대신 적절한 접근자를 제공하세요:
```java
// 잘못된 사용 예
public class Point {
    private final int x;
    private final int y;
    
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}

// 클라이언트 코드 - 이렇게 하지 마세요!
String pointString = point.toString();
String[] coordinates = pointString
    .substring(1, pointString.length() - 1)
    .split(",");
int x = Integer.parseInt(coordinates[0].trim());
int y = Integer.parseInt(coordinates[1].trim());

// 올바른 구현
public class Point {
    private final int x;
    private final int y;
    
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
    
    // 접근자 메서드 제공
    public int getX() { return x; }
    public int getY() { return y; }
}

// 클라이언트 코드
int x = point.getX();
int y = point.getY();
```

Q: 상위 클래스의 toString을 재사용하는 것이 좋을까요?
A: 상황에 따라 다릅니다:
```java
// 상위 클래스의 toString이 적절한 경우
public class ColorPoint extends Point {
    private final Color color;
    
    @Override
    public String toString() {
        return String.format("%s in %s", 
            super.toString(),  // Point의 toString 재사용
            color);
    }
}

// 새로 구현하는 것이 나은 경우
public class Employee {
    private final String name;
    private final int id;
    
    @Override
    public String toString() {
        // 상위 클래스(Object)의 toString은 사용하지 않음
        return String.format("Employee[name=%s, id=%d]", 
            name, id);
    }
}
```

Q: 성능에 민감한 경우 어떻게 해야 할까요?
A: 지연 초기화를 고려해보세요:
```java
public final class RichContent {
    private final byte[] data;
    private String stringForm;  // 캐시된 toString 값
    
    @Override
    public synchronized String toString() {
        if (stringForm == null) {
            // 비용이 큰 toString 연산
            stringForm = computeString();
        }
        return stringForm;
    }
    
    private String computeString() {
        // 복잡한 문자열 생성 로직
        StringBuilder result = new StringBuilder();
        for (byte b : data) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

### 4. 실무에서의 toString 활용

#### 로깅과 디버깅
```java
public class OrderProcessor {
    private final Order order;
    private ProcessingStatus status;
    
    @Override
    public String toString() {
        return String.format("OrderProcessor[order=%s, status=%s, " +
            "processedAt=%s]", 
            order.getId(),  // 핵심 식별 정보만
            status,
            LocalDateTime.now());
    }
    
    public void process() {
        try {
            // 주문 처리 로직
            logger.info("Processing order: {}", this);
        } catch (Exception e) {
            // toString의 결과가 로그에 포함됨
            logger.error("Failed to process order: {}", this, e);
        }
    }
}
```

**로깅 시 고려사항**:
1. **정보의 적절성**
   - 디버깅에 유용한 정보 포함
   - 민감한 정보 제외
   - 간단명료한 형식 사용

2. **성능 고려**
   - 대용량 데이터는 요약 정보만 포함
   - 필요한 경우에만 상세 정보 포함
   - 로깅 레벨에 따른 차별화

#### IDE 자동 생성 코드 활용과 주의점
```java
// IDE가 생성한 기본 toString
@Override
public String toString() {
    return "Customer{" +
           "id=" + id +
           ", name='" + name + '\'' +
           ", email='" + email + '\'' +
           '}';
}

// 개선된 버전
@Override
public String toString() {
    // 필요한 정보만 선택적으로 포함
    return String.format("Customer[%d: %s]", id, name);
}
```

**IDE 생성 코드의 특징**:
1. **장점**
   - 모든 필드를 포함한 기본 구현 제공
   - 일관된 형식 유지
   - 누락 없는 구현

2. **단점**
   - 불필요한 정보까지 모두 포함
   - 가독성이 떨어질 수 있음
   - 성능 고려가 없음

## 요약 (Summary)

1. **toString 재정의 원칙**
   - 모든 중요 정보 포함
   - 읽기 쉽고 이해하기 쉬운 형식 사용
   - 포맷 명시 여부를 신중히 결정

2. **구현 가이드라인**
   - 문서화를 통한 명확한 의도 전달
   - 적절한 접근자 메서드 함께 제공
   - 상황에 맞는 구현 방식 선택

3. **실무 적용 포인트**
   - 로깅과 디버깅을 고려한 설계
   - 성능과 사용성의 균형
   - 유지보수성 고려