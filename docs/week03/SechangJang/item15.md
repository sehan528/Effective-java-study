# Item 15: 클래스와 멤버의 접근권한을 최소화하라

## 정보 은닉의 원칙

### 정보 은닉이란?
- 내부 데이터와 구현 세부사항을 외부로부터 숨기는 것
- 오직 API를 통해서만 다른 컴포넌트와 소통
- 시스템을 구성하는 컴포넌트들을 서로 독립시켜 개발, 테스트, 최적화, 이해 가능

### 정보 은닉의 장점
1. 시스템 개발 속도 향상 (병렬 개발 가능)
2. 시스템 관리 비용 감소
3. 성능 최적화 용이
4. 소프트웨어 재사용성 증가
5. 큰 시스템 개발 난이도 감소

## 접근 제한자의 활용

### 클래스와 인터페이스의 접근 수준
- `public`: 공개 API가 됨
- `package-private`: 해당 패키지 내에서만 사용 가능

### 멤버(필드, 메서드, 중첩 클래스/인터페이스)의 접근 수준
- `private`: 해당 클래스 내에서만 접근 가능
- `package-private`: 같은 패키지 내에서 접근 가능 (기본 접근 수준)
- `protected`: 상속받은 클래스에서 접근 가능
- `public`: 모든 곳에서 접근 가능

## 주의해야 할 상황들

### public 클래스의 인스턴스 필드
```java
// 잘못된 예
public class Point {
    public double x;  // 외부에서 직접 접근 가능
    public double y;  // 스레드 안전성 보장 불가
}

// 올바른 예
public class Point {
    private double x;
    private double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
}
```

### public static final 필드 - 배열

```java
// 보안 허점이 있는 코드
public static final Integer[] VALUES = {1, 2, 3};  // 외부에서 배열 내용 수정 가능

// 해결책 1: private 배열 + public 불변 리스트
private static final Integer[] VALUES = {1, 2, 3};
public static final List<Integer> VALUES_LIST = 
    Collections.unmodifiableList(Arrays.asList(VALUES));

// 해결책 2: private 배열 + 복사본을 반환하는 public 메서드
private static final Integer[] VALUES = {1, 2, 3};
public static Integer[] values() {
    return VALUES.clone();  // 방어적 복사
}
```

#### 두 해결책의 차이점
1. **불변 리스트 방식**
   - 읽기 전용 뷰 제공
   - 원본 배열이 수정되면 뷰에도 반영됨
   
2. **배열 복사 방식**
   - 해당 시점의 복사본 제공
   - 원본 배열이 수정되어도 복사본은 영향받지 않음

## 접근 권한 설계 원칙
1. 가능한 한 모든 클래스와 멤버의 접근성을 좁혀라
2. `public` 클래스는 `public static final` 필드 외에는 `public` 필드를 가지지 말아야 함
3. `public static final` 필드가 참조하는 객체는 반드시 불변이어야 함
4. 길이가 0이 아닌 배열은 항상 변경 가능하므로 주의가 필요

## 예외사항
- 테스트만을 위해 접근 수준을 넓히지 말 것
- 리스코프 치환 원칙에 따라 상위 클래스의 메서드를 재정의할 때는 접근 수준을 좁힐 수 없음

## 핵심 요약
- 접근 권한은 가능한 한 최소화하라
- 정보 은닉(캡슐화)은 시스템 개발/관리/최적화/재사용성에 큰 도움이 된다
- `public` 필드는 스레드 안전성을 보장할 수 없다