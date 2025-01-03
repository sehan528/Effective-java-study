# Item 15: 클래스와 멤버의 접근 권한을 최소화하라

## 핵심 개념 (Main Ideas)

### 1. 정보 은닉과 캡슐화의 본질
- **정의**: 정보 은닉은 구현 세부사항을 감추고 API를 통해서만 다른 컴포넌트와 소통하게 하는 것
- **목적**: 시스템을 구성하는 컴포넌트들을 서로 독립시켜 개발, 테스트, 최적화, 이해를 가능하게 함
- **효과**: 시스템 개발 속도 향상, 관리 비용 감소, 성능 최적화 용이, 재사용성 증가, 대규모 시스템 개발 용이

### 2. 접근 제한의 기본 원칙
- **원칙**: 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 함
- **이유**: 잘 설계된 컴포넌트는 구현 세부사항을 완벽히 숨겨야 함
- **방법**: 각 클래스와 멤버의 접근 수준을 프로그램이 올바르게 동작하는 범위 내에서 가장 낮은 수준으로 설정

## 세부 내용 (Details)

### 1. 접근 수준의 이해와 적용

#### 클래스와 인터페이스 수준
```java
// 톱레벨 클래스와 인터페이스에 부여할 수 있는 접근 수준
public class PublicClass { ... }  // 공개 API
class PackagePrivateClass { ... } // 패키지 내부용

// 한 소스 파일에 톱레벨 클래스를 여러 개 선언하는 경우
class Host { ... }               // 주 클래스
class Helper { ... }            // 도우미 클래스 (같은 패키지에서만 사용)
```

**중요 고려사항**:
- public 클래스: 해당 패키지의 API, 하위 호환성 유지 필요
- package-private 클래스: 패키지 내부 구현에 속함, 다음 릴리스에서 수정/제거/교체 가능
- 한 클래스에서만 사용하는 package-private 클래스는 private static으로 중첩 고려

#### 멤버 (필드, 메서드, 중첩 클래스/인터페이스) 수준
```java
public class MemberExample {
    private int privateField;           // 클래스 내부에서만 접근
    int packagePrivateField;           // 같은 패키지 내에서 접근
    protected int protectedField;      // 하위 클래스에서도 접근
    public int publicField;            // 모든 곳에서 접근

    // 멤버 클래스의 접근 수준 예시
    private static class PrivateStaticClass { ... }
    protected class ProtectedInnerClass { ... }
}
```

**상세 설명**:
1. private: 
   - 가장 제한적인 접근 수준
   - 선언된 톱레벨 클래스에서만 접근 가능
   - 데이터 캡슐화의 기본 도구

2. package-private (default):
   - 같은 패키지 내의 모든 클래스에서 접근 가능
   - 한 패키지를 하나의 모듈처럼 사용할 때 유용

3. protected:
   - package-private의 접근 범위를 포함
   - 하위 클래스에서도 접근 가능
   - 상속을 고려한 설계시 신중히 사용

4. public:
   - 모든 곳에서 접근 가능
   - API의 일부가 되므로 영원히 지원해야 함
   - 가장 신중히 사용해야 함

### 2. 구체적인 사례 연구

#### 사례 1: 안전하지 않은 public 배열 처리
```java
// 보안 문제가 있는 코드
public class SecurityExample {
    public static final Thing[] VALUES = { ... };  // 취약점
}

// 해결책 1: 불변 리스트
public class SecurityExample {
    private static final Thing[] PRIVATE_VALUES = { ... };
    public static final List<Thing> VALUES = 
        Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
}

// 해결책 2: 방어적 복사
public class SecurityExample {
    private static final Thing[] PRIVATE_VALUES = { ... };
    public static Thing[] values() {
        return PRIVATE_VALUES.clone();
    }
}
```

**문제점과 해결방안 분석**:
1. 문제점:
   - public 배열은 클라이언트에서 직접 수정 가능
   - final로 선언해도 배열 내부 값은 변경 가능
   - 보안 취약점이 될 수 있음

2. 해결책 설명:
   - 불변 리스트 방식: 뷰를 통한 접근만 허용
   - 방어적 복사 방식: 복사본을 통한 안전한 데이터 공유
   - 두 방식 모두 내부 데이터 보호 가능

#### 사례 2: 상속과 접근 제한
```java
public class Super {
    public void method() { ... }
}

public class Sub extends Super {
    @Override
    public void method() { ... }  // OK
    
    @Override
    protected void method() { ... }  // 컴파일 에러
}
```

**제약사항과 이유**:
- 상위 클래스의 메서드를 재정의할 때는 접근 수준을 더 좁게 설정할 수 없음
- 리스코프 치환 원칙 위배 방지
- 하위 클래스의 인스턴스가 상위 클래스의 인스턴스로 대체 가능해야 함

### 3. 자주 발생하는 질문과 답변

Q: 정보 은닉의 장점을 구체적으로 설명해주세요.
A: 정보 은닉의 구체적 장점은 다음과 같습니다:
1. **병렬 개발 가능**: 
   - 여러 컴포넌트를 독립적으로 개발 가능
   - 컴포넌트 별로 다른 개발자 할당 가능
   - 개발 속도 향상

2. **부담 없는 최적화**: 
   - 다른 컴포넌트에 영향을 주지 않고 특정 컴포넌트만 최적화 가능
   - 성능 분석 결과에 따른 선택적 최적화 가능

3. **재사용성 향상**: 
   - 독립적인 컴포넌트는 다른 환경에서도 사용 가능
   - 의존성이 낮을수록 재사용성 증가

Q: public 클래스에서 protected 멤버의 사용을 왜 신중히 해야 하나요?
A: protected 멤버는 다음과 같은 이유로 신중히 사용해야 합니다:
1. protected 멤버는 공개 API의 일부가 되어 영원히 지원해야 함
2. 하위 클래스에서의 사용을 예측하기 어려움
3. 캡슐화가 깨질 수 있는 위험이 있음
4. 클래스 내부 동작 방식이 외부로 노출될 수 있음

Q: 모듈 시스템(Java 9+)에서의 접근 제어는 어떻게 다른가요?
A: 모듈 시스템의 접근 제어 특징:
1. 모듈 선언의 exports로 공개할 패키지 지정
2. 모듈 내부 패키지는 캡슐화됨
3. 특정 모듈에만 접근을 허용하는 세밀한 제어 가능
4. 하지만 현재까지는 비모듈 방식이 더 일반적

## 요약 (Summary)

접근 제한자는 캡슐화를 위한 강력한 도구입니다. 다음 원칙들을 기억해야 합니다:

1. 모든 클래스와 멤버의 접근성을 가능한 한 좁혀라
2. public 클래스는 public static final 필드 외에는 public 필드를 가지지 말아야 함
3. public static final 필드가 참조하는 객체는 반드시 불변이어야 함
4. 배열은 변경 가능성이 있으므로 절대로 public static final로 선언하지 말 것
5. 접근자 메서드를 통한 데이터 캡슐화를 항상 고려할 것