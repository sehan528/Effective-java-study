# Item 19: 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라

## 상속용 클래스 설계시 고려사항

### 문서화 요구사항
- 재정의 가능 메서드의 내부 동작 방식을 문서화
- 메서드의 자기사용 패턴을 문서로 남김
- @implSpec을 사용하여 내부 동작 방식 설명

```java
/**
 * @implSpec 
 * 이 메서드는 각 도형에 대해 draw 메서드를 순차적으로 호출한다.
 * 하위 클래스에서 재정의시 각 도형의 draw 동작을 보장해야 한다.
 */
public void draw(String color) {
    for (Shape shape: shapes) {
        shape.draw(color);
    }
}
```

### 상속 제약사항

#### 1. 생성자 작성 규칙
- 생성자에서 재정의 가능 메서드 호출 금지
- private, final, static 메서드만 호출 가능

```java
// 잘못된 예
public class Super {
    public Super() {
        overrideMe();  // 생성자에서 재정의 가능 메서드 호출
    }
    public void overrideMe() {}
}

// 문제 발생 예시
public class Sub extends Super {
    private final Instant instant;
    
    public Sub() {
        instant = Instant.now();  // 초기화되기 전에 상위 클래스에서 호출
    }
    
    @Override
    public void overrideMe() {
        System.out.println(instant);  // NPE 발생 가능
    }
}
```

#### 2. Cloneable/Serializable 관련 규칙
- clone과 readObject에서 재정의 가능 메서드 호출 금지
- readResolve와 writeReplace는 protected로 선언

## 상속 금지하기

### 1. final 클래스 선언
```java
final public class ProhibitInheritance {
    // 클래스 내용
}
```

### 2. 정적 팩터리 메서드 활용
```java
public class ProhibitInheritance {
    private ProhibitInheritance() {}  // 생성자를 private으로
    
    public static ProhibitInheritance getInstance() {
        return new ProhibitInheritance();
    }
}
```

## 실무적 조언

### 상속 허용시 테스트 방법
1. 최소 3개의 하위 클래스를 만들어 검증
2. protected 메서드의 필요성 확인
3. 문서화된 내부 동작 방식 준수 여부 테스트

### 현실적인 접근 방법
1. 코딩 규칙으로 상속 제한
2. 코드 리뷰에 상속 관련 규칙 반영
3. 변수 공유만을 위한 상속 지양

## 핵심 정리

1. **상속용 클래스 요구사항**
   - 문서화: 재정의 가능 메서드의 내부 동작 방식
   - 제약: 생성자에서 재정의 가능 메서드 호출 금지
   - 테스트: 하위 클래스 작성하여 검증

2. **상속을 허용하지 않을 경우**
   - final 클래스로 선언
   - 생성자를 private으로 제한하고 정적 팩터리 제공

3. **실무적 접근**
   - 상속보다는 인터페이스 구현 선호
   - 문서화와 테스트를 통한 안전한 상속 설계
   - 불필요한 상속 지양