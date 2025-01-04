# Item 14: Comparable을 구현할지 고려하라

## 핵심 개념 (Main Ideas)

### 1. Comparable 인터페이스의 의미
- **정의**: 객체 간의 순서를 정의하는 인터페이스
- **목적**: 객체의 자연적 순서(natural ordering) 제공
- **효과**: 검색, 극단값 계산, 자동 정렬 등이 가능해짐

### 2. compareTo 메서드의 특징
- **원칙**: 객체의 순서를 비교하는 메서드
- **이유**: 컬렉션의 정렬과 검색에 활용
- **방법**: 두 객체를 비교하여 정수값 반환

## 세부 내용 (Details)

### 1. compareTo 메서드 구현 방법

#### 기본적인 구현 - 단일 필드 비교
```java
public final class CaseInsensitiveString 
        implements Comparable<CaseInsensitiveString> {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 단순한 compareTo 구현
    @Override
    public int compareTo(CaseInsensitiveString cis) {
        return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
    }
}
```

**이 구현이 설명하려는 것**:
1. **타입 안전성**
   - 제네릭을 사용하여 타입 안전성 보장
   - 컴파일타임에 타입 오류 검출
   - 불필요한 형변환 제거

2. **단순성**
   - 단일 필드 비교는 직접 비교 가능
   - 기존 비교자 활용
   - 코드가 간결하고 명확함

#### 다중 필드 비교 - 기본 방식
```java
public final class PhoneNumber implements Comparable<PhoneNumber> {
    private final short areaCode, prefix, lineNum;

    @Override
    public int compareTo(PhoneNumber pn) {
        // 가장 중요한 필드부터 비교
        int result = Short.compare(areaCode, pn.areaCode);
        if (result == 0) {
            result = Short.compare(prefix, pn.prefix);
            if (result == 0) {
                result = Short.compare(lineNum, pn.lineNum);
            }
        }
        return result;
    }
}
```

**구현의 특징**:
1. **중요도 순서**
   - 가장 중요한 필드부터 비교
   - 필요한 경우에만 다음 필드 비교
   - 성능 최적화 가능

2. **primitive 타입 비교**
   - 박싱된 기본 타입의 compare 메서드 사용
   - 오버플로우 방지
   - 안전한 비교 보장

#### Comparator를 활용한 현대적인 구현
```java
public final class PhoneNumber implements Comparable<PhoneNumber> {
    private static final Comparator<PhoneNumber> COMPARATOR = 
        Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
                  .thenComparingInt(pn -> pn.prefix)
                  .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
}
```

**이 방식의 장점**:
1. **가독성**
   - 비교 로직이 명확히 드러남
   - 메서드 체이닝으로 읽기 쉬움
   - 유지보수가 용이

2. **성능**
   - 상수 Comparator로 인스턴스 생성 비용 절감
   - 메서드 참조로 간결한 코드
   - 최적화된 비교 연산

### 2. compareTo 규약 준수의 예시

#### equals와의 일관성
```java
public final class Money implements Comparable<Money> {
    private final long amount;
    private final Currency currency;

    @Override
    public int compareTo(Money m) {
        // 잘못된 구현 - equals와 일관되지 않음
        return Long.compare(amount, m.amount);  // 통화 무시
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money))
            return false;
        Money m = (Money) o;
        return amount == m.amount && 
               currency.equals(m.currency);  // 통화까지 비교
    }

    // 올바른 구현
    private static final Comparator<Money> COMPARATOR = 
        Comparator.comparingInt((Money m) -> m.currency.hashCode())
                  .thenComparingLong(m -> m.amount);

    @Override
    public int compareTo(Money m) {
        return COMPARATOR.compare(this, m);
    }
}
```

**중요 포인트**:
1. **일관성 보장**
   - equals와 compareTo가 같은 기준 사용
   - 정렬된 컬렉션에서도 예상대로 동작
   - 혼란스러운 동작 방지

## 자주 발생하는 질문과 답변

Q: 기본 타입 필드가 여러 개일 때 어떻게 비교하는 것이 좋을까요?
A: Comparator의 비교자 생성 메서드를 활용하세요:
```java
public final class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;

    // 상수 Comparator - 인스턴스 생성 비용 절감
    private static final Comparator<Version> COMPARATOR = 
        Comparator.comparingInt((Version v) -> v.major)
                  .thenComparingInt(v -> v.minor)
                  .thenComparingInt(v -> v.patch);

    @Override
    public int compareTo(Version ver) {
        return COMPARATOR.compare(this, ver);
    }

    // 잘못된 구현 - 이렇게 하지 마세요!
    public int badCompareTo(Version ver) {
        // 정수 오버플로우 위험 있음
        return (major * 1000 + minor * 100 + patch) - 
               (ver.major * 1000 + ver.minor * 100 + ver.patch);
    }
}
```

