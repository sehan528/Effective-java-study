# Item 11: equals를 재정의하려거든 hashCode도 재정의하라

## 핵심 개념 (Main Ideas)

### 1. hashCode 재정의의 필요성
- **정의**: equals와 일관되게 동작하는 해시 코드 생성 메서드
- **목적**: 해시 기반 컬렉션에서 객체를 효율적으로 사용하기 위함
- **효과**: HashMap, HashSet 등에서 객체를 신뢰성 있게 처리 가능

### 2. hashCode 규약
- **원칙**: equals 비교에 사용되는 정보가 변경되지 않았다면 hashCode 값도 일관되게 유지
- **이유**: 해시 기반 컬렉션의 정상적인 작동을 보장
- **방법**: equals에서 사용하는 필드들을 모두 포함하여 해시 코드 생성

## 세부 내용 (Details)

### 1. 잘못된 hashCode 구현의 문제점

#### 기본 Object.hashCode()만 사용할 경우
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix   = rangeCheck(prefix, 999, "prefix");
        this.lineNum  = rangeCheck(lineNum, 9999, "line num");
    }

    // equals만 재정의하고 hashCode는 재정의하지 않은 경우
    @Override public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }
    // hashCode 재정의 하지 않음
}

// 문제가 발생하는 코드
Map<PhoneNumber, String> m = new HashMap<>();
m.put(new PhoneNumber(707, 867, 5309), "제니");
System.out.println(m.get(new PhoneNumber(707, 867, 5309))); // null 반환!
```

**이 코드가 설명하려는 것**:
1. **해시 기반 컬렉션의 문제**
   - 논리적으로 동일한 객체임에도 다른 해시코드 반환
   - HashMap에서 객체를 찾지 못함
   - 기대와 다른 동작 발생

2. **원인 분석**
   - Object.hashCode는 객체의 메모리 주소 기반으로 해시코드 생성
   - equals로는 같다고 판단되는 객체들이 다른 해시코드를 가짐
   - HashMap의 내부 동작 방식과 충돌

### 2. 올바른 hashCode 구현 방법

#### 좋은 해시 함수 구현
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    @Override public int hashCode() {
        // 1. 초기값 계산
        int result = Short.hashCode(areaCode);
        
        // 2. 각 필드를 31과 곱하여 결합
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        
        return result;
    }

    // Objects.hash를 사용한 간단한 구현
    // (성능이 중요하지 않은 경우)
    @Override public int hashCode() {
        return Objects.hash(areaCode, prefix, lineNum);
    }
}
```

**구현 상세 설명**:
1. **31을 곱하는 이유**
   - 홀수이면서 소수인 수
   - 전통적으로 사용되어 왔으며, JVM이 최적화 가능
   - 오버플로우 시에도 정보 손실이 적음

2. **필드 결합 순서**
   - equals에서 먼저 비교하는 필드부터 포함
   - 성능에 민감한 경우 중요한 필드부터 포함
   - 모든 필드를 빠짐없이 포함

### 3. 성능 최적화 기법

#### 해시코드 캐싱
```java
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    
    // 해시코드를 지연 초기화하는 캐시 필드
    private int hashCode;  // 자동으로 0으로 초기화

    @Override public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Short.hashCode(areaCode);
            result = 31 * result + Short.hashCode(prefix);
            result = 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }
}

// 스레드 안전한 버전
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    
    // volatile로 선언하여 스레드 안전성 확보
    private volatile int hashCode;

    @Override public synchronized int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Short.hashCode(areaCode);
            result = 31 * result + Short.hashCode(prefix);
            result = 31 * result + Short.hashCode(lineNum);
            hashCode = result;
        }
        return result;
    }
}
```

**최적화 기법의 장단점**:
1. **장점**
   - 비용이 큰 해시코드 계산을 한 번만 수행
   - 자주 호출되는 경우 성능 향상
   - 불변 객체에 특히 유용

2. **단점**
   - 추가 메모리 사용
   - 동기화 비용 발생 가능
   - 구현이 더 복잡해짐

## 자주 발생하는 질문과 답변

