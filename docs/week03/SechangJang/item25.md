# Item 25: 톱레벨 클래스는 한 파일에 하나만 담으라

## 톱레벨 클래스의 정의
- 다른 클래스나 인터페이스 내부에 정의되지 않은 클래스
- Java 파일의 최상위 레벨에 정의되는 클래스

## 잘못된 예시: 한 파일에 여러 톱레벨 클래스

```java
// Utensil.java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {  // 같은 파일에 두 번째 톱레벨 클래스
    static final String NAME = "cake";
}
```

### 발생할 수 있는 문제점

1. **컴파일 순서 의존성**
```java
// 컴파일 순서에 따라 결과가 달라짐
javac Main.java Dessert.java    // 컴파일 오류
javac Main.java                 // "pancake" 출력
javac Dessert.java Main.java    // "potpie" 출력
```

2. **유지보수 어려움**
- 코드 찾기 어려움
- 의도하지 않은 클래스 재정의 위험

## 해결 방법

### 1. 톱레벨 클래스를 분리
```java
// Utensil.java
public class Utensil {
    static final String NAME = "pan";
}

// Dessert.java
public class Dessert {
    static final String NAME = "cake";
}
```

### 2. 정적 멤버 클래스 사용
```java
public class TopLevel {
    private static class Utensil {
        static final String NAME = "pan";
    }
    
    private static class Dessert {
        static final String NAME = "cake";
    }
    
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

## 장점과 단점 비교

### 분리된 톱레벨 클래스
#### 장점
- 컴파일 순서에 독립적
- 명확한 코드 구조
- 유지보수 용이

#### 단점
- 파일 수 증가

### 정적 멤버 클래스
#### 장점
- 관련 클래스들을 한 파일에 모아둘 수 있음
- private으로 선언하여 캡슐화 가능

#### 단점
- 파일이 커질 수 있음
- 클래스 계층 구조가 복잡해질 수 있음

## 실무적 가이드라인

### 1. 파일 구성 원칙
```java
// 파일당 하나의 톱레벨 클래스
public class MyClass {
    // 클래스 내용
}
```

### 2. 예외적인 상황 처리
- 밀접하게 관련된 유틸리티 클래스들은 정적 멤버 클래스로 구현
- 테스트 코드의 경우 private 정적 멤버 클래스 활용

## 핵심 정리

1. **기본 원칙**
   - 소스 파일 하나에 톱레벨 클래스 하나
   - 컴파일 순서에 영향받지 않는 코드 작성

2. **대안적 해결책**
   - 관련 있는 클래스들은 정적 멤버 클래스로 구현
   - private 접근 제어자를 활용한 캡슐화

3. **주의사항**
   - public이 아닌 톱레벨 클래스도 분리
   - 컴파일러가 강제하지 않더라도 규칙 준수

4. **예외 케이스**
   - 특별한 이유가 있는 경우만 정적 멤버 클래스 사용
   - 문서화를 통해 의도 명확히 전달