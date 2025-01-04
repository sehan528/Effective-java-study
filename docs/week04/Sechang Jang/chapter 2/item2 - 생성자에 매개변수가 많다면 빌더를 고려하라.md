# Item 2: 생성자에 매개변수가 많다면 빌더를 고려하라

## 핵심 개념 (Main Ideas)

### 1. 객체 생성 패턴의 발전
- **정의**: 객체 생성 시 필수값과 선택값을 유연하게 처리하는 방법의 진화
- **목적**: 객체 생성의 유연성과 가독성, 안전성 확보
- **효과**: 클라이언트 코드의 품질 향상과 개발자 실수 방지

### 2. 빌더 패턴의 장점
- **원칙**: 필수 매개변수와 선택 매개변수를 명확히 구분
- **이유**: 객체 생성 과정의 안전성과 가독성 확보
- **방법**: 빌더 클래스를 통한 단계적 객체 생성

## 세부 내용 (Details)

### 1. 기존 객체 생성 방식의 문제점

#### 점층적 생성자 패턴의 한계
```java
public class Member {
    private final String name;      // 필수
    private final String email;     // 필수
    private final String address;   // 선택
    private final String company;   // 선택
    private final String birthday;  // 선택

    // 필수값만 받는 생성자
    public Member(String name, String email) {
        this(name, email, null);
    }

    // 주소까지 받는 생성자
    public Member(String name, String email, String address) {
        this(name, email, address, null);
    }

    // 회사까지 받는 생성자
    public Member(String name, String email, String address, String company) {
        this(name, email, address, company, null);
    }

    // 모든 값을 받는 생성자
    public Member(String name, String email, String address, String company, String birthday) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.company = company;
        this.birthday = birthday;
    }
}

// 사용 예시
Member member = new Member("홍길동", "hong@example.com", "서울시", "회사", "19900101");
```

**이 코드의 문제점**:
1. **매개변수 순서 의존성**
   - 같은 타입의 매개변수가 연속되면 순서 실수 가능
   - 컴파일러가 타입이 같으면 순서 오류를 잡지 못함
   - 런타임에 예상치 못한 버그 발생 가능

2. **코드 가독성**
   - 매개변수가 많아지면 어떤 값이 어떤 필드에 해당하는지 파악 어려움
   - 생성자 호출 시 각 값의 의미를 알기 어려움
   - 모든 선택값을 넘겨야 할 경우 불필요한 null 전달 필요

### 2. 빌더 패턴을 통한 해결

#### 기본 빌더 패턴 구현
```java
public class Member {
    private final String name;      // 필수
    private final String email;     // 필수
    private final String address;   // 선택
    private final String company;   // 선택
    private final String birthday;  // 선택

    public static class Builder {
        // 필수 매개변수
        private final String name;
        private final String email;

        // 선택 매개변수 - 기본값으로 초기화
        private String address = "";
        private String company = "";
        private String birthday = "";

        // 필수값만 받는 빌더 생성자
        public Builder(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // 선택값을 설정하는 메서드들
        public Builder address(String address) {
            this.address = address;
            return this;  // 메서드 체이닝을 위해 this 반환
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder birthday(String birthday) {
            this.birthday = birthday;
            return this;
        }

        // 최종적으로 Member 객체를 생성하는 메서드
        public Member build() {
            return new Member(this);
        }
    }

    private Member(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.address = builder.address;
        this.company = builder.company;
        this.birthday = builder.birthday;
    }
}

// 사용 예시
Member member = new Member.Builder("홍길동", "hong@example.com")
    .address("서울시")
    .company("회사")
    .birthday("19900101")
    .build();
```

**빌더 패턴의 장점**:
1. **명확한 가독성**
   - 어떤 값을 설정하는지 메서드 이름으로 명확히 알 수 있음
   - 선택적 매개변수는 필요한 것만 호출 가능
   - 메서드 체이닝으로 유연한 객체 생성 가능

2. **안전성**
   - 불변 객체를 만들 수 있음
   - 잘못된 매개변수 순서로 인한 버그 방지
   - 빌더의 매개변수가 유효하지 않으면 build() 메서드에서 검증 가능

### 3. 계층적 빌더 패턴

#### 상속을 활용한 빌더 패턴
```java
// 추상 피자 클래스
public abstract class Pizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }
    final Set<Topping> toppings;

    // 추상 빌더 클래스
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();
        
        // 하위 클래스에서 구현할 메서드
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }
}

// 구체적인 피자 클래스
public class NYPizza extends Pizza {
    public enum Size { SMALL, MEDIUM, LARGE }
    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {
        private final Size size;

        public Builder(Size size) {
            this.size = size;
        }

        @Override
        public NYPizza build() {
            return new NYPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NYPizza(Builder builder) {
        super(builder);
        size = builder.size;
    }
}

// 사용 예시
NYPizza pizza = new NYPizza.Builder(Size.MEDIUM)
    .addTopping(Topping.HAM)
    .addTopping(Topping.MUSHROOM)
    .build();
```

**계층적 빌더의 특징**:
1. **유연한 확장**
   - 공통 속성은 상위 클래스에서 관리
   - 특수한 속성은 하위 클래스에서 추가
   - 빌더도 계층적으로 설계 가능

2. **타입 안전성**
   - 제네릭을 활용한 메서드 체이닝
   - 하위 클래스에서도 형변환 없이 사용 가능
   - 컴파일 타임에 타입 안전성 보장

## 자주 발생하는 질문과 답변

Q: 빌더 패턴은 언제 사용하면 좋을까요?
A: 다음과 같은 상황에서 빌더 패턴 사용을 고려하세요:
```java
// 빌더 패턴이 유용한 경우
public class Order {
    private final long id;               // 필수
    private final String customerName;   // 필수
    private final String address;        // 선택
    private final String email;          // 선택
    private final String phone;          // 선택
    private final String note;           // 선택
    private final LocalDate orderDate;   // 선택

    public static class Builder {
        // 필수 매개변수
        private final long id;
        private final String customerName;

        public Builder(long id, String customerName) {
            this.id = id;
            this.customerName = customerName;
        }

        // 선택 매개변수를 위한 메서드들
        public Builder address(String address) { ... }
        public Builder email(String email) { ... }
        public Builder phone(String phone) { ... }
        public Builder note(String note) { ... }
        public Builder orderDate(LocalDate date) { ... }

        public Order build() {
            // 객체 생성 전 유효성 검증도 가능
            if (email == null && phone == null) {
                throw new IllegalStateException("이메일과 전화번호 중 하나는 필수입니다.");
            }
            return new Order(this);
        }
    }
}
```

Q: 빌더 패턴의 단점은 무엇인가요?
A: 다음과 같은 단점들이 있습니다:
1. 클래스와 별도로 빌더 클래스를 작성해야 하는 부가 비용
2. 객체 생성 시 빌더 객체를 추가로 생성하는 비용
3. Lombok 등의 도구를 사용하면 이러한 단점들을 줄일 수 있음

## 요약 (Summary)

1. **빌더 패턴 사용 기준**
   - 생성자 매개변수가 4개 이상인 경우 고려
   - 선택적 매개변수가 많은 경우 유용
   - 불변 객체를 만들어야 하는 경우 추천

2. **빌더 패턴의 이점**
   - 읽기 쉽고 유지보수하기 좋은 코드
   - 메서드 체이닝으로 유연한 객체 생성
   - 불변성과 안전성 확보 용이

3. **실무 적용 포인트**
   - Lombok @Builder 활용 검토
   - 계층 구조가 있는 경우 추상 빌더 고려
   - 필수 매개변수는 생성자에서 강제