Q: hashCode를 구현할 때 모든 필드를 반드시 사용해야 하나요?
A: equals에서 사용하는 필드는 반드시 포함해야 합니다:
```java
public final class Employee {
    private final String name;
    private final int id;
    private String department;  // equals에서 비교하지 않는 필드

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee))
            return false;
        Employee e = (Employee) o;
        // department는 비교하지 않음
        return e.id == id && Objects.equals(e.name, name);
    }

    @Override
    public int hashCode() {
        // equals에서 사용한 필드만 포함
        return Objects.hash(name, id);
        // department는 포함하지 않음
    }
}
```

Q: 성능상의 이유로 일부 필드를 hashCode 계산에서 제외해도 될까요?
A: 권장하지 않습니다. 다음 예시를 보세요:
```java
public final class MedicalRecord {
    private final String patientId;
    private final String diagnosis;
    private final byte[] medicalImage;  // 큰 데이터

    // 잘못된 구현 - 성능을 위해 큰 필드 제외
    @Override
    public int hashCode() {
        return Objects.hash(patientId);  // diagnosis와 medicalImage 제외
    }

    // 올바른 구현
    @Override
    public int hashCode() {
        int result = Objects.hash(patientId, diagnosis);
        result = 31 * result + Arrays.hashCode(medicalImage);
        return result;
    }
}
```

Q: hashCode의 구체적인 계산 방식을 문서화해야 할까요?
A: 하지 말아야 합니다. 구현의 유연성을 위해 다음과 같이 작성하세요:
```java
/**
 * 이 클래스의 hashCode 메서드는 Object.hashCode 규약을 따릅니다.
 * 구체적인 계산 방식은 구현상의 세부사항이며 향후 변경될 수 있습니다.
 */
@Override 
public int hashCode() {
    // 구체적인 구현
    return Objects.hash(field1, field2);
}
```

### 4. 특별한 상황에서의 hashCode 구현

#### 불변 객체의 해시코드 캐싱
```java
public final class BigObject {
    private final byte[] hugeArray;
    private final String name;
    
    // 해시코드 캐시
    private int hashCode;
    
    // 스레드 안전한 지연 초기화
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            synchronized(this) {
                result = hashCode;
                if (result == 0) {
                    result = Objects.hash(name);
                    result = 31 * result + Arrays.hashCode(hugeArray);
                    hashCode = result;
                }
            }
        }
        return result;
    }
}
```

**구현 설명**:
1. **이중 검사 패턴 사용**
   - 초기화되지 않은 경우에만 동기화
   - 불필요한 동기화 오버헤드 방지
   - 스레드 안전성 보장

2. **성능 고려사항**
   - 계산 비용이 큰 경우에만 캐싱 고려
   - 메모리와 성능의 트레이드오프 고려
   - 사용 빈도에 따른 최적화

#### 상속을 고려한 hashCode 구현
```java
public class Parent {
    private final String parentField;
    
    @Override
    public int hashCode() {
        return Objects.hash(parentField);
    }
}

public class Child extends Parent {
    private final String childField;
    
    @Override
    public int hashCode() {
        // 상위 클래스의 hashCode를 포함
        return 31 * super.hashCode() + Objects.hash(childField);
    }
}
```

**중요 고려사항**:
1. **계층 구조의 일관성**
   - 상위 클래스의 hashCode 활용
   - 모든 관련 필드 포함
   - equals와의 일관성 유지

## 요약 (Summary)

1. **핵심 원칙**
   - equals를 재정의한 클래스는 반드시 hashCode도 재정의
   - equals에서 사용하는 필드는 모두 hashCode 계산에 포함
   - hashCode 규약을 반드시 준수

2. **구현 가이드라인**
   - 성능에 민감하지 않다면 Objects.hash 사용
   - 성능이 중요하면 직접 계산 로직 구현
   - 필요한 경우에만 해시코드 캐싱 적용

3. **실무 적용 포인트**
   - IDE나 라이브러리가 제공하는 도구 활용
   - 해시 기반 컬렉션 사용 시 특히 주의
   - 구현 세부사항 문서화하지 않기