Q: 클래스의 핵심 필드가 객체 참조라면 어떻게 비교해야 하나요?
A: 해당 객체의 compareTo나 비교자를 사용하세요:
```java
public final class Student implements Comparable<Student> {
    private final String name;
    private final LocalDate birthDate;
    private final double gpa;

    // Comparator 구성 방법
    private static final Comparator<Student> COMPARATOR = 
        Comparator.comparing((Student s) -> s.name)  // String은 자연 순서 있음
                  .thenComparing(s -> s.birthDate)   // LocalDate도 자연 순서 있음
                  .thenComparingDouble(s -> s.gpa);  // double 필드 비교

    @Override
    public int compareTo(Student s) {
        return COMPARATOR.compare(this, s);
    }
}
```

Q: 비교 순서가 복잡한 경우는 어떻게 처리하나요?
A: 커스텀 Comparator를 구성하세요:
```java
public class Employee implements Comparable<Employee> {
    private String department;
    private int rank;
    private double salary;

    // 복잡한 비교 규칙 구현
    private static final Comparator<Employee> COMPARATOR = 
        // 1. 부서별로 정렬 (특정 부서 우선)
        Comparator.comparing((Employee e) -> e.department, 
            (d1, d2) -> {
                if (d1.equals("IT") && !d2.equals("IT")) return -1;
                if (!d1.equals("IT") && d2.equals("IT")) return 1;
                return d1.compareTo(d2);
            })
        // 2. 직급 역순 정렬
        .thenComparingInt((Employee e) -> -e.rank)
        // 3. 급여 역순 정렬
        .thenComparingDouble(e -> -e.salary);

    @Override
    public int compareTo(Employee e) {
        return COMPARATOR.compare(this, e);
    }
}

// 사용 예시
List<Employee> employees = Arrays.asList(
    new Employee("IT", 3, 5000),
    new Employee("HR", 2, 4000),
    new Employee("IT", 2, 6000)
);
// IT 부서가 앞으로, 같은 부서내에서는 직급이 높은 순
Collections.sort(employees);
```

### 3. 특수한 비교 상황 처리

#### null 처리
```java
public class OptionalField implements Comparable<OptionalField> {
    private final String requiredField;
    private final String optionalField;  // null 가능

    private static final Comparator<OptionalField> COMPARATOR = 
        Comparator
            .comparing((OptionalField o) -> o.requiredField)
            // null을 가장 작은 값으로 처리
            .thenComparing(o -> o.optionalField, 
                          Comparator.nullsFirst(String::compareTo));

    @Override
    public int compareTo(OptionalField o) {
        return COMPARATOR.compare(this, o);
    }
}
```

**null 처리 방법**:
1. **nullsFirst/nullsLast**
   - null 값의 순서 지정 가능
   - 안전한 null 처리
   - 명시적인 의도 표현

#### 상속 관계에서의 비교
```java
public class Animal implements Comparable<Animal> {
    private final String species;
    private final int weight;

    @Override
    public int compareTo(Animal a) {
        return COMPARATOR.compare(this, a);
    }

    private static final Comparator<Animal> COMPARATOR = 
        Comparator.comparing((Animal a) -> a.species)
                  .thenComparingInt(a -> a.weight);
}

// 하위 클래스
public class Dog extends Animal {
    private final String breed;

    // 추가 필드를 포함한 비교
    private static final Comparator<Dog> DOG_COMPARATOR = 
        ((Comparator<Animal>) Animal.COMPARATOR)
            .thenComparing(d -> d.breed);

    @Override
    public int compareTo(Animal a) {
        if (!(a instanceof Dog))
            return super.compareTo(a);
        return DOG_COMPARATOR.compare(this, (Dog) a);
    }
}
```

**상속 관련 주의사항**:
1. **LSP(리스코프 치환 원칙) 준수**
   - 하위 클래스의 compareTo가 상위 클래스의 규약 준수
   - 일관된 비교 순서 유지
   - 타입 안전성 보장

## 요약 (Summary)

1. **구현 원칙**
   - compareTo 규약 준수
   - equals와의 일관성 유지
   - 불변식 보장

2. **구현 방법 선택**
   - 단순 비교: 직접 구현
   - 다중 필드: Comparator 활용
   - 복잡한 규칙: 커스텀 Comparator

3. **실무 적용 가이드**
   - 정렬이 필요한 값 클래스는 Comparable 구현
   - 박싱된 기본 타입 클래스의 compare 사용
   - Comparator 빌더 활용